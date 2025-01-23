package br.com.fiap.cliente.enums;

public enum StatusCliente {

    ATIVO("ATIVO"),
    DESATIVADO("DESATIVADO");
    
    private final String descricao;

	StatusCliente(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}