# Guia de estudo do Projeto Jonas

Este guia apresenta o projeto para quem está chegando agora. Não é necessário saber programar para acompanhar a ideia geral. Os nomes técnicos aparecem quando ajudam a localizar algo no código, mas cada um é explicado.

## 1. O que o sistema faz

O Projeto Jonas organiza encomendas personalizadas. Ele guarda informações sobre clientes, equipe, produtos, pedidos, itens do pedido, artes e pagamentos.

Um exemplo simples:

1. Um atendente abre um pedido para um cliente.
2. Inclui produtos, quantidades, personalização, material e datas.
3. O financeiro confirma o pedido e registra o sinal.
4. A equipe de criação anexa a arte, atualiza a proposta se necessário e registra a aprovação.
5. A produção inicia o trabalho, marca o pedido como pronto e registra a entrega.
6. O financeiro registra os pagamentos restantes. A entrega só é permitida quando o saldo chega a zero.

O código atual concentra essas regras nas classes do domínio. Ele ainda não tem telas, endereços web para receber solicitações, nem a camada que coordena as operações. Esses itens pertencem a uma fase futura.

## 2. Como as pastas estão organizadas

O código fica em `src/main/java/br/com/projeto/jonas`.

- `domain/entity`: classes que representam os dados e as regras principais do negócio. Uma instância de uma dessas classes representa, por exemplo, um cliente ou um pedido.
- `domain/enums`: listas fechadas de opções, como os estados possíveis de um pedido.
- `src/main/resources`: configurações da aplicação. Atualmente existe o nome da aplicação em `application.yaml`.
- `src/test/java`: testes automáticos. Eles descrevem situações que o código deve aceitar ou rejeitar.
- `docs/uml`: diagrama que mostra as classes e como elas se relacionam.

O projeto usa Spring Boot e Maven. O Maven lê o `pom.xml` para saber as versões do Java, as bibliotecas necessárias e como compilar o código.

## 3. O caminho de um pedido

### Rascunho

Um pedido começa em `RASCUNHO`. O atendente pode incluir e remover itens e ajustar datas e observações. Cada item captura o preço atual do catálogo no momento em que é incluído. Assim, uma mudança futura no preço do produto não muda o valor daquele pedido.

O pedido precisa ter cliente, responsável ativo, pelo menos um item, data do evento e prazo válidos antes de ser confirmado. O prazo não pode ser depois da data do evento, e as datas não podem estar no passado.

### Confirmado

Um colaborador do perfil `FINANCEIRO_GESTAO` confirma o pedido. Depois dessa confirmação, o sistema pode registrar o primeiro pagamento, que deve ser um `SINAL` de pelo menos metade do total.

Com o sinal pago, a equipe `CRIACAO_ARTE` pode anexar ou atualizar a arte e aprová-la. Atualizar uma arte apaga a aprovação anterior; a nova versão precisa ser aprovada novamente.

### Produção, pronto e entrega

Um colaborador de `PRODUCAO_EXPEDICAO` inicia a produção quando o sinal mínimo foi recebido e todas as artes estão aprovadas. O pedido passa para `EM_PRODUCAO`; depois pode ir para `PRONTO` e `ENTREGUE`.

Ao iniciar a produção, alterações de itens e artes ficam bloqueadas. A entrega só pode ser registrada com saldo zerado.

### Cancelamento e reabertura

O financeiro pode cancelar pedidos em rascunho, confirmados ou em produção. O sistema registra motivo, autor e data do cancelamento, preservando os itens e os pagamentos do pedido original.

Um pedido cancelado pode ser reaberto criando **um novo pedido em rascunho**, com novo código e datas. O novo pedido fica vinculado ao original, usa os preços atuais dos produtos e não copia pagamentos nem aprovações de arte.

## 4. Os sete arquivos de entidade

As entidades são as classes que representam os principais registros do sistema. Elas têm campos privados para proteger os dados e métodos próprios para fazer mudanças válidas.

### `Cliente.java`

Representa a pessoa que faz a encomenda. Guarda nome, telefone e email. O construtor e `atualizarContato` exigem nome e telefone; o email pode ficar vazio.

### `Colaborador.java`

Representa uma pessoa da equipe. Guarda nome, perfil de trabalho e situação ativa ou inativa. Permite alterar nome e perfil, ativar ou desativar colaboradores.

O método `exigir` é uma verificação compartilhada: confirma que quem está executando uma ação existe, está ativo e tem o perfil exigido. Por exemplo, confirmar um pedido exige `FINANCEIRO_GESTAO`.

