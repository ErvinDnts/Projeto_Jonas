Integrantes: Ervin, Luiz Henrique e luís Falcone

**Projeto Jonas**

Sistema de gestão de encomendas personalizadas desenvolvido com Java e Spring Boot. Centraliza pedidos, itens, artes e pagamentos para atendimento, financeiro, criação e produção.

**Escopo desta entrega**

Implementar as entidades, atributos, relacionamentos JPA e métodos de negócio. Produzir um diagrama de classes UML coerente com o código.

Não serão implementados Controllers, Services, endpoints ou interface gráfica nesta etapa. Estoque, compras, custos e capacidade produtiva ficam fora do escopo inicial.

**Arquitetura**

Aplicação monolítica organizada em camadas, com regras de negócio nas entidades.

A escolha facilita o trabalho da equipe e mantém o domínio integrado, sem a complexidade de microsserviços. As camadas separam as regras de negócio da persistência e da futura comunicação HTTP.

Pacote-base: br.com.projeto.jonas.

| Pasta existente | Responsabilidade |
|               |                                                                       |
| domain/entity | Cliente, Colaborador, Pedido, ItemPedido, Produto, Pagamento e Arte |
| domain/enums | StatusPedido, PerfilColaborador, TipoPagamento e FormaPagamento |
| repository | Persistência, em etapa futura |
| service | Coordenação de casos de uso, em etapa futura |
| controller | Endpoints HTTP, em etapa futura |

Os testes ficarão em src/test/java/br/com/projeto/jonas e o diagrama em docs/uml.

**Entidades e atributos**

Todas as entidades terão id do tipo Long, com geração automática por JPA.

| Entidade | Atributos principais |
|---|---|
| Cliente | nome, telefone e email: String |
| Colaborador | nome: String; perfil: PerfilColaborador; ativo: boolean |
| Produto | nome e descricao: String; precoBase: BigDecimal; ativo: boolean |
| Pedido | codigoControle: UUID; cliente: Cliente; responsavel: Colaborador; criadoEm: LocalDateTime; dataEvento e prazoEntrega: LocalDate; status: StatusPedido; observacoes e motivoCancelamento: String; canceladoEm: LocalDateTime; canceladoPor: Colaborador; pedidoOrigem: Pedido; itens: List de ItemPedido; pagamentos: List de Pagamento |
| ItemPedido | pedido: Pedido; produto: Produto; quantidade: Integer; precoUnitario: BigDecimal; personalizacao e material: String; arte: Arte |
| Arte | itemPedido: ItemPedido; referenciaArquivo e descricao: String; aprovada: boolean; aprovadaEm: LocalDateTime; atualizadaPor e aprovadaPor: Colaborador |
| Pagamento | pedido: Pedido; valor: BigDecimal; registradoEm: LocalDateTime; tipo: TipoPagamento; forma: FormaPagamento; confirmadoPor: Colaborador |

**Enumerações**

- StatusPedido: RASCUNHO, CONFIRMADO, EM_PRODUCAO, PRONTO, ENTREGUE e CANCELADO.
- PerfilColaborador: ATENDIMENTO, FINANCEIRO_GESTAO, CRIACAO_ARTE e PRODUCAO_EXPEDICAO.
- TipoPagamento: SINAL, PARCIAL e QUITACAO.
- FormaPagamento: PIX, DINHEIRO, CARTAO e TRANSFERENCIA.

**Relacionamentos JPA**

- Pedido referencia Cliente e Colaborador responsável com ManyToOne.
- Pedido possui itens e pagamentos com OneToMany; ItemPedido e Pagamento referenciam Pedido com ManyToOne.
- ItemPedido referencia Produto com ManyToOne.
- ItemPedido e Arte possuem OneToOne, com chave estrangeira em Arte.
- Arte referencia os colaboradores de atualização e aprovação com ManyToOne.
- Pagamento referencia o colaborador confirmador com ManyToOne.
- Pedido referencia canceladoPor e pedidoOrigem com ManyToOne.

Usar Jakarta Persistence, enumerações como STRING e mappedBy no lado inverso dos vínculos bidirecionais. Manter ambos os lados consistentes. Não remover automaticamente pagamentos, clientes, colaboradores ou produtos.

**Regras e métodos de negócio**

Cada operação protegida deve receber o colaborador executor e validar seu perfil e situação ativa.

| Perfil | Responsabilidades |
|---|---|
| ATENDIMENTO | Cadastrar cliente, criar pedido e ajustar itens, datas e observações apenas em RASCUNHO |
| FINANCEIRO_GESTAO | Confirmar pedido, registrar recebimentos, manter catálogo e colaboradores, cancelar e reabrir pedidos |
| CRIACAO_ARTE | Anexar, atualizar e aprovar arte em CONFIRMADO, somente após o sinal |
| PRODUCAO_EXPEDICAO | Iniciar produção, marcar como pronto e entregar |

Regras obrigatórias:

