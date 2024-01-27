# dapr-devservices

This repository aims to use and test Quarkus Dapr DevServices.

## Getting Started

1. Clone Quarkus Dapr Extension.

```shell
git clone git@github.com:mcruzdev/quarkus-dapr.git
```

2. Go to the `quarkus-dapr` repository.

```shell
cd quarkus-dapr
```

3. Go to `try-devservices` branch.

```shell
git checkout try-devservices
```

4. Install the extension.

```shell
mvn clean install
```

5. Clone this repository.

```shell
git clone git@github.com:mcruzdev/dapr-devservices.git
```

6. Go to the `dapr-devservices` repository.


By default, if there is no component inside (`src/main/resources/components`) configured, the Quarkus DevServices will add PubSub and State Store in memory.

In this example we will use Redis for PubSub and StateStore:

7. Execute Redis container:

```shell
docker run -d -p 6379:6379 redis --requirepass ""
```

If you want to use a in-memory, you need to delete all files from `components` folder. We need to implement a way to get in-memory behavior through application.properties.

8. Execute all services.

Subscriber:

```shell
cd subscriber && ./mvnw quarkus:dev
```

Writer:
```shell
cd writer && ./mvnw quarkus:dev
```

Reader:

```shell
cd reader && ./mvnw quarkus:dev
```

9. Testing the endpoint

- Creating a message

```shell
curl --request POST \
  --url 'http://localhost:8081/messages?message=Hello' \
  --header 'Content-Type: application/json' \
  --header 'User-Agent: insomnia/8.5.1'
```

- Reading all messages

```shell
curl --request GET \
  --url http://localhost:8082/messages \
  --header 'User-Agent: insomnia/8.5.1'
```

- Seeing all messages received from PubSub

    **You need to see the `subscriber` logs.**

