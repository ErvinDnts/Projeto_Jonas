  SISTEMA DE GERENCIAMENTO DE VENDAS E NEGÓCIOS

O projeto propõe um sistema de gestão de vendas e encomendas para uma empresa de produtos personalizados destinados a aniversários, casamentos e eventos. Desenvolvido em Java com Spring Boot, o sistema busca centralizar as informações que atualmente estão espalhadas entre mensagens, redes sociais e atendimentos presenciais, evitando registros duplicados e falhas de comunicação.

O público-alvo são os colaboradores de atendimento, produção, administração e entrega, além do proprietário, que precisa acompanhar o andamento das encomendas e a situação do negócio.

Os principais problemas a serem enfrentados são a perda de alterações solicitadas pelos clientes, a falta de visibilidade sobre o andamento dos pedidos, os atrasos decorrentes de prazos incompatíveis com a capacidade de produção, a falta ou o excesso de materiais e a dificuldade de conferir sinais, pagamentos parciais e saldos pendentes.

A proposta é organizar o registro das encomendas, incluindo personalização, quantidade, materiais, prazo e valores; acompanhar suas alterações e etapas de produção; apoiar o controle de materiais; e registrar os pagamentos e os saldos devidos. Com informações centralizadas, a empresa poderá responder aos clientes com mais segurança, reduzir falhas operacionais e melhorar o planejamento das vendas, da produção e das entregas.

O escopo inicial deverá ser priorizado e validado pela equipe com o cliente, buscando uma solução simples e acessível. Nesta segunda etapa, o foco será modelar esse domínio por meio das entidades, dos atributos, dos relacionamentos JPA e dos comportamentos de negócio, além de documentar o diagrama UML e a arquitetura escolhida.

Status: repositório criado para a segunda etapa. A implementação das entidades e a documentação do domínio estão pendentes.

  Sobre o proeto:

Este repositório reúne o código-fonte e a documentação do sistema desenvolvido pela equipe.

A segunda etapa tem como objetivo implementar a modelagem do domínio definida na primeira entrega, representar as entidades em um diagrama de classes UML e documentar a arquitetura do projeto.

O foco desta entrega é o domínio: entidades, atributos, relacionamentos e comportamentos que modificam seu estado. Não é necessário implementar Controllers, Services ou endpoints nesta etapa.

**Checklist da segunda etapa**

**Repositório e colaboração**

- [x] Criar o repositório.
- [x] Adicionar este documento como `README.md`.
- [x] Garantir que todos os integrantes tenham acesso ao repositório.
- [ ] Disponibilizar todo o código desenvolvido no GitHub.
- [ ] Configurar o `.gitignore` conforme as ferramentas utilizadas.
      
**Modelagem do domínio**

- [ ] Implementar todas as entidades previstas para o sistema.
- [ ] Definir os atributos e seus respectivos tipos.
- [ ] Mapear os relacionamentos com JPA.
- [ ] Implementar métodos modificadores de estado que respeitem as regras do domínio.
- [ ] Evitar setters genéricos em alterações que exigem comportamento específico do negócio.

**Documentação**

- [ ] Criar o diagrama de classes UML.
- [ ] Incluir atributos, métodos modificadores, relacionamentos e multiplicidades.
- [ ] Conferir a coerência entre o diagrama e o código.
- [ ] Disponibilizar o diagrama no repositório como imagem, PDF ou arquivo de modelagem.
- [ ] Validar a arquitetura proposta com a equipe.
- [ ] Documentar os pré-requisitos e os comandos reais de execução.

**Escopo desta entrega**

**Objetivo principal:** representar o domínio do sistema em código e em um diagrama de classes consistente.

**Entidades e atributos**

As entidades devem corresponder aos conceitos definidos na primeira entrega. Cada entidade deverá apresentar atributos com tipos adequados e identificação compatível com seu mapeamento JPA.

**Entidades previstas:** [Preencher com os nomes e as responsabilidades das entidades do sistema.]

**Relacionamentos com JPA**

Utilizar as anotações conforme as necessidades do domínio:

