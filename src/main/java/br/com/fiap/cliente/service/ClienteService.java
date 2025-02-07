package br.com.fiap.cliente.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.fiap.cliente.model.Cliente;

@Service
public interface ClienteService {
 
    public List<Cliente> listarClientes();

    public Cliente criarCliente(Cliente cliente);

    public Cliente obterPorId(Long id);

    public Cliente atualizarCliente(Long id, Cliente cliente);

    public void desativarCliente(Long id);
    
    public void ativarCliente(Long id);
}
