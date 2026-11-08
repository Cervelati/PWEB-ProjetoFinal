package com.rosetas.service;

import com.rosetas.domain.*;
import com.rosetas.payment.PagamentoGateway;
import com.rosetas.repository.*;
import com.rosetas.security.AuthUser;
import com.rosetas.web.ApiException;
import com.rosetas.web.Dtos;
import com.rosetas.web.Dtos.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    /** Evita que alguém trave o estoque criando vários pedidos que nunca paga. */
    private static final int MAX_PEDIDOS_ABERTOS_POR_CLIENTE = 3;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final PedidoRepository pedidos;
    private final ClienteRepository clientes;
    private final ModeloRepository modelos;
    private final MaterialRepository materiais;
    private final PagamentoRepository pagamentos;
    private final EstoqueService estoque;
    private final PedidoStatusService status;
    private final PagamentoGateway gateway;
    private final PagamentoService pagamentoService;
    private final ConfiguracaoService config;

    public PedidoService(PedidoRepository pedidos, ClienteRepository clientes, ModeloRepository modelos,
                         MaterialRepository materiais, PagamentoRepository pagamentos, EstoqueService estoque,
                         PedidoStatusService status, PagamentoGateway gateway,
                         PagamentoService pagamentoService, ConfiguracaoService config) {
        this.pedidos = pedidos;
        this.clientes = clientes;
        this.modelos = modelos;
        this.materiais = materiais;
        this.pagamentos = pagamentos;
        this.estoque = estoque;
        this.status = status;
        this.gateway = gateway;
        this.pagamentoService = pagamentoService;
        this.config = config;
    }

    // ---------- criação (pública, sem login) ----------

    @Transactional
    public PedidoPublicoResp criar(CriarPedidoReq req) {
        Cliente cliente = obterCliente(req.cliente());
        if (pedidos.countByClienteIdAndStatus(cliente.getId(), StatusPedido.AGUARDANDO_PAGAMENTO)
                >= MAX_PEDIDOS_ABERTOS_POR_CLIENTE) {
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS,
                    "Você já tem pedidos aguardando pagamento. Pague ou cancele um deles antes de fazer outro.");
        }

        Pedido p = new Pedido();
        p.setCliente(cliente);
        p.setCodigoAcompanhamento(novoCodigo());
        p.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        p.setData(OffsetDateTime.now());
        p.setObservacoes(req.observacoes());

        Set<Long> fitaIds = req.itens().stream().flatMap(i -> i.partes().stream())
                .map(PersonalizacaoReq::fitaId).collect(Collectors.toSet());
        // Só para validar tipo/cor/ativo. O saldo NÃO é lido daqui: a reserva debita direto no banco (EstoqueService).
        Map<Long, Material> fitaPorId = materiais.findAllById(fitaIds).stream()
                .collect(Collectors.toMap(Material::getId, Function.identity()));

        BigDecimal gravacaoExtra = config.precoGravacaoExtra();
        Map<Long, BigDecimal> necessidade = new TreeMap<>(); // ordenado por id: locks sempre na mesma ordem
        BigDecimal total = BigDecimal.ZERO;

        for (ItemReq ir : req.itens()) {
            ModeloRoseta modelo = modelos.findById(ir.modeloId())
                    .filter(ModeloRoseta::isAtivo)
                    .orElseThrow(() -> ApiException.notFound("Modelo não encontrado: " + ir.modeloId()));
            Map<Long, ParteModelo> partesDoModelo = modelo.getPartes().stream()
                    .collect(Collectors.toMap(ParteModelo::getId, Function.identity()));

            ItemPedido item = new ItemPedido();
            item.setPedido(p);
            item.setModelo(modelo);
            item.setQuantidade(ir.quantidade());
            item.setTextoPersonalizado(ir.textoPersonalizado());
            item.setGravacoesExtras(ir.gravacoesExtras());

            // O preço é lido do modelo NO MOMENTO do pedido: alterar o preço no ERP não afeta pedidos já feitos.
            BigDecimal precoBase = precoDoModelo(modelo, ir.franzido());
            item.setFranzido(modelo.temFranzido() ? ir.franzido() : null);
            BigDecimal precoUnitario = precoBase.add(gravacaoExtra.multiply(BigDecimal.valueOf(ir.gravacoesExtras())));
            item.setPrecoUnitario(precoUnitario);

            Set<Long> escolhidas = new HashSet<>();
            for (PersonalizacaoReq pr : ir.partes()) {
                ParteModelo parte = partesDoModelo.get(pr.parteId());
                if (parte == null) {
                    throw ApiException.badRequest("A parte " + pr.parteId() + " não pertence ao modelo " + modelo.getNome());
                }
                if (!escolhidas.add(parte.getId())) {
                    throw ApiException.badRequest("Parte '" + parte.getNome() + "' informada mais de uma vez");
                }
                Material fita = fitaPorId.get(pr.fitaId());
                if (fita == null || !fita.isAtivo() || fita.getTipo() != TipoMaterial.FITA) {
                    throw ApiException.badRequest("Fita indisponível: " + pr.fitaId());
                }
                PersonalizacaoParte pp = new PersonalizacaoParte();
                pp.setItem(item);
                pp.setParte(parte);
                pp.setFita(fita);
                item.getPersonalizacoes().add(pp);

                necessidade.merge(fita.getId(),
                        parte.getMetrosConsumidos().multiply(BigDecimal.valueOf(ir.quantidade())),
                        BigDecimal::add);
            }
            if (escolhidas.size() != partesDoModelo.size()) {
                throw ApiException.badRequest("Escolha a cor de todas as partes do modelo " + modelo.getNome());
            }
            // Materiais fixos do modelo (botton, argola, alfinete...) também saem do estoque.
            for (ComponenteModelo c : modelo.getComponentes()) {
                necessidade.merge(c.getMaterial().getId(),
                        c.getQuantidade().multiply(BigDecimal.valueOf(ir.quantidade())), BigDecimal::add);
            }
            p.getItens().add(item);
            total = total.add(precoUnitario.multiply(BigDecimal.valueOf(ir.quantidade())));
        }
        p.setValorTotal(total);
        pedidos.save(p);

        estoque.reservar(p, necessidade); // 409 se não houver saldo; desfaz a transação inteira

        status.registrarCriacao(p, "cliente:" + cliente.getId());

        PagamentoGateway.Cobranca c = gateway.criarCobrancaPix(p.getId(), total);
        Pagamento pg = new Pagamento();
        pg.setPedido(p);
        pg.setValor(total);
        pg.setMetodo(MetodoPagamento.PIX);
        pg.setStatus(StatusPagamento.PENDENTE);
        pg.setGatewayTransacaoId(c.transacaoId());
        pg.setPixCopiaCola(c.pixCopiaCola());
        pagamentos.save(pg);

        return Dtos.publico(p, pg);
    }

    private Cliente obterCliente(ClienteReq r) {
        String telefone = ClienteService.normalizarTelefone(r.telefone());
        String email = r.email() == null || r.email().isBlank() ? null : r.email().trim();
        Cliente c = clientes.findByTelefone(telefone).orElse(null);
        if (c == null) {
            c = new Cliente();
            c.setNome(r.nome().trim());
            c.setTelefone(telefone);
            c.setEmail(email);
            c.setCriadoEm(OffsetDateTime.now());
            return clientes.save(c);
        }
        if (c.getEmail() == null && email != null) {
            c.setEmail(email);
        }
        return c;
    }

    private static String novoCodigo() {
        byte[] b = new byte[24];
        RANDOM.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    // ---------- consultas públicas (pelo código do link) ----------

    @Transactional(readOnly = true)
    public PedidoPublicoResp detalhePublico(String codigo) {
        Pedido p = buscarPorCodigo(codigo);
        return Dtos.publico(p, pagamentoDe(p));
    }

    /** O cliente só cancela enquanto ainda não pagou. */
    @Transactional
    public PedidoPublicoResp cancelarPublico(String codigo) {
        Long id = pedidos.findIdByCodigo(codigo).orElseThrow(() -> ApiException.notFound("Pedido não encontrado"));
        Pedido p = lockar(id);
        encerrarSemPagamento(p, "cliente:" + p.getCliente().getId());
        return Dtos.publico(p, pagamentoDe(p));
    }

    // ---------- consultas da equipe ----------

    @Transactional(readOnly = true)
    public List<PedidoResp> listarGestao(StatusPedido filtro) {
        List<Pedido> lista = filtro == null ? pedidos.findAllByOrderByDataDesc() : pedidos.findByStatusOrderByData(filtro);
        return lista.stream().map(this::resp).toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoResp> pedidosDoCliente(Long clienteId) {
        return pedidos.findByClienteIdOrderByDataDesc(clienteId).stream().map(this::resp).toList();
    }

    @Transactional(readOnly = true)
    public List<FichaResp> fichasEmProducao() {
        return pedidos.findByStatusOrderByData(StatusPedido.EM_PRODUCAO).stream().map(Dtos::ficha).toList();
    }

    @Transactional(readOnly = true)
    public FichaResp ficha(Long id) {
        Pedido p = buscar(id);
        if (p.getStatus() != StatusPedido.EM_PRODUCAO) {
            throw ApiException.notFound("Pedido não está em produção");
        }
        return Dtos.ficha(p);
    }

    // ---------- transições (equipe) ----------

    /** Pix estático: a gestão conferiu o recebimento no banco e confirma o pagamento manualmente. */
    @Transactional
    public PedidoResp confirmarPagamento(Long id, AuthUser admin) {
        Pedido p = lockar(id);
        if (p.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw ApiException.conflict("O pedido não está aguardando pagamento (status atual: " + p.getStatus() + ")");
        }
        Pagamento pg = pagamentoDe(p);
        if (pg == null || pg.getStatus() != StatusPagamento.PENDENTE) {
            throw ApiException.conflict("Não há pagamento pendente para este pedido");
        }
        pagamentoService.processar(pg.getGatewayTransacaoId(), StatusPagamento.APROVADO, "admin:" + admin.uid());
        return resp(p);
    }

    @Transactional
    public PedidoResp aprovar(Long id, AuthUser admin) {
        Pedido p = lockar(id);
        exigir(p, StatusPedido.AGUARDANDO_APROVACAO);
        estoque.baixar(p);
        status.transicionar(p, StatusPedido.EM_PRODUCAO, admin.uid());
        return resp(p);
    }

    @Transactional
    public PedidoResp recusar(Long id, String motivo, AuthUser admin) {
        Pedido p = lockar(id);
        exigir(p, StatusPedido.AGUARDANDO_APROVACAO);
        String obs = (p.getObservacoes() == null ? "" : p.getObservacoes() + " | ") + "Recusado: " + motivo;
        p.setObservacoes(obs.length() > 1000 ? obs.substring(0, 1000) : obs);
        estoque.estornar(p);
        status.transicionar(p, StatusPedido.CANCELADO, admin.uid());
        log.warn("Pedido {} recusado com pagamento aprovado: reembolso manual necessário", p.getId());
        return resp(p);
    }

    @Transactional
    public PedidoResp entregar(Long id, AuthUser admin) {
        Pedido p = lockar(id);
        exigir(p, StatusPedido.EM_PRODUCAO);
        status.transicionar(p, StatusPedido.ENTREGUE, admin.uid());
        return resp(p);
    }

    /** Chamado pela rotina de expiração (uma transação por pedido). */
    @Transactional
    public void expirar(Long id) {
        Pedido p = lockar(id);
        if (p.getStatus() == StatusPedido.AGUARDANDO_PAGAMENTO) {
            encerrarSemPagamento(p, "sistema");
        }
    }

    private void encerrarSemPagamento(Pedido p, String quem) {
        // Mesmo lock do webhook: evita cancelar ao mesmo tempo em que o pagamento é confirmado.
        Pagamento pg = pagamentos.lockByPedidoId(p.getId()).orElse(null);
        if (p.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO
                || (pg != null && pg.getStatus() == StatusPagamento.APROVADO)) {
            throw ApiException.conflict("Pedido não pode mais ser cancelado nesta etapa");
        }
        if (pg != null && pg.getStatus() == StatusPagamento.PENDENTE) {
            pg.setStatus(StatusPagamento.EXPIRADO);
        }
        estoque.estornar(p);
        status.transicionar(p, StatusPedido.CANCELADO, quem);
    }

    // ---------- helpers ----------

    /** Preço por unidade do franzido escolhido; modelos sem franzido usam o preço base. */
    private BigDecimal precoDoModelo(ModeloRoseta m, Franzido franzido) {
        if (!m.temFranzido()) {
            return m.getPreco();
        }
        if (franzido == null) {
            throw ApiException.badRequest("Escolha o tipo de franzido do modelo " + m.getNome());
        }
        BigDecimal preco = franzido == Franzido.AMERICANO ? m.getPrecoAmericano() : m.getPrecoNormal();
        if (preco == null) {
            throw ApiException.badRequest("O modelo " + m.getNome() + " não é oferecido no franzido "
                    + franzido.name().toLowerCase());
        }
        return preco;
    }

    /**
     * Carrega o pedido já travado (SELECT ... FOR UPDATE). Deve ser a primeira leitura do pedido na transação:
     * assim o status lido é o mais recente e duas ações simultâneas (ex.: recusar duas vezes) se serializam.
     */
    private Pedido lockar(Long id) {
        return pedidos.lockById(id).orElseThrow(() -> ApiException.notFound("Pedido não encontrado"));
    }

    private Pedido buscar(Long id) {
        return pedidos.findById(id).orElseThrow(() -> ApiException.notFound("Pedido não encontrado"));
    }

    private Pedido buscarPorCodigo(String codigo) {
        return pedidos.findByCodigoAcompanhamento(codigo)
                .orElseThrow(() -> ApiException.notFound("Pedido não encontrado"));
    }

    private void exigir(Pedido p, StatusPedido esperado) {
        if (p.getStatus() != esperado) {
            throw ApiException.conflict("Pedido está em " + p.getStatus() + "; esperado " + esperado);
        }
    }

    private Pagamento pagamentoDe(Pedido p) {
        return pagamentos.findFirstByPedidoIdOrderByIdDesc(p.getId()).orElse(null);
    }

    private PedidoResp resp(Pedido p) {
        return Dtos.pedido(p, pagamentoDe(p));
    }
}
