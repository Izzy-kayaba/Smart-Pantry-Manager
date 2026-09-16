@echo off
setlocal

set "PROJECT_DIR=%~dp0"
set "OUTPUT_DIR=%PROJECT_DIR%build\verification"

if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

javac -d "%OUTPUT_DIR%" ^
  "%PROJECT_DIR%app\src\main\java\za\ac\richfield\smartpantry\model\PantryItem.java" ^
  "%PROJECT_DIR%app\src\main\java\za\ac\richfield\smartpantry\model\RecipeRequirement.java" ^
  "%PROJECT_DIR%app\src\main\java\za\ac\richfield\smartpantry\util\IngredientMatcher.java" ^
  "%PROJECT_DIR%verification\StrictMatchingSelfTest.java"

if errorlevel 1 exit /b %errorlevel%

java -cp "%OUTPUT_DIR%" za.ac.richfield.smartpantry.verification.StrictMatchingSelfTest
exit /b %errorlevel%
