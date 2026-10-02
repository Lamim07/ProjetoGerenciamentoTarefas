param([switch]$SomenteCompilar)

$ErrorActionPreference = 'Stop'
$base = $PSScriptRoot
$driver = Join-Path $base 'lib\mssql-jdbc-13.4.0.jre11.jar'
$classes = Join-Path $base 'build\classes'
$fontesDir = Join-Path $base 'src\main\java'

try {
    if (-not (Test-Path -LiteralPath $driver -PathType Leaf)) {
        throw "Driver JDBC não encontrado: $driver"
    }
    if (-not (Test-Path -LiteralPath $fontesDir -PathType Container)) {
        throw "Código-fonte não encontrado: $fontesDir"
    }
    if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
        throw 'JDK 17 ou superior não encontrado. Instale o JDK e configure o PATH.'
    }
    if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
        throw 'Java não encontrado no PATH.'
    }

    $fontes = @(Get-ChildItem -LiteralPath $fontesDir -Recurse -Filter '*.java' -File |
            ForEach-Object FullName)
    if ($fontes.Count -eq 0) { throw 'Nenhum arquivo Java encontrado.' }
    New-Item -ItemType Directory -Path $classes -Force | Out-Null

    Write-Host 'Compilando o aplicativo...'
    & javac --release 17 -encoding UTF-8 -cp $driver -d $classes $fontes
    if ($LASTEXITCODE -ne 0) { throw 'Falha na compilação. Confira os erros acima.' }
    if ($SomenteCompilar) {
        Write-Host 'Compilação concluída com sucesso.'
        exit 0
    }

    $arquivoCredencial = Join-Path $base 'credenciais-sql.dat'
    if (-not (Test-Path -LiteralPath $arquivoCredencial -PathType Leaf)) {
        throw 'Arquivo de credenciais não encontrado. Reconfigure as credenciais do SQL Server.'
    }
    $conteudo = (Get-Content -LiteralPath $arquivoCredencial -Raw).Trim()
    $senhaProtegida = ConvertTo-SecureString $conteudo
    $env:TAREFAS_DB_URL = 'jdbc:sqlserver://127.0.0.1:1433;databaseName=GerenciamentoTarefas;encrypt=true;trustServerCertificate=true'
    $env:TAREFAS_DB_USER = 'projetojava'
    $env:TAREFAS_DB_PASSWORD = [System.Net.NetworkCredential]::new('', $senhaProtegida).Password

    Write-Host 'Abrindo o aplicativo...'
    & java -cp "$classes;$driver" br.edu.tarefas.App
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao iniciar a aplicação.' }
} catch {
    Write-Host ('Erro ao iniciar: ' + $_.Exception.Message) -ForegroundColor Red
    exit 1
} finally {
    Remove-Item Env:TAREFAS_DB_PASSWORD -ErrorAction SilentlyContinue
}
