package br.com.projeto.jonas.domain.entity;

import br.com.projeto.jonas.domain.enums.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Pagamento {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(nullable=false) private Pedido pedido;
    @Column(nullable=false,precision=12,scale=2) private BigDecimal valor;
    @Column(nullable=false) private LocalDateTime registradoEm;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private TipoPagamento tipo;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private FormaPagamento forma;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(nullable=false) private Colaborador confirmadoPor;
    protected Pagamento() {}
    Pagamento(Pedido pedido,BigDecimal valor,TipoPagamento tipo,FormaPagamento forma,Colaborador executor){this.pedido=pedido;this.valor=Produto.preco(valor);this.tipo=java.util.Objects.requireNonNull(tipo);this.forma=java.util.Objects.requireNonNull(forma);this.confirmadoPor=executor;this.registradoEm=LocalDateTime.now();}
    public Long getId(){return id;} public Pedido getPedido(){return pedido;} public BigDecimal getValor(){return valor;} public LocalDateTime getRegistradoEm(){return registradoEm;} public TipoPagamento getTipo(){return tipo;} public FormaPagamento getForma(){return forma;} public Colaborador getConfirmadoPor(){return confirmadoPor;}
}
