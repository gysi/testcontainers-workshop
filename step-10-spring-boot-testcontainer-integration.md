Since Springboot version 3.1.0 Testcontainer support was natively added

https://docs.spring.io/spring-boot/docs/3.1.0/reference/htmlsingle/#features.testing.testcontainers.service-connections

```xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-testcontainers</artifactId>
			<scope>test</scope>
		</dependency>
```

```java
    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:14-alpine"))
            .withDatabaseName("workshop")
            .withUsername("testcontainers")
            .withPassword("testcontainers");
```

And add @Testcontainers to the class
