# Escarapela — Backend

Spring Boot 3.3 · Java 21 · PostgreSQL 16 · Flyway · Spring Security

Arquitetura: **um backend** atendendo **dois fronts**:

| Projeto | Quem usa | Porta (Docker) | Porta (dev) |
|---|---|---|---|
| `rosetas-site` | clientes, **sem conta** | 3000 | 5173 |
| `rosetas-erp` | equipe (funcionária, admin, root) | 3001 | 5174 |
| `rosetas-backend` | API | 8080 | 8080 |

## Rodando tudo com Docker

As três pastas ficam lado a lado (`rosetas-backend`, `rosetas-site`, `rosetas-erp`).

```bash
cd rosetas-backend
docker compose up --build
```

- Site: http://localhost:3000 · ERP: http://localhost:3001 · API: http://localhost:8080
- Se já havia um banco de uma versão anterior e a migração falhar: `docker compose down -v` (apaga o volume) e suba de novo.
- No ERP (modo dev) escolha o perfil: Administrador ou Root podem alterar preços em **Modelos e preços**.

## Rodando sem Docker (desenvolvimento)

```bash
docker compose up -d postgres
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run     # ou rode pelo IntelliJ com o profile dev
# em outros terminais:
cd ../rosetas-site && npm install && npm run dev   # :5173
cd ../rosetas-erp  && npm install && npm run dev   # :5174
```

Variáveis: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `CORS_ORIGINS`, `WEBHOOK_SECRET`,
`PAGAMENTO_EXPIRACAO_MIN` (padrão 30), `PAGAMENTO_RECONCILIACAO_MS` (padrão 60000).
Em produção: `GOOGLE_APPLICATION_CREDENTIALS` (Firebase, só para a equipe) e `WEBHOOK_SECRET` forte; sem `VITE_DEV_TOOLS`.

### Criar a conta ROOT em produção (uma vez)

Crie o usuário da equipe no Firebase e insira a linha em `usuarios` com `perfil = 'ROOT'` (ROOT não é atribuível pela API).
Depois a ROOT cadastra o restante da equipe em **Equipe** no ERP.

## Clientes sem conta

O cliente informa nome + telefone (e e-mail opcional) ao finalizar o pedido. O cliente é identificado pelo telefone
(só dígitos). O pedido recebe um **código de acompanhamento imprevisível** (24 bytes aleatórios); o link
`/pedido/{codigo}` é a "chave" de acesso. Limite de 3 pedidos aguardando pagamento por cliente (HTTP 429).

## Perfis e acesso

| Quem | Acesso |
|---|---|
| público | `/api/catalogo/**`, `/api/publico/**`, webhook de pagamento |
| FUNCIONARIA | `/api/producao/**` — fichas sem valores |
| ADMIN | `/api/admin/**` (pedidos, clientes, fitas/estoque, modelos e preços) |
| ROOT | ADMIN + `/api/admin/usuarios/**` |

## Endpoints

```
GET   /api/catalogo/modelos | /{id}         modelos ativos + partes + preços; `disponivel=false` se faltar botton etc.
GET   /api/catalogo/fitas                   só fitas (cores) ativas com saldo
GET   /api/catalogo/opcoes                  preço da gravação extra

POST  /api/publico/pedidos                  cria pedido (cliente + itens), reserva estoque, gera Pix
GET   /api/publico/pedidos/{codigo}
POST  /api/publico/pedidos/{codigo}/cancelar   só antes do pagamento

POST  /api/pagamentos/webhook               header X-Webhook-Secret; {gatewayTransacaoId, status}

GET   /api/usuarios/me                      equipe

GET   /api/producao/pedidos | /{id}         EM_PRODUCAO, sem valores

GET   /api/admin/pedidos?status=
POST  /api/admin/pedidos/{id}/confirmar-pagamento | aprovar | recusar | entregar
GET/PUT /api/admin/configuracoes/pix        chave Pix, nome e cidade do recebedor
GET   /api/admin/clientes?busca= ; PUT /api/admin/clientes/{id} ; GET /api/admin/clientes/{id}/pedidos
GET/POST/PUT /api/admin/materiais ; POST /api/admin/materiais/{id}/entrada   {quantidade}
GET/POST/PUT /api/admin/modelos ; PUT /api/admin/modelos/{id}/preco   {preco, precoAmericano, precoNormal}
POST  /api/admin/modelos/{id}/partes ; PUT|DELETE /api/admin/modelos/partes/{parteId}
POST  /api/admin/modelos/{id}/componentes ; PUT|DELETE /api/admin/modelos/componentes/{id}
GET/PUT /api/admin/configuracoes/gravacao   preço da gravação extra
POST  /api/admin/modelos/{id}/foto          multipart `arquivo` (JPG/PNG/WEBP, até 5 MB) ; DELETE remove
GET   /api/arquivos/modelos/{arquivo}       fotos (públicas)
GET/POST /api/admin/usuarios ; PATCH /api/admin/usuarios/{id}/perfil | ativo      (ROOT)
```

