package br.com.fiap.cliente.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.cliente.model.entity.ClienteEntity;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {
	
}