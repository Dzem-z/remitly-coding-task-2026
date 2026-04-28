# Stock Market Application

This project is a sample stock market backend application built with Spring Boot, PostgreSQL, and Nginx, containerized using Docker Compose.

## Features
- REST API for managing stocks and wallets
- PostgreSQL database for persistent storage
- Nginx reverse proxy
- Easy startup and shutdown scripts

## Prerequisites
- Docker and Docker Compose installed

## Getting Started

### 1. Start the Application

Run the startup script with your desired port (default replicas is 1):

```
./start_app.sh <PORT> [REPLICAS]
```
Example:
```
./start_app.sh 8080 2
```
This will start the app on http://localhost:8080 with 2 app replicas.

### 2. Stop and Remove Containers

To stop the application and remove all containers, run:

```
./stop_app.sh
```

## API Example

### POST /stocks
Request body:
```
{
  "stocks": [
    { "name": "AAPL", "quantity": 100 },
    { "name": "GOOG", "quantity": 50 }
  ]
}
```

### Error Handling
- Sending a POST to `/stocks` without a valid `stocks` array will result in a 400 Bad Request.

## Configuration
- Database credentials and other settings are in `src/main/resources/application.properties` and `compose.yml`.
- Nginx configuration is in `nginx.conf`.

## Notes
- The app container always listens on port 8080 internally. The external port is mapped via Nginx.
- Unknown fields in JSON requests are ignored.

---

Feel free to modify and extend this project as needed!
