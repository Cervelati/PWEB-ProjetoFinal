-- Configurações editáveis pelo ERP (ex.: chave Pix da empresa)
create table configuracoes (
    chave varchar(50) primary key,
    valor varchar(500) not null
);
