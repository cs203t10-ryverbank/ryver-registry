## ryver-registry

Ryver Registry is the service registry for the entire system. It is built on top of [Netflix Eureka](https://github.com/Netflix/eureka), with the main purpose of **coordinating all other microservices**. Ryver Registry keeps track of all microservices registered in the system, and allows different microservices to communicate to each other without hard-coding any host addresses or port numbers.

> Without the Ryver Registry service, all other services will not be able to coordinate with each other.

Ryver Registry runs on the standard Eureka port of `8761`. To view a dashboard of all running services, visit the [dashboard](http://localhost:8761/) in your browser.

### Registering a client

To be detectable, a client must register with Eureka and provide metadata about itself -- such as host, port, and other details. Eureka receives heartbeat messages from each instance of the client, and keeps track of all instances of the client.

To register a client, simply have the `spring-cloud-starter-netflix-eureka-client` dependency on the classpath.

```xml
  <dependencies>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <!-- other dependencies -->
  </dependencies>
```

Define the port number of the client service in `src/main/resources/application.yml`.

```yml
server:
  port: <your-client-service-port>
```

Finally, define the name of the client service and the location of the Eureka Server in `src/main/resources/application.yml`.

```yml
spring:
  application:
    name: <your-client-service-name>
eureka:
  client:
    service-url:
      default-zone: ${EUREKA_URL:http://localhost:8761}/eureka
  instance:
    prefer-ip-address: true
```

Eureka will automatically configure the Spring Boot application to register itself with the defined Eureka Server.

### Using the registry

To use the registry to find other services to talk to, first activate the `DiscoveryClient` with the `@EnableDiscoveryClient` annotation on the Spring Boot application.

```java
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class YourClientApplication {
    @Autowired
    private DiscoveryClient discoveryClient;

    public static void main(String[] args) {
        List<ServiceInstance> instances = discoveryClient.getInstances("<service-name>");
        // Get the URI of the first instance of "<service-name>".
        URI uri = instances.get(0).getUri();
        ...
    }
}
```

The discovery client lets you find all instances of a client service, given its service name as defined by its `spring.application.name` property in its `src/main/resources/application.yml`.

The `ServiceInstance` interface exposes `getUri()`, `getHost()`, and `getPort()` methods amongst other things. You can then use these to perform the necessary REST interactions with the right service.

## ryver-gateway

Ryver Gateway acts as the public-facing service that handles all requests for the entire Ryver Bank API. It is built on top of [Netflix Zuul](https://github.com/Netflix/zuul).

All requests made will first arrive at Ryver Gateway, then be redirected to the appropriate client microservice accordingly. Ryver Gateway is also able to naturally load-balance requests by integrating with Ryver Registry and selecting appropriate instances of a required client service.

As the public-facing service, Ryver Gateway runs on port `8080` -- the default for Spring Boot applications. To prevent conflicts, ensure that your client service sets a unique port number in its `application.yml`.

### Configuring the route

To redirect a request to a certain client service, add an entry to `src/main/resources/application.yml`.

For example, to redirect all requests for `localhost:8080/auth/...` to the `ryver-auth` service:

```yml
zuul:
  sensitive-headers: Cookie,Set-Cookie  # Ensure that all cookies are passed through the proxy.
  routes:
    auth:
      path: /auth/**
      service-id: ryver-auth
```

