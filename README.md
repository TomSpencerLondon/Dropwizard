# Drop Wizard
This application is practice with Dropwizard.
https://www.toptal.com/java/dropwizard-tutorial-microservices

### What is Dropwizard
Dropwizard is a Java framework for developing ops-friendly, high-performance, RESTful web services.
Dropwizard pulls together stable, mature libraries from the Java ecosystem into a simple, light-weight package that lets you focus on getting things done.

### Application description
We are going to build a simple Dropwizard RESTful service. The service will be a simple address book that will allow you to create, read, update, and delete contacts. The service will be backed by a MySQL database.
We will use the following endpoints:
- GET /parts - to retrieve all parts from DB
- GET /part/{id} - to retrieve a part by id
- POST /parts - to create a new part
- PUT /parts/{id} - to edit an existing part
- DELETE /parts/{id} - to delete a part

We will use OAuth to authenticate the service and add some unit tests.
Instead of including 

### Dropwizard libraries
Dropwizard manages all the libraries needed to create a REST service.
- Jetty - provides a Jetty servlet container for running web applications
- Jersey - REST API implementations. It follows JAX-RS specification and is reference implementation for JAX-RS.
- Jackson - JSON serialization and deserialization
- Metrics - provides a set of tools for gathering metrics and reporting them to a variety of monitoring tools
- Guava - provides a set of core libraries that include collections, caching, primitives support, concurrency libraries, common annotations, string processing, I/O, and so on
- Logback and Slf4j - logging mechanisms
- Freemarker and Mustache - Templating engines

Other libraris included are Joda Time, Liquibase, Apache HTTP Client and Hibernate Validator used by Dropwizard for building REST services.

### Maven configuration
Dropwizard supports maven. We need to add the following dependency to the pom.xml file:
```xml
<dependencies>
  <dependency>
    <groupId>io.dropwizard</groupId>
    <artifactId>dropwizard-core</artifactId>
    <version>${dropwizard.version}</version>
  </dependency>
</dependencies>
```

and the following properties value:
```xml
<properties>
  <dropwizard.version>2.0.0</dropwizard.version>
</properties>
```

We then create a simple configuration class:

```java
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class DropwizardBlogConfiguration extends Configuration {
    private static final String DATABASE = "database";

    @Valid
    @NotNull
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();

    @JsonProperty(DATABASE)
    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }
    
    @JsonProperty(DATABASE)
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }
}
```
We also add an application.yml file for configuration:
```yaml
database:
  driverClass: com.mysql.cj.jdbc.Driver
  url: jdbc:mysql://localhost/dropwizard_blog
  user: dropwizard_blog
  password: dropwizard_blog
  maxWaitForConnection: 1s
  validationQuery: "SELECT 1"
  validationQueryTimeout: 3s
  minSize: 8
  maxSize: 32
  checkConnectionWhileIdle: false
  evictionInterval: 10s
  minIdleTime: 1 minute
  checkConnectionOnBorrow: true
```
we then add an application class in Dropwizard:

```java
import org.example.config.DropwizardBlogConfiguration;

public class DropwizardBlogApplication extends Application<DropwizardBlogConfiguration> {
    private static final String SQL = "sql";
    private static final String DROPWIZARD_BLOG_SERVICE = "Dropwizard Blog Service";
    private static final String BEARER = "Bearer";

    public static void main(String[] args) throws Exception {
        new DropwizardBlogApplication().run(args);
    }

    @Override
    public void run(DropwizardBlogConfiguration configuration, Environment environment) {
        // Datasource configuration
        DataSource dataSource = configuration.getDataSourceFactory().build(environment.metrics(), SQL);
        DBI dbi = new DBI(dataSource);
        
        // Register Health Check
        DropwizardBlogApplicationHealthCheck healthCheck = new DropwizardBlogApplicationHealthCheck(
                db.onDemand(PartsService.class)
        );
        
        environment.healthChecks().register(DROPWIZARD_BLOG_SERVICE, healthCheck);
        
        // Register OAuth authentication
        environment.jersey()
                .register(new AuthDynamicFeature(
                        new OAuthCredentialAuthFilter.Builder<>()
                                .setAuthenticator(new OAuthAuthenticator())
                                .setAuthorizer(new DropwizardBlogAuthorizer())
                                .setPrefix(BEARER)
                                .buildAuthFilter()));
        
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        
        // Register resources
        environment.jersey().register(new PartsResource(dbi.onDemand(PartsService.class)));
    }
}
```

Here we override the Dropwizard run method and instantiate DB connnection, registering our custom health check, initializing
OAuth authentication and registering our Dropwizard resource.

