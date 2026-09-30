# Gerenciamento de Tarefas — Java Swing e SQL Server

Aplicação desktop para cadastro de usuários, login, criação e atribuição de tarefas, listagens de tarefas criadas e recebidas, edição, atualização de status e exclusão. O código separa `model`, `view`, `controller`, `service`, `dao` e `util`.

## Requisitos

- JDK 17 ou superior (a máquina de desenvolvimento usa JDK 26).
- SQL Server com autenticação SQL habilitada e uma conta autorizada a usar o banco.
- O driver JDBC da Microsoft já está em `lib/`. Como alternativa, use Maven com `pom.xml`.

## Preparar o banco

Execute `sql/01-criar-banco.sql` no SQL Server Management Studio ou com `sqlcmd` usando uma conta com permissão para criar banco e tabelas. O script é idempotente e cria `GerenciamentoTarefas`, `usuarios` e `tarefas`.

Inicie a instância SQL Server e habilite TCP/IP. A URL padrão usa `localhost:1433`. Se a instância usar outra porta ou nome, configure `TAREFAS_DB_URL`.

## Configurar e executar no PowerShell

```powershell
$env:TAREFAS_DB_URL = 'jdbc:sqlserver://localhost:1433;databaseName=GerenciamentoTarefas;encrypt=true;trustServerCertificate=true'
$env:TAREFAS_DB_USER = 'seu_usuario_sql'
$env:TAREFAS_DB_PASSWORD = 'sua_senha_sql'
.\executar.ps1
```

Para uma conexão com certificado confiável, configure `trustServerCertificate=false` e um certificado válido. O valor `true` na URL de exemplo facilita testes locais com certificado autoassinado.

Se tiver Maven instalado, também é possível executar `mvn compile exec:java` após configurar as variáveis. Nenhuma credencial fica gravada no código ou no repositório.

## Regras da aplicação

- O login aceita nome de usuário ou e-mail.
- A senha é armazenada como hash PBKDF2 com salt, nunca em texto puro.
- O criador pode editar os dados e excluir uma tarefa.
- O responsável pode alterar o status. Quando criador e responsável são a mesma pessoa, ambas as ações estão disponíveis.
- Toda tarefa tem um criador, um responsável, um prazo, uma prioridade e um status.
- Ao criar uma tarefa, o status inicial é `PENDENTE`.

## Estrutura

```text
src/main/java/br/edu/tarefas/
  model/       Entidades e enumerações
  view/        Telas Swing
  controller/  Navegação e sessão
  service/     Validação e permissões
  dao/         Consultas JDBC
  util/        Hash de senhas
sql/          Esquema do SQL Server
lib/          Driver JDBC da Microsoft
```
