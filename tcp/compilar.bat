@echo off
echo ================================================
echo   Compilando proyecto TCP
echo ================================================
echo.

if not exist bin mkdir bin

javac -d bin src/comun/*.java src/servidor/*.java src/cliente/*.java src/pruebas/*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ================================================
    echo   Compilacion exitosa!
    echo ================================================
) else (
    echo.
    echo ================================================
    echo   Error en la compilacion
    echo ================================================
)

pause
