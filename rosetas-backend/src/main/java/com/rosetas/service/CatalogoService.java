package com.rosetas.service;

import com.rosetas.domain.*;
import com.rosetas.repository.ComponenteModeloRepository;
import com.rosetas.repository.MaterialRepository;
import com.rosetas.repository.ModeloRepository;
import com.rosetas.repository.ParteModeloRepository;
import com.rosetas.web.ApiException;
import com.rosetas.web.Dtos.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
public class CatalogoService {

    private final ModeloRepository modelos;
    private final ParteModeloRepository partes;
    private final ComponenteModeloRepository componentes;
    private final MaterialRepository materiais;
    private final EstoqueService estoque;
    private final ConfiguracaoService config;
    private final FotoStorage fotos;

    public CatalogoService(ModeloRepository modelos, ParteModeloRepository partes,
                           ComponenteModeloRepository componentes, MaterialRepository materiais,
                           EstoqueService estoque, ConfiguracaoService config, FotoStorage fotos) {
        this.fotos = fotos;
        this.modelos = modelos;
        this.partes = partes;
        this.componentes = componentes;
        this.materiais = materiais;
        this.estoque = estoque;
        this.config = config;
    }

    // ---------- público ----------

    @Transactional(readOnly = true)
    public List<ModeloResp> modelosAtivos() {
        Set<Long> indisponiveis = componentes.modelosSemComponentes();
        return modelos.findByAtivoTrueOrderByNome().stream()
                .map(m -> ModeloResp.from(m, !indisponiveis.contains(m.getId()))).toList();
    }

    @Transactional(readOnly = true)
    public ModeloResp modeloAtivo(Long id) {
        return modelos.findById(id).filter(ModeloRoseta::isAtivo).map(this::resp)
                .orElseThrow(() -> ApiException.notFound("Modelo não encontrado"));
    }

    /** RN16: só cores de fita que existem em estoque. */
    @Transactional(readOnly = true)
    public List<FitaResp> fitasDisponiveis() {
        return materiais.findByTipoAndAtivoTrueAndSaldoGreaterThanOrderByNome(TipoMaterial.FITA, BigDecimal.ZERO)
                .stream().map(FitaResp::from).toList();
    }

    @Transactional(readOnly = true)
    public OpcoesResp opcoes() {
        return new OpcoesResp(config.precoGravacaoExtra());
    }

    // ---------- administração: materiais / estoque ----------

    @Transactional(readOnly = true)
    public List<MaterialResp> todosMateriais() {
        return materiais.findAllByOrderByTipoAscNomeAsc().stream().map(MaterialResp::from).toList();
    }

    @Transactional
    public MaterialResp criarMaterial(MaterialReq req) {
        Material m = new Material();
        aplicar(m, req);
        return MaterialResp.from(materiais.save(m));
    }

    @Transactional
    public MaterialResp atualizarMaterial(Long id, MaterialReq req) {
        Material m = materiais.findById(id).orElseThrow(() -> ApiException.notFound("Material não encontrado"));
        aplicar(m, req);
        return MaterialResp.from(m);
    }

    /** O saldo só muda por entrada/reserva/estorno (movimentos), nunca por edição do cadastro. */
    @Transactional
    public MaterialResp entradaEstoque(Long id, BigDecimal quantidade) {
        estoque.entrada(id, quantidade);
        return MaterialResp.from(materiais.findById(id).orElseThrow());
    }

    private void aplicar(Material m, MaterialReq req) {
        if (req.tipo() == TipoMaterial.FITA && (req.hex() == null || req.hex().isBlank())) {
            throw ApiException.badRequest("Informe a cor (#RRGGBB) da fita");
        }
        m.setNome(req.nome().trim());
        m.setTipo(req.tipo());
        m.setUnidade(req.unidade());
        m.setHex(req.tipo() == TipoMaterial.FITA ? req.hex().toUpperCase() : null);
        m.setFoto(req.foto());
        if (req.ativo() != null) {
            m.setAtivo(req.ativo());
        }
    }

    // ---------- administração: modelos ----------

    @Transactional(readOnly = true)
    public List<ModeloResp> todosModelos() {
        Set<Long> indisponiveis = componentes.modelosSemComponentes();
        return modelos.findAllByOrderByNome().stream()
                .map(m -> ModeloResp.from(m, !indisponiveis.contains(m.getId()))).toList();
    }

    @Transactional
    public ModeloResp criarModelo(ModeloReq req) {
        ModeloRoseta m = new ModeloRoseta();
        aplicar(m, req, null);
        m.setAtivo(req.ativo() == null || req.ativo());
        if (req.partes() != null) {
            for (ParteReq pr : req.partes()) {
                m.getPartes().add(novaParte(m, pr));
            }
        }
        if (req.componentes() != null) {
            for (ComponenteReq cr : req.componentes()) {
                m.getComponentes().add(novoComponente(m, cr));
            }
        }
        return resp(modelos.save(m));
    }

    @Transactional
    public ModeloResp atualizarModelo(Long id, ModeloReq req) {
        ModeloRoseta m = buscar(id);
        aplicar(m, req, id);
        if (req.ativo() != null) {
            m.setAtivo(req.ativo());
        }
        return resp(m);
    }

    /** Troca a foto do modelo (null remove). Apaga o arquivo anterior se era um upload. */
    @Transactional
    public ModeloResp definirFoto(Long id, java.util.function.Function<Long, String> gravar) {
        ModeloRoseta m = buscar(id);
        String antiga = m.getImagemBase();
        m.setImagemBase(gravar.apply(id));
        if (antiga != null && !antiga.equals(m.getImagemBase())) {
            fotos.apagar(antiga);
        }
        return resp(m);
    }

