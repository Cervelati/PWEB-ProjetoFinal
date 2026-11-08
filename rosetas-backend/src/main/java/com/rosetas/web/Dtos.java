package com.rosetas.web;

import com.rosetas.domain.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public final class Dtos {

    private Dtos() {
    }

    // ---------- Usuário (equipe) ----------
    public record CriarUsuarioReq(@NotBlank @Size(max = 128) String id,
                                  @NotBlank @Size(max = 150) String nome,
                                  @NotBlank @Email @Size(max = 255) String email,
                                  @Size(max = 30) String telefone,
                                  @NotNull Perfil perfil) {
    }

    public record PerfilReq(@NotNull Perfil perfil) {
    }

    public record AtivoReq(boolean ativo) {
    }

    public record UsuarioResp(String id, String nome, String email, String telefone, Perfil perfil, boolean ativo) {
        public static UsuarioResp from(Usuario u) {
            return new UsuarioResp(u.getId(), u.getNome(), u.getEmail(), u.getTelefone(), u.getPerfil(), u.isAtivo());
        }
    }

    // ---------- Cliente (cadastro de contato) ----------
    public record ClienteReq(@NotBlank @Size(max = 150) String nome,
                             @NotBlank @Size(max = 30) String telefone,
                             @Email @Size(max = 255) String email) {
    }

    public record ClienteResp(Long id, String nome, String telefone, String email) {
        public static ClienteResp from(Cliente c) {
            return new ClienteResp(c.getId(), c.getNome(), c.getTelefone(), c.getEmail());
        }
    }

    // ---------- Material (estoque) ----------
    public record MaterialReq(@NotBlank @Size(max = 100) String nome,
                              @NotNull TipoMaterial tipo,
                              @NotNull UnidadeMaterial unidade,
                              @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "use o formato #RRGGBB") String hex,
                              @Size(max = 500) String foto,
                              Boolean ativo) {
    }

    public record EntradaEstoqueReq(@NotNull @DecimalMin("0.001") BigDecimal quantidade) {
    }

    /** Visão pública (cores de fita disponíveis): não expõe saldo. */
    public record FitaResp(Long id, String nome, String hex, String foto) {
        public static FitaResp from(Material m) {
            return new FitaResp(m.getId(), m.getNome(), m.getHex(), m.getFoto());
        }
    }

    public record MaterialResp(Long id, String nome, TipoMaterial tipo, UnidadeMaterial unidade, String hex,
                               BigDecimal saldo, String foto, boolean ativo) {
        public static MaterialResp from(Material m) {
            return new MaterialResp(m.getId(), m.getNome(), m.getTipo(), m.getUnidade(), m.getHex(), m.getSaldo(),
                    m.getFoto(), m.isAtivo());
        }
    }

    // ---------- Modelo ----------
    public record ParteReq(@NotBlank @Size(max = 100) String nome,
                           GrupoParte grupo,
                           @NotNull @DecimalMin("0.001") BigDecimal metrosConsumidos) {
    }

    public record ComponenteReq(@NotNull Long materialId, @NotNull @DecimalMin("0.001") BigDecimal quantidade) {
    }

    /** Criação e edição de modelo. Em edição, partes e componentes são ignorados (têm endpoints próprios). */
    public record ModeloReq(@Size(max = 40) String referencia,
                            @NotBlank @Size(max = 150) String nome,
                            @Size(max = 1000) String descricao,
                            @Size(max = 40) String categoria,
                            @Size(max = 20) String formato,
                            @DecimalMin("0.0") @Digits(integer = 4, fraction = 1) BigDecimal diametroCm,
                            @Min(0) @Max(20) Integer voltas,
                            @Min(0) @Max(20) int rabiolaQtd,
                            @DecimalMin("0.0") @Digits(integer = 5, fraction = 1) BigDecimal rabiolaComprimentoCm,
                            @DecimalMin("0.0") @Digits(integer = 3, fraction = 1) BigDecimal larguraFitaCm,
                            @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal preco,
                            @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal precoAmericano,
                            @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal precoNormal,
                            @Min(0) @Max(20) int gravacoesInclusas,
                            @Size(max = 500) String imagemBase,
                            Boolean ativo,
                            @Valid List<ParteReq> partes,
                            @Valid List<ComponenteReq> componentes) {
    }

    /**
     * Altera só os preços. Com franzido: informe americano e/ou normal (nulo = não oferecido).
     * Sem franzido: informe apenas "preco".
     */
    public record PrecoReq(@DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal preco,
                           @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal precoAmericano,
                           @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal precoNormal) {
    }

    public record PrecoGravacaoReq(@NotNull @DecimalMin("0.00") @Digits(integer = 6, fraction = 2) BigDecimal preco) {
    }

    public record OpcoesResp(BigDecimal precoGravacaoExtra) {
    }

    public record ParteResp(Long id, String nome, GrupoParte grupo, BigDecimal metrosConsumidos) {
    }

    public record ComponenteResp(Long id, Long materialId, String materialNome, TipoMaterial tipo,
                                 UnidadeMaterial unidade, BigDecimal quantidade) {
    }

    public record ModeloResp(Long id, String referencia, String nome, String descricao, String categoria,
                             String formato, BigDecimal diametroCm, Integer voltas, int rabiolaQtd,
                             BigDecimal rabiolaComprimentoCm, BigDecimal larguraFitaCm, BigDecimal preco,
                             BigDecimal precoAmericano, BigDecimal precoNormal, int gravacoesInclusas,
                             String imagemBase, boolean ativo, boolean disponivel,
                             List<ParteResp> partes, List<ComponenteResp> componentes) {
        public static ModeloResp from(ModeloRoseta m, boolean disponivel) {
            return new ModeloResp(m.getId(), m.getReferencia(), m.getNome(), m.getDescricao(), m.getCategoria(),
                    m.getFormato(), m.getDiametroCm(), m.getVoltas(), m.getRabiolaQtd(),
                    m.getRabiolaComprimentoCm(), m.getLarguraFitaCm(), m.getPreco(), m.getPrecoAmericano(),
                    m.getPrecoNormal(), m.getGravacoesInclusas(), m.getImagemBase(), m.isAtivo(), disponivel,
                    m.getPartes().stream()
                            .map(p -> new ParteResp(p.getId(), p.getNome(), p.getGrupo(), p.getMetrosConsumidos()))
                            .toList(),
                    m.getComponentes().stream()
                            .map(c -> new ComponenteResp(c.getId(), c.getMaterial().getId(), c.getMaterial().getNome(),
                                    c.getMaterial().getTipo(), c.getMaterial().getUnidade(), c.getQuantidade()))
                            .toList());
        }
    }

    // ---------- Pedido ----------
    public record PersonalizacaoReq(@NotNull Long parteId, @NotNull Long fitaId) {
    }

    public record ItemReq(@NotNull Long modeloId,
                          @Min(1) @Max(1000) int quantidade,
                          Franzido franzido,
                          @Min(0) @Max(20) int gravacoesExtras,
                          @Size(max = 255) String textoPersonalizado,
                          @NotNull @Valid List<PersonalizacaoReq> partes) {
    }

    public record CriarPedidoReq(@NotNull @Valid ClienteReq cliente,
                                 @Size(max = 1000) String observacoes,
                                 @NotEmpty @Valid List<ItemReq> itens) {
    }

    public record RecusarReq(@NotBlank @Size(max = 300) String motivo) {
    }

    public record PersonalizacaoResp(Long parteId, String parteNome, GrupoParte parteGrupo, Long fitaId,
                                     String fitaNome, String fitaHex) {
    }

    /** Sem valores financeiros: também compõe a ficha de produção. */
    public record ItemResp(Long id, Long modeloId, String modeloNome, String referencia, int quantidade,
                           Franzido franzido, int gravacoesExtras, String textoPersonalizado,
                           List<PersonalizacaoResp> partes) {
    }

    public record PagamentoResp(Long id, BigDecimal valor, MetodoPagamento metodo, StatusPagamento status,
                                String pixCopiaCola) {
    }

    /** Visão da equipe (ERP). */
    public record PedidoResp(Long id, ClienteResp cliente, StatusPedido status, BigDecimal valorTotal,
                             OffsetDateTime data, String observacoes, String codigoAcompanhamento,
                             List<ItemResp> itens, PagamentoResp pagamento) {
    }

    /** Visão pública, acessada pelo código do link: sem dados de contato do cliente. */
    public record PedidoPublicoResp(String codigo, StatusPedido status, BigDecimal valorTotal, OffsetDateTime data,
                                    String clienteNome, String observacoes, List<ItemResp> itens,
                                    PagamentoResp pagamento) {
    }

    /** Ficha de produção: sem qualquer valor financeiro (RF05). */
    public record FichaResp(Long pedidoId, StatusPedido status, OffsetDateTime data, String observacoes,
                            List<ItemResp> itens) {
    }

    public static ItemResp item(ItemPedido i) {
        return new ItemResp(i.getId(), i.getModelo().getId(), i.getModelo().getNome(), i.getModelo().getReferencia(),
                i.getQuantidade(), i.getFranzido(), i.getGravacoesExtras(), i.getTextoPersonalizado(),
                i.getPersonalizacoes().stream().map(pp -> new PersonalizacaoResp(
                        pp.getParte().getId(), pp.getParte().getNome(), pp.getParte().getGrupo(),
                        pp.getFita().getId(), pp.getFita().getNome(), pp.getFita().getHex())).toList());
    }

    private static PagamentoResp pagamento(Pagamento pg) {
        return pg == null ? null
                : new PagamentoResp(pg.getId(), pg.getValor(), pg.getMetodo(), pg.getStatus(), pg.getPixCopiaCola());
    }

    public static PedidoResp pedido(Pedido p, Pagamento pg) {
        return new PedidoResp(p.getId(), ClienteResp.from(p.getCliente()), p.getStatus(), p.getValorTotal(),
                p.getData(), p.getObservacoes(), p.getCodigoAcompanhamento(),
                p.getItens().stream().map(Dtos::item).toList(), pagamento(pg));
    }

    public static PedidoPublicoResp publico(Pedido p, Pagamento pg) {
        return new PedidoPublicoResp(p.getCodigoAcompanhamento(), p.getStatus(), p.getValorTotal(), p.getData(),
                p.getCliente().getNome(), p.getObservacoes(),
                p.getItens().stream().map(Dtos::item).toList(), pagamento(pg));
    }

    public static FichaResp ficha(Pedido p) {
        return new FichaResp(p.getId(), p.getStatus(), p.getData(), p.getObservacoes(),
                p.getItens().stream().map(Dtos::item).toList());
    }

    // ---------- Pagamento / webhook ----------
    public record WebhookPagamentoReq(@NotBlank String gatewayTransacaoId, @NotNull StatusPagamento status) {
    }

    // ---------- Configuração do Pix ----------
    public record PixConfigReq(@NotBlank @Size(max = 77) String chave,
                               @NotBlank @Size(max = 25) String nome,
                               @NotBlank @Size(max = 15) String cidade) {
    }

    public record PixConfigResp(String chave, String nome, String cidade, boolean configurado) {
    }
}
