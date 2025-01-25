package br.com.fiap.cliente.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.cliente.model.dto.ClienteRequestDTO;
import br.com.fiap.cliente.model.dto.ClienteResponseDTO;
import br.com.fiap.cliente.service.ClienteService;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

	private final ClienteService clienteService;

	public ClienteController(ClienteService clienteService) {
		this.clienteService = clienteService;
	}

	@GetMapping
	public ResponseEntity<List<ClienteResponseDTO>> listarClientes() {
		List<ClienteResponseDTO> clientesDTO = clienteService.listarClientes();
		return ResponseEntity.ok(clientesDTO);
	}

	@PostMapping
	public ResponseEntity<ClienteResponseDTO> criarCliente(@RequestBody ClienteRequestDTO clienteRequestDTO) {
		ClienteResponseDTO clienteCriado = clienteService.criarCliente(clienteRequestDTO);
		return ResponseEntity.status(201).body(clienteCriado);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ClienteResponseDTO> obterClientePorId(@PathVariable Long id) {
		ClienteResponseDTO cliente = clienteService.obterPorId(id);
		return ResponseEntity.ok(cliente);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ClienteResponseDTO> atualizarCliente(@PathVariable Long id,
			@RequestBody ClienteRequestDTO clienteRequest) {
		ClienteResponseDTO clienteAtualizado = clienteService.atualizarCliente(id, clienteRequest);
		return ResponseEntity.ok(clienteAtualizado);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluirCliente(@PathVariable Long id) {
		clienteService.excluirCliente(id);
		return ResponseEntity.noContent().build();
	}
}