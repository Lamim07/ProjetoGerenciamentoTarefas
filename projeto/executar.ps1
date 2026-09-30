$ErrorActionPreference = 'Stop'
$base = $PSScriptRoot
$driver = Join-Path $base 'lib\mssql-jdbc-13.4.0.jre11.jar'
$classes = Join-Path $base 'build\classes'
if (-not (Test-Path -LiteralPath $driver)) {
    throw "Driver JDBC não encontrado: $driver"
}
New-Item -ItemType Directory -Path $classes -Force | Out-Null
$fontes = @(Get-ChildItem -LiteralPath (Join-Path $base 'src\main\java') -Recurse -Filter '*.java' | ForEach-Object FullName)
if ($fontes.Count -eq 0) { throw 'Nenhum código Java encontrado.' }
& javac --release 17 -encoding UTF-8 -cp $driver -d $classes $fontes
if ($LASTEXITCODE -ne 0) { throw 'Falha na compilação.' }
& java -cp "$classes;$driver" br.edu.tarefas.App
if ($LASTEXITCODE -ne 0) { throw 'Falha ao iniciar a aplicação.' }
