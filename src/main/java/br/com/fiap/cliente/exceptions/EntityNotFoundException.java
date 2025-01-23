package br.com.fiap.cliente.exceptions;

public class EntityNotFoundException extends RuntimeException {
	
    public EntityNotFoundException(){
        super();
    }
    public EntityNotFoundException(String mensagem){
        super(mensagem);
    }
}
