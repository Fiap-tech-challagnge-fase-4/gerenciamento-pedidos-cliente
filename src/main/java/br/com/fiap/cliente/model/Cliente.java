package br.com.fiap.cliente.model;

import br.com.fiap.cliente.enums.StatusCliente;
import lombok.Getter;

@Getter
public class Cliente {

    private Long id;
    private String nome;
    private String documento;
    private String email;
    private String telefone;
    private String endereco;
    private String cep;
    private StatusCliente status;
    
    public Cliente(Long id, String nome, String documento, String email, String telefone, String endereco, String cep) {
        this.id = id;
        this.nome = nome;
        this.documento = documento;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
        this.cep = cep;
        this.status = StatusCliente.ATIVO;
    }
    
    public Cliente(Long id, String nome, String documento, String email, String telefone, String endereco, String cep, StatusCliente status) {
        this.id = id;
        this.nome = nome;
        this.documento = documento;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
        this.status = status;
        this.cep = cep;
    }
    
    public boolean isAtivo() {
        return StatusCliente.ATIVO.equals(this.status);
    }
    
    public boolean isDesativado() {
        return StatusCliente.DESATIVADO.equals(this.status);
    }
}
