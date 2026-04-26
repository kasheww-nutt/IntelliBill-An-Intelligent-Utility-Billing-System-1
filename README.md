# IntelliBill

IntelliBill is a Java desktop application for managing utility consumers, generating bills, recording payments, and viewing reports. It includes a JavaFX GUI and a console fallback mode.

## Features

- Add and manage residential, commercial, and industrial consumers
- Generate electricity, water, and internet bills
- Track paid amounts, pending bills, penalties, and payment records
- View dashboard and report screens with JavaFX
- Store data in MySQL when available, with a local file fallback

## Tech Stack

- Java
- JavaFX
- MySQL
- JDBC
- FXML and CSS

## Project Structure

```text
src/main/java/com/intellibill/
  controller/   JavaFX screen controllers
  database/     JDBC connection and DAO classes
  exception/    Custom exceptions
  main/         Application launchers
  model/        Domain models
  service/      Business logic and persistence services
  strategy/     Billing calculation strategies
  ui/           Navigation helper
  util/         Utility classes

src/main/resources/
  schema.sql                    MySQL database schema
  com/intellibill/ui/*.fxml      JavaFX layouts
  com/intellibill/ui/*.css       App styling
```

## Requirements

- JDK 17 or newer
- JavaFX SDK or the bundled `javafx-lib/` jars
- MySQL Server, optional but recommended
- MySQL Connector/J jar, only needed for MySQL mode

## Database Setup

1. Start MySQL.
2. Run the schema file:

```sql
SOURCE src/main/resources/schema.sql;
```

Or open `src/main/resources/schema.sql` in a MySQL client and execute it.

The app reads these environment variables:

```bat
set INTELLIBILL_DB_URL=jdbc:mysql://localhost:3306/intellibill_db?useSSL=false&serverTimezone=UTC
set INTELLIBILL_DB_USER=root
set INTELLIBILL_DB_PASSWORD=your_password
```

If MySQL is not available, the app falls back to local files in `data/`.

## Run on Windows

From the project folder:

```bat
run.bat
```

The scripts use the bundled `javafx-lib/` folder by default. To use another JavaFX SDK, set `JAVAFX_LIB` before running:

```bat
set JAVAFX_LIB=C:\path\to\javafx-sdk\lib
run.bat
```

To enable MySQL mode, download MySQL Connector/J and set `MYSQL_JAR`:

```bat
set MYSQL_JAR=C:\path\to\mysql-connector-j.jar
run.bat
```

## Compile Only

```bat
compile.bat
```

Compiled classes and copied resources are written to `out/`.

## Console Mode

After compiling, you can start the console interface directly:

```bat
java --module-path javafx-lib --add-modules javafx.controls,javafx.fxml -cp out com.intellibill.main.Launcher --console
```

If you are using MySQL, include the connector jar in the classpath.

## Notes for GitHub

Generated folders such as `out/`, `target/`, and runtime `data/` are ignored by Git. Keep source files, resources, scripts, and this README in the repository.
