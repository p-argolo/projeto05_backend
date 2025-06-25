## Usar a JDK 21 como base para rodar a aplicação
#FROM eclipse-temurin:21-jdk
#
## Criar diretório da aplicação dentro do container
#WORKDIR /app
#ENV MAPS_API_KEY=AIzaSyC3GUEkNJMlRX8K27sjqHe7nDowP-VSTNQ
#
## Copiar o jar para dentro do container
#COPY target/LocalSeguro-0.0.1-SNAPSHOT.jar app.jar
#
## Expor a porta 1602 para fora do container
#EXPOSE 1602
#
## Comando para rodar a aplicação na porta 1602
#ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=1602"]
#
#


FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY . .

RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app
EXPOSE 1602

COPY --from=build /app/target/LocalSeguro-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]