- Ao realizar um pedido, é cobrado 50% do valor total adiantado, visando evitar prejuízos.
- A partir da enumeração "EM_PRODUCAO" do StatusPedido, torna-se imutável as requisições do pedido.
- Gerar codigoControle único e imutável na criação do pedido.
- Confirmar somente pedidos com cliente, responsável ativo, datas válidas e pelo menos um item.
- Exigir quantidade e preço positivos, material informado e prazo de entrega não posterior ao evento. Datas não podem estar no passado.
- Capturar o preço do catálogo na inclusão do item. Não aceitar preço arbitrário; mudanças no catálogo não alteram itens existentes.
- Calcular subtotal pelo preço registrado e quantidade. Total, total pago e saldo são calculados, não informados manualmente.
- Usar BigDecimal com duas casas decimais. Arredondar o sinal mínimo de 50% para cima ao centavo.
- O primeiro recebimento deve ser SINAL e atingir o mínimo exigido. Recebimentos seguintes são PARCIAL ou QUITACAO, sem ultrapassar o saldo.
- Registrar pagamentos já confirmados somente em CONFIRMADO, EM_PRODUCAO ou PRONTO. Não permitir alteração posterior dos registros.
- Iniciar produção somente em CONFIRMADO, com sinal recebido e todas as artes aprovadas.
- Atualizar uma arte invalida sua aprovação anterior. Bloquear alterações de arte após o início da produção.
- Bloquear alterações de itens após a confirmação do início da produção, preservando o valor utilizado no sinal.
- Seguir o fluxo RASCUNHO, CONFIRMADO, EM_PRODUCAO, PRONTO e ENTREGUE. Entregar somente com saldo zerado.
- Cancelar apenas em RASCUNHO, CONFIRMADO ou EM_PRODUCAO, registrando motivo, autor e data e preservando o histórico.
- Reabrir somente pedido CANCELADO, criando outro pedido em RASCUNHO, vinculado ao original, com novo código, datas válidas e itens aos preços atuais. Não transferir pagamentos nem aprovações. Estorno fica fora do escopo.
- Priorizar pedidos aptos à produção pela data do evento; desempatar por prazo de entrega, criação e código de controle.

Métodos principais de Pedido: adicionarItem, removerItem, alterarPrazo, alterarDataEvento, alterarObservacoes, confirmarPedido, registrarPagamento, iniciarProducao, marcarComoPronto, entregarPedido, cancelarPedido, reabrirPedido e métodos de cálculo.

As demais entidades devem oferecer métodos específicos, como alterarQuantidade, alterarPersonalizacao, alterarMaterial, atualizarArte, aprovarArte, atualizarContato e alterarPrecoBase.

Não usar setters genéricos para contornar regras. Proteger as coleções e validar toda a operação antes de modificar o estado. O construtor sem argumentos exigido pelo JPA deve ser protegido.

**Diagrama UML**

O diagrama de classes atualizado está em [docs/uml/projeto-jonas.puml](docs/uml/projeto-jonas.puml). Ele lista as sete entidades, quatro enumerações, atributos, métodos, relacionamentos e multiplicidades do modelo.

**Execução e testes**

O projeto usa Java 21 (`java.version` no `pom.xml`) e Spring Boot 4.1.2 (parent no `pom.xml`). O Maven Wrapper está configurado para Maven 3.10.1. O build inclui H2 como dependência de runtime; sem configuração externa de datasource, Spring Boot usa o H2 embarcado durante os testes.

Para compilar e executar os testes na raiz da pasta `jonas`:

```powershell
java -version
.\mvnw.cmd -version
.\mvnw.cmd clean test
```

Os testes cobrem regras de domínio e persistência/recarregamento dos relacionamentos JPA. Neste ambiente, Java 21.0.8 e Maven 3.10.1 foram identificados. A execução de `mvn test` não concluiu: o Maven não conseguiu criar o arquivo de bloqueio no repositório local `C:\Users\ervin\.m2\repository`, fora da pasta de trabalho permitida. Portanto, os testes estão adicionados, mas ainda precisam ser executados em um ambiente com repositório Maven gravável.

Testar principalmente permissões, transições de estado, preços de catálogo, sinal mínimo, aprovação de arte, bloqueio de entrega, cancelamento, reabertura e prioridade da fila. Operações inválidas não podem deixar alterações parciais.

**Orientação para o Codex CLI**

Leia este README e inspecione o projeto existente. Implemente as sete entidades e quatro enumerações, preservando os nomes e pacotes. Configure os relacionamentos JPA e concentre as regras nas entidades, sem setters que permitam contorná-las. Não implemente Controllers, Services ou endpoints. Crie testes das regras, execute compilação e testes quando possível e gere o UML coerente com o código em docs/uml. Atualize as instruções de execução somente com informações verificadas.

**Entrega**

- Código atualizado no GitHub, com acesso para todos os integrantes.
- README com descrição, justificativa da arquitetura e instruções de execução.
- Diagrama UML disponível no repositório.
- Link do repositório e diagrama entregues no Google Sala de Aula.
