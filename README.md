# CarPro Ferrari

Progetto monorepo contenente due applicazioni:

## Struttura

```
├── desktop/    → Applicazione Java Swing (GUI desktop)
└── web/        → Applicazione Spring Boot (Web server)
```

## Desktop (Java Swing)

Applicazione desktop per la gestione del catalogo auto.

```bash
cd desktop
java -jar bin/app.jar
```

## Web (Spring Boot)

Applicazione web accessibile su `http://localhost:8081/`

```bash
cd web
mvn spring-boot:run
```
