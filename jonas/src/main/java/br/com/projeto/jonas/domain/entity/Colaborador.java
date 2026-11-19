package br.com.projeto.jonas.domain.entity;

import br.com.projeto.jonas.domain.enums.PerfilColaborador;
import jakarta.persistence.*;

@Entity
public class Colaborador {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String nome;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private PerfilColaborador perfil;
    private boolean ativo;
    protected Colaborador() {}
    public Colaborador(String nome, PerfilColaborador perfil) { this.nome=obrigatorio(nome); this.perfil=java.util.Objects.requireNonNull(perfil); this.ativo=true; }
    public void alterarPerfil(Colaborador executor, PerfilColaborador perfil) { exigir(executor, PerfilColaborador.FINANCEIRO_GESTAO); this.perfil=java.util.Objects.requireNonNull(perfil); }
    public void alterarNome(Colaborador executor, String nome) { exigir(executor, PerfilColaborador.FINANCEIRO_GESTAO); this.nome=obrigatorio(nome); }
    public void desativar(Colaborador executor) { exigir(executor, PerfilColaborador.FINANCEIRO_GESTAO); ativo=false; }
    public void ativar(Colaborador executor) { exigir(executor, PerfilColaborador.FINANCEIRO_GESTAO); ativo=true; }
    public static void exigir(Colaborador c, PerfilColaborador p) { if(c==null || !c.ativo || c.perfil!=p) throw new IllegalStateException("Colaborador ativo com perfil " + p + " obrigatorio"); }
    private static String obrigatorio(String s){if(s==null||s.isBlank())throw new IllegalArgumentException("nome obrigatorio");return s.trim();}
    public Long getId(){return id;} public String getNome(){return nome;} public PerfilColaborador getPerfil(){return perfil;} public boolean isAtivo(){return ativo;}
}
