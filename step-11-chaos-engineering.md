# Step 11: Using Testcontainers for Chaos Engineering

So far we have tested our system under very expected conditions.
But in reality, we know that things can go wrong.
The network can be slow, the database can be unavailable, and so on.

In this step, we will use Testcontainers to simulate such conditions and see how our system behaves.

For this, we will use the [Toxiproxy](https://www.testcontainers.org/modules/toxiproxy/) module.

Check out the documentation and write a test that checks the following test scenario:
1. Initially, PostgreSQL is available, and we can record a rating.
2. We then simulate a network outage between our application and PostgreSQL using Toxiproxy, and we expect the endpoint to return an error.
3. We then simulate a network recovery, and we expect the endpoint to return a success.

## Hint

You need to add the Toxiproxy module to your project's dependencies.
You also need a Toxiproxy client, such as:
```gradle
testImplementation("eu.rekawek.toxiproxy:toxiproxy-java:2.1.0")
```

```maven
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>toxiproxy</artifactId>
    <scope>test</scope>
</dependency>
```

```java
    static final Network network = Network.newNetwork();

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine")
            .withNetwork(network)
            .withNetworkAliases("postgres");

    static final ToxiproxyContainer toxiproxy = new ToxiproxyContainer("ghcr.io/shopify/toxiproxy:2.5.0")
            .withNetwork(network);
    
    protected static Proxy postgresProxy;
    
    ...

ToxiproxyClient toxiproxyClient = new ToxiproxyClient(toxiproxy.getHost(), toxiproxy.getControlPort());
postgresProxy = toxiproxyClient.createProxy("postgres", "0.0.0.0:8666", "postgres:5432");
final String ipAddressViaToxiproxy = toxiproxy.getHost();
final int portViaToxiproxy = toxiproxy.getMappedPort(8666);
        ...
registry.add("spring.datasource.url", () -> "jdbc:postgresql://" + ipAddressViaToxiproxy + ":" + portViaToxiproxy + "/" + postgres.getDatabaseName());

```
