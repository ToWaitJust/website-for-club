# 后端启动脚本:读取同目录下 .env 注入环境变量,再启动 yudao-server.jar
# 用法: 在 PowerShell 进入 yudao-backend 目录后执行  .\start-server.ps1

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

# 计算脚本所在目录:$PSScriptRoot 优先,失败则退回当前目录(本脚本约定在 yudao-backend 下运行)
$here = $PSScriptRoot
if (-not $here) { $here = (Get-Location).Path }
Set-Location $here

# 读取 .env(K=V 逐行解析,跳过空行与 # 注释)
$envFile = Join-Path $here ".env"
if (-not (Test-Path $envFile)) {
    Write-Host "[错误] 未找到 $envFile ,请先复制 .env.example 为 .env" -ForegroundColor Red
    exit 1
}
Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()
    if ($line -and (-not $line.StartsWith("#")) -and $line.Contains("=")) {
        $idx = $line.IndexOf("=")
        $k = $line.Substring(0, $idx).Trim()
        $v = $line.Substring($idx + 1).Trim()
        # 去掉可能包住值的引号
        if ($v.Length -ge 2) {
            if (($v.StartsWith('"') -and $v.EndsWith('"')) -or ($v.StartsWith("'") -and $v.EndsWith("'"))) {
                $v = $v.Substring(1, $v.Length - 2)
            }
        }
        Set-Item ("Env:" + $k) $v
    }
}
Write-Host "[信息] 已加载 .env 连接配置" -ForegroundColor Green

# 定位并启动后端 jar(用绝对路径,避免相对路径解析问题)
$jar = Join-Path $here "yudao-server\target\yudao-server.jar"
if (-not (Test-Path $jar)) {
    Write-Host "[错误] 未找到 $jar ,请先 mvn package" -ForegroundColor Red
    exit 1
}

$java = "D:\dev-tools\java\bin\java.exe"
if (-not (Test-Path $java)) { $java = "java" }
Write-Host "[启动] java -jar $jar --server.port=48080" -ForegroundColor Cyan
& $java -jar $jar "--server.port=48080"