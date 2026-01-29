# API Gateway with Eureka Discovery

API Gateway sử dụng Spring Cloud Gateway và Eureka Service Discovery.

## Kiến trúc

```
Client → API Gateway (Port 8080) → Eureka Server (Port 8761) → Services
                                                               ├─ Auth Service
                                                               ├─ Student Service
                                                               └─ Grade Service
```

## Cấu trúc

```
api-gateway/
├── src/main/java/com/savvy/gateway/
│   ├── ApiGatewayApplication.java
│   ├── config/
│   │   └── GatewayConfig.java           # Route configuration
│   ├── filter/
│   │   ├── LoggingFilter.java           # Request/Response logging
│   │   └── AuthenticationFilter.java    # JWT validation (TODO)
│   ├── exception/
│   │   └── GatewayExceptionHandler.java # Error handling
│   └── controller/
│       └── GatewayController.java       # Health check & service info
└── pom.xml
```

## Các tính năng

### 1. Service Discovery

-    Tự động discover services qua Eureka
-    Load balancing giữa các instances
-    Circuit breaker (có thể thêm Resilience4j)

### 2. Routing

-    **Auth Service**: `/api/v1/auth/**` → `lb://auth-service`
-    **Student Service**: `/api/v1/students/**` → `lb://student-service`
-    **Grade Service**: `/api/v1/grades/**` → `lb://grade-service`

### 3. Filters

-    **LoggingFilter**: Log mọi request/response với request ID
-    **AuthenticationFilter**: JWT validation (cần implement)
-    Auto-inject headers: `X-Request-ID`, `X-Gateway`

### 4. CORS

-    Configured cho tất cả origins
-    Allow methods: GET, POST, PUT, DELETE, OPTIONS

## Chạy hệ thống

### Bước 1: Start Eureka Server

```bash
cd d:\Savvy\micro\eureka-server
mvn spring-boot:run
```

Dashboard: http://localhost:8761

### Bước 2: Start API Gateway

```bash
cd d:\Savvy\micro\api-gateway
mvn spring-boot:run
```

Gateway: http://localhost:8080

### Bước 3: Start các Services

```bash
# Auth Service (port: 8081)
cd d:\Savvy\micro\auth-service
mvn spring-boot:run

# Student Service (port: 8082)
cd d:\Savvy\micro\student-service
mvn spring-boot:run

# Grade Service (port: 8083)
cd d:\Savvy\micro\grade-service
mvn spring-boot:run
```

## API Endpoints

### Gateway Management

```bash
# Health check
GET http://localhost:8080/api/v1/gateway/health

# List registered services
GET http://localhost:8080/api/v1/gateway/services

# Gateway actuator
GET http://localhost:8080/actuator/gateway/routes
```

### Service Routing

```bash
# Auth endpoints
POST http://localhost:8080/api/v1/auth/login
POST http://localhost:8080/api/v1/auth/register

# Student endpoints
GET http://localhost:8080/api/v1/students
GET http://localhost:8080/api/v1/students/{id}
POST http://localhost:8080/api/v1/students

# Grade endpoints
GET http://localhost:8080/api/v1/grades
GET http://localhost:8080/api/v1/grades/{id}
POST http://localhost:8080/api/v1/grades
```

## Configuration

### application.properties

-    **Gateway Port**: 8080
-    **Eureka URL**: http://localhost:8761/eureka/
-    **Discovery**: Auto-enabled
-    **Load Balancer**: Round-robin (default)

## Build và Deploy

```bash
# Build
cd d:\Savvy\micro\api-gateway
mvn clean package

# Run JAR
java -jar target/api-gateway-1.0.0.jar

# Docker (nếu có Dockerfile)
docker build -t api-gateway .
docker run -p 8080:8080 api-gateway
```

## Monitoring

-    **Eureka Dashboard**: http://localhost:8761
-    **Actuator**: http://localhost:8080/actuator
-    **Gateway Routes**: http://localhost:8080/actuator/gateway/routes
-    **Health**: http://localhost:8080/actuator/health

## Troubleshooting

### Service không register

1. Check Eureka Server đã chạy chưa
2. Verify `eureka.client.service-url.defaultZone`
3. Check network connectivity

### Route không hoạt động

1. Check service name trong Eureka
2. Verify route configuration
3. Check logs: `logging.level.org.springframework.cloud.gateway=DEBUG`

## TODO

-    [ ] Implement JWT validation trong AuthenticationFilter
-    [ ] Add Rate Limiting (Redis)
-    [ ] Add Circuit Breaker (Resilience4j)
-    [ ] Add Request/Response caching
-    [ ] Implement API versioning
-    [ ] Add security headers