| Anotação | Relacionamento |
|---|---|
| `@OneToOne` | Um para um |
| `@OneToMany` | Um para muitos |
| `@ManyToOne` | Muitos para um |
| `@ManyToMany` | Muitos para muitos |

Não é obrigatório utilizar todas as anotações. Cada relacionamento deve refletir uma necessidade real do sistema.

Definir o lado proprietário e, quando aplicável, o `mappedBy`. Em relacionamentos bidirecionais, manter os dois lados consistentes. Configurações de cascata e remoção de órfãos devem seguir o ciclo de vida das entidades.

**Métodos modificadores de estado**

As alterações de estado devem expressar ações do negócio, validar suas condições e preservar a consistência das entidades.

Por exemplo, se o domínio possuir pedidos, ações como `confirmarPedido()` ou `cancelarPedido()` são preferíveis a um setter genérico de status. Esses nomes são apenas ilustrativos e devem ser adaptados ao sistema.

**Fora do escopo**

- Implementação de Controllers.
- Implementação de Services.
- Criação de endpoints HTTP.
- Desenvolvimento de interface gráfica.

**Tecnologias**

| Área | Tecnologia ou definição |
|---|---|
| Linguagem | Java |
| Mapeamento das entidades | Jakarta Persistence (JPA), com Spring Data JPA |
| Provedor JPA | Hibernate, padrão do Spring Boot com Spring Data JPA |
| Framework de aplicação | Spring Boot |
| Ferramenta de build | Maven ou Gradle — a definir pela equipe |
| Banco de dados | A definir, caso seja utilizado nesta etapa |
| Modelagem UML | Ferramenta escolhida pela equipe |
| Versionamento | Git e GitHub |

As versões do Java e do Spring Boot deverão ser registradas após a configuração do projeto. Para utilizar JPA, incluir a dependência `spring-boot-starter-data-jpa`. Caso a persistência seja configurada, incluir também o driver do banco escolhido.

No Spring Boot 3 ou superior, utilizar as anotações de persistência do pacote `jakarta.persistence`.

**Arquitetura**

**Escolha proposta: arquitetura em camadas**

A arquitetura proposta é uma aplicação monolítica em Java e Spring Boot, organizada em camadas, com separação entre domínio, aplicação, infraestrutura e apresentação.

**Esta escolha deve ser validada pela equipe**, considerando o sistema definido na primeira entrega. Nesta etapa, apenas a camada de domínio precisa ser implementada; as demais representam a organização prevista para a evolução do projeto. O uso do Spring Boot não exige a criação de Controllers, Services ou endpoints nesta entrega.

**Justificativa**

A arquitetura em camadas é proposta pelos seguintes motivos:

- **Necessidades do negócio:** permite concentrar as regras e os comportamentos nas entidades, favorecendo uma representação clara do domínio.
- **Complexidade do sistema:** oferece separação de responsabilidades sem introduzir a complexidade operacional de serviços distribuídos.
- **Características da equipe:** facilita a divisão de tarefas e a revisão de código em um projeto acadêmico colaborativo. A adequação deve ser confirmada conforme a experiência dos integrantes.
- **Evolução:** permite adicionar persistência, casos de uso e interfaces em etapas posteriores, sem misturar essas responsabilidades com o comportamento das entidades.

As anotações JPA nas entidades atendem ao requisito desta entrega. Isso representa um acoplamento explícito do modelo ao mapeamento de persistência, aceito na proposta.

**Responsabilidades das camadas**

| Camada | Responsabilidade | Situação nesta etapa |
|---|---|---|
| Domínio | Entidades, atributos, relacionamentos e comportamentos de negócio | Implementar |
| Aplicação | Coordenação de casos de uso e serviços de aplicação | Prevista para etapas futuras |
| Infraestrutura | Configuração técnica e acesso à persistência | Implementar apenas se necessário para validar o modelo |
| Apresentação | Controllers, endpoints e interfaces de entrada | Fora do escopo |

As entidades não devem depender de Controllers ou Services para manter suas regras e invariantes.

**Organização do código**

Estrutura sugerida para o projeto Java com Spring Boot:

