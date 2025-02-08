package br.com.fiap.cliente.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.fiap.cliente.enums.StatusCliente;
import br.com.fiap.cliente.exceptions.EntityNotFoundException;
import br.com.fiap.cliente.mapper.ClienteMapper;
import br.com.fiap.cliente.model.Cliente;
import br.com.fiap.cliente.model.entity.ClienteEntity;
import br.com.fiap.cliente.repository.ClienteRepository;
import br.com.fiap.cliente.service.impl.ClienteServiceImpl;

class ClienteServiceImplTest {
	
    @Mock
    private ClienteRepository clienteRepository;
    
    private ClienteServiceImpl clienteServiceImpl;

    private AutoCloseable openMocks;
    
	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		ClienteMapper clienteMapper = new ClienteMapper();
		clienteServiceImpl = new ClienteServiceImpl(clienteRepository, clienteMapper);
	}
	@AfterEach
	void teardown() throws Exception {
		openMocks.close();
	}
	
	@Test
	void devePermitirListarClientes() {
		// Arrange
		List<ClienteEntity> listaClientes = gerarListaDeClienteEntity();
		when(clienteRepository.findAll()).thenReturn(listaClientes);
		
		// Act
		List<Cliente> listaObtida = clienteServiceImpl.listarClientes();
		
		// Assert
		verify(clienteRepository, times(1)).findAll();
		assertThat(listaObtida)
	    .hasSize(3)
	    .allSatisfy(cliente -> assertThat(cliente).isNotNull().isInstanceOf(Cliente.class));
	}
	
	@Test
	void devePermitirRegistrarUmCliente() {
		// Arrange
		Cliente clienteRequest = gerarUmCliente();
		ClienteEntity clienteEntity = gerarUmClienteEntity();
		when(clienteRepository.save(any(ClienteEntity.class))).thenReturn(clienteEntity);
        
		// Act
		Cliente clienteResponse = clienteServiceImpl.criarCliente(clienteRequest);
		
		// Assert
		verify(clienteRepository, times(1)).save(any(ClienteEntity.class));
		assertThat(clienteResponse).isInstanceOf(Cliente.class).isNotNull();
		assertThat(clienteRequest.getNome()).isEqualTo(clienteResponse.getNome());
		assertThat(clienteRequest.getDocumento()).isEqualTo(clienteResponse.getDocumento());
		assertThat(clienteRequest.getEmail()).isEqualTo(clienteResponse.getEmail());
		assertThat(clienteRequest.getTelefone()).isEqualTo(clienteResponse.getTelefone());
		assertThat(clienteRequest.getEndereco()).isEqualTo(clienteResponse.getEndereco());
		assertThat(clienteRequest.getCep()).isEqualTo(clienteResponse.getCep());
		assertThat(clienteResponse.getStatus()).isEqualTo(StatusCliente.ATIVO);
	}
	
	@Test
	void devePermitirListarUmClientePorId() {
		// Arrange
		ClienteEntity clienteEntity = gerarUmClienteEntity();
		when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(clienteEntity));
		
		// Act
		Cliente clienteObtido = clienteServiceImpl.obterPorId(clienteEntity.getId());
		
		// Assert
		verify(clienteRepository, times(1)).findById(anyLong());
		assertThat(clienteObtido).isInstanceOf(Cliente.class).isNotNull();
		assertThat(clienteObtido.getId()).isEqualTo(clienteEntity.getId());
		assertThat(clienteObtido.getNome()).isEqualTo(clienteEntity.getNome());
		assertThat(clienteObtido.getDocumento()).isEqualTo(clienteEntity.getDocumento());
		assertThat(clienteObtido.getEmail()).isEqualTo(clienteEntity.getEmail());
		assertThat(clienteObtido.getTelefone()).isEqualTo(clienteEntity.getTelefone());
		assertThat(clienteObtido.getEndereco()).isEqualTo(clienteEntity.getEndereco());
		assertThat(clienteObtido.getCep()).isEqualTo(clienteEntity.getCep());
		assertThat(clienteObtido.getStatus()).isEqualTo(clienteEntity.getStatus());
	}
	
	@Test
	void deveGerarExceptionAoBuscarClienteInexistente() {
		// Arrange
		Long idInexistente = 113123L;
		when(clienteRepository.findById(idInexistente)).thenReturn(Optional.empty());
		
		// Act & Assert
		assertThatThrownBy(() -> clienteServiceImpl.obterPorId(idInexistente))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage("Cliente não encontrado.");
		verify(clienteRepository, times(1)).findById(anyLong());
	}
	
	@Test
	void devePermitirAtualizarUmCliente() {
		// Arrange
		Cliente clienteSemMoficacao = new Cliente(7L, "João Silva Pereira", "123.456.789-01",
				"jspereira245@outlook.com.br", "91123-0091", "Rua das Flores, 101", "02559-110", StatusCliente.ATIVO);
		ClienteEntity clienteEntity = new ClienteEntity(7L, "João Silva Pereira", "123.456.789-01",
				"jspereira245@outlook.com.br", "91123-0091", "Rua das Flores, 101", "02559-110", StatusCliente.ATIVO);
		Cliente clienteModificado = new Cliente(null, "João S. Pereira", "123.456.789-01",
				"joao.silva123@outlook.com.br", "11 91123-0091", "Rua das Flores, 101", "02559-110");
		
		when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(clienteEntity));
		when(clienteRepository.save(any(ClienteEntity.class))).thenReturn(clienteEntity);
		
		// Act
		Cliente clienteObtido = clienteServiceImpl.atualizarCliente(7L, clienteModificado);
		
		// Assert
		verify(clienteRepository, times(1)).findById(anyLong());
		verify(clienteRepository, times(1)).save(any(ClienteEntity.class));
		assertThat(clienteObtido).isInstanceOf(Cliente.class).isNotNull();
		assertThat(clienteObtido.getId()).isEqualTo(clienteSemMoficacao.getId());
		assertThat(clienteObtido.getNome()).isNotEqualTo(clienteSemMoficacao.getNome());
		assertThat(clienteObtido.getDocumento()).isEqualTo(clienteSemMoficacao.getDocumento());
		assertThat(clienteObtido.getEmail()).isNotEqualTo(clienteSemMoficacao.getEmail());
		assertThat(clienteObtido.getTelefone()).isNotEqualTo(clienteSemMoficacao.getTelefone());
		assertThat(clienteObtido.getEndereco()).isEqualTo(clienteSemMoficacao.getEndereco());
		assertThat(clienteObtido.getCep()).isEqualTo(clienteSemMoficacao.getCep());
		assertThat(clienteObtido.getStatus()).isEqualTo(clienteSemMoficacao.getStatus());
	}
	
	@Test
	void deveGerarExceptionAoAtualizarUmClienteDesativado() {
		// Arrange
		Long clienteId = 7L;
		Cliente clienteModificado = new Cliente(7L, "João", "123.456.789-01",
				"jpsilva123@outlook.com.br", "91123-0091", "Rua das Flores, 101", "02559-110", StatusCliente.DESATIVADO);
		ClienteEntity clienteEntity = new ClienteEntity(7L, "João Silva Pereira", "123.456.789-01",
				"jspereira245@outlook.com.br", "91123-0091", "Rua das Flores, 101", "02559-110", StatusCliente.DESATIVADO);
		
		when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(clienteEntity));
		
		// Act & Assert
	    assertThatThrownBy(() -> clienteServiceImpl.atualizarCliente(clienteId, clienteModificado))
        	.isInstanceOf(IllegalStateException.class)
        	.hasMessage("O cliente informado não está ATIVO.");
	    verify(clienteRepository, times(1)).findById(anyLong());
	    assertThat(clienteEntity.getStatus()).isEqualTo(StatusCliente.DESATIVADO);
	}
	
	@Test
	void deveGerarExceptionAoAtualizarUmClienteInexistente() {
		// Arrange
		Long idInexistente = 113123L;
		Cliente clienteRequest = new Cliente(null, "Maisa Santos", "935.782.990-30",
				"maria.santos@email.com", "92365-4521", "Rua dos Ventos, 23", "04853-190");
		when(clienteRepository.findById(idInexistente)).thenReturn(Optional.empty());
		
		// Act & Assert
		assertThatThrownBy(() -> clienteServiceImpl.atualizarCliente(idInexistente, clienteRequest))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage("Cliente não encontrado.");
		verify(clienteRepository, times(1)).findById(anyLong());
	}
	
	@Test
	void devePermitirDesativarUmCliente() {
		// Arrange
		Long id = 1L;
		ClienteEntity clienteEntity = gerarUmClienteEntity();
		when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteEntity));
		when(clienteRepository.save(any(ClienteEntity.class))).thenReturn(clienteEntity);

		// Act
		clienteServiceImpl.desativarCliente(id);
		
		// Assert
		verify(clienteRepository, times(1)).findById(anyLong());
		verify(clienteRepository, times(1)).save(any(ClienteEntity.class));
		assertThat(clienteEntity.getStatus()).isEqualTo(StatusCliente.DESATIVADO);
	}
	
	@Test
	void deveGerarExceptionAoDesativarClienteJaDesativado() {
		// Arrange
		Long id = 1L;
		ClienteEntity clienteEntity = gerarUmClienteEntity();
		clienteEntity.setStatus(StatusCliente.DESATIVADO);
		when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteEntity));

		// Act & Assert
		assertThatThrownBy(() -> clienteServiceImpl.desativarCliente(id))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("O cliente já está DESATIVADO.");
		verify(clienteRepository, times(1)).findById(anyLong());
		verify(clienteRepository, times(0)).save(any(ClienteEntity.class));
		assertThat(clienteEntity.getStatus()).isEqualTo(StatusCliente.DESATIVADO);
	}
	
	@Test
	void devePermitirAtivarUmCliente() {
		// Arrange
		Long id = 1L;
		ClienteEntity clienteEntity = gerarUmClienteEntity();
		clienteEntity.setStatus(StatusCliente.DESATIVADO);
		when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteEntity));
		when(clienteRepository.save(any(ClienteEntity.class))).thenReturn(clienteEntity);

		// Act
		clienteServiceImpl.ativarCliente(id);
		
		// Assert
		verify(clienteRepository, times(1)).findById(anyLong());
		verify(clienteRepository, times(1)).save(any(ClienteEntity.class));
		assertThat(clienteEntity.getStatus()).isEqualTo(StatusCliente.ATIVO);
	}
	
	@Test
	void deveGerarExceptionAoAtivarClienteJaAtivo() {
		// Arrange
		Long id = 1L;
		ClienteEntity cliente = gerarUmClienteEntity();
		when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

		// Act & Assert
		assertThatThrownBy(() -> clienteServiceImpl.ativarCliente(id))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("O cliente já está ATIVO.");
		verify(clienteRepository, times(1)).findById(anyLong());
		verify(clienteRepository, times(0)).save(any(ClienteEntity.class));
		assertThat(cliente.getStatus()).isEqualTo(StatusCliente.ATIVO);
	}
	

	
	private List<ClienteEntity> gerarListaDeClienteEntity() {
		
		return  Arrays.asList (
				new ClienteEntity(1L, "João Silva", "355.347.740-70", "joao.silva@email.com",
						"98765-4321", "Rua das Flores, 101", "08295-100", StatusCliente.ATIVO),
				new ClienteEntity(2L, "Maisa Santos", "935.782.990-30", "maria.santos@email.com",
						"92365-4521", "Rua dos Ventos, 23", "03414-180", StatusCliente.ATIVO),
				new ClienteEntity(3L, "Carlos Oliveira", "123.456.789-00", "carlos.oliveira@email.com", 
			            "91928-1234", "Avenida Brasil, 11", "02314-090", StatusCliente.ATIVO)
				);
	}
	
	private ClienteEntity gerarUmClienteEntity() {
		return new ClienteEntity(7L, "João Silva", "123.456.789-01", "joao.silva@email.com",
				"98765-4321", "Rua das Flores, 101", "08470-093", StatusCliente.ATIVO);
	}
	
	private Cliente gerarUmCliente() {
		return new Cliente(null, "João Silva", "123.456.789-01", "joao.silva@email.com",
				"98765-4321", "Rua das Flores, 101", "08470-093");
	}
 }
