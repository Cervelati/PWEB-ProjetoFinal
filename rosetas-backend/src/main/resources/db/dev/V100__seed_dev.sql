-- Dados de DESENVOLVIMENTO (só carregados com o perfil "dev"). Valores de exemplo.

insert into usuarios (id, nome, email, telefone, perfil, ativo) values
    ('dev-root',    'Root (dev)',        'root@dev.local',    null, 'ROOT',        true),
    ('dev-admin',   'Administrador (dev)', 'admin@dev.local', null, 'ADMIN',       true),
    ('dev-func',    'Funcionária (dev)', 'func@dev.local',    null, 'FUNCIONARIA', true),
    ('dev-cliente', 'Cliente (dev)',     'cliente@dev.local', null, 'CLIENTE',     true)
on conflict (id) do nothing;

insert into fitas (nome, hex, metros_estoque) values
    ('Vermelho',   '#C8102E', 200),
    ('Azul royal', '#1F3FBF', 200),
    ('Branco',     '#FFFFFF', 200),
    ('Amarelo',    '#F2C200', 150),
    ('Verde',      '#1E8E3E', 150),
    ('Rosa',       '#F06AA8', 100),
    ('Preto',      '#111111', 100),
    ('Dourado',    '#C9A227',  50);

with m as (
    insert into modelos_roseta (nome, descricao, preco)
    values ('Roseta Tripla 12.30.3 T', 'Três voltas, fita de 5 cm, 30 cm de comprimento.', 15.50)
    returning id)
insert into partes_modelo (modelo_id, nome, metros_consumidos)
select id, 'Fita externa', 0.500 from m
union all select id, 'Fita interna', 0.400 from m
union all select id, 'Pontas', 0.300 from m;

with m as (
    insert into modelos_roseta (nome, descricao, preco)
    values ('Roseta 5 voltas 12.50.5 T', 'Cinco voltas, fita de 5 cm, 50 cm de comprimento.', 19.50)
    returning id)
insert into partes_modelo (modelo_id, nome, metros_consumidos)
select id, 'Fita externa', 0.800 from m
union all select id, 'Fita interna', 0.600 from m
union all select id, 'Pontas', 0.400 from m;

with m as (
    insert into modelos_roseta (nome, descricao, preco)
    values ('Roseta Grande 12.50.5 L1a', 'Modelo grande de 18 cm de diâmetro.', 37.30)
    returning id)
insert into partes_modelo (modelo_id, nome, metros_consumidos)
select id, 'Fita externa', 1.200 from m
union all select id, 'Fita interna', 0.900 from m
union all select id, 'Pontas', 0.500 from m;
