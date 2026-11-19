package br.com.projeto.jonas;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import br.com.projeto.jonas.domain.entity.*;
import br.com.projeto.jonas.domain.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JonasApplicationTests {
	@Autowired EntityManager entityManager;

	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	void persisteERecarregaRelacionamentosDoPedido() {
		Colaborador atendimento = new Colaborador("Ana", PerfilColaborador.ATENDIMENTO);
		Colaborador financeiro = new Colaborador("Bia", PerfilColaborador.FINANCEIRO_GESTAO);
		Colaborador criacao = new Colaborador("Caio", PerfilColaborador.CRIACAO_ARTE);
		Cliente cliente = new Cliente("Cliente", "9999", "cliente@example.com");
		Produto produto = new Produto("Caneca", "Ceramica", new BigDecimal("100.00"));
		entityManager.persist(atendimento);
		entityManager.persist(financeiro);
		entityManager.persist(criacao);
		entityManager.persist(cliente);
		entityManager.persist(produto);
		entityManager.flush();

		Pedido pedido = new Pedido(atendimento, cliente, atendimento);
		pedido.adicionarItem(atendimento, produto, 2, "Nome", "ceramica");
		pedido.alterarDataEvento(atendimento, LocalDate.now().plusDays(10));
		pedido.alterarPrazo(atendimento, LocalDate.now().plusDays(8));
		pedido.confirmarPedido(financeiro);
		pedido.registrarPagamento(financeiro, new BigDecimal("100.00"), TipoPagamento.SINAL, FormaPagamento.PIX);
		ItemPedido item = pedido.getItens().get(0);
		Arte arte = item.anexarArte(criacao, "arte.png", "Prova");
		arte.aprovarArte(criacao);
		entityManager.persist(pedido);
		entityManager.flush();
		Long id = pedido.getId();
		entityManager.clear();

		Pedido recarregado = entityManager.find(Pedido.class, id);
		assertEquals(1, recarregado.getItens().size());
		assertEquals(1, recarregado.getPagamentos().size());
		assertTrue(recarregado.getItens().get(0).getArte().isAprovada());
		assertEquals(new BigDecimal("200.00"), recarregado.getTotal());
	}

}
