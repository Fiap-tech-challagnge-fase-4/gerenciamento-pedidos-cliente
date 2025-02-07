package br.com.fiap.cliente.model.entity;

import br.com.fiap.cliente.enums.StatusCliente;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String documento;
    private String email;
    private String telefone;
    private String endereco;
    private String cep;
    private StatusCliente status;
    
    public boolean isAtivo() {
        return StatusCliente.ATIVO.equals(this.status);
    }
    
    public boolean isDesativado() {
        return StatusCliente.DESATIVADO.equals(this.status);
    }
}
