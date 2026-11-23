# backsgcrweb

**SGCR Web Back-End — Sistema de Gestão de Contas a Receber**

Back-end em **Java + Spring Boot** do **SGCR (Sistema de Gestão de Contas a Receber)**, um ERP financeiro e de vendas voltado para o varejo. Este repositório nasce da refatoração do projeto legado desktop (`prg03projetosgcr` em Java Swing) para uma **API REST**, desacoplando a camada visual para atender o front-end Web SPA em React ([`prg04sgcrweb`](https://github.com/joao-luis7/prg04sgcrweb)) e clientes HTTP.

## Proposta da Refatoração (Evolução Arquitetural)

- **Desacoplamento da View (Swing -> API REST):** Remoção gradual da camada visual desktop (`view/` e `SpringContext`) e conversão dos controladores em endpoints REST (`@RestController`) com retorno em JSON via `ResponseEntity<?>`.
- **Arquitetura em Camadas (*Package by Feature*):** Separação clara de responsabilidades por domínio (`Controller` -> `Service` -> `Repository` -> `PostgreSQL`), utilizando classes de modelo (`Entity` / `DTO`) apenas para o trânsito de dados entre camadas.
- **Integração Front-End / Back-End:** Exposição de endpoints padronizados (`GET`, `POST`, `PUT`, `DELETE`, `PATCH`) com códigos de status HTTP adequados, validação *fail-fast* e tratamento centralizado de regras de negócio.

## Módulos de Domínio

O código-fonte reside em `src/main/java/br/com/ifba/prg03projetosgcr/` e é organizado por funcionalidade:

- **`categoria/`** — Agrupamento e gerenciamento lógico de categorias de produtos.
- **`produto/`** — Catálogo de itens, controle de estoque e alerta de estoque crítico (`<= 5` unidades).
- **`cliente/`** — Cadastro de clientes, endereço (`Endereco`), limite de crédito opcional e controle de `saldoDevedor`.
- **`venda/`** — Registro de vendas (`Venda`, `ItemVenda`, `StatusVenda`) nas modalidades à vista (`DINHEIRO`, `CARTAO`, `PIX`) e a prazo (`FIADO`), com baixa de estoque e cancelamento transacional.
- **`pagamento/`** — Recebimento de contas (`Pagamento`, `FormaPagamento`) com abatimento no `saldoDevedor` e amortização FIFO (das vendas em `FIADO` mais antigas para as mais recentes).
- **`funcionario/`** — Autenticação e controle de acesso por perfil (`PerfilAcesso`: `ADMIN`, `CAIXA`).
- **`dashboard/`** — Consultas analíticas de faturamento, vendas do dia, clientes ativos e inadimplência.
- **Pacotes transversais:** `core/` (`Transacao`), `infrastructure/` (`PersistenceEntity`, `RegraNegocioException`), `config/` e `util/` (`ValidatorUtil`, `CampoPesquisaUtil`).

## Stack Tecnológica

- **Linguagem:** Java 21
- **Framework:** Spring Boot 4.0.6 (Spring Data JPA / Hibernate; evolução para Spring Web)
- **Banco de Dados:** PostgreSQL (Neon)
- **Utilitários:** Lombok, SLF4J
- **Build:** Maven Wrapper (`mvnw` / `mvnw.cmd`)

## Como Executar Localmente

1. Crie o arquivo `.env.properties` na raiz do projeto (ignorado pelo Git) com as credenciais do PostgreSQL:

```properties
DB_URL=jdbc:postgresql://<host>/<database>?sslmode=require
DB_USERNAME=<usuario>
DB_PASSWORD=<senha>
```

2. Compile, execute os testes ou inicie a aplicação usando o Maven Wrapper:

```bash
./mvnw clean compile
./mvnw test
./mvnw spring-boot:run
```

No Windows (PowerShell/CMD), utilize `mvnw.cmd` no lugar de `./mvnw`.
