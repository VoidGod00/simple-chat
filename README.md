# 💬 Redis Real-Time Chat API

A high-performance, real-time chat application built with **Spring Boot** and **Redis**. This project demonstrates the power of Redis data structures (Lists, Sets, Pub/Sub) to handle real-time messaging, chat history, and room management with low latency.

## 🚀 Features

* **Real-Time Messaging:** Uses **Redis Publish/Subscribe** to broadcast messages instantly to all users in a room.
* **Chat History:** Persists the last *N* messages using **Redis Lists**
* **Room Management:** Manages active chat rooms and participants using **Redis Sets** and **Hashes**.
* **Scalable Architecture:** Stateless service design allowing for horizontal scaling.
* **Robust Error Handling:** Custom exception handling for duplicate rooms, missing rooms, and connection failures.
* **Unit Testing:** Comprehensive JUnit 5 tests using **Mockito** to simulate Redis operations.

## 🛠️ Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 3.4
* **Database:** Redis (Upstash Cloud / Local Docker)
* **Build Tool:** Maven
* **Testing:** JUnit 5, Mockito

## 🔌 API Endpoints

Base URL: `http://localhost:8080/api/chatapp/chatrooms`

| Method | Endpoint | Description |
| --- | --- | --- |
| **POST** | `/` | Create a new chat room |
| **POST** | `/{roomId}/join` | Join an existing room |
| **POST** | `/{roomId}/messages` | Send a message (Publishes to room) |
| **GET** | `/{roomId}/messages?limit=N` | Retrieve chat history (Last N messages) |

### Example Request Bodies

**1. Create Room**

```json
POST /api/chatapp/chatrooms
{
  "roomName": "general"
}

```

**2. Send Message**

```json
POST /api/chatapp/chatrooms/general/messages
{
  "participant": "Sahil",
  "message": "Hello Redis!"
}

```
## 🧪 Testing with Postman

You can test the API endpoints using [Postman Desktop](https://www.postman.com/downloads/).

### 1. Create a Room
* **Method:** `POST`
* **URL:** `https://simple-chat-skli.onrender.com/api/chatapp/chatrooms`
* **Body (JSON):**
    ```json
    {
      "roomName": "general"
    }
    ```

### 2. Join a Room
* **Method:** `POST`
* **URL:** `https://simple-chat-skli.onrender.com/api/chatapp/chatrooms/general/join`
* **Body (JSON):**
    ```json
    {
      "participant": "Sahil"
    }
    ```

### 3. Send a Message
* **Method:** `POST`
* **URL:** `https://simple-chat-skli.onrender.com/api/chatapp/chatrooms/general/messages`
* **Body (JSON):**
    ```json
    {
      "participant": "Sahil",
      "message": "Hello Redis!"
    }
    ```

### 4. Get Chat History
* **Method:** `GET`
* **URL:** `https://simple-chat-skli.onrender.com/api/chatapp/chatrooms/general/messages?limit=10`
  
### 5. for local testing 
* **Replace:** `https://simple-chat-skli.onrender.com with localhost:8080`

## ⚙️ Configuration & Setup

### 1. Prerequisites

* Java 21 SDK
* Maven
* A Redis instance (Local Docker or Upstash Cloud)

### 2. Clone the Repository

```bash
git clone https://github.com/VoidGod00/simple-chat.git
cd simple-chat

```

### 3. Configure Redis

You can run this app with a local Redis instance or a cloud provider.

**Option A: Local Docker (Recommended for Dev)**

```bash
docker run -d --name my-redis -p 6379:6379 redis

```

*Update `src/main/resources/application.properties`:*

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.ssl.enabled=false

```

**Option B: Upstash (Cloud)**
Set the following environment variables (or update `application.properties`):

```bash
export REDIS_HOST=your-endpoint.upstash.io
export REDIS_PORT=6379
export REDIS_PASSWORD=your-password
export REDIS_SSL=true

```

### 4. Build and Run

```bash
mvn clean install
mvn spring-boot:run

```
## 🧪 Running Tests

This project uses **Mockito** to test logic without requiring a running Redis server.

```bash
mvn test

```

* **ChatServiceTest.java**: Verifies room creation logic, message storage (`rightPush`), and message publishing (`convertAndSend`).

## 📂 Project Structure

```plaintext
src/main/java/com.test.simple_chat
├── config
│   └── RedisConfig.java        # RedisTemplate & Serializer setup
│   └── RedisConnectionTester   # Redis Connection check
├── controller
│   └── ChatController.java     # REST API endpoints
├──  exception
│   └── GlobalExceptionHandler.java         # Error handling
├── model
│   └── ChatMessage.java        # Data model 
└── service
    └── ChatService.java        # logic 


```

**Author:** [Your Name]
**License:** MIT
