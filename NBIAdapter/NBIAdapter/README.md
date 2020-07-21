This template is used to start a new microservice project using Spring-boot.
This project has been generated with springboot-microservice-archetype version 1.0.7

* [Build](#build)
  * [Maven](#maven)
    * [Maven wrapper](#maven-wrapper)
    * [Maven configuration](#maven-configuration)
    * [Profiles](#profiles)
    * [Build command](#build-command)
  * [Openshift](#openshift)
    * [odo](#odo)
      * [deploy your project dependencies](#deploy-your-project-dependencies)
      * [deploy and manage your project](#deploy-and-manage-your-project)
      * [hot code reload with Spring-boot devtools](#hot-code-reload-with-spring-boot-devtools)
* [Development](#development)
  * [Logs](#logs)
  * [Database migration](#database-migration)
  * [Database access with Spring-data-jpa](#database-access-with-spring-data-jpa)
    * [Model definition](#model-definition)
    * [CRUD and query](#crud-and-query)
    * [RSQL queries](#rsql-queries)
    * [Pagination and Sorting](#pagination-and-sorting)
  * [REST client](#rest-client)
  * [REST service](#rest-service)
    * [HTTP status code](#http-status-code)
    * [DTO to entity mapping](#dto-to-entity-mapping)
    * [Exception handling](#exception-handling)
    * [Adding HTTP links in your JSON response with Spring HATEOAS](#adding-http-links-in-your-json-response-with-spring-hateoas)
    * [Pagination](#pagination)
    * [Documentation with OpenAPI and Swagger](#documentation-with-openapi-and-swagger)
      * [Enable the REST API and UI](#enable-the-rest-api-and-ui)
  * [SOAP Web Service](#soap-web-service)
    * [Configuration](#configuration)
    * [HTTP status code](#ws-http-status-code)
    * [Messages validation](#messages-validation)
    * [Exception handling](#ws-exception-handling)
  * [Transaction](#transaction)
* [Testing](#testing)
  * [Kind of Tests](#kind-of-tests)
  * [Run the tests](#run-the-tests)
  * [Testing the database](#testing-the-database)
  * [Testing a REST service](#testing-a-rest-service)
  * [Contract tests](#contract-tests)
* [Continuous integration](#continuous-integration)
* [Continuous deployment](#continuous-deployment)
* [Production](#production)
  * [Monitoring](#monitoring)
  * [Logs configuration for production](#logs-configuration-for-production)

# Build
## Maven
We are using Maven to build this project. 

### Maven wrapper
You don’t need to have Maven installed on your environment to build it.
The script mvnw will download Maven automatically and run the mvn command afterwards. [More info](https://github.com/takari/maven-wrapper)
The maven wrapper is configured in the .mvn/wrapper directory. The file maven-wrapper.properties let you choose the maven version required
by your project.
**You can the use a different Maven version for each project** if required.

### Maven configuration
The file .mvn/jvm.config contains the JVM configuration when running Maven. You can define there the memory allocated for compilation for
example, or the proxy configuration.
[Configure Maven](https://maven.apache.org/configure.html)

### Profiles
Several different profiles exist. They are used to run the Maven in a different way for each profile :
* utTest : used to select unit tests only when running the tests with Maven
* itTest : used to select integration tests only when running the tests with Maven
* bbTest : used to select black box tests only when running the tests with Maven
* local : used to run the application in local or a developer openshift project (considered then as local).
It adds the Spring boot devtools dependencies
* openshift : used to build the application for non local environment. Add some git information and build information for Spring actuator

### Build command
To build a Spring-boot application, you compile the java source code and package it with its dependencies in a single jar. 
To do that :
```shell script
mvnw clean install
# to build and skip the tests
mvnw clean install -DskipTests -DskipITs

# By default, the spring devtools are included in the build
# For production use, you have to specify the openshift maven profile, so that they are not included
# This concern the CI pipeline mainly
mvnw clean install -P openshift
```
By default the jar version is set to 1.0-SNAPSHOT. This is not a problem for local development. For production, of course, we need
a real version number. We decided to use a version like "v1.0.0-2-g7e95aa8". To build the jar with the correct version :
```shell script
# you need an annonated tag on your branch with the proper version number, eg v1.0.0
# This gives you the version number
git describe --long
mvnw clean install -Popenshift -Drevision=v1.0.0-2-g7e95aa8
```

## Openshift
Normally, the code image is built by a CI pipeline and then pushed into Openshift using a CD pipeline.
This can not work during development phase. You need a fast feedback loop. You can’t do :
* git commit 
* wait for the pipeline to build the image, run the tests and push the image into a docker registry
* wait for the deployment pipeline to deploy this image

This is clearly not a fast feedback. If you have to do that for each line of code change ...

We then need a solution to change the code and deploy it into Openshift without committing to the git repo.

### odo
[Openshift do](https://docs.openshift.com/container-platform/4.3/cli_reference/openshift_developer_cli/understanding-odo.html)
is a Red Hat tool designed to speed up this process

#### deploy your project dependencies
Create a database if needed
```shell script
oc new-app mysql-persistent -n amurat -l app=mysql -p MYSQL_USER=user -p MYSQL_PASSWORD=pwd -p MYSQL_ROOT_PASSWORD=root -p MYSQL_DATABASE=mydb
```
If you already have a database server in your project, you can create a database and the connection secret doing the following
```shell script
# connect to your mysql pod in your project
oc project mynamespace
oc get pods
oc rsh pods/myMySQLpod
sh$ mysql -u root
mysql> create database mydb;
mysql> CREATE USER 'user'@'%' IDENTIFIED BY 'pwd';
mysql> GRANT ALL ON mydb.* TO 'user'@'%';

# create the secret with db/user/password information
oc create secret generic myapplication-mysql-secret --from-literal=database-name=mydb --from-literal=database-user=user --from-literal=database-password=pwd
```
If you need to access the database from your laptop, forward the port to the target database pod
```shell script
# get the mysql pod
oc get pods -n mynamespace
# forward the port : oc port-forward mysqlpod localhostport:openshiftport
oc port-forward mysqlpod 3306:3306
```
#### deploy and manage your project
* List the components available
```shell script
odo catalog list components
```
* Build your project using maven. Build for the local environment without specifying the revision parameter.
* Add a java 11 component to your project.
```shell script
# backend is the name of your application
odo create java:11 backend --binary target/NBIAdapter-1.0-SNAPSHOT.jar
   ```
* You may want to change the default configuration, for example, if you want to connect to a database, change the log level ...
```shell script
odo config view
odo config set --env SPRING_DATASOURCE_HOST=mysql --env SPRING_PROFILES_ACTIVE=local --env JAVA_OPTIONS="-Xmx512m -Xms256m" --env LOGGING_LEVEL_ROOT=DEBUG
```
* deploy your component in Openshift
```shell script
odo push
```
* Get the logs of your deployment
```shell script
odo log -f
```
* Get the status
```shell script
odo list
```
* If logs are OK, you can create a URL to access the application
```shell script
odo url create backend --port 8080
odo push
```
* Now, you can change your code, build with maven and odo push to update Openshift
* To delete the application
```shell script
odo delete backend
```

#### hot code reload with Spring-boot devtools
Using odo, you can build your code with maven and push it to Openshift. That’s good but every time you make the slightiest change, you still need
to recompile the whole project and push to Openshift. Odo makes it quick but we are still talking about 1-2 min.
How can we do better than that ?
The answer is spring-boot devtools.

[Spring-boot devtools](https://docs.spring.io/spring-boot/docs/current/reference/html/using-spring-boot.html#using-boot-devtools)
is a set of tools aiming at restarting and reloading your spring-boot application when the code changes on your dev environment.

Because the application run remotely in a docker container on Openshift, we have to use the 
[Remote capabilities](https://docs.spring.io/spring-boot/docs/current/reference/html/using-spring-boot.html#using-boot-devtools-remote)
provided. By default, when you build the project with maven and the default profile (local), the spring-boot devtools are included in the jar (see
the spring-boot.repackage.excludeDevtools property in the pom.xml). The property "spring.devtools.remote.secret" set the password 
to connect to these tools. Once the application is running in Openshift, you need to run a client that connects to the server.
Follow [these steps](https://docs.spring.io/spring-boot/docs/current/reference/html/using-spring-boot.html#running-the-remote-client-application)
to do so.

# Development
## Lombok
We use Lombok to remove boilerplate codes (like getter/setter, toString ...).
You need to install Lombok in IntelliJ so that IntelliJ can generate the code properly.
Regarding the maven build, the Lombok code is generated during at compilation time. See the maven-compiler-plugin configuration in the
pom.xml file.

## Logs
With Spring-boot, you can define log levels in the application*.yaml files and/or log system specific file.
We define the general log level in :
* src/main/resources/application-local.yaml : logging.level.ROOT=DEBUG
* src/main/resources/application-openshift.yaml : logging.level.ROOT=INFO

This way, by default, when developing in local, logs level is set to DEBUG. When running the application in a remote environment (uat, prod)
, the log level is set to INFO.
If you want, you can define log level by classes/packages here too. But, in order to not clutter the configuration with log levels, I find
it better to set the log configuration in a particular file.

2 files are used to configure logs :
* src/main/resources/logback-spring.xml : used when you run the application
* src/test/resources/logback-spring.xml : used when you test the application. Usually, during testing, you need more detailed logs.

If you want to increase the log level by logging automatically for each method call :
* method entry and its parameter
* method exit and its parameter

You can specify the "debug" property, same as Spring-boot. For example you can start your app with the option --debug or with the
environment variable DEBUG=true.
It will enable a Logging aspect that logs every method call for you.
Have a look at the LoggingAspect class and the AspectConfiguration class.

## Database migration
To facilitate the database schema migration, we use [Flyway](https://flywaydb.org/).
Flyway is easy to use :
* Drop your SQL files in src/main/resources/db/migration directory
* SQL files must be named VERSION_whatitdoes.sql, eg V1.0.0_initschema.sql
* SQL files contain standard SQL : create table, add column, whatever
* You can drop one file per version. Files are sorted by version number and executed in order. 
* Version migration file (starting with V) are run only once.
* Repeatable migration file (starting with U) are run every time their checksum changes.

## Database access with Spring-data-jpa
We use JPA to manage the persistence of Java objects in a database.

### Model definition
Have a look at the **domain** package, containing the entities. They are regular java objects whose state will be synchronised with a DB.
Think of entity as database table.

### CRUD and query
With spring-data-jpa, CRUD queries and finder queries are OOO or easy to do. Usually, no coding is required.
Have a look at the **repository** package.

### RSQL queries
[What is RSQL](https://millwall.cofe.btireland.ie/talos/talos-wiki/-/wikis/training/tutorials/databasejpa)

For this to work, you don’t need to code anything. Your repository must extend ```JpaRsqlRepository```. 
You can then do : ```java myRepository.findAllByRsqlQuery("extRef=='200*';product=='PFIB')```
Very handy to provide your customer with the ability to search for objects without coding anything.
Of course, these RSQL queries are used from your REST endpoints. If you want to look for objects from your own Java code,
you have to use the standard spring data queries.

### Pagination and Sorting
How can you paginate/sort the results of a query ? For example, let’s say you want to get ExampleOrder 10 by 10.
[Spring-data provides us with a solution](https://www.baeldung.com/spring-data-jpa-pagination-sorting)

No code is required, it is built-in Spring-data.
Have a look at the ExampleOrderRepositoryTest class to see how it work.

## REST client
An example REST client is inside the client.asset package and service package.
This client can create a new asset or get an old one. 
The corresponding test classes (client and services test classse) show how to use a mock server 
during integration test.

[For more info](https://millwall.cofe.btireland.ie/talos/talos-wiki/-/wikis/training/tutorials/rest-services#rest-client-with-spring-cloud-feign)

## REST service
An example REST service (or Controller in the Spring world) is inside the **rest** package.
This service allows a client to CRUD an ExampleOrder. The client is not using our domain model directly as input/output.
In order to be loosely coupled, the API uses DTO object. [Have a look here](https://thoughts-on-java.org/dont-expose-entities-in-api/) to see
why it can be a bad idea to expose your domain model in your API. It is better to have a level of indirection.
At the REST controller level, we only use DTO object.
The REST controller deals with the HTTP logic (code, format, URL mapping ...) and delegates to a Facade object.

The Facade components (**facade** package) are responsible to :
* translate DTO objects to domain model objects and vice versa
* orchestrate the calls to the business layer 

The services (**package** service, what I call the business layer) deals with business logic and can access the database using the 
repositories objects. It only deals with domain model objects. No DTO are present here.

### HTTP status code
By default, Spring sends back a 200. If you need to change it to 201 for example, you can annotate your methods with @ResponseStatus.
Example :
```java @ResponseStatus(HttpStatus.CREATED)```

### DTO to entity mapping
The facade objects are mapping DTO to entity. This can be a cumbersome task. To help us with it, we can use 
[mapstruct](https://mapstruct.org/). This library generates a lot of the boilerplate code for us. 
Have a look at the ExampleOrderMapper interface. I just defined the method. The implementation is generated at compile time by 
mapstruct.

### Validation
Validate user input is important. For that, you can use annotations on your input Object. Spring will make sure that 
the input object satisfies the annotations constraints. [wiki](https://millwall.cofe.btireland.ie/talos/talos-wiki/-/wikis/training/tutorials/rest-services#validation)

### Exception handling
Most exceptions occurring in your codes and not caught will bubble up to the REST layer. Here, they will be transformed into an 
HTTP 500 error with an exception message. This behaviour can be fine for some exceptions, incorrect for others.

Have a look at the [exception handling documentation](https://millwall.cofe.btireland.ie/talos/talos-wiki/-/wikis/training/tutorials/spring#rest-exception-handling)
to understand how it works in Spring.

As an example, you can look at the GlobalExceptionHandler class in the **exception** package. There are defined 2 exceptions handling so that
proper errors are sent back to the client.

### Adding HTTP links in your JSON response with Spring HATEOAS
It is a good practice to include links to related entities or pagination in your JSON response. This way, the client can query those 
without knowing how to call them.

Example without links :
```json
{
  "content": [
    {
      "id": 2,
      "externalReference": "ref102",
      "type": "PFIB",
      "createdAt": "2020-01-16T10:30:37",
      "items": [
        {
          "id": 3,
          "productName": "FTTH",
          "quantity": 2
        },
        {
          "id": 4,
          "productName": "VOIP",
          "quantity": 2
        }
      ]
    }
  ],
  "pageable": {
    "sort": {
      "sorted": false,
      "unsorted": true,
      "empty": true
    },
    "offset": 1,
    "pageNumber": 1,
    "pageSize": 1,
    "unpaged": false,
    "paged": true
  },
  "totalElements": 3,
  "totalPages": 3,
  "last": false,
  "number": 1,
  "size": 1,
  "sort": {
    "sorted": false,
    "unsorted": true,
    "empty": true
  },
  "numberOfElements": 1,
  "first": false,
  "empty": false
}
```

Examples with pagination links when we want to get multiple orders by page :
```json
{
  "_embedded": {
    "exampleOrderDTOList": [
      {
        "id": 2,
        "externalReference": "ref102",
        "type": "PFIB",
        "createdAt": "2020-01-16T10:30:37",
        "items": [
          {
            "id": 3,
            "productName": "FTTH",
            "quantity": 2
          },
          {
            "id": 4,
            "productName": "VOIP",
            "quantity": 2
          }
        ]
      }
    ]
  },
  "_links": {
    "first": {
      "href": "http://localhost:8080/services/MyApplication/api/v1/exampleorders?filters=externalReference==%27ref%2A%27&page=0&size=1"
    },
    "prev": {
      "href": "http://localhost:8080/services/MyApplication/api/v1/exampleorders?filters=externalReference==%27ref%2A%27&page=0&size=1"
    },
    "self": {
      "href": "http://localhost:8080/services/MyApplication/api/v1/exampleorders?filters=externalReference==%27ref%2A%27&page=1&size=1"
    },
    "next": {
      "href": "http://localhost:8080/services/MyApplication/api/v1/exampleorders?filters=externalReference==%27ref%2A%27&page=2&size=1"
    },
    "last": {
      "href": "http://localhost:8080/services/MyApplication/api/v1/exampleorders?filters=externalReference==%27ref%2A%27&page=2&size=1"
    }
  },
  "page": {
    "size": 1,
    "totalElements": 3,
    "totalPages": 3,
    "number": 1
  }
}
```

Nice, isn’t it ?

### Pagination
REST calls can be received with pagination parameters. All you need to do is adding a Pageable parameter to your REST controller method.
Then, it is able to understand that kind of URL :
/api/v1/exampleorders?filters=externalReference=='ref*'&page=1&size=1&sort=externalReference,desc 

It takes into account :
* page parameter : number of the page you want to display
* size parameter : page size
* sort parameter : which property you want to sort on

Have a look at the ExampleOrderController#findAll() method.

### Documentation with OpenAPI and Swagger
OpenAPI is a specification used to describe you REST API. It uses a yaml/json file. Tools can then use this specification file to generate
server/client stubs, mocks or online documentation. With springdoc-openapi, we can [annotate](https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations) 
our code to generate at boot-time this OpenAPI 
json file. And then use Swagger-ui to generate a Web interface based on this file, that exposes the API documentation.
[OpenAPI with Spring-boot](https://springdoc.org/)

Once the application is started, you can get the OpenAPI 3 yaml file at http://localhost:8080/v3/api-docs.yaml

The swagger UI is available at http://localhost:8080/swagger-ui.html

Check the class ```ExampleOrderController``` to see how we configure it.

#### Enable the REST API and UI
If you are using the local profile, it is enabled by deafault (see springdoc properties in the config yaml file).

If you are using the openshift profile, it is disabled by default (see the same properties in the openshift yaml file).
That is to prevent to enable it in production. If your environment is dev or QA and you want to enable it, you can do so
by providing the following environment variables :
* SPRINGDOC_API_DOCS_ENABLED=false : disable the REST api
* SPRINGDOC_SWAGGER_UI_ENABLED=false : disable the swagger ui

## SOAP Web Service
An example SOAP service is inside the **soap** package. [Gitlab documentation](https://millwall.cofe.btireland.ie/talos/talos-wiki/-/wikis/training/tutorials/spring#soap-web-services-with-spring)
This service allows a client to send a notification and get an Order. Spring will map for you the request/response from/to XML
from/to Objects.
For this mapping to work, you have to :
* Either use the XSD file to generate the java objects with annotation. This uses JAXB maven plugin to do so.
This is the option chosen in the example. To generate the classes from the XSD, run ```./mvnw clean compile```
* Or use existing/new objects. You have to put the JAXB annotations in the objects yourself in that case.
It can be useful in case you want to reuse existing objects. For instance, you may want to return an ExampleOrderDTO to your client.
You can then put JAXB annotations on the object field. Spring will then generate XML using those.

### Configuration
The class WebServiceConfiguration is in charge of the configuration.
Here, you can expose the WSDL for your client. You also define the path to access the web services.

### WS HTTP status code
By default, Spring sends back :
* 200 if there is a response
* 202 if success but no response
* 500 if there is an error

### Messages validation
To validate the request and/or response, you can define in the configuration an EndpointInterceptor.
Have a look at the payloadValidatingInterceptor in the WebServiceConfiguration class. We validate the incoming/outgoing messages
against the XML schema. We then add this interceptor in the methode addInterceptors

### WS Exception handling
If an exception is thrown, Spring-ws generates a fault for us.
The default exception handling mechanism is explained in the [tutorial](https://millwall.cofe.btireland.ie/talos/talos-wiki/-/wikis/training/tutorials/spring#soap-web-services-with-spring)
Here, we define also 
* a custom PayloadValidatingInterceptor to validate the client request
* a custom EndpointExceptionResolver to manage the server exception. Have a look at the class DetailSoapFaultAnnotationExceptionResolver

## Transaction
By default, every request sent to the database using a JpaRepository is executed in its own transaction. That can lead to problems. 
For example, let’s say that when you receive an order, you want to :
* create this order in DB
* add an event entry in the customer profile

If an error happens when you add the event entry, then your system can be in an inconsistent state, with an order created and no 
corresponding event entry. Solution is to open and close the transaction at the Facade layer. This way, every call to services and
repositories are part of the same transaction. And everything can be committed or rollbacked at the same time.

To do this, you can use ```@Transactional``` annotation at class/method level. See ExampleOrderFacade for an example.

# Testing

## Kind of Tests
There are 3 kinds of tests :
* unit test : test of one class only. Everything else is mocked
* integration test : if you need to start a spring context to wire up components, then it is an integration test
* black box test : test of the application as an image. We deploy everything in Openshift and run the tests
against a real application.

When you are creating a new test class :
* Annotate it with ```@UnitTest``` if you are developing a unit test
* Annotate it with ```@IntegrationTest``` if you are developing a integration test
* Annotate it with ```@BlackBoxTest``` if you are developing a black box test

Example :

```
@UnitTest
public class MyUnitTest {

}
```


## Run the tests
To run the tests using maven :
* ```mvnw clean test``` will run the unit and integration tests
* ```mvnw clean test -P utTest``` will run the unit tests only
* ```mvnw clean test -P itTest``` will run the integration tests
* ```mvnw clean verify -P bbTest -Drevision=yourVersionWithGitCommitHere``` will run the black box tests

  You need to put the revision as a parameter because the black box test deploy a version of your app in Openshift and run the tests
  against it. So obviously, it needs to know which version to deploy
* ```mvnw clean test -Dtest=ExampleOrderMapperTest``` will only run the ExampleOrderMapperTest test class.

## Testing the database
[A short intro to database testing with Spring](https://millwall.cofe.btireland.ie/talos/talos-wiki/-/wikis/training/tutorials/testing#testing-the-database-with-an-in-memory-database)

Here is a code example from the ExampleOrderRepositoryTest class.
```java
// Standard Spring-boot annotation to scan entity classes and configure repositories
@DataJpaTest
// because we use a custom repository base class, we need to load it as it is not loaded by default
// by @DataJpaTest
@Import(DatabaseConfiguration.class)
@DisplayName("ExampleOrderRepository")
@IntegrationTest

// We define a test profile so that we can override the default datasource configured by Spring
// This way, we can use a H2 database with a Hikari pool in front and set the auto-commit property to false
@ActiveProfiles("test")

// We won’t use the default H2 database configuration provided by Spring as we want to 
// use the one defined in the test profile
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ExampleOrderRepositoryTest {

    @Autowired
    private ExampleOrderRepository repository;

    @Test
    @DisplayName("Check that we can find an order by its external reference")
    void findByExternalReference() {

        ExampleOrder expected = ExampleOrder.builder()
                .externalReference("myRef1").type(ExampleOrder.Type.PFIB)
                .build();

        // insert 2 example orders in the DB
        repository.save(expected);
        repository.save(ExampleOrder.builder().externalReference("myRef2").type(ExampleOrder.Type.CFIB).build());

        // test that the example order we find by externalReference is the one we expect
        Assertions.assertThat(repository.findByExternalReference("myRef1")).isEqualToIgnoringGivenFields(expected, "id");
    }
}
```

## Testing a REST service
[How to test the REST layer only](https://millwall.cofe.btireland.ie/talos/talos-wiki/-/wikis/training/tutorials/testing) explains 
how you can test the REST layer. The request must be parsed correctly by your service and the response must be generated properly.
Dependencies should be mocked.

ExampleOrderControllerTest shows how to test a CRUD service, send request, compare response and status code.

## Contract tests
We use Pact to implement contracts testing. Contract tests make sure that a consumer and a provider speak the same language.
You can find more information in the [architecture section](https://millwall.cofe.btireland.ie/talos/talos-wiki/-/wikis/architecture/quality#contract-tests)

# Continuous integration

# Continuous deployment

# Production
## Monitoring
To monitor the application, we use [Spring actuator](https://millwall.cofe.btireland.ie/talos/talos-wiki/-/wikis/training/tutorials/application-monitoring)
A discovery page, with all the metrics available is http://localhost:8080/actuator

You can query individual actuator endpoint. For example, to get an overall application status, you can query http://localhost:8080/actuator/health
Response example
```json
{
  "status" : "UP"
}
```
By default, we expose the following endpoints :
* configprops : application configuration
* env : environment
* health
* info : 
* logfile : 
* loggers : 
* prometheus : 
* threaddump : 
* flyway : 

You can find all the endpoints available in the reference documentation.
To change the endpoints exposed, change the property management.endpoints.web.exposure.include

## Logs configuration for production
To understand how the logs are managed in Openshift, [follow this link](https://millwall.cofe.btireland.ie/talos/talos-wiki/-/wikis/openshift/logs-management)

By default, with the local profile, the multi-line logs events are printed on muliple lines. 
This is perfect for development time for increased human readability.
In production, that is a problem as explained in the link above.
To transform the multi-line logs like a stack trace in a single line log event, we use a different logging pattern for
the Openshift profile. Have a look at the application-openshift.yaml. The logging pattern is there.
