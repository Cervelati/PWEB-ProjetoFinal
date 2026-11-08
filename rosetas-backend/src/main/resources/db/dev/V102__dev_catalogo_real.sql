-- Os 3 modelos de exemplo do V100 foram substituídos pelo catálogo real (V5).
update modelos_roseta set ativo = false
where referencia is null
  and nome in ('Roseta Tripla 12.30.3 T', 'Roseta 5 voltas 12.50.5 T', 'Roseta Grande 12.50.5 L1a');

-- Estoque de exemplo dos materiais em unidades (só desenvolvimento)
update fitas set metros_estoque = 500 where tipo in ('BOTTON', 'ARGOLA', 'ALFINETE');
