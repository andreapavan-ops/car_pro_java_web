FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copia le dipendenze
COPY lib/ ./lib/
COPY database/ ./database/
COPY resources/ ./resources/

# Copia il codice sorgente
COPY src/ ./src/

# Compila con il classpath corretto
RUN mkdir -p out && \
    javac -cp "lib/*" -d out \
    src/dao/*.java \
    src/model/*.java \
    src/util/*.java \
    src/logic/*.java \
    src/gui/components/*.java \
    src/gui/*.java

# Esegui con classpath che include sia le classi compilate che i JAR
CMD ["java", "-cp", "out:lib/*", "gui.MainFrame"]