### `Produto.java`

Representa um produto do catálogo, com nome, descrição, preço-base e situação ativa ou inativa. O preço precisa ser positivo e ter até duas casas decimais. Alterar o catálogo exige perfil financeiro.

Quando um produto entra em um pedido, o item guarda uma cópia do preço daquele momento. O produto do catálogo pode mudar depois sem recalcular pedidos antigos.

### `Pedido.java`

É a classe que coordena o fluxo principal. Guarda cliente, responsável, datas, estado, observações, cancelamento, vínculo com pedido original, itens e pagamentos.

Entre seus métodos estão confirmar, registrar pagamento, iniciar produção, marcar como pronto, entregar, cancelar e reabrir. Também calcula total, total pago, saldo e sinal mínimo; esses valores não são digitados manualmente.

O código de controle é criado automaticamente e deve ser único. A lista de itens e a de pagamentos são expostas como listas não modificáveis, para impedir que outra classe as altere diretamente.

### `ItemPedido.java`

Representa uma linha do pedido: produto, quantidade, preço capturado, personalização e material. Calcula seu subtotal multiplicando preço pela quantidade.

Alterar quantidade, personalização ou material exige um colaborador ativo de atendimento e pedido ainda em rascunho. O item também pode guardar uma arte.

### `Arte.java`

Representa a arte ligada a um item. Guarda a referência do arquivo, descrição, situação de aprovação, data e colaboradores responsáveis por atualizar e aprovar.

Anexar, atualizar ou aprovar arte exige perfil de criação de arte, pedido confirmado e sinal mínimo pago. Atualizar uma arte limpa a aprovação anterior.

### `Pagamento.java`

Representa um recebimento registrado no pedido. Guarda valor, data, tipo, forma de pagamento e colaborador que confirmou.

Não há métodos públicos para editar ou apagar um pagamento depois de registrado. Os tipos são sinal, parcial e quitação; as formas são PIX, dinheiro, cartão e transferência.

## 5. Os quatro arquivos de enumeração

Uma enumeração é uma lista fixa de opções. Ela evita que o sistema receba variações digitadas à mão, como “em produção”, “producao” e “produção” para o mesmo estado.

- `StatusPedido.java`: `RASCUNHO`, `CONFIRMADO`, `EM_PRODUCAO`, `PRONTO`, `ENTREGUE` e `CANCELADO`.
- `PerfilColaborador.java`: `ATENDIMENTO`, `FINANCEIRO_GESTAO`, `CRIACAO_ARTE` e `PRODUCAO_EXPEDICAO`.
- `TipoPagamento.java`: `SINAL`, `PARCIAL` e `QUITACAO`.
- `FormaPagamento.java`: `PIX`, `DINHEIRO`, `CARTAO` e `TRANSFERENCIA`.

No banco de dados, os perfis e estados são armazenados como texto legível, em vez de um número que poderia mudar de significado.

## 6. Os demais arquivos importantes

- `JonasApplication.java`: ponto de entrada da aplicação Spring Boot. É o arquivo executado para iniciar o programa.
- `src/main/resources/application.yaml`: configuração básica; por enquanto, informa o nome `jonas`.
- `pom.xml`: lista Java 21, Spring Boot 4.1.2 e bibliotecas como JPA, H2 e ferramentas de teste. JPA é a biblioteca que relaciona objetos Java com tabelas do banco.
- `mvnw` e `mvnw.cmd`: scripts do Maven Wrapper para Linux/macOS e Windows. Eles ajudam a usar a versão de Maven indicada pelo projeto.
- `.mvn/wrapper/maven-wrapper.properties`: informa ao Wrapper que a versão de Maven desejada é 3.10.1.
- `docs/uml/projeto-jonas.puml`: diagrama textual do modelo, incluindo classes, opções e relacionamentos.
- `readme.md`: escopo, regras e critérios da entrega da primeira fase.
- `HELP.md`: arquivo de orientações padrão criado pelo Spring Initializr. Não contém regra específica do negócio.

## 7. O que foi alterado nesta etapa

As correções foram feitas para que algumas regras importantes funcionem também quando alguém chama diretamente os métodos das entidades:

