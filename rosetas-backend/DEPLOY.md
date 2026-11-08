# Colocando no ar (produção)

Requisitos: um servidor Linux com Docker (VPS de 2 GB de RAM já atende), um domínio e uma conta Google (Firebase, gratuito).
Use dois endereços, por exemplo `escarapela.com.br` (site dos clientes) e `gestao.escarapela.com.br` (ERP da equipe).

## 1. DNS
No provedor do domínio, crie dois registros **A** apontando para o IP do servidor: o do site e o do ERP.

## 2. Firebase (login da equipe)
1. https://console.firebase.google.com → **Adicionar projeto**.
2. **Authentication → Começar → E-mail/senha → ativar**.
3. **Authentication → Usuários → Adicionar usuário**: crie o e-mail e a senha de quem vai usar o ERP. Anote o **UID** de cada um.
4. **Configurações do projeto → Geral → Seus apps → Web (`</>`)**: registre o app e copie `apiKey`, `authDomain`, `projectId`, `appId` para o `.env`.
5. **Configurações do projeto → Contas de serviço → Gerar nova chave privada**: salve o arquivo como `rosetas-backend/secrets/firebase.json`.
6. **Authentication → Configurações → Domínios autorizados**: adicione o domínio do ERP.

## 3. Servidor
```bash
# instalar Docker (Ubuntu): https://docs.docker.com/engine/install/ubuntu/
# libere as portas 80 e 443 no firewall

mkdir -p /opt/rosetas && cd /opt/rosetas
# copie as pastas rosetas-backend, rosetas-site e rosetas-erp para cá (scp, git ou zip)

cd rosetas-backend
cp deploy/env.example .env
nano .env                 # domínios, senhas e dados do Firebase
mkdir -p secrets          # e coloque o firebase.json aqui
docker compose -f docker-compose.prod.yml up -d --build
```
O primeiro build demora alguns minutos. O HTTPS é emitido sozinho pelo Caddy (precisa do DNS já apontado).

## 4. Criar o primeiro usuário (Root)
O primeiro usuário não pode ser criado pela tela (ninguém tem permissão ainda). Insira direto no banco, com o UID do Firebase:
```bash
docker compose -f docker-compose.prod.yml exec postgres psql -U rosetas -d rosetas -c \
"insert into usuarios (id, nome, email, perfil, ativo) values ('UID_DO_FIREBASE', 'Seu Nome', 'seu@email.com', 'ROOT', true);"
```
Entre no ERP com esse e-mail e senha. Os outros usuários (admin, funcionária) o Root cadastra em **Equipe**, usando o UID de cada um no Firebase.

## 5. Configuração inicial no ERP (antes de divulgar o site)
1. **Configurações**: chave Pix, nome e cidade do recebedor (sem isso o QR code não funciona) e preço da gravação extra.
2. **Estoque**: dê entrada nas fitas (metros) e em botton, argola e alfinete (unidades). Em produção eles começam zerados e os modelos aparecem como indisponíveis até haver estoque.
3. **Modelos e preços**: confira preços e a ficha técnica (o consumo de fita do corpo é estimado).
4. Faça um pedido de teste no site, pague um valor pequeno e use "Confirmar pagamento".

## 6. Backup (importante)
```bash
./deploy/backup.sh        # gera backups/banco-AAAA-MM-DD.sql.gz e fotos-AAAA-MM-DD.tar.gz
```
Agende no `crontab -e`: `30 3 * * * cd /opt/rosetas/rosetas-backend && ./deploy/backup.sh`.
Copie a pasta `backups` para fora do servidor de vez em quando (Drive, outro PC).

## Atualizar depois
Substitua as pastas pelas novas (mantendo `.env` e `secrets/`) e rode de novo `docker compose -f docker-compose.prod.yml up -d --build`. Os dados ficam nos volumes.

## Lembretes
- Nunca use `docker compose down -v` em produção: apaga o banco e as fotos.
- O site não tem captcha nem limite de requisições por IP; se aparecerem pedidos falsos, ative a proteção do Cloudflare na frente do domínio.
- Pix estático: confira cada pagamento no app do banco antes de "Confirmar pagamento".
