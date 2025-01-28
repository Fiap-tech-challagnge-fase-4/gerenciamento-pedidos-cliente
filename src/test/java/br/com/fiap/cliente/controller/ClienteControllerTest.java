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
import br.com.fiap.cliente.model.dto.ClienteRequestDTO;
import br.com.fiap.cliente.model.dto.ClienteResponseDTO;
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
		clienteController = new ClienteController(clienteService);

		mockMvc = MockMvcBuilders.standaloneSetup(clienteController).build();
	}

	@AfterEach
	void teardown() throws Exception {
		openMocks.close();
	}

	@Test
	void devePermitirListarClientes() throws Exception {
		// Arrange
		List<ClienteResponseDTO> clientes = Arrays.asList(
				new ClienteResponseDTO(1L, "João", "12345678901", "joao@email.com", "123456789", "Rua A, 123",
						StatusCliente.ATIVO),
				new ClienteResponseDTO(2L, "Maria", "98765432100", "maria@email.com", "987654321", "Rua B, 456",
						StatusCliente.ATIVO));
		when(clienteService.listarClientes()).thenReturn(clientes);

		// Act & Assert
		mockMvc.perform(get("/api/clientes")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].nome").value("João")).andExpect(jsonPath("$[1].nome").value("Maria"));
	}

	@Test
	void devePermitirRegistrarUmCliente() throws Exception {
		// Arrange
		ClienteRequestDTO request = gerarUmClienteRequestDTO();
		ClienteResponseDTO response = gerarUmClienteResponseDTO();

		when(clienteService.criarCliente(any(ClienteRequestDTO.class))).thenReturn(response);

		// Act & Assert
		mockMvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(asJsonString(request)))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.idCliente").value(1L))
				.andExpect(jsonPath("$.nome").value("João Silva"))
				.andExpect(jsonPath("$.documento").value("123.456.789-01"))
				.andExpect(jsonPath("$.email").value("joao.silva@email.com"))
				.andExpect(jsonPath("$.telefone").value("98765-4321"))
				.andExpect(jsonPath("$.endereco").value("Rua das Flores, 101"))
				.andExpect(jsonPath("$.status").value(StatusCliente.ATIVO.getDescricao()));
	}

	@Test
	void devePermitirListarUmClientePorId() throws Exception {
		// Arrange
		Long id = 1L;
		ClienteResponseDTO response = gerarUmClienteResponseDTO();
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
				.andExpect(jsonPath("$.status").value(StatusCliente.ATIVO.getDescricao()));
	}

	@Test
	void devePermitirAtualizarUmCliente() throws Exception {
		// Arrange
		Long id = 1L;
		ClienteRequestDTO requestModificado = gerarUmClienteRequestDTO();
		ClienteResponseDTO responseAtualizado = gerarUmClienteResponseDTO();
		when(clienteService.atualizarCliente(anyLong(), any(ClienteRequestDTO.class))).thenReturn(responseAtualizado);

		// Act & Assert
        mockMvc.perform(put("/api/clientes/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestModificado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCliente").value(1L))
				.andExpect(jsonPath("$.nome").value("João Silva"))
				.andExpect(jsonPath("$.documento").value("123.456.789-01"))
				.andExpect(jsonPath("$.email").value("joao.silva@email.com"))
				.andExpect(jsonPath("$.telefone").value("98765-4321"))
				.andExpect(jsonPath("$.endereco").value("Rua das Flores, 101"))
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
				"Rua das Flores, 101");
	}

	private ClienteResponseDTO gerarUmClienteResponseDTO() {
		return new ClienteResponseDTO(1L, "João Silva", "123.456.789-01", "joao.silva@email.com", "98765-4321",
				"Rua das Flores, 101", StatusCliente.ATIVO);
	}

}
