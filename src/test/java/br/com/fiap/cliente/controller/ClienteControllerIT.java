package br.com.fiap.cliente.controller;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import br.com.fiap.cliente.enums.StatusCliente;
import br.com.fiap.cliente.model.Cliente;
import br.com.fiap.cliente.model.dto.ClienteRequestDTO;
import br.com.fiap.cliente.repository.ClienteRepository;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClienteControllerIT {

	@LocalServerPort
	private int port;
    
    @Autowired
    private ClienteRepository clienteRepository;

	@BeforeEach
	public void setup() {
	    RestAssured.port = port;
	    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
	}

	@Test
	void devePermitirRegistrarUmCliente() {
		// Arrange
		ClienteRequestDTO requestCliente = gerarUmClienteRequestDTO();

		// Act & Assert
		given().filter(new AllureRestAssured()).contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(requestCliente)
			.when().post("/api/clientes")
			.then().statusCode(HttpStatus.CREATED.value())
			.body(matchesJsonSchemaInClasspath("./schemas/ClienteSchema.json"))
			.body("$", hasKey("idCliente"))
			.body("$", hasKey("nome"))
			.body("$", hasKey("documento"))
			.body("$", hasKey("email"))
			.body("$", hasKey("telefone"))
			.body("$", hasKey("endereco"))
			.body("$", hasKey("status"))
			.body("idCliente", greaterThan(0));
	}
	
	@Test
	void devePermitirListarUmClientePorId() {
		// Arrange
		Cliente cliente =  clienteRepository.save(new Cliente(1L, "João Silva", "355.347.740-70", "joao.silva@email.com",
				"98765-4321", "Rua das Flores, 101", StatusCliente.ATIVO));

		// Act & Assert
		given().filter(new AllureRestAssured())
	          .contentType(MediaType.APPLICATION_JSON_VALUE)
	          .when().get("/api/clientes/{id}", cliente.getId())
	          .then().statusCode(HttpStatus.OK.value())
	          .body(matchesJsonSchemaInClasspath("./schemas/ClienteSchema.json"));
	}
	
	@Test
	void devePermitirListarClientes() {
		// Arrange
		clienteRepository.save(new Cliente(1L, "João Silva", "355.347.740-70", "joao.silva@email.com", "98765-4321",
				"Rua das Flores, 101", StatusCliente.ATIVO));
		clienteRepository.save(new Cliente(2L, "Maisa Santos", "935.782.990-30", "maria.santos@email.com", "92365-4521",
				"Rua dos Ventos, 23", StatusCliente.ATIVO));

		// Act & Assert
		given().filter(new AllureRestAssured())
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/api/clientes")
			.then().statusCode(HttpStatus.OK.value())
			.body(matchesJsonSchemaInClasspath("./schemas/ClienteSchema.json"));
	}
	
	@Test
	void devePermitirAtualizarUmCliente() {
		// Arrange
		Cliente cliente =  clienteRepository.save(new Cliente(null, "João Silva", "355.347.740-70", "joao.silva@email.com",
				"98765-4321", "Rua das Flores, 101", StatusCliente.ATIVO));
		cliente.setEndereco("Rua do Teste, 123");
		cliente.setEmail("email@teste.com.br");
		
		ClienteRequestDTO clienteAtualizadoRequest = new ClienteRequestDTO(cliente.getNome(), cliente.getDocumento(),
				cliente.getEmail(), cliente.getTelefone(), cliente.getEndereco());

		// Act & Assert
		given().filter(new AllureRestAssured())
			.contentType(MediaType.APPLICATION_JSON_VALUE).body(clienteAtualizadoRequest)
			.when().put("/api/clientes/{id}", cliente.getId())
			.then().statusCode(HttpStatus.OK.value())
			.body(matchesJsonSchemaInClasspath("./schemas/ClienteSchema.json"));
	}
	
	@Test
	void devePermitirExcluirUmCliente() {
		// Arrange
		Cliente cliente =  clienteRepository.save(new Cliente(null, "João Silva Pereira", "355.347.740-70", "joao.silva.pereira@email.com",
				"98765-4321", "Rua das Flores, 101", StatusCliente.ATIVO));

		// Act & Assert
		given().filter(new AllureRestAssured())
		.contentType(MediaType.APPLICATION_JSON_VALUE)
		.when().delete("/api/clientes/{id}", cliente.getId())
		.then().statusCode(HttpStatus.NO_CONTENT.value());
	}

	private ClienteRequestDTO gerarUmClienteRequestDTO() {
		return new ClienteRequestDTO("João Silva", "123.456.789-01", "joao.silva@email.com", "98765-4321",
				"Rua das Flores, 101");
	}

}
