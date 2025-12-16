# Email Service Backend

A Spring Boot REST API backend for a full-featured email client with JWT authentication, email management, advanced search, and keyboard shortcuts support.

## 🚀 Live Demo
- **Production API**: https://email-service-backend-production.up.railway.app
- **Frontend Live Demo**: https://email-service-frontend-rho.vercel.app/inbox
- **Frontend Repository**: https://github.com/Niduka292/email-service-frontend.git

## ✨ Features

### 🚀 Core Functionality
- **User Authentication**: Secure JWT-based login and registration
- **Email Management**: Send, receive, reply, and delete emails
- **Folder System**: Support for Inbox, Sent, Starred, and Trash folders
- **Email Operations**: Star/unstar emails, move to trash, restore

### 🔎 Advanced Features
- **AI Summarization**: Dedicated API endpoint to summarize email content using the Gemini model, significantly improving user efficiency.
- **Full-Text Search**: Search across subject, content, sender name, and sender email
- **Keyboard Shortcuts API**: Backend support for user-customizable shortcuts
- **RESTful API**: Clean and well-documented endpoints
- **Security**: Spring Security with JWT tokens and CORS configuration

## 🛠️ Technology Stack

- **Java 21**
- **Spring Boot 3.5.7**
- **Spring Security 6.5.6**
- **Spring Data JPA**
- **PostgreSQL**
- **Google Gemini API**
- **JWT (JSON Web Tokens)**
- **Maven**
- **Lombok**

## 📦 Prerequisites

- Java 21 or higher
- Maven 3.6+
- PostgreSQL 12+ (for local development)
- Git
- Gemini API Key (required for the summarization feature)

## ⚙️ Local Development Setup

### 1. Clone the Repository
```bash
git clone https://github.com/Niduka292/email-service-backend.git
cd email-service-backend
```

### 2. Configure Database

Create a PostgreSQL database locally:
```sql
CREATE DATABASE email_system_db;
```

### 3. Configure Application Properties

Update `src/main/resources/application.properties`:
```properties
# Server Configuration
spring.application.name=email-service
server.port=8080

# Database configuration - LOCAL
spring.datasource.url=jdbc:postgresql://localhost:5432/email_system_db
spring.datasource.username=postgres
spring.datasource.password=your_local_password  # <--- CHANGE THIS
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Connection Pool Settings
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000

# Logging
logging.level.root=INFO
logging.level.com.emailapp=DEBUG

# JWT Configuration - LOCAL
jwt.secret=your-local-secret-key-at-least-256-bits-long-for-development
jwt.expiration=86400000

# GEMINI API Configuration
# Note: This is resolved via an environment variable for security
gemini.api.key=${GEMINI_API_KEY}
```
### 4. Set Environment Variable for Gemini API
For the AI summarization feature to work, you must set your Gemini API key as an environment variable before running the application.

In your terminal (Linux/macOS):
```bash
export GEMINI_API_KEY="YOUR_ACTUAL_GEMINI_API_KEY"
```
In IntelliJ IDEA/IDE:

Set the GEMINI_API_KEY variable in the Run/Debug Configuration settings.

### 5. Run the Application

**Using Maven:**
```bash
./mvnw spring-boot:run
```

**Or using your IDE:**
- Open the project in IntelliJ IDEA or Eclipse
- Run `EmailServiceBackendApplication.java`

The backend will start on **`http://localhost:8080`**

