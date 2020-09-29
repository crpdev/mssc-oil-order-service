# Circle CI Build Status: [![CircleCI](https://circleci.com/gh/crpdev/mssc-oil-service.svg?style=svg)](https://circleci.com/gh/crpdev/mssc-oil-service)

Microservice Services Using Spring Cloud - Oil Service

**Technologies covered**:
- *Java* 8
- *Spring Boot* 2.3.2.RELEASE
- *Message Driven* using JMS [Active MQ]
- Sagas using *Spring State Machine*
- Spring *Cloud Gateway*
- *Service Registration* with Eureka
- *Service Discovery* with Eureka
- *Circuit Breaker* Pattern
- Spring *Cloud Config*
- *Distributed Tracing* using Zipkin
- *Securing* Spring Cloud using Basic Authentication
- *Containerising* the services using Docker
- *Consolidated Logging* Using ELK [Elastic Stack]
- Deploying to *Cloud Platform* [Digital Ocean] using *Docker Swarm*

**Development Environment**:
- **Machine**: MacBook Pro 16GB
- **OS** - macOS Catalina 10.15.7
- **IDE** - IntelliJ IDEA 2020.2.1 (Ultimate Edition)

# Architecture

![Oil Services Architecture](https://i.ibb.co/z2rMYW4/mssc-oil-ws.jpg)

# Watch Demo

[![Project Showcase: Spring Boot Micro-services](https://i.ibb.co/cTS4WY2/vlcsnap-2020-09-29-07h08m45s824.png)](https://youtu.be/lC-nW6g55z4)

# Services

## Oil Service

The main service which is exposed to the customer/ client.

Exposed Endpoints:
 
| Endpoint | Description | Method |
|:--|:--|:--|
| /api/v1/oil | list of oils available in the factory [Paginated] | GET |
| /api/v1/oil/{oilid} | Get details about a specific oil product using it's id] | GET |
| /api/v1/oil/productCode/{productCode} | Get details about a specific product using it's code] | GET |
| /api/v1/oil | Create a new oil | POST |
| /api/v1/oil/{oilid} | Update details of a specific oil using it's id | POST |

## Inventory Service

Inventory service holds details about the stock of the products. Oil service makes a HTTP REST call to get details about the inventory of a product. 

Exposed Endpoints:
 
| Endpoint | Description | Method |
|:--|:--|:--|
| /api/v1/oil/{oilId}/inventory | Get inventory about a specific oil id | GET |

## Order Service

*State Machine* Implementation using the *Saga Design Patten* that manages Order workflow.

**States**: NEW, VALIDATED, VALIDATION_PENDING, VALIDATION_EXCEPTION, ALLOCATION_PENDING, ALLOCATED, ALLOCATION_EXCEPTION, CANCELLED, PENDING_INVENTORY, PICKED_UP, DELIVERED, DELIVERY_EXCEPTION

**Events**: VALIDATE_ORDER, VALIDATION_PASSED, VALIDATION_FAILED, ALLOCATE_ORDER, ALLOCATION_SUCCESS, ALLOCATION_NO_INVENTORY, ALLOCATION_FAILED, CANCEL_ORDER, ORDER_PICKED_UP

## Inventory Failover Service

Implemented as part of the *Circuit Breaker Design Pattern* using Feign: Hystrix and Resilience4J

| Endpoint | Description | Method |
|:--|:--|:--|
| inventory-failover | Default fallback response when inventory service is unreachable | GET |

## Gateway

Implemented using *Spring Cloud Gateway* to work along with Eureka server to manage requests to the endpoints. Gateway service is configured to manage all requests to be load balanced and filtered for fallbacks [inventory service].

## Eureka

*Netflix Eureka* is implemented in all services as part of *Service Registration and Discovery*, which provides a consolidated view of all active services. Gateway communicates with Eureka server to get details about running instances and delegate service requests.

## Config Server

*Spring Cloud Config* has been implemented to fetch configuration at runtime from Github. Services upon startup communicate with config server for the active profile properties.

## Spring Profiles

All services are managed by Spring Profiles, where the configuration specific to a runtime are managed using a properties file.

| Profile | Description |
|:--|:--|
| Default | No active configuration and uses in-memory H2 DB |
| localmysql | Uses MYSQL server instead of H2 |
| local-discovery | Enables Spring Cloud Features |
| digitalocean | Enables Spring Cloud Features specific to Digital Ocean Cloud Platform |

## Execution

The service can be run in 3 modes

| Mode | Description |
|:--|:--|
| Local | Services run on local machines |
| Local Docker | Single Node machine configuration using Docker |
| Cloud Platform | Multi-node setup using Docker Swarm [2 replicas each] |

# Docker

All the images implemented as part of this project are available at my [Docker Hub](https://hub.docker.com/repositories) 

| Service | Image |
|:--|:--|
| oil-service | crpdev/mssc-oil-service |
| inventory | crpdev/mssc-oil-inventory-service |
| order | crpdev/mssc-oil-order-service |
| inventory-failover | crpdev/mssc-oil-inventory-failover |
| eureka | crpdev/mssc-oil-eureka |
| gateway | crpdev/mssc-oil-gateway |
| config | crpdev/mssc-oil-config-server |
| filebeat | crpdev/filebeat |
| jms | vromero/activemq-artemis |
| zipkin | openzipkin/zipkin |
| elastic search | docker.elastic.co/elasticsearch/elasticsearch:7.9.1 |
| kibana | docker.elastic.co/kibana/kibana:7.9.1 |

# Port Configuration

| Service | Port |
|:--|:--|
| oil-service | 8080 |
| inventory | 8082 |
| order | 8081 |
| inventory-failover | 8083 |
| eureka | 8761 |
| gateway | 9090 |
| zipkin | 9411 |
| elastic search | 9200 |
| kibana | 5601 |
| config | 8888 |

# Source Code

| Service | Github |
|:--|:--|
| oil-service | https://github.com/crpdev/mssc-oil-service |
| inventory | https://github.com/crpdev/mssc-oil-inventory-service |
| order | https://github.com/crpdev/mssc-oil-order-service |
| inventory-failover | https://github.com/crpdev/mssc-oil-inventory-failover |
| eureka | https://github.com/crpdev/mssc-oil-eureka |
| gateway | https://github.com/crpdev/mssc-oil-gateway |
| config-server | https://github.com/crpdev/mssc-oil-config-server |
| config | https://github.com/crpdev/mssc-oil-factory-config-repo |
