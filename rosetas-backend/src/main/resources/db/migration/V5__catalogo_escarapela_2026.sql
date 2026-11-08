-- Catálogo Escarapela 2026 (lista de preços). Importado uma única vez; depois tudo é editável pelo ERP.
-- Preços: "americano" e "normal" são os dois tipos de franzido da lista; NULL = não oferecido.
insert into modelos_roseta (referencia, nome, descricao, categoria, formato, diametro_cm, voltas, rabiola_qtd,
                            rabiola_comprimento_cm, largura_fita_cm, preco_americano, preco_normal, preco, gravacoes_inclusas, ativo) values
('5.7.2M', 'Roseta 5.7.2M', 'Diâmetro 8 cm · 1 volta · rabiola com 2 fitas de 7 cm × 2.5 cm', '1 volta', null, 8.0, 1, 2, 7.0, 2.5, null, 6.9, 6.9, 2, true),
('9.12.2Smini', 'Roseta 9.12.2Smini', 'Diâmetro 8 cm · 1 volta · rabiola com 2 fitas de 12 cm × 4 cm', '1 volta', null, 8.0, 1, 2, 12.0, 4.0, null, 7.5, 7.5, 2, true),
('9.20.2S', 'Roseta 9.20.2S', 'Diâmetro 10 cm · 1 volta · rabiola com 2 fitas de 20 cm × 4 cm', '1 volta', null, 10.0, 1, 2, 20.0, 4.0, 9.5, 8.0, 8.0, 2, true),
('9.20.3S', 'Roseta 9.20.3S', 'Diâmetro 10 cm · 1 volta · rabiola com 3 fitas de 20 cm × 4 cm', '1 volta', null, 10.0, 1, 3, 20.0, 4.0, 10.0, 8.5, 8.5, 2, true),
('9.30.3S', 'Roseta 9.30.3S', 'Diâmetro 10 cm · 1 volta · rabiola com 3 fitas de 30 cm × 4 cm', '1 volta', null, 10.0, 1, 3, 30.0, 4.0, 10.5, 9.1, 9.1, 2, true),
('9.30.3 SE', 'Roseta 9.30.3 SE', 'Diâmetro 10 cm · 1 volta · rabiola com 3 fitas de 30 cm × 4 cm', '1 volta', null, 10.0, 1, 3, 30.0, 4.0, 11.0, 9.5, 9.5, 2, true),
('12.20.3S', 'Roseta 12.20.3S', 'Diâmetro 10 cm · 1 volta · rabiola com 3 fitas de 20 cm × 4 cm', '1 volta', null, 10.0, 1, 3, 20.0, 4.0, 10.5, 9.2, 9.2, 2, true),
('9.30.3 D', 'Roseta 9.30.3 D', 'Diâmetro 11 cm · 2 voltas · rabiola com 3 fitas de 30 cm × 4 cm', '2 voltas', null, 11.0, 2, 3, 30.0, 4.0, 11.1, 9.7, 9.7, 2, true),
('12.30.3 D', 'Roseta 12.30.3 D', 'Diâmetro 11 cm · 2 voltas · rabiola com 3 fitas de 30 cm × 5 cm', '2 voltas', null, 11.0, 2, 3, 30.0, 5.0, 12.1, 10.5, 10.5, 2, true),
('12.30.3 DW', 'Roseta 12.30.3 DW', 'Diâmetro 11 cm · 2 voltas · rabiola com 3 fitas de 30 cm × 5 cm', '2 voltas', null, 11.0, 2, 3, 30.0, 5.0, 12.6, 11.5, 11.5, 2, true),
('12.40.3 D', 'Roseta 12.40.3 D', 'Diâmetro 11 cm · 2 voltas · rabiola com 3 fitas de 40 cm × 5 cm', '2 voltas', null, 11.0, 2, 3, 40.0, 5.0, 12.6, 11.5, 11.5, 2, true),
('12.30.3 Dea', 'Roseta 12.30.3 Dea', 'Diâmetro 11 cm · 3 voltas · rabiola com 3 fitas de 30 cm × 5 cm', '2 voltas', null, 11.0, 3, 3, 30.0, 5.0, 13.3, null, 13.3, 2, true),
('12.40.3 Dea', 'Roseta 12.40.3 Dea', 'Diâmetro 11 cm · 3 voltas · rabiola com 3 fitas de 40 cm × 5 cm', '2 voltas', null, 11.0, 3, 3, 40.0, 5.0, 14.3, null, 14.3, 2, true),
('12.30.3 DXA', 'Roseta 12.30.3 DXA', 'Diâmetro 13 cm · 3 voltas · rabiola com 3 fitas de 30 cm × 5 cm', '2 voltas', null, 13.0, 3, 3, 30.0, 5.0, 18.0, null, 18.0, 2, true),
('12.40.5 Dxa', 'Roseta 12.40.5 Dxa', 'Diâmetro 13 cm · 3 voltas · rabiola com 3 fitas de 40 cm × 5 cm', '2 voltas', null, 13.0, 3, 3, 40.0, 5.0, 20.0, null, 20.0, 2, true),
('9.20.2 Dm - mini', 'Roseta 9.20.2 Dm - mini', 'Diâmetro 9 cm · 2 voltas · rabiola com 2 fitas de 20 cm × 4 cm', '2 voltas', null, 9.0, 2, 2, 20.0, 4.0, 10.2, 8.6, 8.6, 2, true),
('9.30.3 Dm - mini', 'Roseta 9.30.3 Dm - mini', 'Diâmetro 9 cm · 2 voltas · rabiola com 3 fitas de 30 cm × 4 cm', '2 voltas', null, 9.0, 2, 3, 30.0, 4.0, 11.1, 9.5, 9.5, 2, true),
('12/09.13.2Tm - mini', 'Roseta 12/09.13.2Tm - mini', '3 voltas · rabiola com 2 fitas de 13 cm', '2 voltas', null, null, 3, 2, 13.0, null, null, 10.5, 10.5, 2, true),
('12.13.3Tm - mini', 'Roseta 12.13.3Tm - mini', '3 voltas · rabiola com 3 fitas de 13 cm', '2 voltas', null, null, 3, 3, 13.0, null, null, 11.5, 11.5, 2, true),
('9.30.3 T', 'Roseta 9.30.3 T', 'Diâmetro 12 cm · 3 voltas · rabiola com 3 fitas de 30 cm × 4 cm', '3 voltas', null, 12.0, 3, 3, 30.0, 4.0, 14.1, 12.5, 12.5, 2, true),
('12.30.3 T', 'Roseta 12.30.3 T', 'Diâmetro 12 cm · 3 voltas · rabiola com 3 fitas de 30 cm × 5 cm', '3 voltas', null, 12.0, 3, 3, 30.0, 5.0, 15.5, 14.1, 14.1, 2, true),
('12.40.3 T', 'Roseta 12.40.3 T', 'Diâmetro 13 cm · 3 voltas · rabiola com 3 fitas de 40 cm × 5 cm', '3 voltas', null, 13.0, 3, 3, 40.0, 5.0, 17.3, 15.8, 15.8, 2, true),
('12.50.3 T', 'Roseta 12.50.3 T', 'Diâmetro 13 cm · 3 voltas · rabiola com 3 fitas de 50 cm × 5 cm', '3 voltas', null, 13.0, 3, 3, 50.0, 5.0, 17.7, 16.5, 16.5, 2, true),
('12.40.5 T', 'Roseta 12.40.5 T', 'Diâmetro 13 cm · 3 voltas · rabiola com 5 fitas de 40 cm × 5 cm', '3 voltas', null, 13.0, 3, 5, 40.0, 5.0, 18.5, 17.0, 17.0, 2, true),
('12.50.5 T', 'Roseta 12.50.5 T', 'Diâmetro 13 cm · 3 voltas · rabiola com 5 fitas de 50 cm × 5 cm', '3 voltas', null, 13.0, 3, 5, 50.0, 5.0, 19.5, 18.0, 18.0, 2, true),
('9.20.2 Tm - mini', 'Roseta 9.20.2 Tm - mini', 'Diâmetro 10 cm · 3 voltas · rabiola com 2 fitas de 20 cm × 4 cm', '3 voltas', null, 10.0, 3, 2, 20.0, 4.0, 11.0, 10.0, 10.0, 2, true),
('9.30.3 Tm - mini', 'Roseta 9.30.3 Tm - mini', 'Diâmetro 10 cm · 3 voltas · rabiola com 3 fitas de 30 cm × 4 cm', '3 voltas', null, 10.0, 3, 3, 30.0, 4.0, 12.0, 10.5, 10.5, 2, true),
('12.40.3 Q', 'Roseta 12.40.3 Q', 'Diâmetro 13 cm · 4 voltas · rabiola com 3 fitas de 40 cm × 5 cm', '4 voltas', null, 13.0, 4, 3, 40.0, 5.0, 17.7, 16.5, 16.5, 2, true),
('12.50.3 Q', 'Roseta 12.50.3 Q', 'Diâmetro 13 cm · 4 voltas · rabiola com 3 fitas de 50 cm × 5 cm', '4 voltas', null, 13.0, 4, 3, 50.0, 5.0, 19.5, 18.0, 18.0, 2, true),
('12.40.5 Q', 'Roseta 12.40.5 Q', 'Diâmetro 13 cm · 4 voltas · rabiola com 5 fitas de 40 cm × 5 cm', '4 voltas', null, 13.0, 4, 5, 40.0, 5.0, 19.5, 18.0, 18.0, 2, true),
('12.50.5 Q', 'Roseta 12.50.5 Q', '4 voltas · rabiola com 5 fitas de 50 cm × 5 cm', '4 voltas', null, null, 4, 5, 50.0, 5.0, 20.9, 20.2, 20.2, 2, true),
('12.30.3 BWa', 'Roseta 12.30.3 BWa', 'Diâmetro 17 cm · 4 voltas · rabiola com 3 fitas de 30 cm × 5 cm', '4 voltas', null, 17.0, 4, 3, 30.0, 5.0, 32.0, null, 32.0, 2, true),
('9.20.3 EPSa', 'Roseta 9.20.3 EPSa', 'modelo esp.D · rabiola com 3 fitas de 20 cm × 5 cm', 'Especiais', 'esp.D', null, null, 3, 20.0, 5.0, 14.5, null, 14.5, 2, true),
('12.30.3 EPSa', 'Roseta 12.30.3 EPSa', 'Diâmetro 17 cm · modelo esp.D · rabiola com 3 fitas de 30 cm × 5 cm', 'Especiais', 'esp.D', 17.0, null, 3, 30.0, 5.0, 17.7, null, 17.7, 2, true),
('12.40.3 EPSa', 'Roseta 12.40.3 EPSa', 'Diâmetro 17 cm · modelo esp.D · rabiola com 3 fitas de 40 cm × 5 cm', 'Especiais', 'esp.D', 17.0, null, 3, 40.0, 5.0, 18.7, null, 18.7, 2, true),
('12.30.5MEP', 'Roseta 12.30.5MEP', 'Diâmetro 14 cm · modelo esp.E · rabiola com 5 fitas de 30 cm × 5 cm', 'Especiais', 'esp.E', 14.0, null, 5, 30.0, 5.0, null, 17.5, 17.5, 2, true),
('9.20.3 X', 'Roseta 9.20.3 X', 'Diâmetro 11 cm · formato estrela · rabiola com 3 fitas de 20 cm × 4 cm', 'Especiais', 'estrela', 11.0, null, 3, 20.0, 4.0, 14.5, 12.7, 12.7, 2, true),
('9.30.3 X', 'Roseta 9.30.3 X', 'Diâmetro 11 cm · formato estrela · rabiola com 3 fitas de 30 cm × 4 cm', 'Especiais', 'estrela', 11.0, null, 3, 30.0, 4.0, 15.0, 14.0, 14.0, 2, true),
('12.30.3 X', 'Roseta 12.30.3 X', 'Diâmetro 11 cm · formato estrela · rabiola com 3 fitas de 30 cm × 5 cm', 'Especiais', 'estrela', 11.0, null, 3, 30.0, 5.0, 16.5, 15.5, 15.5, 2, true),
('9.30.1 XM', 'Roseta 9.30.1 XM', 'Diâmetro 12 cm · formato estrela · rabiola com 1 fita de 30 cm × 4 cm', 'Especiais', 'estrela', 12.0, null, 1, 30.0, 4.0, null, 15.8, 15.8, 2, true),
('9.30.3 XM', 'Roseta 9.30.3 XM', 'Diâmetro 12 cm · formato estrela · rabiola com 3 fitas de 30 cm × 4 cm', 'Especiais', 'estrela', 12.0, null, 3, 30.0, 4.0, null, 17.0, 17.0, 2, true),
('12.20.3MGXG', 'Roseta 12.20.3MGXG', 'rabiola com 3 fitas de 20 cm × 5 cm', 'Especiais', null, null, null, 3, 20.0, 5.0, null, 20.0, 20.0, 2, true),
('12.30.5MGXG', 'Roseta 12.30.5MGXG', 'rabiola com 5 fitas de 30 cm × 5 cm', 'Especiais', null, null, null, 5, 30.0, 5.0, null, 23.0, 23.0, 2, true),
('12.30.5 XB', 'Roseta 12.30.5 XB', 'Diâmetro 16 cm · formato estrela · rabiola com 5 fitas de 30 cm × 5 cm', 'Especiais', 'estrela', 16.0, null, 5, 30.0, 5.0, null, 23.0, 23.0, 2, true),
('9.20.3Xa1', 'Roseta 9.20.3Xa1', 'modelo esp.E · rabiola com 3 fitas de 20 cm × 4 cm', 'Especiais', 'esp.E', null, null, 3, 20.0, 4.0, 18.0, null, 18.0, 2, true),
('22.30.3 Xa1', 'Roseta 22.30.3 Xa1', 'Diâmetro 12 cm · formato estrela · rabiola com 3 fitas de 30 cm × 5 cm', 'Especiais', 'estrela', 12.0, null, 3, 30.0, 5.0, 21.7, null, 21.7, 2, true),
('9.40.4 XD', 'Roseta 9.40.4 XD', 'Diâmetro 17 cm · rabiola com 4 fitas de 40 cm × 4 cm', 'Especiais', null, 17.0, null, 4, 40.0, 4.0, 25.5, null, 25.5, 2, true),
('12.40.5 XMF', 'Roseta 12.40.5 XMF', 'formato estrela · rabiola com 5 fitas de 40 cm × 5 cm', 'Especiais', 'estrela', null, null, 5, 40.0, 5.0, 25.0, null, 25.0, 2, true),
('12.30.5 SOLa', 'Roseta 12.30.5 SOLa', 'Diâmetro 15 cm · formato estrela · rabiola com 5 fitas de 30 cm × 5 cm', 'Especiais', 'estrela', 15.0, null, 5, 30.0, 5.0, 33.0, null, 33.0, 2, true),
('12.40.5 SOLa', 'Roseta 12.40.5 SOLa', 'Diâmetro 15 cm · formato estrela · rabiola com 5 fitas de 40 cm × 5 cm', 'Especiais', 'estrela', 15.0, null, 5, 40.0, 5.0, 34.0, null, 34.0, 2, true),
('12.50.5 SOLa', 'Roseta 12.50.5 SOLa', 'Diâmetro 15 cm · formato estrela · rabiola com 5 fitas de 50 cm × 5 cm', 'Especiais', 'estrela', 15.0, null, 5, 50.0, 5.0, 35.0, null, 35.0, 2, true),
('12.30.5 PR', 'Roseta 12.30.5 PR', 'Diâmetro 15 cm · rabiola com 5 fitas de 30 cm × 5 cm', 'Especiais', null, 15.0, null, 5, 30.0, 5.0, 21.8, null, 21.8, 2, true),
('12.30.5 PRa', 'Roseta 12.30.5 PRa', 'Diâmetro 15 cm · rabiola com 5 fitas de 30 cm × 5 cm', 'Especiais', null, 15.0, null, 5, 30.0, 5.0, 22.5, null, 22.5, 2, true),
('12.40.5 XP1a', 'Roseta 12.40.5 XP1a', 'Diâmetro 15 cm · modelo esp. · rabiola com 3 fitas de 40 cm × 5 cm', 'Especiais', 'esp.', 15.0, null, 3, 40.0, 5.0, null, 27.0, 27.0, 2, true),
('12.30.3 XP1a', 'Roseta 12.30.3 XP1a', 'Diâmetro 15 cm · modelo esp. · rabiola com 3 fitas de 30 cm × 5 cm', 'Especiais', 'esp.', 15.0, null, 3, 30.0, 5.0, null, 22.5, 22.5, 2, true),
('12.30.3 XPa', 'Roseta 12.30.3 XPa', 'Diâmetro 13 cm · modelo esp. · rabiola com 3 fitas de 30 cm × 5 cm', 'Especiais', 'esp.', 13.0, null, 3, 30.0, 5.0, null, 21.0, 21.0, 2, true),
('12.30.5 XPa', 'Roseta 12.30.5 XPa', 'Diâmetro 13 cm · modelo esp. · rabiola com 5 fitas de 30 cm × 5 cm', 'Especiais', 'esp.', 13.0, null, 5, 30.0, 5.0, null, 23.0, 23.0, 2, true),
('12.40.5 XPa', 'Roseta 12.40.5 XPa', 'Diâmetro 13 cm · modelo esp. · rabiola com 5 fitas de 40 cm × 5 cm', 'Especiais', 'esp.', 13.0, null, 5, 40.0, 5.0, null, 25.0, 25.0, 2, true),
('12.40.6CB', 'Roseta 12.40.6CB', 'Diâmetro 17 cm · modelo esp. · rabiola com 6 fitas de 40 cm × 5 cm', 'Especiais', 'esp.', 17.0, null, 6, 40.0, 5.0, null, 40.5, 40.5, 2, true),
('12.40.CH', 'Roseta 12.40.CH', 'Diâmetro 15 cm · modelo esp. · rabiola com 5 fitas de 40 cm × 5 cm', 'Especiais', 'esp.', 15.0, null, 5, 40.0, 5.0, null, 35.0, 35.0, 2, true),
('12.40.5B3', 'Roseta 12.40.5B3', 'Diâmetro 17 cm · modelo esp. · rabiola com 5 fitas de 40 cm × 5 cm', 'Especiais', 'esp.', 17.0, null, 5, 40.0, 5.0, null, 39.3, 39.3, 2, true),
('12.40.7ML', 'Roseta 12.40.7ML', 'Diâmetro 19 cm · modelo esp. · rabiola com 7 fitas de 40 cm × 5 cm', 'Especiais', 'esp.', 19.0, null, 7, 40.0, 5.0, 50.0, null, 50.0, 2, true),
('12.30.9LBF', 'Roseta 12.30.9LBF', 'Diâmetro 18 cm · modelo esp. · rabiola com 7 fitas de 30 cm × 5 cm', 'Especiais', 'esp.', 18.0, null, 7, 30.0, 5.0, 40.0, null, 40.0, 2, true),
('12.30.8RL', 'Roseta 12.30.8RL', 'modelo esp. · rabiola com 7 fitas de 30 cm × 5 cm', 'Especiais', 'esp.', null, null, 7, 30.0, 5.0, 38.0, null, 38.0, 2, true),
('12.40.5JAB', 'Roseta 12.40.5JAB', 'Diâmetro 17 cm · modelo esp. · rabiola com 5 fitas de 40 cm × 5 cm', 'Grande', 'esp.', 17.0, null, 5, 40.0, 5.0, 34.5, null, 34.5, 2, true),
('12.50.5 La', 'Roseta 12.50.5 La', 'Diâmetro 15 cm · modelo L · rabiola com 5 fitas de 50 cm × 5 cm', 'Grande', 'L', 15.0, null, 5, 50.0, 5.0, 31.5, null, 31.5, 2, true),
('12.50.5 L1a', 'Roseta 12.50.5 L1a', 'Diâmetro 18 cm · modelo L · rabiola com 5 fitas de 50 cm × 5 cm', 'Grande', 'L', 18.0, null, 5, 50.0, 5.0, 37.3, null, 37.3, 2, true),
('12.70.5 L1a', 'Roseta 12.70.5 L1a', 'Diâmetro 18 cm · modelo L · rabiola com 5 fitas de 70 cm × 5 cm', 'Grande', 'L', 18.0, null, 5, 70.0, 5.0, 39.0, null, 39.0, 2, true),
('12.50.5 LX1a', 'Roseta 12.50.5 LX1a', 'Diâmetro 18 cm · modelo L · rabiola com 5 fitas de 50 cm × 5 cm', 'Grande', 'L', 18.0, null, 5, 50.0, 5.0, 38.5, null, 38.5, 2, true),
('12.50.5 L2a', 'Roseta 12.50.5 L2a', 'Diâmetro 22 cm · modelo L · rabiola com 5 fitas de 50 cm × 5 cm', 'Grande', 'L', 22.0, null, 5, 50.0, 5.0, 49.0, null, 49.0, 2, true),
('12.70.5 L2a', 'Roseta 12.70.5 L2a', 'Diâmetro 22 cm · modelo L · rabiola com 5 fitas de 70 cm × 5 cm', 'Grande', 'L', 22.0, null, 5, 70.0, 5.0, 51.0, null, 51.0, 2, true),
('12.50.5LAÇO', 'Roseta 12.50.5LAÇO', 'Diâmetro 23 cm · modelo L · rabiola com 5 fitas de 50 cm × 5 cm', 'Grande', 'L', 23.0, null, 5, 50.0, 5.0, 46.8, null, 46.8, 2, true),
('12.50.5 C', 'Roseta 12.50.5 C', 'Diâmetro 18 cm · modelo L · rabiola com 5 fitas de 50 cm × 5 cm', 'Grande', 'L', 18.0, null, 5, 50.0, 5.0, null, 27.0, 27.0, 2, true),
('12.50.5 CX', 'Roseta 12.50.5 CX', 'Diâmetro 18 cm · modelo L · rabiola com 5 fitas de 50 cm × 5 cm', 'Grande', 'L', 18.0, null, 5, 50.0, 5.0, null, 27.0, 27.0, 2, true),
('12.40.3 TE', 'Roseta 12.40.3 TE', 'Diâmetro 13 cm · rabiola com 3 fitas de 40 cm × 5 cm', 'Grande', null, 13.0, null, 3, 40.0, 5.0, null, 23.0, 23.0, 2, true),
('9.40.4 QE', 'Roseta 9.40.4 QE', 'Diâmetro 13 cm · rabiola com 4 fitas de 40 cm × 4 cm', 'Grande', null, 13.0, null, 4, 40.0, 4.0, null, 23.0, 23.0, 2, true),
('12.50.5 M2GEP', 'Roseta 12.50.5 M2GEP', 'Diâmetro 19 cm · modelo XL · rabiola com 5 fitas de 50 cm × 5 cm', 'Grande', 'XL', 19.0, null, 5, 50.0, 5.0, null, 42.0, 42.0, 2, true),
('12.50.5 M1GEP', 'Roseta 12.50.5 M1GEP', 'Diâmetro 21 cm · modelo XL · rabiola com 5 fitas de 50 cm × 5 cm', 'Grande', 'XL', 21.0, null, 5, 50.0, 5.0, null, 47.0, 47.0, 2, true),
('12.70.5 MGEP', 'Roseta 12.70.5 MGEP', 'Diâmetro 22 cm · modelo XL · rabiola com 5 fitas de 70 cm × 5 cm', 'Grande', 'XL', 22.0, null, 5, 70.0, 5.0, null, 52.0, 52.0, 2, true),
('12.50.5M2GXG', 'Roseta 12.50.5M2GXG', 'Diâmetro 19 cm · modelo XL · rabiola com 5 fitas de 50 cm × 5 cm', 'Grande', 'XL', 19.0, null, 5, 50.0, 5.0, null, 42.0, 42.0, 2, true),
('12.50.5M1GXG', 'Roseta 12.50.5M1GXG', 'Diâmetro 21 cm · modelo XL · rabiola com 5 fitas de 50 cm × 5 cm', 'Grande', 'XL', 21.0, null, 5, 50.0, 5.0, null, 47.0, 47.0, 2, true),
('12.70.5MGXG', 'Roseta 12.70.5MGXG', 'Diâmetro 22 cm · modelo XL · rabiola com 5 fitas de 70 cm × 5 cm', 'Grande', 'XL', 22.0, null, 5, 70.0, 5.0, null, 52.0, 52.0, 2, true),
('12.50.5M2GXP', 'Roseta 12.50.5M2GXP', 'Diâmetro 20 cm · modelo XL · rabiola com 5 fitas de 50 cm × 5 cm', 'Grande', 'XL', 20.0, null, 5, 50.0, 5.0, null, 42.0, 42.0, 2, true),
('12.50.5M1GXP', 'Roseta 12.50.5M1GXP', 'Diâmetro 21 cm · modelo XL · rabiola com 5 fitas de 50 cm × 5 cm', 'Grande', 'XL', 21.0, null, 5, 50.0, 5.0, null, 47.0, 47.0, 2, true),
('12.70.5MGXP', 'Roseta 12.70.5MGXP', 'Diâmetro 22 cm · modelo XL · rabiola com 5 fitas de 70 cm × 5 cm', 'Grande', 'XL', 22.0, null, 5, 70.0, 5.0, null, 52.0, 52.0, 2, true),
('12.50.5 M2E', 'Roseta 12.50.5 M2E', 'Diâmetro 20 cm · modelo XL · rabiola com 5 fitas de 50 cm × 5 cm', 'Grande', 'XL', 20.0, null, 5, 50.0, 5.0, null, 46.0, 46.0, 2, true),
('12.50.5 M1E', 'Roseta 12.50.5 M1E', 'Diâmetro 21 cm · modelo XL · rabiola com 5 fitas de 50 cm × 5 cm', 'Grande', 'XL', 21.0, null, 5, 50.0, 5.0, null, 51.0, 51.0, 2, true),
('12.70.5 ME', 'Roseta 12.70.5 ME', 'Diâmetro 22 cm · modelo XL · rabiola com 5 fitas de 70 cm × 5 cm', 'Grande', 'XL', 22.0, null, 5, 70.0, 5.0, null, 61.0, 61.0, 2, true),
('12.35.7M3BIC', 'Roseta 12.35.7M3BIC', 'Diâmetro 16 cm · modelo XL · rabiola com 5 fitas de 50 cm × 5 cm', 'Grande', 'XL', 16.0, null, 5, 50.0, 5.0, 47.0, null, 47.0, 2, true),
('12.50.5M2BIC', 'Roseta 12.50.5M2BIC', 'Diâmetro 19 cm · modelo XL · rabiola com 5 fitas de 50 cm × 5 cm', 'Grande', 'XL', 19.0, null, 5, 50.0, 5.0, 47.0, null, 47.0, 2, true),
('12.50.5M1BIC', 'Roseta 12.50.5M1BIC', 'Diâmetro 20 cm · modelo XL · rabiola com 5 fitas de 50 cm × 5 cm', 'Grande', 'XL', 20.0, null, 5, 50.0, 5.0, 57.0, null, 57.0, 2, true),
('12.70.5MBIC', 'Roseta 12.70.5MBIC', 'Diâmetro 22 cm · modelo XL · rabiola com 5 fitas de 70 cm × 5 cm', 'Grande', 'XL', 22.0, null, 5, 70.0, 5.0, 67.0, null, 67.0, 2, true),
('12.90.7JAB', 'Roseta 12.90.7JAB', 'Diâmetro 27 cm · modelo XXL · rabiola com 7 fitas de 90 cm × 5 cm', 'Extra grande', 'XXL', 27.0, null, 7, 90.0, 5.0, 93.0, null, 93.0, 2, true),
('12.90.7 GEP', 'Roseta 12.90.7 GEP', 'Diâmetro 30 cm · modelo XXL · rabiola com 7 fitas de 90 cm × 5 cm', 'Extra grande', 'XXL', 30.0, null, 7, 90.0, 5.0, null, 100.0, 100.0, 2, true),
('12.90.7 GXP', 'Roseta 12.90.7 GXP', 'Diâmetro 30 cm · modelo XXL · rabiola com 7 fitas de 90 cm × 5 cm', 'Extra grande', 'XXL', 30.0, null, 7, 90.0, 5.0, null, 100.0, 100.0, 2, true),
('12.90.7 GXG', 'Roseta 12.90.7 GXG', 'Diâmetro 30 cm · modelo XXL · rabiola com 7 fitas de 90 cm × 5 cm', 'Extra grande', 'XXL', 30.0, null, 7, 90.0, 5.0, null, 100.0, 100.0, 2, true),
('12.90.7BIC', 'Roseta 12.90.7BIC', 'Diâmetro 30 cm · modelo XXL · rabiola com 7 fitas de 90 cm × 5 cm', 'Extra grande', 'XXL', 30.0, null, 7, 90.0, 5.0, 140.0, null, 140.0, 2, true),
('12.90.7BICG', 'Roseta 12.90.7BICG', 'Diâmetro 42 cm · modelo XXL · rabiola com 7 fitas de 90 cm × 5 cm', 'Extra grande', 'XXL', 42.0, null, 7, 90.0, 5.0, 190.0, null, 190.0, 2, true),
('12.90.7 E', 'Roseta 12.90.7 E', 'Diâmetro 30 cm · modelo XXL · rabiola com 7 fitas de 90 cm × 5 cm', 'Extra grande', 'XXL', 30.0, null, 7, 90.0, 5.0, null, 140.0, 140.0, 2, true),
('12.90.7 KVC', 'Roseta 12.90.7 KVC', 'Diâmetro 30 cm · modelo XXL · rabiola com 7 fitas de 90 cm × 5 cm', 'Extra grande', 'XXL', 30.0, null, 7, 90.0, 5.0, 140.0, null, 140.0, 2, true),
('12.90.7 SOLGa', 'Roseta 12.90.7 SOLGa', 'Diâmetro 30 cm · modelo XXL · rabiola com 7 fitas de 90 cm × 5 cm', 'Extra grande', 'XXL', 30.0, null, 7, 90.0, 5.0, 140.0, null, 140.0, 2, true),
('12.90.7CEMG', 'Roseta 12.90.7CEMG', 'Diâmetro 42 cm · modelo XXL · rabiola com 7 fitas de 90 cm × 5 cm', 'Extra grande', 'XXL', 42.0, null, 7, 90.0, 5.0, 190.0, null, 190.0, 2, true),
('12.90.5SEG', 'Roseta 12.90.5SEG', 'Diâmetro 42 cm · modelo XXL · rabiola com 7 fitas de 90 cm × 5 cm', 'Extra grande', 'XXL', 42.0, null, 7, 90.0, 5.0, 190.0, null, 190.0, 2, true);