### 5. Test the API
```bash
# Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'

# Get inbox emails
curl -X GET http://localhost:8080/api/emails/inbox \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Search emails
curl -X GET "http://localhost:8080/api/emails/search?query=meeting" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 📁 Project Structure
```
src/main/java/com/emailapp/emailservice/
├── config/              # Configuration classes (Security, CORS, etc.)
│   └── WebConfig.java
├── controller/          # REST API controllers
│   ├── AuthController.java
│   ├── EmailController.java
│   └── UserController.java
├── entity/              # Entity classes
│   ├── User.java
│   ├── UserMailbox.java
│   ├── Mail.java
│   ├── MailRole.java
│   └── MailFolder.java
├── repository/         # JPA repositories
│   ├── UserRepository.java
│   ├── MailRepository.java
│   └── UserMailboxRepository.java
├── security/           # Security components (JWT, filters)
│   ├── JwtUtil.java
│   ├── SecurityConfig.java
│   └── JwtAuthenticationFilter.java
├── service/            # Business logic
│   ├── UserService.java
│   ├── AIService.java
│   ├── CustomUserDetailsService.java
│   └── MailService.java
├── dto/            # DTO classes
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── SendMailRequest.java
│   │   └── SignupRequest.java
│   └── response/
│   │   ├── ApiResponse.java
│   │   ├── AuthResponse.java
│   │   ├── MailResponse.java
│   │   └── UserResponse.java
└── EmailServiceBackendApplication.java
```

## 🔑 API Endpoints

### Authentication
- `POST /api/signup` - Register new user
- `POST /api/auth/login` - User login (returns JWT token)

### Emails (Protected - Requires JWT Token)
- `GET /api/emails/inbox` - Get inbox emails
- `GET /api/emails/sent` - Get sent emails
- `GET /api/emails/starred` - Get starred emails
- `GET /api/emails/trash` - Get trashed emails
- `POST /api/emails` - Send/compose new email
- `POST /api/emails/{id}/reply` - Reply to email
- `GET /api/emails/{id}` - Get specific email
- `PUT /api/emails/{id}/star` - Star/unstar email
- `DELETE /api/emails/{id}` - Move email to trash

## 🧪 Testing Credentials

Use these test accounts for development and testing:

| Username | Password | Email |
|----------|----------|-------|
| `george` | `george123` | `george@example.com` |
| `alice` | `alice123` | `alice@example.com` |

Or register a new user via `/api/auth/register`

## 🔒 Environment Variables

### For Local Development
Set in `application.properties`:
```properties
jwt.secret=your-local-secret-key-at-least-256-bits
jwt.expiration=86400000
spring.datasource.url=jdbc:postgresql://localhost:5432/email_system_db
spring.datasource.username=postgres
spring.datasource.password=your_password

# OS Environment Variable (must be exported):
GEMINI_API_KEY=YOUR_API_KEY
```

### For Production (Railway)
Set as environment variables:
```env
DATABASE_URL=postgresql://user:password@host:port/database
JWT_SECRET=your-production-secret-key-minimum-256-bits
JWT_EXPIRATION=86400000
PORT=8080
GEMINI_API_KEY=YOUR_PRODUCTION_GEMINI_KEY
```

## 🚀 Deployment

### Railway Deployment

1. **Connect Repository** to Railway
2. **Add PostgreSQL Database** (Railway addon)
3. **Set Environment Variables**:

```env
   JWT_SECRET=your-production-secret-key
   JWT_EXPIRATION=86400000
   DATABASE_URL=${{Postgres.DATABASE_URL}}
   GEMINI_API_KEY=YOUR_PRODUCTION_GEMINI_KEY
```
4. **Deploy** - Railway auto-deploys on push to main

The application automatically detects Railway's `DATABASE_URL` and configures itself.

## 🐛 Troubleshooting

### Port Already in Use
```bash
# Linux/Mac
lsof -ti:8080 | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Database Connection Issues
```bash
# Check if PostgreSQL is running
sudo service postgresql status

# Check database exists
psql -U postgres -l

# Test connection
psql -U postgres -d email_system_db
```

### JWT Issues
- Ensure `jwt.secret` is at least 256 bits (32+ characters)
- Verify token is being sent in Authorization header: `Bearer <token>`

## AI Summarization Not Working
- Crucially: Ensure the GEMINI_API_KEY environment variable is set correctly both locally and in your deployment environment.
- Check the application logs (logging.level.root=INFO) for any connection errors or API rate limits.

### CORS Issues
- Ensure `WebConfig.java` has correct allowed origins
- For local development: `http://localhost:3000`, `http://localhost:5173`
- For production: Your frontend deployment URL

## 📝 Development Workflow

1. **Create feature branch:**
```bash
   git checkout -b feature/your-feature
```

2. **Develop and test locally** with PostgreSQL running

3. **Commit changes:**
```bash
   git add .
   git commit -m "Add feature description"
   git push origin feature/your-feature
```

4. **Create Pull Request** and merge to main

5. **Railway auto-deploys** from main branch

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👥 Authors

- **Niduka** - [GitHub](https://github.com/Niduka292)

## 🔗 Related Links

- **Frontend Repository**: https://github.com/Niduka292/email-service-frontend.git
- **Live Demo**: https://email-service-frontend-rho.vercel.app/inbox
- **Production API**: https://email-service-backend-production.up.railway.app

## 📞 Support

For issues and questions:
- Open an issue on GitHub
- Check the API documentation
- Review troubleshooting section
