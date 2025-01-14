package br.com.fiap.cliente.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.cliente.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
}