insert into modelos_roseta (referencia, nome, descricao, categoria, rabiola_qtd, rabiola_comprimento_cm, largura_fita_cm,
                            preco, gravacoes_inclusas, ativo) values
    ('FL', 'Fita lisa (FL)',                   'Fita lisa de 20 cm × 5 cm, 2 gravações incluídas.',            'Diversos', 1, 20, 5, 3.40, 2, true),
    ('FA', 'Fita com argola (FA)',             'Fita de 20 cm × 5 cm com argola, 2 gravações incluídas.',      'Diversos', 1, 20, 5, 4.00, 2, true),
    ('BF', 'Bottom com fita (BF)',             'Bottom com fita de 20 cm × 4 cm, 1 gravação incluída.',        'Diversos', 1, 20, 4, 4.00, 1, true),
    ('BA', 'Bottom com alfinete (BA)',         'Bottom com alfinete, 1 gravação incluída.',                    'Diversos', 0, null, null, 3.40, 1, true);


-- Corpo da roseta: uma cor por volta (3 quando a lista não informa as voltas). Consumo estimado = perímetro × 2,5 (franzido).
insert into partes_modelo (modelo_id, nome, grupo, metros_consumidos)
select m.id,
       case when n.total = 1 then 'Volta'
            when v.i = 1 then 'Volta ' || v.i || ' (externa)'
            when v.i = n.total then 'Volta ' || v.i || ' (interna)'
            else 'Volta ' || v.i end,
       'CORPO',
       round((pi() * (coalesce(m.diametro_cm, 12) * (n.total - v.i + 1) / n.total / 100.0) * 2.5)::numeric, 3)
