# Contributing

## Conventional Commits

Mensagens de commit seguem o padrão [Conventional Commits](https://www.conventionalcommits.org/):

```
<tipo>(<escopo opcional>): <descrição curta>

[corpo opcional]

[footer opcional]
```

Tipos aceitos:

- `feat` — nova funcionalidade visível para o usuário
- `fix` — correção de bug
- `refactor` — mudança de código sem alterar comportamento
- `docs` — apenas documentação
- `test` — testes
- `chore` — tarefas operacionais (deps, build, ci)
- `perf` — melhoria de performance
- `style` — formatação, ponto-e-vírgula, sem mudança de código

Exemplos:

```
feat(session): exibir "última vez" abaixo do campo de carga
fix(auth): tratar 401 no interceptor do axios
chore(ci): adicionar passo de cache do maven
```

## Branches

- `main`: protegida, deploy automático para produção via GitHub Actions
- `feat/*`, `fix/*`: branches de trabalho, abrem PR contra `main`

## Code style

- **Backend**: Java 21, Maven, formatação padrão IntelliJ. Constructor injection (sem `@Autowired` em campos).
- **Frontend**: TypeScript estrito, ESLint, componentes funcionais com hooks. Schemas Zod para formulários.
- Identificadores **em inglês**, strings de UI **em pt-BR**.

## Rodando localmente

```bash
docker compose up -d         # mysql + backend + frontend (build na primeira vez)
# Frontend em http://localhost:5173
# API em http://localhost:8080  Swagger em http://localhost:8080/swagger-ui.html
```

## Rodando testes

```bash
cd backend && mvn verify          # inclui Testcontainers (precisa de Docker)
cd frontend && npm run build      # build é o "test" mínimo do front
```
