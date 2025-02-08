package br.com.fiap.cliente.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import br.com.fiap.cliente.enums.StatusCliente;
import br.com.fiap.cliente.exceptions.EntityNotFoundException;
import br.com.fiap.cliente.model.Cliente;
import br.com.fiap.cliente.model.entity.ClienteEntity;
import br.com.fiap.cliente.repository.ClienteRepository;

@AutoConfigureTestDatabase
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
		clienteRepository.save(new ClienteEntity(1L, "João Silva", "355.347.740-70", "joao.silva@email.com", "98765-4321",
				"Rua das Flores, 101", "05115-070", StatusCliente.ATIVO));
		clienteRepository.save(new ClienteEntity(2L, "Maisa Santos", "935.782.990-30", "maisa.santos@email.com", "92365-4521",
				"Rua dos Ventos, 23", "03589-130", StatusCliente.ATIVO));
		
		// Act
		List<Cliente> listaClientes = clienteService.listarClientes();
		
		// Assert
		assertThat(listaClientes)
	    .isNotEmpty()
	    .hasSizeGreaterThan(0)
	    .allSatisfy(cliente -> assertThat(cliente).isNotNull());
	}
	
	@Test
	void devePermitirRegistrarUmCliente() {
		// Arrange
		Cliente clienteRequest = gerarUmCliente();
        
		// Act
		Cliente clienteResponse = clienteService.criarCliente(clienteRequest);
		
		// Assert
		assertThat(clienteResponse).isInstanceOf(Cliente.class).isNotNull();
		assertThat(clienteResponse.getId()).isNotNull();
		assertThat(clienteResponse.getNome()).isEqualTo(clienteRequest.getNome());
		assertThat(clienteResponse.getDocumento()).isEqualTo(clienteRequest.getDocumento());
		assertThat(clienteResponse.getEmail()).isEqualTo(clienteRequest.getEmail());
		assertThat(clienteResponse.getTelefone()).isEqualTo(clienteRequest.getTelefone());
		assertThat(clienteResponse.getEndereco()).isEqualTo(clienteRequest.getEndereco());
		assertThat(clienteResponse.getCep()).isEqualTo(clienteRequest.getCep());
		assertThat(clienteResponse.getStatus()).isEqualTo(StatusCliente.ATIVO);
	}
	
	@Test
	void devePermitirListarUmClientePorId() {
		// Arrange
		ClienteEntity clienteEntity = clienteRepository.save(new ClienteEntity(null, "Maisa Santos", "935.782.990-30", "maisa.santos@email.com", "92365-4521",
				"Rua dos Ventos, 23", "08050-005", StatusCliente.ATIVO));
		
		// Act
		Cliente clienteResponse = clienteService.obterPorId(clienteEntity.getId());
		
		// Assert
		assertThat(clienteResponse).isInstanceOf(Cliente.class).isNotNull();
		assertThat(clienteResponse.getId()).isNotNull();
		assertThat(clienteResponse.getNome()).isEqualTo(clienteEntity.getNome());
		assertThat(clienteResponse.getDocumento()).isEqualTo(clienteEntity.getDocumento());
		assertThat(clienteResponse.getEmail()).isEqualTo(clienteEntity.getEmail());
		assertThat(clienteResponse.getTelefone()).isEqualTo(clienteEntity.getTelefone());
		assertThat(clienteResponse.getEndereco()).isEqualTo(clienteEntity.getEndereco());
		assertThat(clienteResponse.getCep()).isEqualTo(clienteEntity.getCep());
		assertThat(clienteResponse.getStatus()).isEqualTo(clienteEntity.getStatus());
	}
	
	@Test
	void devePermitirDesativarUmCliente() {
		// Arrange
		ClienteEntity clienteEntity = clienteRepository.save(new ClienteEntity(null, "Maiara Santos", "935.782.990-30",
				"maisa.santos@email.com", "92365-4521", "Rua dos Ventos, 23", "02169-240", StatusCliente.ATIVO));

		// Act
		clienteService.desativarCliente(clienteEntity.getId());

		// Assert
		ClienteEntity clienteAtualizado = clienteRepository.findById(clienteEntity.getId())
				.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
		assertThat(clienteAtualizado.getStatus()).isEqualTo(StatusCliente.DESATIVADO);
	}
	
	@Test
	void deveGerarExceptionAoDesativarClienteJaDesativado() {
		// Arrange
		ClienteEntity clienteEntity = clienteRepository.save(new ClienteEntity(null, "Maiara Santos", "935.782.990-30",
				"maisa.santos@email.com", "92365-4521", "Rua dos Ventos, 23", "02357-097", StatusCliente.DESATIVADO));
		Long id = clienteEntity.getId();

		// Act & Assert
		assertThatThrownBy(() -> clienteService.desativarCliente(id))
				.isInstanceOf(IllegalStateException.class).hasMessage("O cliente já está DESATIVADO.");
	}
	
	@Test
	void devePermitirAtivarUmCliente() {
		// Arrange
		ClienteEntity clienteEntity = clienteRepository.save(new ClienteEntity(null, "Ryan Santos", "935.782.990-30",
				"ryan.santos@email.com", "92365-4521", "Rua dos Ventos, 23", "05790-390", StatusCliente.DESATIVADO));

		// Act
		clienteService.ativarCliente(clienteEntity.getId());

		// Assert
		ClienteEntity clienteAtualizado = clienteRepository.findById(clienteEntity.getId())
				.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
		assertThat(clienteAtualizado.getStatus()).isEqualTo(StatusCliente.ATIVO);
	}
	
	@Test
	void deveGerarExceptionAoAtivarClienteJaAtivo() {
		// Arrange
		ClienteEntity clienteEntity = clienteRepository.save(new ClienteEntity(null, "Maiara Santos", "935.782.990-30",
				"maisa.santos@email.com", "92365-4521", "Rua dos Ventos, 23", "04327-175", StatusCliente.ATIVO));
		Long id = clienteEntity.getId();

		// Act & Assert
		assertThatThrownBy(() -> clienteService.ativarCliente(id))
				.isInstanceOf(IllegalStateException.class).hasMessage("O cliente já está ATIVO.");
	}
	
	@Test
	void devePermitirAtualizarUmCliente() {
		// Arrange
		ClienteEntity clienteSemModificacao = gerarClienteEntity();
		ClienteEntity clienteModificado = clienteRepository.save(gerarClienteEntity());
		clienteModificado.setNome("Nome Teste");
		clienteModificado.setTelefone("11111-1111");
		clienteModificado.setEndereco("Endereco Teste");
		Long id = clienteModificado.getId();
		
		Cliente clienteModificadoRequest = new Cliente(null, clienteModificado.getNome(), clienteModificado.getDocumento(),
				clienteModificado.getEmail(), clienteModificado.getTelefone(), clienteModificado.getEndereco(), clienteModificado.getCep(), clienteModificado.getStatus());
		
		// Act
		Cliente clienteObtido = clienteService.atualizarCliente(id, clienteModificadoRequest);
		
		// Assert
		assertThat(clienteObtido).isInstanceOf(Cliente.class).isNotNull();
		assertThat(clienteObtido.getId()).isEqualTo(id);
		assertThat(clienteObtido.getNome()).isNotEqualTo(clienteSemModificacao.getNome());
		assertThat(clienteObtido.getDocumento()).isEqualTo(clienteSemModificacao.getDocumento());
		assertThat(clienteObtido.getEmail()).isEqualTo(clienteSemModificacao.getEmail());
		assertThat(clienteObtido.getTelefone()).isNotEqualTo(clienteSemModificacao.getTelefone());
		assertThat(clienteObtido.getEndereco()).isNotEqualTo(clienteSemModificacao.getEndereco());
		assertThat(clienteObtido.getCep()).isEqualTo(clienteSemModificacao.getCep());
	}

	private ClienteEntity gerarClienteEntity() {
		return new ClienteEntity(null, "Maiara Santos", "935.782.990-30", "maisa.santos@email.com", "92365-4521",
				"Rua dos Ventos, 23", "03813-340", StatusCliente.ATIVO);
	}
	
	private Cliente gerarUmCliente() {
		return new Cliente(null, "João Silva", "123.456.789-01", "joao.silva@email.com",
				"98765-4321", "Rua das Flores, 101", "04717-970");
	}
}
