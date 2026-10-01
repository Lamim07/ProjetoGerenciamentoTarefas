$ErrorActionPreference = 'Stop'

try {
    $arquivoCredencial = Join-Path $PSScriptRoot 'credenciais-sql.dat'
    if (-not (Test-Path -LiteralPath $arquivoCredencial)) {
        throw 'Arquivo de credenciais nao encontrado. Reconfigure as credenciais do SQL Server.'
    }

    $conteudo = (Get-Content -LiteralPath $arquivoCredencial -Raw).Trim()
    $senhaProtegida = ConvertTo-SecureString $conteudo
    $env:TAREFAS_DB_URL = 'jdbc:sqlserver://127.0.0.1:1433;databaseName=GerenciamentoTarefas;encrypt=true;trustServerCertificate=true'
    $env:TAREFAS_DB_USER = 'projetojava'
    $env:TAREFAS_DB_PASSWORD = [System.Net.NetworkCredential]::new('', $senhaProtegida).Password

    & (Join-Path $PSScriptRoot 'executar.ps1')
} catch {
    Write-Host ('Erro ao iniciar: ' + $_.Exception.Message) -ForegroundColor Red
    exit 1
} finally {
    Remove-Item Env:TAREFAS_DB_PASSWORD -ErrorAction SilentlyContinue
}
