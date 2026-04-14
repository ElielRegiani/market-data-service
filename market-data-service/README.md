# Market Data Service

Market Data Service em Kotlin + Spring Boot para ingestao, processamento, enriquecimento e distribuicao de dados de mercado.

## Stack

- Kotlin 1.9 + Spring Boot 3
- Spring Batch
- PostgreSQL + Flyway
- Apache Kafka
- Resilience4j
- Gradle
- Docker / Docker Compose

## Arquitetura

Estruturado em Hexagonal Architecture:

- `domain`: modelos e regras de negocio puras
- `application`: casos de uso e DTOs
- `infrastructure`: adapters (providers, banco, Kafka, configs)
- `entrypoint`: REST e Batch

## Endpoints

- `GET /market-data/{symbol}`: preco atual enriquecido
- `GET /market-data/{symbol}/history?limit=100`: historico
- `POST /market-data/{symbol}/refresh`: forca atualizacao via use case

## Job Batch

`MarketDataJob` executa os steps:

1. Fetch (com failover de providers)
2. Normalize
3. Enrich (SMA, EMA, RSI, volatilidade)
4. Persist
5. Publish (Kafka topic `market-data-updated`)

## Configuracoes principais

Arquivo: `src/main/resources/application.yaml`

- `app.market-data.symbols`
- `app.market-data.job.enabled`
- `app.market-data.job.cron`
- `app.market-data.job.zone`
- `app.market-data.providers.active`
- `app.kafka.topics.market-data-updated`

## Executar local

1. Suba infra:

```bash
docker compose up -d postgres zookeeper kafka
```

2. Rode a aplicacao:

```bash
./gradlew bootRun
```

## Executar tudo com Docker

```bash
docker compose up -d --build
```

O `Dockerfile` e multi-stage: compila com Gradle dentro da imagem e copia apenas `build/libs/app.jar` (nome fixo via `tasks.bootJar`). Porta do processo: **8080** (`EXPOSE 8080`).

## Testes

```bash
./gradlew test
```

