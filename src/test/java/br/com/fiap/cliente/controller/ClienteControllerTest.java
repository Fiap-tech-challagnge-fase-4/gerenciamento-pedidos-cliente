package br.com.fiap.cliente.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.cliente.enums.StatusCliente;
import br.com.fiap.cliente.mapper.ClienteMapper;
import br.com.fiap.cliente.model.Cliente;
import br.com.fiap.cliente.model.dto.ClienteRequestDTO;
import br.com.fiap.cliente.service.ClienteService;

class ClienteControllerTest {

	private MockMvc mockMvc;

	private AutoCloseable openMocks;

	@Mock
	private ClienteService clienteService;
	

	private ClienteController clienteController;

	@BeforeEach
	void setup() {
		openMocks = MockitoAnnotations.openMocks(this);
		
		ClienteMapper clienteMapper = new ClienteMapper();
		clienteController = new ClienteController(clienteService, clienteMapper);

		mockMvc = MockMvcBuilders.standaloneSetup(clienteController).build();
	}

	@AfterEach
	void teardown() throws Exception {
		openMocks.close();
	}

	@Test
	void devePermitirListarClientes() throws Exception {
		// Arrange
		List<Cliente> clientes = Arrays.asList(
				new Cliente(1L, "João", "12345678901", "joao@email.com", "123456789", "Rua A, 123", "49045-190",
						StatusCliente.ATIVO),
				new Cliente(2L, "Maria", "98765432100", "maria@email.com", "987654321", "Rua B, 456", "49045-190",
						StatusCliente.ATIVO));
		when(clienteService.listarClientes()).thenReturn(clientes);

		// Act & Assert
		mockMvc.perform(get("/api/clientes"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].nome").value("João"))
			.andExpect(jsonPath("$[1].nome").value("Maria"));
	}

	@Test
	void devePermitirRegistrarUmCliente() throws Exception {
		// Arrange
		ClienteRequestDTO request = gerarUmClienteRequestDTO();
		Cliente response = gerarUmCliente();

		when(clienteService.criarCliente(any(Cliente.class))).thenReturn(response);

		// Act & Assert
		mockMvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(asJsonString(request)))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.idCliente").value(1L))
				.andExpect(jsonPath("$.nome").value("João Silva"))
				.andExpect(jsonPath("$.documento").value("123.456.789-01"))
				.andExpect(jsonPath("$.email").value("joao.silva@email.com"))
				.andExpect(jsonPath("$.telefone").value("98765-4321"))
				.andExpect(jsonPath("$.endereco").value("Rua das Flores, 101"))
				.andExpect(jsonPath("$.cep").value("49045-190"))
				.andExpect(jsonPath("$.status").value(StatusCliente.ATIVO.getDescricao()));
	}

	@Test
	void devePermitirListarUmClientePorId() throws Exception {
		// Arrange
		Long id = 1L;
		Cliente response = gerarUmCliente();
		when(clienteService.obterPorId(anyLong())).thenReturn(response);

		// Act & Assert
		mockMvc.perform(
				get("/api/clientes/{id}", id).contentType(MediaType.APPLICATION_JSON).content(asJsonString(response)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.idCliente").value(1L))
				.andExpect(jsonPath("$.nome").value("João Silva"))
				.andExpect(jsonPath("$.documento").value("123.456.789-01"))
				.andExpect(jsonPath("$.email").value("joao.silva@email.com"))
				.andExpect(jsonPath("$.telefone").value("98765-4321"))
				.andExpect(jsonPath("$.endereco").value("Rua das Flores, 101"))
				.andExpect(jsonPath("$.cep").value("49045-190"))
				.andExpect(jsonPath("$.status").value(StatusCliente.ATIVO.getDescricao()));
	}

	@Test
	void devePermitirAtualizarUmCliente() throws Exception {
		// Arrange
		Long id = 1L;
		ClienteRequestDTO requestModificado = gerarUmClienteRequestDTOModificado();
		Cliente responseAtualizado = gerarUmClienteAtualizado();
		when(clienteService.atualizarCliente(anyLong(), any(Cliente.class))).thenReturn(responseAtualizado);

		// Act & Assert
        mockMvc.perform(put("/api/clientes/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestModificado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCliente").value(1L))
				.andExpect(jsonPath("$.nome").value("João Pereira Silva"))
				.andExpect(jsonPath("$.documento").value("123.456.789-01"))
				.andExpect(jsonPath("$.email").value("joao.silva123@email.com"))
				.andExpect(jsonPath("$.telefone").value("11 98765-4321"))
				.andExpect(jsonPath("$.endereco").value("Rua das Flores, 103"))
				.andExpect(jsonPath("$.status").value(StatusCliente.ATIVO.getDescricao()));
	}
	
	@Test
	void devePermitirDesativarUmCliente() throws Exception {
		// Arrange
		Long id = 1L;
        doNothing().when(clienteService).desativarCliente(id);

		// Act & Assert
        mockMvc.perform(patch("/api/clientes/desativar/{id}", id))
                .andExpect(status().isOk());
	}
	
	@Test
	void devePermitirAtivarUmCliente() throws Exception {
		// Arrange
		Long id = 1L;
        doNothing().when(clienteService).ativarCliente(id);

		// Act & Assert
        mockMvc.perform(patch("/api/clientes/ativar/{id}", id))
                .andExpect(status().isOk());
	}

	public static String asJsonString(final Object object) {
		try {
			return new ObjectMapper().writeValueAsString(object);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	private ClienteRequestDTO gerarUmClienteRequestDTO() {
		return new ClienteRequestDTO("João Silva", "123.456.789-01", "joao.silva@email.com", "98765-4321",
				"Rua das Flores, 101", "49045-190");
	}
	
	private Cliente gerarUmCliente() {
		return new Cliente(1L, "João Silva", "123.456.789-01", "joao.silva@email.com", "98765-4321",
				"Rua das Flores, 101", "49045-190", StatusCliente.ATIVO);
	}
	
	private ClienteRequestDTO gerarUmClienteRequestDTOModificado() {
		return new ClienteRequestDTO("João Pereira Silva", "123.456.789-01", "joao.silva123@email.com", "11 98765-4321",
				"Rua das Flores, 103", "49045-190");
	}
	
	private Cliente gerarUmClienteAtualizado() {
		return new Cliente(1L, "João Pereira Silva", "123.456.789-01", "joao.silva123@email.com", "11 98765-4321",
				"Rua das Flores, 103", "49045-190", StatusCliente.ATIVO);
	}

}
