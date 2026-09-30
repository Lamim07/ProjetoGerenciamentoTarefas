IF DB_ID(N'GerenciamentoTarefas') IS NULL
    CREATE DATABASE GerenciamentoTarefas;
GO
USE GerenciamentoTarefas;
GO
IF OBJECT_ID(N'dbo.usuarios', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.usuarios (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nome NVARCHAR(120) NOT NULL,
        nome_usuario NVARCHAR(60) NOT NULL UNIQUE,
        email NVARCHAR(180) NOT NULL UNIQUE,
        senha_hash NVARCHAR(300) NOT NULL
    );
END;
GO
IF OBJECT_ID(N'dbo.tarefas', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.tarefas (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        titulo NVARCHAR(180) NOT NULL,
        descricao NVARCHAR(MAX) NOT NULL,
        criador_id BIGINT NOT NULL,
        responsavel_id BIGINT NOT NULL,
        prazo DATE NOT NULL,
        prioridade NVARCHAR(10) NOT NULL,
        status NVARCHAR(20) NOT NULL CONSTRAINT DF_tarefas_status DEFAULT N'PENDENTE',
        criada_em DATETIME2(0) NOT NULL CONSTRAINT DF_tarefas_criada_em DEFAULT SYSDATETIME(),
        CONSTRAINT FK_tarefas_criador FOREIGN KEY (criador_id) REFERENCES dbo.usuarios(id),
        CONSTRAINT FK_tarefas_responsavel FOREIGN KEY (responsavel_id) REFERENCES dbo.usuarios(id),
        CONSTRAINT CK_tarefas_prioridade CHECK (prioridade IN (N'BAIXA', N'MEDIA', N'ALTA')),
        CONSTRAINT CK_tarefas_status CHECK (status IN (N'PENDENTE', N'EM_ANDAMENTO', N'CONCLUIDA'))
    );
    CREATE INDEX IX_tarefas_criador ON dbo.tarefas(criador_id);
    CREATE INDEX IX_tarefas_responsavel ON dbo.tarefas(responsavel_id);
END;
GO