    /** Altera só os preços. Vale para novos pedidos; pedidos já feitos mantêm o valor que foi cobrado. */
    @Transactional
    public ModeloResp atualizarPreco(Long id, PrecoReq req) {
        ModeloRoseta m = buscar(id);
        aplicarPrecos(m, req.preco(), req.precoAmericano(), req.precoNormal());
        return resp(m);
    }

    @Transactional
    public ModeloResp adicionarParte(Long modeloId, ParteReq req) {
        ModeloRoseta m = buscar(modeloId);
        m.getPartes().add(novaParte(m, req));
        modelos.flush();
        return resp(m);
    }

    @Transactional
    public ModeloResp atualizarParte(Long parteId, ParteReq req) {
        ParteModelo p = partes.findById(parteId).orElseThrow(() -> ApiException.notFound("Parte não encontrada"));
        p.setNome(req.nome());
        p.setGrupo(req.grupo() == null ? p.getGrupo() : req.grupo());
        p.setMetrosConsumidos(req.metrosConsumidos());
        return resp(p.getModelo());
    }

    @Transactional
    public ModeloResp removerParte(Long parteId) {
        ParteModelo p = partes.findById(parteId).orElseThrow(() -> ApiException.notFound("Parte não encontrada"));
        ModeloRoseta m = p.getModelo();
        m.getPartes().remove(p);
        try {
            modelos.flush();
        } catch (DataIntegrityViolationException e) {
            throw ApiException.conflict("Esta parte já foi usada em pedidos e não pode ser removida; ajuste o nome ou o consumo.");
        }
        return resp(m);
    }

    @Transactional
    public ModeloResp adicionarComponente(Long modeloId, ComponenteReq req) {
        ModeloRoseta m = buscar(modeloId);
        m.getComponentes().add(novoComponente(m, req));
        modelos.flush();
        return resp(m);
    }

    @Transactional
    public ModeloResp atualizarComponente(Long componenteId, ComponenteReq req) {
        ComponenteModelo c = componentes.findById(componenteId)
                .orElseThrow(() -> ApiException.notFound("Componente não encontrado"));
        c.setMaterial(materialExistente(req.materialId()));
        c.setQuantidade(req.quantidade());
        return resp(c.getModelo());
    }

    @Transactional
    public ModeloResp removerComponente(Long componenteId) {
        ComponenteModelo c = componentes.findById(componenteId)
                .orElseThrow(() -> ApiException.notFound("Componente não encontrado"));
        ModeloRoseta m = c.getModelo();
        m.getComponentes().remove(c);
        modelos.flush();
        return resp(m);
    }

    // ---------- helpers ----------

    private ModeloRoseta buscar(Long id) {
        return modelos.findById(id).orElseThrow(() -> ApiException.notFound("Modelo não encontrado"));
    }

    private ModeloResp resp(ModeloRoseta m) {
        return ModeloResp.from(m, !componentes.modelosSemComponentes().contains(m.getId()));
    }

    private void aplicar(ModeloRoseta m, ModeloReq r, Long idAtual) {
        String ref = r.referencia() == null || r.referencia().isBlank() ? null : r.referencia().trim();
        if (ref != null) {
            boolean existe = idAtual == null ? modelos.existsByReferenciaIgnoreCase(ref)
                    : modelos.existsByReferenciaIgnoreCaseAndIdNot(ref, idAtual);
            if (existe) {
                throw ApiException.conflict("Já existe um modelo com a referência " + ref);
            }
        }
        m.setReferencia(ref);
        m.setNome(r.nome().trim());
        m.setDescricao(r.descricao());
        m.setCategoria(r.categoria());
        m.setFormato(r.formato());
        m.setDiametroCm(r.diametroCm());
        m.setVoltas(r.voltas());
        m.setRabiolaQtd(r.rabiolaQtd());
        m.setRabiolaComprimentoCm(r.rabiolaComprimentoCm());
        m.setLarguraFitaCm(r.larguraFitaCm());
        m.setGravacoesInclusas(r.gravacoesInclusas());
        m.setImagemBase(r.imagemBase());
        aplicarPrecos(m, r.preco(), r.precoAmericano(), r.precoNormal());
    }

    /** Com franzido, o preço base é o menor dos dois ("a partir de"); sem franzido, vale o preço informado. */
    private void aplicarPrecos(ModeloRoseta m, BigDecimal preco, BigDecimal americano, BigDecimal normal) {
        if (americano == null && normal == null) {
            if (preco == null) {
                throw ApiException.badRequest("Informe o preço (ou os preços dos franzidos)");
            }
            m.setPrecoAmericano(null);
            m.setPrecoNormal(null);
            m.setPreco(preco);
        } else {
            m.setPrecoAmericano(americano);
            m.setPrecoNormal(normal);
            m.setPreco(americano == null ? normal : normal == null ? americano : americano.min(normal));
        }
    }

    private ParteModelo novaParte(ModeloRoseta m, ParteReq pr) {
        ParteModelo p = new ParteModelo();
        p.setModelo(m);
        p.setNome(pr.nome());
        p.setGrupo(pr.grupo() == null ? GrupoParte.CORPO : pr.grupo());
        p.setMetrosConsumidos(pr.metrosConsumidos());
        return p;
    }

    private ComponenteModelo novoComponente(ModeloRoseta m, ComponenteReq cr) {
        ComponenteModelo c = new ComponenteModelo();
        c.setModelo(m);
        c.setMaterial(materialExistente(cr.materialId()));
        c.setQuantidade(cr.quantidade());
        return c;
    }

    private Material materialExistente(Long id) {
        return materiais.findById(id).orElseThrow(() -> ApiException.badRequest("Material não encontrado: " + id));
    }
}