## Pix

Pix **estático** com a chave da empresa, cadastrada no ERP em **Configurações**. O código "copia e cola" (BR Code) leva o valor do pedido
e o site mostra também o QR code. Não há confirmação automática: a gestão confere o recebimento no banco e usa **Confirmar pagamento** no pedido.
Sem chave configurada, o sistema gera um código de teste (`PIX-STUB-…`). O prazo para pagar é `PAGAMENTO_EXPIRACAO_MIN` (padrão 2880 min = 2 dias);
pedidos expirados liberam o estoque e não podem mais ser confirmados.

O preço é lido do modelo no momento do pedido: alterar o preço não afeta pedidos já feitos.

## Fluxo do pedido

```
AGUARDANDO_PAGAMENTO --webhook APROVADO--> AGUARDANDO_APROVACAO --aprovar--> EM_PRODUCAO --entregar--> ENTREGUE
        |                                          |
        +-- cancelar / expirar --> CANCELADO       +-- recusar --> CANCELADO
```

## Catálogo e estoque

**Catálogo 2026** (migration `V5`): 103 modelos da lista de preços, com referência, categoria, diâmetro, voltas, rabiola,
preço base e preço por franzido (americano/normal; vazio = não oferecido) e gravações incluídas. Tudo é editável no ERP.

**Personalização por modelo**: cada modelo tem *partes* (fitas cuja cor o cliente escolhe: voltas do corpo e fitas da rabiola,
com metros consumidos) e *componentes* (materiais fixos em unidades: botton, argola, alfinete). O consumo de fita do corpo
gerado na importação é uma **estimativa** — ajuste na ficha técnica de cada modelo.

**Fotos**: 38 modelos já vêm com a foto do catálogo (migration `V6`, arquivos em `src/main/resources/fotos`). Os demais recebem foto
pelo ERP (Modelos > Editar > Foto). Uploads ficam em `UPLOADS_DIR` (volume `fotos` no compose); faça backup desse volume.

**Estoque** (`fitas`/`Material`): fitas em metros, botton/argola/alfinete em unidades. Saldo = disponível.
`ENTRADA` soma · `RESERVA` subtrai ao criar o pedido · `ESTORNO` devolve ao cancelar/recusar/expirar · `BAIXA` registra o consumo
definitivo ao aprovar. Modelos cujo material fixo está zerado aparecem como indisponíveis.

**Concorrência**: a reserva é um `UPDATE ... SET saldo = saldo - :qtd WHERE saldo >= :qtd` atômico (sem ler-e-gravar),
em ordem crescente de id, tudo-ou-nada na transação (o perdedor recebe `409`). Transições de pedido e confirmação de pagamento
travam o pedido (`FOR UPDATE`) antes do pagamento e dos materiais, e baixa/estorno são idempotentes — webhook e confirmação
manual simultâneos não duplicam nem perdem estoque.

## Pagamento

`PagamentoGateway` é a interface; `StubPagamentoGateway` é a implementação de desenvolvimento.
Para integrar o gateway real, crie outra implementação (e remova o `@Component` do stub).
Para simular a confirmação em dev:

```bash
curl -X POST localhost:8080/api/pagamentos/webhook \
  -H 'Content-Type: application/json' -H "X-Webhook-Secret: troque-este-segredo" \
  -d '{"gatewayTransacaoId":"stub-...","status":"APROVADO"}'
```

`PagamentoReconciliador` (a cada 60 s) consulta o gateway dos pagamentos pendentes (RNF14) e cancela
pedidos não pagos após o tempo de expiração, devolvendo o estoque.

## Pendências conhecidas

- Envio real das `notificacoes` (hoje só são gravadas como `PENDENTE` a cada mudança de status).
- Auditoria de alterações de permissão (RF07) — não está no modelo de entidades, então não foi criada.
- Reembolso quando um pedido já pago é recusado (o sistema apenas registra um aviso no log).
- Se rodar mais de uma instância, proteger o `PagamentoReconciliador` (ex.: ShedLock).
