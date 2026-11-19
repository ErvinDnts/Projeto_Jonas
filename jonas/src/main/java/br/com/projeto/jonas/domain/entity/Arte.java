package br.com.projeto.jonas.domain.entity;

import br.com.projeto.jonas.domain.enums.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Arte {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(nullable=false,unique=true) private ItemPedido itemPedido;
    private String referenciaArquivo;
    @Column(length=2000) private String descricao;
    private boolean aprovada;
    private LocalDateTime aprovadaEm;
    @ManyToOne(fetch=FetchType.LAZY) private Colaborador atualizadaPor;
    @ManyToOne(fetch=FetchType.LAZY) private Colaborador aprovadaPor;
    protected Arte() {}
    Arte(ItemPedido item,String referencia,String descricao,Colaborador autor){this.itemPedido=item;atualizarInterno(referencia,descricao,autor);}
    public void atualizarArte(Colaborador executor,String referencia,String descricao){Colaborador.exigir(executor,PerfilColaborador.CRIACAO_ARTE);itemPedido.getPedido().exigirStatus(StatusPedido.CONFIRMADO);if(itemPedido.getPedido().getTotalPago().compareTo(itemPedido.getPedido().getSinalMinimo())<0)throw new IllegalStateException("Sinal minimo nao recebido");atualizarInterno(referencia,descricao,executor);}
    private void atualizarInterno(String referencia,String descricao,Colaborador autor){if(referencia==null||referencia.isBlank())throw new IllegalArgumentException("referencia obrigatoria");this.referenciaArquivo=referencia.trim();this.descricao=descricao;this.atualizadaPor=autor;this.aprovada=false;this.aprovadaEm=null;this.aprovadaPor=null;}
    public void aprovarArte(Colaborador executor){Colaborador.exigir(executor,PerfilColaborador.CRIACAO_ARTE);itemPedido.getPedido().exigirStatus(StatusPedido.CONFIRMADO);if(itemPedido.getPedido().getTotalPago().compareTo(itemPedido.getPedido().getSinalMinimo())<0)throw new IllegalStateException("Sinal minimo nao recebido");if(referenciaArquivo==null)throw new IllegalStateException("Arte ainda nao anexada");aprovada=true;aprovadaEm=LocalDateTime.now();aprovadaPor=executor;}
    void bloquearSeProducao(){if(itemPedido.getPedido().getStatus().ordinal()>=StatusPedido.EM_PRODUCAO.ordinal())throw new IllegalStateException("Arte bloqueada apos inicio da producao");}
    public Long getId(){return id;} public ItemPedido getItemPedido(){return itemPedido;} public String getReferenciaArquivo(){return referenciaArquivo;} public String getDescricao(){return descricao;} public boolean isAprovada(){return aprovada;} public LocalDateTime getAprovadaEm(){return aprovadaEm;} public Colaborador getAtualizadaPor(){return atualizadaPor;} public Colaborador getAprovadaPor(){return aprovadaPor;}
}
