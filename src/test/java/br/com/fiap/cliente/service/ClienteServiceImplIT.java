package br.com.fiap.cliente.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import br.com.fiap.cliente.enums.StatusCliente;
import br.com.fiap.cliente.exceptions.EntityNotFoundException;
import br.com.fiap.cliente.model.Cliente;
import br.com.fiap.cliente.model.dto.ClienteRequestDTO;
import br.com.fiap.cliente.model.dto.ClienteResponseDTO;
import br.com.fiap.cliente.repository.ClienteRepository;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClienteServiceImplIT {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private ClienteService clienteService;
	
    @Autowired
    private ClienteRepository clienteRepository;
	
	@Test
	void devePermitirListarClientes() {
		// Arrange
		clienteRepository.save(new Cliente(1L, "João Silva", "355.347.740-70", "joao.silva@email.com", "98765-4321",
				"Rua das Flores, 101", StatusCliente.ATIVO));
		clienteRepository.save(new Cliente(2L, "Maisa Santos", "935.782.990-30", "maisa.santos@email.com", "92365-4521",
				"Rua dos Ventos, 23", StatusCliente.ATIVO));
		
		// Act
		List<ClienteResponseDTO> listaClientes = clienteService.listarClientes();
		
		// Assert
		assertThat(listaClientes).isNotEmpty().hasSizeGreaterThan(0);
		assertThat(listaClientes).allSatisfy(cliente -> {
			assertThat(cliente).isNotNull();
		});
	}
	
	@Test
	void devePermitirRegistrarUmCliente() {
		// Arrange
		ClienteRequestDTO clienteRequest = gerarUmClienteRequestDTO();
        
		// Act
		ClienteResponseDTO clienteResponse = clienteService.criarCliente(clienteRequest);
		
		// Assert
		assertThat(clienteResponse).isInstanceOf(ClienteResponseDTO.class).isNotNull();
		assertThat(clienteResponse.idCliente()).isNotNull();
		assertThat(clienteResponse.nome()).isEqualTo(clienteRequest.nome());
		assertThat(clienteResponse.documento()).isEqualTo(clienteRequest.documento());
		assertThat(clienteResponse.email()).isEqualTo(clienteRequest.email());
		assertThat(clienteResponse.telefone()).isEqualTo(clienteRequest.telefone());
		assertThat(clienteResponse.endereco()).isEqualTo(clienteRequest.endereco());
		assertThat(clienteResponse.status()).isEqualTo(StatusCliente.ATIVO);
	}
	
	@Test
	void devePermitirListarUmClientePorId() {
		// Arrange
		Cliente cliente = clienteRepository.save(new Cliente(null, "Maisa Santos", "935.782.990-30", "maisa.santos@email.com", "92365-4521",
				"Rua dos Ventos, 23", StatusCliente.ATIVO));
		
		// Act
		ClienteResponseDTO clienteResponse = clienteService.obterPorId(cliente.getId());
		
		// Assert
		assertThat(clienteResponse).isInstanceOf(ClienteResponseDTO.class).isNotNull();
		assertThat(clienteResponse.idCliente()).isNotNull();
		assertThat(clienteResponse.nome()).isEqualTo(cliente.getNome());
		assertThat(clienteResponse.documento()).isEqualTo(cliente.getDocumento());
		assertThat(clienteResponse.email()).isEqualTo(cliente.getEmail());
		assertThat(clienteResponse.telefone()).isEqualTo(cliente.getTelefone());
		assertThat(clienteResponse.endereco()).isEqualTo(cliente.getEndereco());
		assertThat(clienteResponse.status()).isEqualTo(cliente.getStatus());
	}
	
	@Test
	void devePermitirExcluirUmCliente() {
		// Arrange
		Cliente cliente = clienteRepository.save(new Cliente(null, "Maiara Santos", "935.782.990-30", "maisa.santos@email.com", "92365-4521",
				"Rua dos Ventos, 23", StatusCliente.ATIVO));
		
		// Act
		clienteService.excluirCliente(cliente.getId());
		
		// Assert
		assertThatThrownBy(() -> clienteService.obterPorId(cliente.getId()))
		.isInstanceOf(EntityNotFoundException.class)
		.hasMessage("Cliente não encontrado.");
	}
	
	@Test
	void devePermitirAtualizarUmCliente() {
		// Arrange
		Cliente clienteSemModificacao = gerarCliente();
		Cliente clienteModificado = clienteRepository.save(gerarCliente());
		clienteModificado.setNome("Nome Teste");
		clienteModificado.setTelefone("11111-1111");
		clienteModificado.setEndereco("Endereco Teste");
		Long id = clienteModificado.getId();
		ClienteRequestDTO clienteModificadoRequest = new ClienteRequestDTO(clienteModificado.getNome(), clienteModificado.getDocumento(),
				clienteModificado.getEmail(), clienteModificado.getTelefone(), clienteModificado.getEndereco());
		
		// Act
		ClienteResponseDTO clienteObtido = clienteService.atualizarCliente(id, clienteModificadoRequest);
		
		// Assert
		assertThat(clienteObtido).isInstanceOf(ClienteResponseDTO.class).isNotNull();
		assertThat(clienteObtido.idCliente()).isEqualTo(id);
		assertThat(clienteObtido.nome()).isNotEqualTo(clienteSemModificacao.getNome());
		assertThat(clienteObtido.documento()).isEqualTo(clienteSemModificacao.getDocumento());
		assertThat(clienteObtido.email()).isEqualTo(clienteSemModificacao.getEmail());
		assertThat(clienteObtido.telefone()).isNotEqualTo(clienteSemModificacao.getTelefone());
		assertThat(clienteObtido.endereco()).isNotEqualTo(clienteSemModificacao.getEndereco());
	}

	private Cliente gerarCliente() {
		return new Cliente(null, "Maiara Santos", "935.782.990-30", "maisa.santos@email.com", "92365-4521",
				"Rua dos Ventos, 23", StatusCliente.ATIVO);
	}
	
	private ClienteRequestDTO gerarUmClienteRequestDTO() {
		return new ClienteRequestDTO("João Silva", "123.456.789-01", "joao.silva@email.com",
				"98765-4321", "Rua das Flores, 101");
	}
}