- `Pedido` agora exige um colaborador de atendimento ao ser criado.
- Os métodos de alteração do item agora recebem o colaborador e recusam quem não é de atendimento ou cujo pedido já saiu do rascunho.
- A primeira arte só pode ser anexada depois que o sinal mínimo foi pago. O item também mantém a referência da arte, para que o pedido consiga verificar a aprovação antes de iniciar a produção.
- Preços com frações que precisariam ser arredondadas para duas casas são recusados. Por exemplo, `1,005` não é silenciosamente convertido em outro valor.
- Foram adicionados testes para regras do pedido e para persistir e recarregar seus relacionamentos.
- O UML foi atualizado e está no local indicado pelo README.

## 8. Testes e como tentar executá-los

O teste `RegrasPedidoTest.java` cobre criação e fluxo de arte, permissões para mudar itens, validação de preços, sinal, entrega, prioridade e reabertura.

O teste `JonasApplicationTests.java` verifica se a aplicação inicia e se pedido, itens, pagamentos e arte podem ser gravados e carregados novamente pelo JPA.

Para rodar no Windows, abra o PowerShell na pasta do projeto (`jonas`) e execute:

```powershell
java -version
.\mvnw.cmd -version
.\mvnw.cmd clean test
```

Na primeira execução, o Maven pode baixar bibliotecas da internet e gravá-las no cache do usuário. O teste só fica confirmado quando o comando termina com `BUILD SUCCESS` e os relatórios do JUnit mostram os testes executados sem falhas.

**Estado da validação neste ambiente:** Java 21.0.8 e Maven 3.10.1 foram identificados. A compilação de produção chegou a reportar `BUILD SUCCESS`, mas não foi possível confirmar a execução dos testes. O compilador Java encontrou `AccessDeniedException` ao resolver o caminho real de um JAR do classpath dentro do ambiente gerenciado. Esse erro ocorre no acesso ao sistema de arquivos do ambiente durante o compilador; não indica, por si só, falha em uma regra do projeto. Execute o comando acima em um PowerShell local normal. Se ele falhar, guarde e compartilhe a saída completa para diagnóstico.

## 9. Como a fase seguinte poderá acrescentar Controllers e Services

Hoje as entidades sabem aplicar as regras, mas ainda não existe uma porta de entrada para uma tela ou outro programa solicitar ações. A fase seguinte pode acrescentar essas camadas sem transferir as regras para elas:

1. **Controller (porta de entrada):** recebe uma solicitação, por exemplo “confirmar o pedido 42”, e devolve uma resposta. É a parte que conversa com o navegador, aplicativo ou outro sistema.
2. **Service (coordenador):** procura os dados necessários, verifica o contexto da operação e chama os métodos adequados das entidades. Por exemplo, carrega o pedido e o colaborador e chama `pedido.confirmarPedido(colaborador)`.
3. **Repository (acesso ao banco):** localiza, grava e atualiza entidades no banco. Em termos simples, funciona como a parte que busca e guarda registros.
4. **Entity (regra do negócio):** continua sendo a autoridade sobre o que é permitido. O Controller não deve mudar campos diretamente; o Service chama os métodos de negócio da entidade.

O fluxo futuro pode ser imaginado assim:

```text
Tela ou aplicativo
        ↓ solicitação
Controller
        ↓ chama a operação
Service
        ↓ busca e grava dados
Repository ↔ Banco de dados
        ↓ entrega os objetos ao Service
Entidade aplica e protege as regras
```

Exemplo: para confirmar um pedido, o Controller recebe o número do pedido e a identificação do colaborador; o Service carrega ambos; a entidade `Pedido` confere perfil, estado, datas e itens. Se alguma regra não for atendida, a operação é recusada. O Controller então transforma o resultado em uma resposta compreensível para quem fez a solicitação.

## 10. Pequeno glossário

- **Entidade:** objeto do programa que representa um registro importante, como cliente ou pedido.
- **Banco de dados:** local estruturado onde os registros ficam guardados.
- **Relacionamento:** ligação entre registros. Um pedido, por exemplo, aponta para um cliente e contém itens.
- **JPA:** tecnologia que ajuda a gravar e recuperar entidades no banco.
- **Enumeração:** conjunto fechado de opções possíveis.
- **Perfil:** função de trabalho do colaborador, usada para decidir quais operações ele pode fazer.
- **Subtotal:** preço de uma unidade multiplicado pela quantidade.
- **Saldo:** total do pedido menos os pagamentos já registrados.
- **UML:** desenho que resume as classes do sistema e suas ligações.
- **Teste automatizado:** verificação executada pelo computador para conferir se um comportamento esperado continua funcionando.
