package br.com.fiap.cliente.mapper;

import org.springframework.stereotype.Component;

import br.com.fiap.cliente.model.Cliente;
import br.com.fiap.cliente.model.dto.ClienteRequestDTO;
import br.com.fiap.cliente.model.dto.ClienteResponseDTO;
import br.com.fiap.cliente.model.entity.ClienteEntity;

@Component
public class ClienteMapper {

    public Cliente converterRequestDTOParaCliente(ClienteRequestDTO clienteRequestDTO) {
        return new Cliente(
            null,
            clienteRequestDTO.nome(),
            clienteRequestDTO.documento(),
            clienteRequestDTO.email(),
            clienteRequestDTO.telefone(),
            clienteRequestDTO.endereco(),
            clienteRequestDTO.cep()
        );
    }
    
    public ClienteResponseDTO converterClienteParaResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
        	cliente.getId(),
        	cliente.getNome(),
        	cliente.getDocumento(),
        	cliente.getEmail(),
        	cliente.getTelefone(),
        	cliente.getEndereco(),
        	cliente.getCep(),
        	cliente.getStatus()
        );
    }
    
    public Cliente converterClienteEntityParaCliente(ClienteEntity clienteEntity) {
        return new Cliente(
        	clienteEntity.getId(),
        	clienteEntity.getNome(),
        	clienteEntity.getDocumento(),
            clienteEntity.getEmail(),
            clienteEntity.getTelefone(),
            clienteEntity.getEndereco(),
            clienteEntity.getCep(),
            clienteEntity.getStatus()
            
        );
    }
}
