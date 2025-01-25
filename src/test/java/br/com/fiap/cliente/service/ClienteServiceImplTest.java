package br.com.fiap.cliente.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
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
import org.springframework.test.web.servlet.MockMvc;

import br.com.fiap.cliente.enums.StatusCliente;
import br.com.fiap.cliente.exceptions.EntityNotFoundException;
import br.com.fiap.cliente.model.Cliente;
import br.com.fiap.cliente.model.dto.ClienteRequestDTO;
import br.com.fiap.cliente.model.dto.ClienteResponseDTO;
import br.com.fiap.cliente.repository.ClienteRepository;
import br.com.fiap.cliente.service.impl.ClienteServiceImpl;

class ClienteServiceImplTest {
	
    @Mock
    private ClienteRepository clienteRepository;
    
    private ClienteServiceImpl clienteServiceImpl;
    
    private MockMvc mockMvc;

    private AutoCloseable openMocks;
    
	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		clienteServiceImpl = new ClienteServiceImpl(clienteRepository);
	}
	@AfterEach
	void teardown() throws Exception {
		openMocks.close();
	}
	
	@Test
	void devePermitirListarClientes() {
		// Arrange
		List<Cliente> listaClientes = gerarClientes();
		when(clienteRepository.findAll()).thenReturn(listaClientes);
		
		// Act
		List<ClienteResponseDTO> listaObtida = clienteServiceImpl.listarClientes();
		
		// Assert
		verify(clienteRepository, times(1)).findAll();
		assertThat(listaObtida).hasSize(3);
		assertThat(listaObtida).allSatisfy( cliente -> {
			assertThat(cliente).isNotNull().isInstanceOf(ClienteResponseDTO.class);
		});
	}
	
	@Test
	void devePermitirRegistrarUmCliente() {
		// Arrange
		ClienteRequestDTO clienteRequest = gerarUmClienteRequestDTO();
		Cliente cliente = gerarUmCliente();
		when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        
		// Act
		ClienteResponseDTO clienteResponse = clienteServiceImpl.criarCliente(clienteRequest);
		
		// Assert
		verify(clienteRepository, times(1)).save(any(Cliente.class));
		assertThat(clienteResponse).isInstanceOf(ClienteResponseDTO.class).isNotNull();
		assertThat(clienteRequest.nome()).isEqualTo(clienteResponse.nome());
		assertThat(clienteRequest.documento()).isEqualTo(clienteResponse.documento());
		assertThat(clienteRequest.email()).isEqualTo(clienteResponse.email());
		assertThat(clienteRequest.telefone()).isEqualTo(clienteResponse.telefone());
		assertThat(clienteRequest.endereco()).isEqualTo(clienteResponse.endereco());
		assertThat(clienteResponse.status()).isEqualTo(StatusCliente.ATIVO);
	}
	
	@Test
	void devePermitirListarUmClientePorId() {
		// Arrange
		Cliente cliente = gerarUmCliente();
		when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));
		
		// Act
		ClienteResponseDTO clienteObtido = clienteServiceImpl.obterPorId(cliente.getId());
		
		// Assert
		verify(clienteRepository, times(1)).findById(anyLong());
		assertThat(clienteObtido).isInstanceOf(ClienteResponseDTO.class).isNotNull();
		assertThat(clienteObtido.idCliente()).isEqualTo(cliente.getId());
		assertThat(clienteObtido.nome()).isEqualTo(cliente.getNome());
		assertThat(clienteObtido.documento()).isEqualTo(cliente.getDocumento());
		assertThat(clienteObtido.email()).isEqualTo(cliente.getEmail());
		assertThat(clienteObtido.telefone()).isEqualTo(cliente.getTelefone());
		assertThat(clienteObtido.endereco()).isEqualTo(cliente.getEndereco());
		assertThat(clienteObtido.status()).isEqualTo(cliente.getStatus());
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
		Cliente clienteSemMoficacao = gerarUmCliente();
		Cliente cliente = gerarUmCliente();
		ClienteRequestDTO clienteModificado = new ClienteRequestDTO("João Silva Pereira", "123.456.789-01",
				"jspereira245@outlook.com.br", "91123-0091", "Rua das Flores, 101");
		
		when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));
		when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
		
		// Act
		ClienteResponseDTO clienteObtido = clienteServiceImpl.atualizarCliente(7L, clienteModificado);
		
		// Assert
		verify(clienteRepository, times(1)).findById(anyLong());
		verify(clienteRepository, times(1)).save(any(Cliente.class));
		assertThat(clienteObtido).isInstanceOf(ClienteResponseDTO.class).isNotNull();
		assertThat(clienteObtido.idCliente()).isEqualTo(clienteSemMoficacao.getId());
		assertThat(clienteObtido.nome()).isNotEqualTo(clienteSemMoficacao.getNome());
		assertThat(clienteObtido.documento()).isEqualTo(clienteSemMoficacao.getDocumento());
		assertThat(clienteObtido.email()).isNotEqualTo(clienteSemMoficacao.getEmail());
		assertThat(clienteObtido.telefone()).isNotEqualTo(clienteSemMoficacao.getTelefone());
		assertThat(clienteObtido.endereco()).isEqualTo(clienteSemMoficacao.getEndereco());
		assertThat(clienteObtido.status()).isEqualTo(clienteSemMoficacao.getStatus());
	}
	
	@Test
	void deveGerarExceptionAoAtualizarUmClienteInexistente() {
		// Arrange
		Long idInexistente = 113123L;
		ClienteRequestDTO clienteRequest = new ClienteRequestDTO("Maisa Santos", "935.782.990-30",
				"maria.santos@email.com", "92365-4521", "Rua dos Ventos, 23");
		when(clienteRepository.findById(idInexistente)).thenReturn(Optional.empty());
		
		// Act & Assert
		assertThatThrownBy(() -> clienteServiceImpl.atualizarCliente(idInexistente, clienteRequest))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage("Cliente não encontrado.");
		verify(clienteRepository, times(1)).findById(anyLong());
	}
	
	@Test
	void devePermitirExcluirUmCliente() {
		// Arrange
		Long idInexistente = 113123L;
		when(clienteRepository.existsById(anyLong())).thenReturn(true);
		doNothing().when(clienteRepository).deleteById(anyLong());
		
		// Act
		clienteServiceImpl.excluirCliente(idInexistente);
		
		// Assert
		verify(clienteRepository, times(1)).existsById(anyLong());
		verify(clienteRepository, times(1)).deleteById(anyLong());
	}
	
	@Test
	void deveGerarExceptionAoExcluirUmClienteInexistente() {
		// Arrange
		Long idInexistente = 113123L;
		when(clienteRepository.existsById(anyLong())).thenReturn(false);
		
		// Act & Assert
		assertThatThrownBy(() -> clienteServiceImpl.excluirCliente(idInexistente))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage("Cliente não encontrado.");
	}
	
	private Cliente gerarUmCliente() {
		return new Cliente(7L, "João Silva", "123.456.789-01", "joao.silva@email.com",
				"98765-4321", "Rua das Flores, 101", StatusCliente.ATIVO);
	}
	
	private List<Cliente> gerarClientes() {
		
		return  Arrays.asList (
				new Cliente(1L, "João Silva", "355.347.740-70", "joao.silva@email.com",
						"98765-4321", "Rua das Flores, 101", StatusCliente.ATIVO),
				new Cliente(2L, "Maisa Santos", "935.782.990-30", "maria.santos@email.com",
						"92365-4521", "Rua dos Ventos, 23", StatusCliente.ATIVO),
				new Cliente(3L, "Carlos Oliveira", "123.456.789-00", "carlos.oliveira@email.com", 
			            "91928-1234", "Avenida Brasil, 11", StatusCliente.ATIVO)
				);
	}
	
	private ClienteRequestDTO gerarUmClienteRequestDTO() {
		return new ClienteRequestDTO("João Silva", "123.456.789-01", "joao.silva@email.com",
				"98765-4321", "Rua das Flores, 101");
	}
 }
