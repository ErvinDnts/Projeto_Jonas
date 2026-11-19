package br.com.projeto.jonas.domain.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String nome;
    @Column(nullable=false) private String telefone;
    private String email;
    protected Cliente() {}
    public Cliente(String nome, String telefone, String email) { atualizarContato(nome, telefone, email); }
    public void atualizarContato(String nome, String telefone, String email) {
        this.nome = textoObrigatorio(nome, "nome"); this.telefone = textoObrigatorio(telefone, "telefone");
        this.email = email == null || email.isBlank() ? null : email.trim();
    }
    private static String textoObrigatorio(String s, String campo) { if (s == null || s.isBlank()) throw new IllegalArgumentException(campo + " obrigatorio"); return s.trim(); }
    public Long getId(){return id;} public String getNome(){return nome;} public String getTelefone(){return telefone;} public String getEmail(){return email;}
}
