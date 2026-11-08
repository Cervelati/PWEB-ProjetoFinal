package com.rosetas.service;

import com.rosetas.web.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/** Guarda as fotos dos modelos em disco (volume do Docker) e devolve o caminho público. */
@Component
public class FotoStorage {

    public static final String PREFIXO = "/api/arquivos/modelos/";

    private final Path dir;

    public FotoStorage(@Value("${app.uploads.dir:./uploads}") String base) throws IOException {
        this.dir = Path.of(base, "modelos").toAbsolutePath().normalize();
        Files.createDirectories(dir);
    }

    public Path diretorio() {
        return dir;
    }

    /** Valida pelo conteúdo (não pelo nome/Content-Type enviado) e salva com nome aleatório. */
    public String salvar(Long modeloId, MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw ApiException.badRequest("Envie um arquivo de imagem");
        }
        try (InputStream in = arquivo.getInputStream()) {
            byte[] h = in.readNBytes(12);
            String ext = extensao(h);
            if (ext == null) {
                throw ApiException.badRequest("Formato inválido: envie JPG, PNG ou WEBP");
            }
            String nome = "modelo-" + modeloId + "-" + UUID.randomUUID().toString().substring(0, 8) + "." + ext;
            Path destino = dir.resolve(nome).normalize();
            try (InputStream completo = arquivo.getInputStream()) {
                Files.copy(completo, destino, StandardCopyOption.REPLACE_EXISTING);
            }
            return PREFIXO + nome;
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao gravar a foto", e);
        }
    }

    /** Apaga o arquivo antigo, se for um upload (as fotos do catálogo ficam no jar e não são apagadas). */
    public void apagar(String caminho) {
        if (caminho == null || !caminho.startsWith(PREFIXO)) {
            return;
        }
        Path p = dir.resolve(caminho.substring(PREFIXO.length())).normalize();
        if (p.startsWith(dir)) {
            try {
                Files.deleteIfExists(p);
            } catch (IOException ignorada) {
                // arquivo órfão não impede a troca da foto
            }
        }
    }

    private static String extensao(byte[] h) {
        if (h.length >= 3 && (h[0] & 0xFF) == 0xFF && (h[1] & 0xFF) == 0xD8 && (h[2] & 0xFF) == 0xFF) return "jpg";
        if (h.length >= 8 && (h[0] & 0xFF) == 0x89 && h[1] == 'P' && h[2] == 'N' && h[3] == 'G') return "png";
        if (h.length >= 12 && h[0] == 'R' && h[1] == 'I' && h[2] == 'F' && h[3] == 'F'
                && h[8] == 'W' && h[9] == 'E' && h[10] == 'B' && h[11] == 'P') return "webp";
        return null;
    }
}
