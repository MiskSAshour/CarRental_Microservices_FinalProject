# Car Rental Microservices System

## 📋 Project Structure

```
CarRental_Microservices/
├── eureka-server/              # Service Registry
├── api-gateway/                # API Gateway with Spring Cloud Gateway
├── booking-service/            # Booking Service with GraphQL (Bonus)
├── fleet-service/              # Fleet Management Service
├── payment-service/            # Payment Processing Service
├── customer-service/           # Customer Management Service
├── notification-service/       # Notification Service (Event-Driven)
├── docker-compose.yml          # Docker Compose Configuration
└── pom.xml                     # Parent Maven POM
```

## 🏗️ Architecture Overview

- **Eureka Server**: Service Discovery (Port 8761)
- **API Gateway**: Central Entry Point (Port 8080)
- **Booking Service**: Core Booking Logic with GraphQL (Port 8081)
- **Fleet Service**: Vehicle Management (Port 8083)
- **Payment Service**: Payment Processing (Port 8085)
- **Customer Service**: Customer Management (Port 8082)
- **Notification Service**: Event-Driven Notifications (Port 8086)
- **Kafka**: Message Broker for Async Communication (Port 9092)

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- Git

### Step 1: Clone/Setup Project
```bash
cd /home/ubuntu
# If cloning from GitHub:
# git clone https://github.com/MiskSAshour/CarRental_Microservices.git
# cd CarRental_Microservices

# If using existing code:
cd CarRental_Microservices
```

### Step 2: Build All Services
```bash
# Build parent and all modules
mvn clean package -DskipTests

# This will create JAR files in each service's target/ folder
```

### Step 3: Run with Docker Compose
```bash
# Start all services
docker-compose up -d

# Check logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Step 4: Verify Services are Running
```bash
# Eureka Dashboard
http://localhost:8761

# API Gateway Health
http://localhost:8080/actuator/health

# Booking Service GraphQL
http://localhost:8081/graphiql
```

## 📡 API Endpoints

### Via API Gateway (Port 8080)
```
POST   /api/v1/bookings
GET    /api/v1/bookings/{id}
GET    /api/v1/bookings/customer/{customerId}

POST   /api/v1/vehicles
GET    /api/v1/vehicles
GET    /api/v1/vehicles/available

POST   /api/v1/payments/process
GET    /api/v1/payments/{id}

POST   /api/v1/customers
GET    /api/v1/customers/{id}
```

### Direct Service Endpoints (for testing)
```
Booking Service:    http://localhost:8081
Fleet Service:      http://localhost:8083
Payment Service:    http://localhost:8085
Customer Service:   http://localhost:8082
Notification:       http://localhost:8086
```

## 🔄 Saga Pattern Implementation

### Booking Workflow (Orchestration)
1. **Booking Service** receives booking request
2. Validates customer via **Customer Service** (Feign)
3. Checks vehicle availability via **Fleet Service** (Feign)
4. Reserves vehicle
5. Processes payment via **Payment Service** (Feign)
6. Publishes `booking-confirmed` event to Kafka
7. **Notification Service** listens and sends notifications

### Compensating Transactions
If payment fails:
- Fleet Service releases the vehicle
- Booking status set to CANCELLED
- Event published to Kafka

## 📊 Kafka Topics

- `booking-confirmed`: Published when booking is confirmed
- `booking-cancelled`: Published when booking is cancelled
- `payment-completed`: Published when payment succeeds
- `payment-failed`: Published when payment fails
- `customer-registered`: Published when new customer registers

## 🐳 Docker Compose Services

```yaml
Services:
- zookeeper:8181          # Kafka dependency
- kafka:9092              # Message broker
- eureka-server:8761      # Service registry
- api-gateway:8080        # API Gateway
- customer-service:8082   # Customer Service
- fleet-service:8083      # Fleet Service
- booking-service:8081    # Booking Service
- payment-service:8085    # Payment Service
- notification-service:8086  # Notification Service
```

## 🧪 Testing the System

### 1. Create a Customer
```bash
curl -X POST http://localhost:8080/customer-service/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ahmed Hassan",
    "email": "ahmed@example.com",
    "phone": "+970123456789",
    "address": "Gaza City",
    "city": "Gaza",
    "country": "Palestine"
  }'
```

### 2. Add a Vehicle
```bash
curl -X POST http://localhost:8080/fleet-service/api/v1/vehicles \
  -H "Content-Type: application/json" \
  -d '{
    "make": "Toyota",
    "model": "Camry",
    "year": 2023,
    "licensePlate": "GZ-2023-001",
    "color": "Silver",
    "dailyRate": 50.0
  }'
```

### 3. Create a Booking
```bash
curl -X POST http://localhost:8080/booking-service/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-xxxxx",
    "vehicleId": "VEH-xxxxx",
    "pickupDate": "2026-06-01T09:00:00",
    "returnDate": "2026-06-05T18:00:00",
    "insuranceType": "Full-Coverage",
    "paymentMethod": "CreditCard"
  }'
```

### 4. Check Booking Status
```bash
curl http://localhost:8080/booking-service/api/v1/bookings/BK-xxxxx
```

### 5. Query GraphQL (Bonus)
```bash
curl -X POST http://localhost:8081/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "{ checkStatus }"
  }'
```

## 🔧 Troubleshooting

### Services not registering with Eureka
- Check Eureka dashboard: http://localhost:8761
- Ensure all services have correct `eureka.client.service-url.defaultZone`
- Wait 30 seconds for registration

### Kafka connection errors
- Ensure Kafka is running: `docker-compose logs kafka`
- Check bootstrap servers configuration in application.properties

### Feign client not working
- Verify service names match Eureka registration
- Check `@EnableFeignClients` annotation in main class
- Ensure Feign client interfaces are in correct package

### Port conflicts
- Change port in `application.properties` if needed
- Update docker-compose.yml accordingly

## 📝 Configuration Files

All services use `application.properties` in `src/main/resources/`

Key configurations:
- `server.port`: Service port
- `spring.application.name`: Eureka registration name
- `eureka.client.service-url.defaultZone`: Eureka server location
- `spring.kafka.bootstrap-servers`: Kafka broker location
- `spring.graphql.graphiql.enabled`: Enable GraphQL UI

## 🎯 Next Steps

1. Build and test locally
2. Push to GitHub
3. Setup GitHub Actions for CI/CD
4. Record demo video
5. Submit to instructor

## 📞 Support

For issues or questions, check:
- Service logs: `docker-compose logs <service-name>`
- Eureka dashboard: http://localhost:8761
- GraphQL playground: http://localhost:8081/graphiql
