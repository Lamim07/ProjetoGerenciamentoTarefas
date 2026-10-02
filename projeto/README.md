# Gerenciamento de Tarefas — Java Swing e SQL Server

Aplicação desktop para cadastro de usuários, login, criação e atribuição de tarefas, listagens de tarefas criadas e recebidas, categorias, comentários, edição, atualização de status e exclusão. O código separa `model`, `view`, `controller`, `service`, `dao` e `util`.

## Requisitos

- JDK 17 ou superior (a máquina de desenvolvimento usa JDK 26).
- SQL Server com autenticação SQL habilitada e uma conta autorizada a usar o banco.
- O driver JDBC da Microsoft já está em `lib/`. Como alternativa, use Maven com `pom.xml`.

## Preparar o banco

O banco `GerenciamentoTarefas` deve conter as tabelas `usuarios`, `tarefas`, `categorias` e `comentarios_tarefa`. A coluna `tarefas.categoria_id` deve referenciar `categorias.id`. Os scripts de criação e migração foram executados no SQL Server e removidos deste projeto conforme solicitado. O diagrama MER na pasta superior documenta o esquema.

Inicie a instância SQL Server e habilite TCP/IP. A URL padrão usa `localhost:1433`. Se a instância usar outra porta ou nome, configure `TAREFAS_DB_URL`.

## Configurar e executar no PowerShell

Nesta instalação, abra `iniciar-aplicativo.cmd` com duplo clique no Explorador de Arquivos. O iniciador compila o código atualizado, configura a conexão local com SQL Server e abre a aplicação. A senha fica em `credenciais-sql.dat`, protegida para a conta Windows que criou o arquivo; não copie esse arquivo para outra máquina ou usuário esperando que funcione.

Para configurar manualmente em outro ambiente:

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
- Toda tarefa tem um criador, um responsável, uma categoria, um prazo, uma prioridade e um status.
- Ao criar uma tarefa, o status inicial é `PENDENTE`.
- Usuários autenticados podem cadastrar e editar categorias. A categoria `Geral` e categorias com tarefas não podem ser excluídas.
- Criador e responsável podem comentar uma tarefa; cada autor pode excluir seus próprios comentários. Excluir uma tarefa também exclui seus comentários.

## Estrutura

```text
src/main/java/br/edu/tarefas/
  model/       Entidades e enumerações
  view/        Telas Swing
  controller/  Navegação, sessão e coordenação dos casos de uso
  service/     Validação e permissões
  dao/         Consultas JDBC
  util/        Hash de senhas
lib/          Driver JDBC da Microsoft
```

As telas (`view`) recebem controllers e não acessam serviços nem DAOs diretamente.
Os controllers de tarefas, categorias e comentários são criados após o login para o
usuário da sessão. Os serviços aplicam validações e permissões; os DAOs executam JDBC.