- `src/main/java/[pacote-base]/`: classe principal da aplicação Spring Boot, quando configurada, acima dos demais pacotes para permitir sua descoberta automática.
- `src/main/java/[pacote-base]/domain/entity/`: entidades JPA e seus métodos de domínio.
- `src/main/java/[pacote-base]/domain/enums/`: enumerações, quando necessárias.
- `src/main/java/[pacote-base]/application/`: camada futura de casos de uso.
- `src/main/java/[pacote-base]/infrastructure/`: configurações e persistência, quando necessárias.
- `src/main/java/[pacote-base]/presentation/`: camada futura de entrada.
- `src/main/resources/`: arquivos de configuração, se utilizados.
- `src/test/java/[pacote-base]/`: testes do domínio.
- `docs/uml/`: diagrama de classes e seu arquivo editável, quando disponível.
- `README.md`: descrição, arquitetura e orientações de execução.

As pastas de camadas futuras não precisam ser criadas vazias. O pacote-base e a estrutura definitiva serão ajustados à stack escolhida.

**Diagrama de classes UML**

**Local previsto:** `docs/uml/`.

O diagrama deverá apresentar:

- Todas as entidades implementadas.
- Atributos e seus tipos.
- Métodos que modificam o estado das entidades.
- Relacionamentos e multiplicidades.
- Correspondência entre o modelo UML e o código JPA.

**Arquivo do diagrama:** [Preencher com o caminho ou link relativo após adicioná-lo ao repositório.]

**Instalação e execução**

**Situação atual**

O repositório acaba de ser criado. A stack definida é Java com Spring Boot. As versões, a ferramenta de build e os comandos de execução ainda precisam ser configurados e validados.

**Preparação prevista**

1. Obter uma cópia do repositório GitHub.
2. Instalar um JDK compatível com a versão escolhida do Spring Boot.
3. Abrir o projeto na IDE de preferência.
4. Configurar Maven ou Gradle e incluir as dependências necessárias.
5. Caso a persistência seja utilizada, configurar o banco e a conexão em `src/main/resources/application.properties` ou `application.yml`, sem versionar credenciais.
6. Compilar o código e executar os testes pelo build configurado.

Se o projeto disponibilizar Maven Wrapper ou Gradle Wrapper, utilizá-lo para manter a versão da ferramenta de build consistente entre os integrantes.

**Informações a completar antes da entrega**

| Item | Informação |
|---|---|
| URL do repositório | [Preencher] |
| Versão do Java/JDK | [Preencher] |
| Versão do Spring Boot | [Preencher] |
| Ferramenta de build e versão | [Preencher] |
| Comando de compilação | [Preencher após configurar e validar] |
| Comando de testes | [Preencher após configurar e validar] |
| Configuração de persistência | [Informar, se necessária] |

Como esta etapa não exige endpoints nem uma aplicação web em execução, a validação pode ocorrer pela compilação e pelos testes do domínio. Se houver um programa executável, documentar também seu comando de inicialização.

**Testes e validação**

Recomenda-se verificar:

- Transições de estado válidas e inválidas.
- Preservação das regras de negócio.
- Consistência dos relacionamentos bidirecionais, quando existentes.
- Correspondência entre entidades, tipos, multiplicidades e diagrama UML.

Caso a persistência seja configurada, validar também os mapeamentos JPA por meio de testes de integração.

**Convenções de desenvolvimento**

Diretrizes propostas para o início do projeto:

- Manter alterações pequenas e focadas.
- Utilizar mensagens de commit claras e descritivas.
- Registrar tarefas e problemas nas issues, quando disponíveis.
- Documentar mudanças que afetem a instalação ou o uso do projeto.
- Não versionar senhas, tokens, chaves de API ou outros dados sensíveis.
- Quando houver variáveis de ambiente, disponibilizar um arquivo `.env.example` sem credenciais reais.

**Contribuição**

Todos os integrantes deverão ter acesso ao repositório.

Antes de implementar uma entidade, alinhar sua responsabilidade, seus relacionamentos e suas regras com a equipe. Alterações no modelo devem ser acompanhadas pela atualização do diagrama UML.

O fluxo de branches e revisão será definido em conjunto.


**Equipe**
Ervin Leite, Luiz Henrique e Luís Felipe Falcone
