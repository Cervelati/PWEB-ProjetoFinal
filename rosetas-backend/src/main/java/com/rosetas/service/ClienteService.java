package com.rosetas.service;

import com.rosetas.domain.Cliente;
import com.rosetas.repository.ClienteRepository;
import com.rosetas.web.ApiException;
import com.rosetas.web.Dtos.ClienteReq;
import com.rosetas.web.Dtos.ClienteResp;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clientes;

    public ClienteService(ClienteRepository clientes) {
        this.clientes = clientes;
    }

    /** Remove tudo que não é dígito e valida o tamanho (DDD + número, com ou sem 55). */
    public static String normalizarTelefone(String bruto) {
        String t = bruto == null ? "" : bruto.replaceAll("\\D", "");
        if (t.length() < 10 || t.length() > 13) {
            throw ApiException.badRequest("Telefone inválido: informe DDD + número");
        }
        return t;
    }

    @Transactional(readOnly = true)
    public List<ClienteResp> buscar(String busca) {
        String b = busca == null ? "" : busca.trim();
        String digitos = b.replaceAll("\\D", "");
        if (digitos.isEmpty()) {
            digitos = "#"; // nunca casa com telefone; evita listar todos ao buscar por nome
        }
        if (b.isEmpty()) {
            digitos = ""; // busca vazia lista todos
        }
        return clientes.buscar(b, digitos, PageRequest.of(0, 200)).stream().map(ClienteResp::from).toList();
    }

    @Transactional
    public ClienteResp atualizar(Long id, ClienteReq req) {
        Cliente c = clientes.findById(id).orElseThrow(() -> ApiException.notFound("Cliente não encontrado"));
        String tel = normalizarTelefone(req.telefone());
        clientes.findByTelefone(tel).filter(outro -> !outro.getId().equals(id)).ifPresent(outro -> {
            throw ApiException.conflict("Já existe outro cliente com este telefone");
        });
        c.setNome(req.nome().trim());
        c.setTelefone(tel);
        c.setEmail(req.email() == null || req.email().isBlank() ? null : req.email().trim());
        return ClienteResp.from(c);
    }
}
