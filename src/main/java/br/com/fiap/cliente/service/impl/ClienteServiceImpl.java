package br.com.fiap.cliente.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.cliente.enums.StatusCliente;
import br.com.fiap.cliente.exceptions.EntityNotFoundException;
import br.com.fiap.cliente.mapper.ClienteMapper;
import br.com.fiap.cliente.model.Cliente;
import br.com.fiap.cliente.model.entity.ClienteEntity;
import br.com.fiap.cliente.repository.ClienteRepository;
import br.com.fiap.cliente.service.ClienteService;

@Service
public class ClienteServiceImpl implements ClienteService {

	private final ClienteMapper clienteMapper;
	private final ClienteRepository clienteRepository;

	public ClienteServiceImpl(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
		this.clienteRepository = clienteRepository;
		this.clienteMapper = clienteMapper;
	}

	public List<Cliente> listarClientes() {
		List<ClienteEntity> clienteEntityList = clienteRepository.findAll();
		return clienteEntityList.stream().map(clienteMapper::converterClienteEntityParaCliente).toList();
	}

	@Override
	@Transactional
	public Cliente criarCliente(Cliente cliente) {
		ClienteEntity clienteEntity = new ClienteEntity();
		clienteEntity.setNome(cliente.getNome());
		clienteEntity.setDocumento(cliente.getDocumento());
		clienteEntity.setEmail(cliente.getEmail());
		clienteEntity.setTelefone(cliente.getTelefone());
		clienteEntity.setEndereco(cliente.getEndereco());
		clienteEntity.setCep(cliente.getCep());
		clienteEntity.setStatus(cliente.getStatus());

		ClienteEntity clienteSalvo = clienteRepository.save(clienteEntity);

		return clienteMapper.converterClienteEntityParaCliente(clienteSalvo);
	}

	@Override
	public Cliente obterPorId(Long id) {
		ClienteEntity clienteEntity = clienteRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));

		return clienteMapper.converterClienteEntityParaCliente(clienteEntity);
	}

	@Override
	@Transactional
	public Cliente atualizarCliente(Long id, Cliente clienteRequest) {
		ClienteEntity clienteEntityRecuperado = clienteRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
		
	    Cliente cliente = clienteMapper.converterClienteEntityParaCliente(clienteEntityRecuperado);

		if (cliente.isDesativado()) {
			throw new IllegalStateException("O cliente informado não está ATIVO.");
		}

	    clienteEntityRecuperado.setNome(clienteRequest.getNome());
	    clienteEntityRecuperado.setDocumento(clienteRequest.getDocumento());
	    clienteEntityRecuperado.setEmail(clienteRequest.getEmail());
	    clienteEntityRecuperado.setTelefone(clienteRequest.getTelefone());
	    clienteEntityRecuperado.setEndereco(clienteRequest.getEndereco());
	    clienteEntityRecuperado.setCep(clienteRequest.getCep());

	    ClienteEntity clienteEntityAtualizado = clienteRepository.save(clienteEntityRecuperado);

		return clienteMapper.converterClienteEntityParaCliente(clienteEntityAtualizado);
	}

	@Override
	@Transactional
    public void desativarCliente(Long id) {
		ClienteEntity clienteEntity = clienteRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
		
	    Cliente cliente = clienteMapper.converterClienteEntityParaCliente(clienteEntity);

        if (cliente.isDesativado()) {
            throw new IllegalStateException("O cliente já está DESATIVADO.");
        }

        clienteEntity.setStatus(StatusCliente.DESATIVADO);
        clienteRepository.save(clienteEntity);
    }
	
	@Override
	@Transactional
    public void ativarCliente(Long id) {
		ClienteEntity clienteEntity = clienteRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
		
	    Cliente cliente = clienteMapper.converterClienteEntityParaCliente(clienteEntity);

        if (cliente.isAtivo()) {
            throw new IllegalStateException("O cliente já está ATIVO.");
        }

        clienteEntity.setStatus(StatusCliente.ATIVO);
        clienteRepository.save(clienteEntity);
    }
}
