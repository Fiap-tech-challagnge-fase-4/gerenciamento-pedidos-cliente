package br.com.fiap.cliente.model.dto;

public record ClienteRequestDTO (
		//@NotNull(message = "Atributo nome é obrigatório e não pode estar em branco.")
		//@NotBlank(message = "Atributo nome não pode estar em branco")
	    String nome,
		//@NotNull(message = "Atributo documento é obrigatório e não pode estar em branco.")
		//@NotBlank(message = "Atributo documento não pode estar em branco")
	    String documento,
	    String email,
		//@NotNull(message = "Atributo telefone é obrigatório e não pode estar em branco.")
		//@NotBlank(message = "Atributo telefone não pode estar em branco")
	    String telefone,
		//@NotNull(message = "Atributo endereco é obrigatório e não pode estar em branco.")
		//@NotBlank(message = "Atributo endereco não pode estar em branco")
	    String endereco
) {}