from modelos_roseta m
cross join lateral (select coalesce(m.voltas, 3) as total) n
cross join lateral generate_series(1, n.total) as v(i)
where m.categoria <> 'Diversos';

-- Rabiola: uma cor por fita; consumo = comprimento da fita.
insert into partes_modelo (modelo_id, nome, grupo, metros_consumidos)
select m.id,
       case when m.categoria = 'Diversos' then 'Fita'
            when m.rabiola_qtd = 1 then 'Fita da rabiola'
            else 'Fita da rabiola ' || j.i end,
       'RABIOLA',
       round((m.rabiola_comprimento_cm / 100.0)::numeric, 3)
from modelos_roseta m
cross join lateral generate_series(1, m.rabiola_qtd) as j(i)
where m.referencia is not null and m.rabiola_qtd > 0;

-- Componentes fixos (não escolhidos pelo cliente): botton em toda roseta, argola/alfinete nos diversos.
insert into componentes_modelo (modelo_id, material_id, quantidade)
select m.id, (select id from fitas where nome = 'Botton' and tipo = 'BOTTON'), 1
from modelos_roseta m
where m.referencia is not null and (m.categoria <> 'Diversos' or m.referencia in ('BF', 'BA'));

insert into componentes_modelo (modelo_id, material_id, quantidade)
select m.id, (select id from fitas where nome = 'Argola' and tipo = 'ARGOLA'), 1
from modelos_roseta m where m.referencia = 'FA';

insert into componentes_modelo (modelo_id, material_id, quantidade)
select m.id, (select id from fitas where nome = 'Alfinete' and tipo = 'ALFINETE'), 1
from modelos_roseta m where m.referencia = 'BA';
