package br.com.projeto.jonas.domain.entity;

import br.com.projeto.jonas.domain.enums.PerfilColaborador;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Produto {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String nome;
    private String descricao;
    @Column(nullable=false, precision=12, scale=2) private BigDecimal precoBase;
    private boolean ativo;
    protected Produto() {}
    public Produto(String nome,String descricao,BigDecimal precoBase){this.nome=obrigatorio(nome);this.descricao=descricao;this.precoBase=preco(precoBase);this.ativo=true;}
    public void alterarPrecoBase(Colaborador executor,BigDecimal preco){Colaborador.exigir(executor,PerfilColaborador.FINANCEIRO_GESTAO);this.precoBase=preco(preco);}
    public void atualizar(Colaborador executor,String nome,String descricao){Colaborador.exigir(executor,PerfilColaborador.FINANCEIRO_GESTAO);this.nome=obrigatorio(nome);this.descricao=descricao;}
    public void desativar(Colaborador executor){Colaborador.exigir(executor,PerfilColaborador.FINANCEIRO_GESTAO);ativo=false;}
    private static String obrigatorio(String s){if(s==null||s.isBlank())throw new IllegalArgumentException("nome obrigatorio");return s.trim();}
    static BigDecimal preco(BigDecimal v){if(v==null||v.signum()<=0)throw new IllegalArgumentException("preco deve ser positivo");try{return v.setScale(2,java.math.RoundingMode.UNNECESSARY);}catch(ArithmeticException e){throw new IllegalArgumentException("preco deve ter no maximo duas casas decimais",e);}}
    public Long getId(){return id;} public String getNome(){return nome;} public String getDescricao(){return descricao;} public BigDecimal getPrecoBase(){return precoBase;} public boolean isAtivo(){return ativo;}
}
