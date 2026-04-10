# Commerce API 🛒

REST API completa de e-commerce construída com **Java + Spring Boot**, com autenticação stateless via JWT, controle de acesso por roles e um ciclo de vida de pedidos totalmente automatizado via schedulers.

---

## Tecnologias utilizadas

| Tecnologia | Uso |
|---|---|
| Spring Boot | Framework principal |
| Spring Security + JWT | Autenticação stateless |
| Spring Data JPA | Persistência de dados |
| PostgreSQL | Banco de dados relacional |
| Lombok | Redução de boilerplate |
| OpenAPI / Swagger | Documentação da API |
| Spring Scheduler | Automações de status |

---

## Arquitetura em camadas

```
Controller  →  recebe e responde requisições HTTP
Service     →  toda a lógica de negócio e validações
Mapper      →  conversão entre entidades e DTOs
Repository  →  acesso ao banco via Spring Data JPA
Handler     →  tratamento global de exceções (GlobalExceptionHandler)
```

A separação entre entidades e DTOs garante que o modelo interno nunca é exposto diretamente na API.

---

## Autenticação e segurança

A autenticação é **stateless** — nenhuma sessão é mantida no servidor. A cada requisição, o token JWT é validado pelo `SecurityFilter` antes de qualquer processamento.

### Fluxo de autenticação

1. `POST /v1/auth/register` — cadastra o usuário com senha encriptada via BCrypt
2. `POST /v1/auth/login` — autentica e retorna o token JWT (expira em 2h)
3. Todas as demais rotas exigem o header `Authorization: Bearer <token>`

### Controle de acesso

O controle é feito em dois níveis complementares:

**1. Via `SecurityConfig` (rotas e métodos HTTP):**

| Rota | Acesso |
|---|---|
| `GET /v1/products/**` | Público |
| `GET /v1/categories/**` | Público |
| `POST /v1/auth/**` | Público |
| `POST/PUT/DELETE /v1/products` | ADMIN |
| `POST/PUT/DELETE /v1/categories` | ADMIN |
| `GET /v1/users/**` | ADMIN |
| `GET /v1/payments/**` | ADMIN |
| `GET /v1/orders` | ADMIN |
| `GET /v1/orders/**` | ADMIN |
| `PATCH /v1/orders/{id}/cancel` | ADMIN |
| Demais rotas | Autenticado |

**2. Via `@PreAuthorize` com beans customizados:**

- `@orderSecurity.validateOwner(orderId, userId)` — garante que só o dono do pedido pode pagar, cancelar itens ou confirmar entrega
- `@userSecurity.isOwner(userId, principal)` — garante que o usuário só edita/deleta a própria conta

---

## Ciclo de vida de um pedido

O pedido passa por até **6 status distintos**, com transições automáticas via `@Scheduled` e manuais via endpoints.

```
                   ┌──────────────────┐
                   │  WAITING_PAYMENT │  ← criado ao chamar POST /orders
                   └────────┬─────────┘
                            │  POST /orders/{id}/pay
                            ▼
                   ┌──────────────────┐
                   │     PENDING      │  ← Payment gerado
                   └────────┬─────────┘
                            │  @Scheduled (30s) — Payment APPROVED
                            ▼
                   ┌──────────────────┐
                   │      PAID        │  ← confirmado pelo gateway
                   └────────┬─────────┘
                            │  @Scheduled (1min) — envio simulado
                            ▼
                   ┌──────────────────┐
                   │    SHIPPED       │  ← pedido enviado
                   └────────┬─────────┘
                            │  POST /orders/{id}/confirm-delivery
                            ▼
                   ┌──────────────────┐
                   │   DELIVERED      │  ← confirmado pelo dono
                   └──────────────────┘
```

### Cancelamentos

| Quem cancela | Quando |
|---|---|
| `@Scheduled` | `WAITING_PAYMENT` expira após 30 minutos sem pagamento |
| `@Scheduled` | `PENDING` com Payment retornando status `CANCELED` |
| Admin (manual) | Qualquer status **exceto** `PAID` e `CANCELED` |

> Um pedido `PAID` não pode ser cancelado — o pagamento já foi processado.

### Detalhes das automações

| Job | Intervalo | O que faz |
|---|---|---|
| `cancelExpiredOrders` | 10 min | Cancela `WAITING_PAYMENT` com mais de 30min |
| `processApprovedPayments` | 30s | Avança `PENDING` para `PAID` ou `CANCELED` conforme status do Payment |
| `shippedOrders` | 1 min | Avança todos os pedidos `PAID` para `SHIPPED` |

