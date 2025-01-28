package br.com.fiap.cliente.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.fiap.cliente.model.dto.ClienteRequestDTO;
import br.com.fiap.cliente.model.dto.ClienteResponseDTO;

@Service
public interface ClienteService {
 
    public List<ClienteResponseDTO> listarClientes();

    public ClienteResponseDTO criarCliente(ClienteRequestDTO cliente);

    public ClienteResponseDTO obterPorId(Long id);

    public ClienteResponseDTO atualizarCliente(Long id, ClienteRequestDTO cliente);

    public void desativarCliente(Long id);
    
    public void ativarCliente(Long id);
}
