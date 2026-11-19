package br.com.projeto.jonas.domain.entity;

import br.com.projeto.jonas.domain.enums.*;
import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity
public class Pedido {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true,updatable=false) private UUID codigoControle;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(nullable=false) private Cliente cliente;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(nullable=false) private Colaborador responsavel;
    @Column(nullable=false,updatable=false) private LocalDateTime criadoEm;
    private LocalDate dataEvento;
    private LocalDate prazoEntrega;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private StatusPedido status;
    @Column(length=4000) private String observacoes;
    private String motivoCancelamento;
    private LocalDateTime canceladoEm;
    @ManyToOne(fetch=FetchType.LAZY) private Colaborador canceladoPor;
    @ManyToOne(fetch=FetchType.LAZY) private Pedido pedidoOrigem;
    @OneToMany(mappedBy="pedido",cascade=CascadeType.ALL,orphanRemoval=true) private List<ItemPedido> itens=new ArrayList<>();
    @OneToMany(mappedBy="pedido",cascade=CascadeType.PERSIST) private List<Pagamento> pagamentos=new ArrayList<>();
    protected Pedido() {}
    public Pedido(Colaborador executor,Cliente cliente,Colaborador responsavel){Colaborador.exigir(executor,PerfilColaborador.ATENDIMENTO);inicializar(cliente,responsavel);}
    private Pedido(Cliente cliente,Colaborador responsavel){inicializar(cliente,responsavel);}
    private void inicializar(Cliente cliente,Colaborador responsavel){this.codigoControle=UUID.randomUUID();this.criadoEm=LocalDateTime.now();this.status=StatusPedido.RASCUNHO;this.cliente=Objects.requireNonNull(cliente);this.responsavel=Objects.requireNonNull(responsavel);if(!responsavel.isAtivo())throw new IllegalArgumentException("Responsavel deve estar ativo");}
    public void adicionarItem(Colaborador executor,Produto produto,int quantidade,String personalizacao,String material){autorizar(executor,PerfilColaborador.ATENDIMENTO);exigirRascunho();if(produto==null||!produto.isAtivo())throw new IllegalArgumentException("Produto ativo obrigatorio");ItemPedido item=new ItemPedido(this,produto,quantidade,personalizacao,material);itens.add(item);}
    public void removerItem(Colaborador executor,ItemPedido item){autorizar(executor,PerfilColaborador.ATENDIMENTO);exigirRascunho();if(!itens.remove(item))throw new IllegalArgumentException("Item nao pertence ao pedido");}
    public void alterarPrazo(Colaborador executor,LocalDate prazo){autorizar(executor,PerfilColaborador.ATENDIMENTO);exigirRascunho();validarData(prazo,"prazo");if(dataEvento!=null&&prazo.isAfter(dataEvento))throw new IllegalArgumentException("Prazo nao pode ser posterior ao evento");prazoEntrega=prazo;}
    public void alterarDataEvento(Colaborador executor,LocalDate data){autorizar(executor,PerfilColaborador.ATENDIMENTO);exigirRascunho();validarData(data,"evento");if(prazoEntrega!=null&&prazoEntrega.isAfter(data))throw new IllegalArgumentException("Prazo nao pode ser posterior ao evento");dataEvento=data;}
    public void alterarObservacoes(Colaborador executor,String valor){autorizar(executor,PerfilColaborador.ATENDIMENTO);exigirRascunho();observacoes=valor;}
    public void confirmarPedido(Colaborador executor){autorizar(executor,PerfilColaborador.FINANCEIRO_GESTAO);exigirRascunho();if(cliente==null||responsavel==null||!responsavel.isAtivo()||itens.isEmpty())throw new IllegalStateException("Cliente, responsavel ativo e itens sao obrigatorios");validarDatas();if(getTotal().signum()<=0)throw new IllegalStateException("Total deve ser positivo");status=StatusPedido.CONFIRMADO;}
    public Pagamento registrarPagamento(Colaborador executor,BigDecimal valor,TipoPagamento tipo,FormaPagamento forma){autorizar(executor,PerfilColaborador.FINANCEIRO_GESTAO);if(!(status==StatusPedido.CONFIRMADO||status==StatusPedido.EM_PRODUCAO||status==StatusPedido.PRONTO))throw new IllegalStateException("Pedido nao aceita pagamentos neste status");Objects.requireNonNull(tipo);BigDecimal v=Produto.preco(valor);if(pagamentos.isEmpty()){if(tipo!=TipoPagamento.SINAL||v.compareTo(getSinalMinimo())<0)throw new IllegalArgumentException("Primeiro pagamento deve ser sinal de pelo menos 50%");}else if(tipo==TipoPagamento.SINAL)throw new IllegalArgumentException("Sinal ja registrado");if(v.compareTo(getSaldo())>0)throw new IllegalArgumentException("Pagamento excede o saldo");if((tipo==TipoPagamento.QUITACAO)!=(v.compareTo(getSaldo())==0))throw new IllegalArgumentException("QUITACAO deve corresponder ao saldo integral");Pagamento p=new Pagamento(this,v,tipo,forma,executor);pagamentos.add(p);return p;}
    public void iniciarProducao(Colaborador executor){autorizar(executor,PerfilColaborador.PRODUCAO_EXPEDICAO);exigirStatus(StatusPedido.CONFIRMADO);if(pagamentos.isEmpty()||pagamentos.get(0).getTipo()!=TipoPagamento.SINAL||getTotalPago().compareTo(getSinalMinimo())<0)throw new IllegalStateException("Sinal minimo nao recebido");if(itens.stream().anyMatch(i->i.getArte()==null||!i.getArte().isAprovada()))throw new IllegalStateException("Todas as artes devem estar aprovadas");status=StatusPedido.EM_PRODUCAO;}
    public void marcarComoPronto(Colaborador executor){autorizar(executor,PerfilColaborador.PRODUCAO_EXPEDICAO);exigirStatus(StatusPedido.EM_PRODUCAO);status=StatusPedido.PRONTO;}
    public void entregarPedido(Colaborador executor){autorizar(executor,PerfilColaborador.PRODUCAO_EXPEDICAO);exigirStatus(StatusPedido.PRONTO);if(getSaldo().signum()!=0)throw new IllegalStateException("Pedido possui saldo pendente");status=StatusPedido.ENTREGUE;}
    public void cancelarPedido(Colaborador executor,String motivo){autorizar(executor,PerfilColaborador.FINANCEIRO_GESTAO);if(!(status==StatusPedido.RASCUNHO||status==StatusPedido.CONFIRMADO||status==StatusPedido.EM_PRODUCAO))throw new IllegalStateException("Status nao permite cancelamento");if(motivo==null||motivo.isBlank())throw new IllegalArgumentException("Motivo obrigatorio");motivoCancelamento=motivo.trim();canceladoPor=executor;canceladoEm=LocalDateTime.now();status=StatusPedido.CANCELADO;}
    public Pedido reabrirPedido(Colaborador executor,LocalDate novoEvento,LocalDate novoPrazo){autorizar(executor,PerfilColaborador.FINANCEIRO_GESTAO);exigirStatus(StatusPedido.CANCELADO);validarData(novoEvento,"evento");validarData(novoPrazo,"prazo");if(novoPrazo.isAfter(novoEvento))throw new IllegalArgumentException("Prazo nao pode ser posterior ao evento");Pedido novo=new Pedido(cliente,executor);novo.pedidoOrigem=this;novo.dataEvento=novoEvento;novo.prazoEntrega=novoPrazo;for(ItemPedido i:itens){if(!i.getProduto().isAtivo())throw new IllegalStateException("Produto inativo: "+i.getProduto().getNome());novo.itens.add(new ItemPedido(novo,i.getProduto(),i.getQuantidade(),i.getPersonalizacao(),i.getMaterial()));}return novo;}
    public BigDecimal getTotal(){return itens.stream().map(ItemPedido::getSubtotal).reduce(BigDecimal.ZERO,(a,b)->a.add(b)).setScale(2,RoundingMode.HALF_UP);}
    public BigDecimal getTotalPago(){return pagamentos.stream().map(Pagamento::getValor).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,RoundingMode.HALF_UP);}
    public BigDecimal getSaldo(){return getTotal().subtract(getTotalPago()).max(BigDecimal.ZERO).setScale(2,RoundingMode.HALF_UP);}
    public BigDecimal getSinalMinimo(){return getTotal().multiply(new BigDecimal("0.50")).setScale(2,RoundingMode.CEILING);}
    public static Comparator<Pedido> prioridadeProducao(){return Comparator.comparing(Pedido::getDataEvento,Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Pedido::getPrazoEntrega,Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Pedido::getCriadoEm).thenComparing(Pedido::getCodigoControle);}
    void exigirRascunho(){exigirStatus(StatusPedido.RASCUNHO);} void exigirStatus(StatusPedido esperado){if(status!=esperado)throw new IllegalStateException("Status esperado: "+esperado);}
    private void autorizar(Colaborador c,PerfilColaborador perfil){Colaborador.exigir(c,perfil);}
    private void validarDatas(){if(dataEvento==null||prazoEntrega==null)throw new IllegalStateException("Datas do evento e prazo sao obrigatorias");validarData(dataEvento,"evento");validarData(prazoEntrega,"prazo");if(prazoEntrega.isAfter(dataEvento))throw new IllegalArgumentException("Prazo nao pode ser posterior ao evento");}
    private static void validarData(LocalDate d,String n){if(d==null||d.isBefore(LocalDate.now()))throw new IllegalArgumentException("Data de "+n+" invalida ou no passado");}
    public Long getId(){return id;} public UUID getCodigoControle(){return codigoControle;} public Cliente getCliente(){return cliente;} public Colaborador getResponsavel(){return responsavel;} public LocalDateTime getCriadoEm(){return criadoEm;} public LocalDate getDataEvento(){return dataEvento;} public LocalDate getPrazoEntrega(){return prazoEntrega;} public StatusPedido getStatus(){return status;} public String getObservacoes(){return observacoes;} public String getMotivoCancelamento(){return motivoCancelamento;} public LocalDateTime getCanceladoEm(){return canceladoEm;} public Colaborador getCanceladoPor(){return canceladoPor;} public Pedido getPedidoOrigem(){return pedidoOrigem;} public List<ItemPedido> getItens(){return Collections.unmodifiableList(itens);} public List<Pagamento> getPagamentos(){return Collections.unmodifiableList(pagamentos);}
}