---

## Endpoints principais

### Auth
| Método | Rota | Descrição |
|---|---|---|
| POST | `/v1/auth/register` | Cadastro de novo usuário |
| POST | `/v1/auth/login` | Login e geração de token |

### Orders
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| POST | `/v1/orders` | USER | Cria um novo pedido |
| GET | `/v1/orders` | ADMIN | Lista todos os pedidos |
| GET | `/v1/orders/me` | USER | Lista pedidos do usuário autenticado |
| GET | `/v1/orders/{id}` | ADMIN | Busca pedido por ID |
| DELETE | `/v1/orders/{id}` | USER (dono) | Deleta pedido em `WAITING_PAYMENT` |
| PATCH | `/v1/orders/{id}/pay` | USER (dono) | Inicia o pagamento |
| PATCH | `/v1/orders/{id}/cancel` | ADMIN | Cancela o pedido |
| PATCH | `/v1/orders/{id}/confirm-delivery` | USER (dono) | Confirma o recebimento |
| POST | `/v1/orders/{id}/items` | USER (dono) | Adiciona item ao pedido |
| PUT | `/v1/orders/{id}/items/{productId}` | USER (dono) | Atualiza quantidade de item |
| DELETE | `/v1/orders/{id}/items/{productId}` | USER (dono) | Remove item do pedido |

### Products
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/v1/products` | Público | Lista todos os produtos |
| GET | `/v1/products/{id}` | Público | Busca produto por ID |
| POST | `/v1/products` | ADMIN | Cria produto |
| PUT | `/v1/products/{id}` | ADMIN | Atualiza produto |
| DELETE | `/v1/products/{id}` | ADMIN | Remove produto |
| POST | `/v1/products/{id}/categories` | ADMIN | Adiciona categorias ao produto |
| PUT | `/v1/products/{id}/categories` | ADMIN | Define categorias do produto |
| DELETE | `/v1/products/{id}/categories` | ADMIN | Remove categorias do produto |

### Categories
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/v1/categories` | Público | Lista categorias |
| GET | `/v1/categories/{id}` | Público | Busca categoria por ID |
| POST | `/v1/categories` | ADMIN | Cria categoria |
| PUT | `/v1/categories/{id}` | ADMIN | Atualiza categoria |
| DELETE | `/v1/categories/{id}` | ADMIN | Remove categoria |

### Users
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/v1/users` | ADMIN | Lista usuários |
| GET | `/v1/users/{id}` | ADMIN | Busca usuário por ID |
| PUT | `/v1/users/{id}` | USER (dono) | Atualiza próprios dados |
| DELETE | `/v1/users/{id}` | USER (dono) | Deleta própria conta |

### Payments
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/v1/payments` | ADMIN | Lista todos os pagamentos |
| GET | `/v1/payments/{id}` | ADMIN | Busca pagamento por ID |

---

## Tratamento de erros

Todas as exceções são capturadas pelo `GlobalExceptionHandler` e retornam um corpo padronizado:

```json
{
  "timestamp": "2025-01-01T12:00:00Z",
  "status": 400,
  "message": "Cannot pay an empty order",
  "errors": null,
  "path": "/v1/orders/5/pay"
}
```

| Exceção | Status HTTP |
|---|---|
| `ResourceNotFoundException` | 404 Not Found |
| `BusinessException` e subclasses | 400 Bad Request |
| `MethodArgumentNotValidException` | 400 com mapa de campos |
| `Exception` genérica | 500 Internal Server Error |

Subclasses de `BusinessException` implementadas: `EmptyOrderException`, `OrderStatusException`, `PaymentAlreadyExistsException`, `InvalidPriceException`, `InvalidQuantityException`.

---

## Documentação interativa

Com a aplicação rodando, acesse o Swagger UI em:

```
http://localhost:8080/swagger-ui/index.html
```

---

## Como executar

### Pré-requisitos

- Java 17+
- PostgreSQL rodando localmente
- Maven

### Configuração

No `application.properties` (ou `application.yml`), configure:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/commerce_db
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update

api.security.token.secret=seu_secret_jwt_aqui
```

### Executando

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/commerce-api.git
cd commerce-api

# Execute com Maven
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

---

## O que vem por aí

- [ ] Testes unitários e de integração
- [ ] Tratamento de edge cases nos schedulers
- [ ] Integração real com gateway de pagamento
- [ ] Paginação nas listagens
- [ ] Docker + Docker Compose

---

## Autor

Feito com dedicação durante meus estudos de backend com Java e Spring Boot.  
Feedbacks são muito bem-vindos!
