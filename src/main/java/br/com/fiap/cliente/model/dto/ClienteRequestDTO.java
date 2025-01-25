package br.com.fiap.cliente.model.dto;

public record ClienteRequestDTO (
	    String nome,
	    String documento,
	    String email,
	    String telefone,
	    String endereco
) {}
