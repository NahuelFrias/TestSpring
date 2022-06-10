package com.example.nahuel.test.springboot.app.exceptions;

public class DineroInsuficienteException extends RuntimeException{
    public DineroInsuficienteException(String message) {
        super(message); //constructor que pase el mensaje de la exception
    }
}
