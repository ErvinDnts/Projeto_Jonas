package br.com.projeto.jonas.domain.entity;

import br.com.projeto.jonas.domain.enums.*;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class ItemPedido {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(nullable=false) private Pedido pedido;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(nullable=false) private Produto produto;
    @Column(nullable=false) private Integer quantidade;
    @Column(nullable=false,precision=12,scale=2) private BigDecimal precoUnitario;
    private String personalizacao;
    @Column(nullable=false) private String material;
    @OneToOne(mappedBy="itemPedido",cascade=CascadeType.ALL,orphanRemoval=true,fetch=FetchType.LAZY) private Arte arte;
    protected ItemPedido() {}
    ItemPedido(Pedido pedido,Produto produto,Integer quantidade,String personalizacao,String material){this.pedido=pedido;this.produto=produto;this.precoUnitario=Produto.preco(produto.getPrecoBase());if(quantidade==null||quantidade<=0)throw new IllegalArgumentException("quantidade deve ser positiva");this.quantidade=quantidade;this.personalizacao=personalizacao;this.material=obrigatorio(material);}
    public void alterarQuantidade(Colaborador executor,Integer q){Colaborador.exigir(executor,PerfilColaborador.ATENDIMENTO);pedido.exigirRascunho();if(q==null||q<=0)throw new IllegalArgumentException("quantidade deve ser positiva");quantidade=q;}
    public void alterarPersonalizacao(Colaborador executor,String p){Colaborador.exigir(executor,PerfilColaborador.ATENDIMENTO);pedido.exigirRascunho();personalizacao=p;}
    public void alterarMaterial(Colaborador executor,String m){Colaborador.exigir(executor,PerfilColaborador.ATENDIMENTO);pedido.exigirRascunho();material=obrigatorio(m);}
    public Arte anexarArte(Colaborador executor,String referencia,String descricao){Colaborador.exigir(executor,PerfilColaborador.CRIACAO_ARTE);pedido.exigirStatus(StatusPedido.CONFIRMADO);if(pedido.getTotalPago().compareTo(pedido.getSinalMinimo())<0)throw new IllegalStateException("Sinal minimo nao recebido");if(arte==null)arte=new Arte(this,referencia,descricao,executor);else arte.atualizarArte(executor,referencia,descricao);return arte;}
    public BigDecimal getSubtotal(){return precoUnitario.multiply(BigDecimal.valueOf(quantidade)).setScale(2,java.math.RoundingMode.HALF_UP);}
    private static String obrigatorio(String s){if(s==null||s.isBlank())throw new IllegalArgumentException("material obrigatorio");return s.trim();}
    public Long getId(){return id;} public Pedido getPedido(){return pedido;} public Produto getProduto(){return produto;} public Integer getQuantidade(){return quantidade;} public BigDecimal getPrecoUnitario(){return precoUnitario;} public String getPersonalizacao(){return personalizacao;} public String getMaterial(){return material;} public Arte getArte(){return arte;}
}
