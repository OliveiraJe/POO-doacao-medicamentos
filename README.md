# POODoacao — Doação de Medicamentos

Sistema desktop em **JavaFX + SQLite** para doação de medicamentos entre doadores (pessoas físicas) e receptores (instituições).

**Integrantes:** Henrique Oliveira · Jefferson Pereira de Oliveira

**Tema:** gestão de doações de medicamentos.

## Problema resolvido

Medicamentos em bom estado ficam parados em casa ou são descartados de forma incorreta, enquanto instituições carentes não têm como localizá-los. O sistema cadastra doadores e receptores, mantém o estoque de medicamentos disponíveis (com lote, quantidade e validade) e registra/cancela doações, devolvendo a quantidade ao estoque quando uma doação é cancelada.

## Entidades

| Entidade | Descrição |
|---|---|
| `Perfil` | Dados comuns de acesso (nome, e-mail, senha em SHA-256, telefone, endereço) e tipo: doador ou receptor |
| `Doador` | Perfil doador identificado por CPF |
| `Receptor` | Perfil receptor identificado por CNPJ |
| `Medicamento` | Item doado: nome, descrição, lote, quantidade e validade, vinculado ao CPF do doador |
| `Doacao` | Código, medicamento, doador, receptor, data e quantidade doada |

## Execução

Requisitos: **Java 21+**. Não é necessário instalar JavaFX nem o driver SQLite.

```bash
./gradlew run
```

Ou usando o jar empacotado (download na aba [Releases](../../releases)):

```bash
java -jar POODoacao-all.jar
```

O banco `app.db` é criado no diretório atual na primeira execução, a partir de `app/src/main/resources/db/schema.sql`. Para gerar o jar: `./gradlew fatJar` (saída em `app/build/libs/`).

## Divisão de responsabilidades

| Integrante | Responsabilidades |
|---|---|
| Henrique Oliveira | Banco/Controller, login, cadastro de perfis, painel do receptor e doações |
| Jefferson Pereira de Oliveira | Telas de cadastro, painel do doador e cadastro de medicamentos |

## Vídeo de apresentação

Link do YouTube (público ou não listado): _(a preencher)_
