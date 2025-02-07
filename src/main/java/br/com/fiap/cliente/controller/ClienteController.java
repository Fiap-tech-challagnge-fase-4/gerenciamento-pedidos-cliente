package br.com.fiap.cliente.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.cliente.mapper.ClienteMapper;
import br.com.fiap.cliente.model.Cliente;
import br.com.fiap.cliente.model.dto.ClienteRequestDTO;
import br.com.fiap.cliente.model.dto.ClienteResponseDTO;
import br.com.fiap.cliente.service.ClienteService;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

	private final ClienteMapper clienteMapper;
	private final ClienteService clienteService;

	public ClienteController(ClienteService clienteService, ClienteMapper clienteMapper) {
        this.clienteService = clienteService;
        this.clienteMapper = clienteMapper;
    }

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<ClienteResponseDTO> listarClientes() {
		List<Cliente> clientes = clienteService.listarClientes();
		return clientes.stream().map(clienteMapper::converterClienteParaResponseDTO).toList();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ClienteResponseDTO criarCliente(@RequestBody ClienteRequestDTO clienteRequestDTO) {
		Cliente cliente = clienteMapper.converterRequestDTOParaCliente(clienteRequestDTO);
		Cliente clienteCriado = clienteService.criarCliente(cliente);
		return clienteMapper.converterClienteParaResponseDTO(clienteCriado);
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ClienteResponseDTO obterClientePorId(@PathVariable Long id) {
		Cliente cliente = clienteService.obterPorId(id);
		return clienteMapper.converterClienteParaResponseDTO(cliente);
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ClienteResponseDTO atualizarCliente(@PathVariable Long id, @RequestBody ClienteRequestDTO clienteRequest) {
		Cliente clienteModificado = clienteMapper.converterRequestDTOParaCliente(clienteRequest);
		Cliente clienteAtualizado = clienteService.atualizarCliente(id, clienteModificado);
		return clienteMapper.converterClienteParaResponseDTO(clienteAtualizado);
	}

    @PatchMapping("/ativar/{id}")
	@ResponseStatus(HttpStatus.OK)
    public void ativarCliente(@PathVariable Long id) {
        clienteService.ativarCliente(id);
    }
    
    @PatchMapping("/desativar/{id}")
	@ResponseStatus(HttpStatus.OK)
    public void desativarCliente(@PathVariable Long id) {
        clienteService.desativarCliente(id);
    }
}