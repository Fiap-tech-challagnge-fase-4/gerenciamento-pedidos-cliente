package br.com.fiap.cliente.model.dto;

import br.com.fiap.cliente.enums.StatusCliente;

public record ClienteResponseDTO (
		
		Long idCliente,
		String nome,
		String documento,
		String email,
		String telefone,
		String endereco,
		String cep,
		StatusCliente status
) {}
