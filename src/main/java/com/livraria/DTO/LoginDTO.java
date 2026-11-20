package com.livraria.DTO;

public class LoginDTO {
    private String identificador; // Aceita CPF ou E-mail
    private String email;
    private String senha;

    public String getIdentificador() {
        return (identificador != null && !identificador.isBlank()) ? identificador : email;
    }

    public void setIdentificador(String identificador) { this.identificador = identificador; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}