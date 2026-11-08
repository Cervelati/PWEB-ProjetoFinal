package com.rosetas.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Locale;

/** Gera o "Pix copia e cola" estático (BR Code, padrão EMV do Banco Central) com valor fixo. */
public final class PixBrCode {

    private PixBrCode() {
    }

    public static String gerar(String chave, String nomeRecebedor, String cidade, BigDecimal valor, String txid) {
        String conta = tlv("00", "br.gov.bcb.pix") + tlv("01", chave.trim());
        String adicional = tlv("05", txid);

        String payload = tlv("00", "01")
                + tlv("01", "11")
                + tlv("26", conta)
                + tlv("52", "0000")
                + tlv("53", "986")
                + tlv("54", valor.setScale(2, RoundingMode.HALF_UP).toPlainString())
                + tlv("58", "BR")
                + tlv("59", texto(nomeRecebedor, 25))
                + tlv("60", texto(cidade, 15))
                + tlv("62", adicional)
                + "6304";
        return payload + crc16(payload);
    }

    private static String tlv(String id, String valor) {
        return id + String.format("%02d", valor.getBytes(StandardCharsets.UTF_8).length) + valor;
    }

    /** Maiúsculas, sem acentos, só caracteres simples, limitado ao tamanho do campo. */
    static String texto(String s, int max) {
        String t = Normalizer.normalize(s.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^A-Za-z0-9 ]", "")
                .toUpperCase(Locale.ROOT);
        return t.length() > max ? t.substring(0, max).trim() : t;
    }

    /** CRC16/CCITT-FALSE (poly 0x1021, init 0xFFFF), 4 dígitos hexadecimais maiúsculos. */
    static String crc16(String s) {
        int crc = 0xFFFF;
        for (byte b : s.getBytes(StandardCharsets.UTF_8)) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                crc = (crc & 0x8000) != 0 ? ((crc << 1) ^ 0x1021) : (crc << 1);
                crc &= 0xFFFF;
            }
        }
        return String.format("%04X", crc);
    }
}
