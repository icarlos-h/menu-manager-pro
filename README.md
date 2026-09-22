# Menu Manager Pro

Sistema full stack para administrar cardápios de várias unidades, com catálogo público e painéis para administradores e usuários de unidade.

## Funcionalidades

- Cadastro de produtos, categorias, unidades e usuários.
- Autenticação com JWT e controle de acesso por perfil.
- Disponibilidade e preço de produtos por unidade.
- Cardápio público e upload de imagens de produtos.

## Tecnologias

Java 17, Spring Boot, Spring Security, Spring Data JPA, H2, Vue 3, Vue Router e Vite.

## Executar localmente

Requisitos: JDK 17 ou superior, Node.js compatível com Vite 7 e npm. O Maven Wrapper está em `backend/`.

Em um terminal, gere uma chave local e inicie a API:

```sh
cd backend
export SECURITY_JWT_SECRET="$(openssl rand -hex 32)"
./mvnw spring-boot:run
```

Em outro terminal:

```sh
cd frontend
npm ci
npm run dev
```

Frontend: `http://localhost:5173`. API: `http://localhost:8080`.

O seed cria a conta de demonstração `admin@menumgr.com`, senha `admin123`. O banco H2 é temporário, recriado na inicialização. Esse perfil e a conta de exemplo são destinados ao desenvolvimento local. Configure persistência, contas, chave JWT, HTTPS e políticas de acesso antes de hospedar uma instância pública.

## Verificações

```sh
cd backend
./mvnw test
```

```sh
cd frontend
npm ci
npm run build
```

## Organização

- `backend/`: API, regras de negócio, segurança e persistência.
- `frontend/`: interface Vue e integração com a API.

Arquivos de ambiente, dependências instaladas e saídas de compilação não fazem parte da versão publicada. Os arquivos e imagens de terceiros mantêm suas respectivas condições de uso.
