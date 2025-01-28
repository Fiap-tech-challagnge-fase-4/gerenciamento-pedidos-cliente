package br.com.fiap.cliente.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.cliente.enums.StatusCliente;
import br.com.fiap.cliente.exceptions.EntityNotFoundException;
import br.com.fiap.cliente.model.Cliente;
import br.com.fiap.cliente.model.dto.ClienteRequestDTO;
import br.com.fiap.cliente.model.dto.ClienteResponseDTO;
import br.com.fiap.cliente.repository.ClienteRepository;
import br.com.fiap.cliente.service.ClienteService;

@Service
public class ClienteServiceImpl implements ClienteService {

	private final ClienteRepository clienteRepository;

	public ClienteServiceImpl(ClienteRepository clienteRepository) {
		this.clienteRepository = clienteRepository;
	}

	public List<ClienteResponseDTO> listarClientes() {
		List<Cliente> clientes = clienteRepository.findAll();

		return clientes.stream().map(this::converterClienteEmClienteDTO).toList();
	}

	@Override
	@Transactional
	public ClienteResponseDTO criarCliente(ClienteRequestDTO clienteRequest) {
		Cliente cliente = new Cliente();
		cliente.setNome(clienteRequest.nome());
		cliente.setDocumento(clienteRequest.documento());
		cliente.setEmail(clienteRequest.email());
		cliente.setTelefone(clienteRequest.telefone());
		cliente.setEndereco(clienteRequest.endereco());
		cliente.setStatus(StatusCliente.ATIVO);

		Cliente clienteSalvo = clienteRepository.save(cliente);

		return converterClienteEmClienteDTO(clienteSalvo);
	}

	@Override
	public ClienteResponseDTO obterPorId(Long id) {
		Cliente cliente = clienteRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));

		return converterClienteEmClienteDTO(cliente);
	}

	@Override
	@Transactional
	public ClienteResponseDTO atualizarCliente(Long id, ClienteRequestDTO clienteRequest) {
		Cliente clienteRecuperado = clienteRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));

		clienteRecuperado.setNome(clienteRequest.nome());
		clienteRecuperado.setDocumento(clienteRequest.documento());
		clienteRecuperado.setEmail(clienteRequest.email());
		clienteRecuperado.setTelefone(clienteRequest.telefone());
		clienteRecuperado.setEndereco(clienteRequest.endereco());

		Cliente clienteAtualizado = clienteRepository.save(clienteRecuperado);

		return converterClienteEmClienteDTO(clienteAtualizado);
	}

	@Override
	@Transactional
    public void desativarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        if (!cliente.isAtivo()) {
            throw new IllegalStateException("O cliente já está desativado.");
        }

        cliente.setStatus(StatusCliente.DESATIVADO);
        clienteRepository.save(cliente);
    }
	
	@Override
	@Transactional
    public void ativarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        if (!cliente.isDesativado()) {
            throw new IllegalStateException("O cliente já está ativo.");
        }

        cliente.setStatus(StatusCliente.ATIVO);
        clienteRepository.save(cliente);
    }

	public ClienteResponseDTO converterClienteEmClienteDTO(Cliente cliente) {
		return new ClienteResponseDTO(cliente.getId(), cliente.getNome(), cliente.getDocumento(), cliente.getEmail(),
				cliente.getTelefone(), cliente.getEndereco(), cliente.getStatus());
	}
}
