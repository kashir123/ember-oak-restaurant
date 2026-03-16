# 🔥 Ember & Oak — Restaurant Microservices System

A production-ready restaurant platform built with microservices architecture.

## 📁 Project Structure

```
restaurant-system/
├── frontend/                   # HTML + CSS + Vanilla JS
│   ├── index.html              # Home page
│   ├── css/main.css            # Global styles
│   ├── js/
│   │   ├── api.js              # REST API wrapper
│   │   ├── main.js             # Shared utilities
│   │   ├── menu.js             # Menu page logic
│   │   ├── order.js            # Order page logic
│   │   └── reservation.js      # Reservation logic
│   └── pages/
│       ├── menu.html
│       ├── order.html
│       ├── reservation.html
│       └── contact.html
├── services/
│   ├── userservice/            # Spring Boot :8081
│   ├── menuservice/            # Spring Boot :8082
│   ├── orderservice/           # Spring Boot :8083
│   └── reservationservice/     # Spring Boot :8084
├── api-gateway/                # Spring Cloud Gateway :8080
├── database/schema.sql         # PostgreSQL schema + seed data
├── docker-compose.yml          # Local dev environment
├── docker/nginx.conf           # Frontend proxy config
└── kubernetes/                 # K8s deployment manifests
    ├── configmap.yaml
    ├── postgres.yaml
    └── services.yaml
```

## 🚀 Quick Start — Docker Compose

```bash
# 1. Clone and enter the project
cd restaurant-system

# 2. Start everything
docker-compose up --build

# 3. Open the app
open http://localhost:3000

# API Gateway
open http://localhost:8080
```

## 🌐 API Endpoints

| Service     | Base URL                           |
|-------------|-------------------------------------|
| Users       | http://localhost:8081/api/users     |
| Menu        | http://localhost:8082/api/menu      |
| Orders      | http://localhost:8083/api/orders    |
| Reservations| http://localhost:8084/api/reservations |
| Gateway     | http://localhost:8080/api/*         |

### Example Requests

```bash
# Register
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jane","lastName":"Doe","email":"jane@example.com","password":"secret123"}'

# Get menu
curl http://localhost:8080/api/menu/items

# Place order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Jane Doe","customerEmail":"jane@example.com","orderType":"DINE_IN","items":[{"menuItemId":1,"quantity":2,"unitPrice":58.00}]}'

# Book table
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Jane Doe","customerEmail":"jane@example.com","reservationDate":"2025-12-25","reservationTime":"7:00 PM","partySize":2}'
```

## ☸️ Kubernetes Deployment

```bash
# Apply all manifests
kubectl apply -f kubernetes/configmap.yaml
kubectl apply -f kubernetes/postgres.yaml
kubectl apply -f kubernetes/services.yaml

# Check pods
kubectl get pods

# Get gateway external IP
kubectl get service api-gateway
```

## 🗄️ Database Setup (manual)

```bash
psql -U postgres -f database/schema.sql
```

## 🔑 Environment Variables

| Variable             | Default              | Description         |
|----------------------|----------------------|---------------------|
| DB_HOST              | localhost            | Database host       |
| DB_USER              | postgres             | DB username         |
| DB_PASS              | postgres             | DB password         |
| JWT_SECRET           | (see config)         | JWT signing key     |
| USER_SERVICE_HOST    | localhost            | For gateway routing |
