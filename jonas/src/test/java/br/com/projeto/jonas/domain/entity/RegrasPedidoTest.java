package br.com.projeto.jonas.domain.entity;

import br.com.projeto.jonas.domain.enums.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RegrasPedidoTest {
    private final Colaborador atendimento = new Colaborador("Ana", PerfilColaborador.ATENDIMENTO);
    private final Colaborador financeiro = new Colaborador("Bia", PerfilColaborador.FINANCEIRO_GESTAO);
    private final Colaborador arte = new Colaborador("Caio", PerfilColaborador.CRIACAO_ARTE);
    private final Colaborador producao = new Colaborador("Dani", PerfilColaborador.PRODUCAO_EXPEDICAO);
    private final Cliente cliente = new Cliente("Cliente", "9999", null);
    private final Produto produto = new Produto("Caneca", null, new BigDecimal("100.00"));

    private Pedido pedidoComItem() {
        Pedido pedido = new Pedido(atendimento, cliente, atendimento);
        pedido.adicionarItem(atendimento, produto, 1, null, "ceramica");
        pedido.alterarDataEvento(atendimento, LocalDate.now().plusDays(10));
        pedido.alterarPrazo(atendimento, LocalDate.now().plusDays(8));
        pedido.confirmarPedido(financeiro);
        return pedido;
    }

    @Test
    void criaArteSomenteDepoisDoSinalEProduzComArteAprovada() {
        Pedido pedido = pedidoComItem();
        ItemPedido item = pedido.getItens().get(0);
        assertThrows(IllegalStateException.class, () -> item.anexarArte(arte, "arte.png", "Prova"));

        pedido.registrarPagamento(financeiro, new BigDecimal("50.00"), TipoPagamento.SINAL, FormaPagamento.PIX);
        Arte novaArte = item.anexarArte(arte, "arte.png", "Prova");
        assertSame(novaArte, item.getArte());
        novaArte.aprovarArte(arte);
        pedido.iniciarProducao(producao);
        assertEquals(StatusPedido.EM_PRODUCAO, pedido.getStatus());
    }

    @Test
    void alteracoesDeItemExigemAtendimentoEPedidoEmRascunho() {
        Pedido pedido = new Pedido(atendimento, cliente, atendimento);
        pedido.adicionarItem(atendimento, produto, 1, null, "ceramica");
        ItemPedido item = pedido.getItens().get(0);

        assertThrows(IllegalStateException.class, () -> item.alterarQuantidade(financeiro, 2));
        item.alterarQuantidade(atendimento, 2);
        assertEquals(2, item.getQuantidade());
    }

    @Test
    void recusaPrecoPositivoQueArredondariaParaZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new Produto("Invalido", null, new BigDecimal("0.001")));
        assertThrows(IllegalArgumentException.class,
                () -> new Produto("Invalido", null, new BigDecimal("1.005")));
    }

    @Test
    void validacaoDePagamentoNaoDeixaEstadoParcialEEntregaExigeQuitacao() {
        Pedido pedido = pedidoComItem();
        assertThrows(IllegalArgumentException.class,
                () -> pedido.registrarPagamento(financeiro, new BigDecimal("40.00"), TipoPagamento.SINAL, FormaPagamento.PIX));
        assertTrue(pedido.getPagamentos().isEmpty());

        pedido.registrarPagamento(financeiro, new BigDecimal("50.00"), TipoPagamento.SINAL, FormaPagamento.PIX);
        ItemPedido item = pedido.getItens().get(0);
        Arte novaArte = item.anexarArte(this.arte, "arte.png", "Prova");
        novaArte.aprovarArte(this.arte);
        pedido.iniciarProducao(producao);
        pedido.marcarComoPronto(producao);
        assertThrows(IllegalStateException.class, () -> pedido.entregarPedido(producao));
        pedido.registrarPagamento(financeiro, new BigDecimal("50.00"), TipoPagamento.QUITACAO, FormaPagamento.PIX);
        pedido.entregarPedido(producao);
        assertEquals(StatusPedido.ENTREGUE, pedido.getStatus());
    }

    @Test
    void prioridadeOrdenaPelaDataDoEventoAntesDosDesempates() {
        Pedido maisTarde = new Pedido(atendimento, cliente, atendimento);
        Pedido maisCedo = new Pedido(atendimento, cliente, atendimento);
        maisTarde.alterarDataEvento(atendimento, LocalDate.now().plusDays(15));
        maisTarde.alterarPrazo(atendimento, LocalDate.now().plusDays(12));
        maisCedo.alterarDataEvento(atendimento, LocalDate.now().plusDays(12));
        maisCedo.alterarPrazo(atendimento, LocalDate.now().plusDays(10));

        assertTrue(Pedido.prioridadeProducao().compare(maisCedo, maisTarde) < 0);
    }

    @Test
    void reaberturaCriaRascunhoNovoComPrecoAtualESemPagamentoOuArte() {
        Pedido original = pedidoComItem();
        original.cancelarPedido(financeiro, "Ajuste solicitado");
        produto.alterarPrecoBase(financeiro, new BigDecimal("120.00"));

        Pedido reaberto = original.reabrirPedido(financeiro, LocalDate.now().plusDays(20), LocalDate.now().plusDays(18));
        assertEquals(StatusPedido.RASCUNHO, reaberto.getStatus());
        assertNotEquals(original.getCodigoControle(), reaberto.getCodigoControle());
        assertSame(original, reaberto.getPedidoOrigem());
        assertEquals(new BigDecimal("120.00"), reaberto.getItens().get(0).getPrecoUnitario());
        assertTrue(reaberto.getPagamentos().isEmpty());
        assertNull(reaberto.getItens().get(0).getArte());
    }

    @Test
    void deveCriarPedidoEmRascunhoERejeitarResponsavelInativo() {
        Pedido pedido = new Pedido(atendimento, cliente, atendimento);

        assertEquals(StatusPedido.RASCUNHO, pedido.getStatus());
        assertNotNull(pedido.getCodigoControle());
        assertEquals(cliente, pedido.getCliente());
        assertEquals(atendimento, pedido.getResponsavel());
        assertTrue(pedido.getItens().isEmpty());

        Colaborador inativo = new Colaborador("Carlos", PerfilColaborador.ATENDIMENTO);
        inativo.desativar(financeiro);

        assertThrows(IllegalArgumentException.class,
                () -> new Pedido(atendimento, cliente, inativo));
    }

    @Test
    void deveGerenciarItensDatasEObservacoesEmRascunho() {
        Pedido pedido = new Pedido(atendimento, cliente, atendimento);
        pedido.adicionarItem(atendimento, produto, 2, "Nome gravado", "ceramica");
        pedido.alterarObservacoes(atendimento, "Entrega na recepcao");

        ItemPedido item = pedido.getItens().get(0);
        item.alterarPersonalizacao(atendimento, "Nome e data");
        item.alterarMaterial(atendimento, "porcelana");

        assertEquals(new BigDecimal("200.00"), pedido.getTotal());
        assertEquals(new BigDecimal("100.00"), pedido.getSinalMinimo());
        assertEquals("Nome e data", item.getPersonalizacao());
        assertEquals("porcelana", item.getMaterial());
        assertEquals("Entrega na recepcao", pedido.getObservacoes());

        LocalDate evento = LocalDate.now().plusDays(10);
        LocalDate prazoValido = LocalDate.now().plusDays(7);
        LocalDate prazoInvalido = LocalDate.now().plusDays(12);

        pedido.alterarDataEvento(atendimento, evento);
        pedido.alterarPrazo(atendimento, prazoValido);
        assertEquals(prazoValido, pedido.getPrazoEntrega());

        assertThrows(IllegalArgumentException.class,
                () -> pedido.alterarPrazo(atendimento, prazoInvalido));
        assertThrows(IllegalArgumentException.class,
                () -> pedido.alterarPrazo(atendimento, LocalDate.now().minusDays(1)));

        pedido.removerItem(atendimento, item);
        assertTrue(pedido.getItens().isEmpty());
        assertEquals(new BigDecimal("0.00"), pedido.getTotal());
    }
}
