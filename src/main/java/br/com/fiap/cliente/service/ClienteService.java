package br.com.fiap.cliente.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fiap.cliente.model.Cliente;
import br.com.fiap.cliente.repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;
    
    // Método para listar todos os clientes
    public List<Cliente> listarCliente() {
        return clienteRepository.findAll();
    }

    // Método para criar um novo cliente
    public Cliente criarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // Método para obter um cliente pelo ID
    public Cliente obterCliente(Integer id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente.orElse(null);
    }

    // Método para atualizar um cliente existente
    public Cliente atualizarCliente(Integer id, Cliente cliente) {
        if (clienteRepository.existsById(id)) {
            cliente.setId(id);
            return clienteRepository.save(cliente);
        }
        return null;
    }

    // Método para excluir um cliente pelo ID
    public void excluirCliente(Integer id) {
        clienteRepository.deleteById(id);
    }
}
