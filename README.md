##Todo Summary Assistant

---

## Project Overview

Todo Summary Assistant is a full-stack application enabling users to:

* Create, view, update, and delete to-do tasks (CRUD operations).
* Summarize pending to-dos using Cohere's LLM API.
* Post summaries to a Slack channel for team visibility.

### Key Features

* Frontend: React-based with Axios for API calls.
* Backend: Spring Boot with MySQL and JPA.
* Integrations: Cohere API for LLM, Slack Incoming Webhooks for notifications.
* Secure and maintainable with layered architecture, logging, and validation.

---

## Table of Contents

* [Architecture](#architecture)
* [Prerequisites](#prerequisites)
* [Setup Instructions](#setup-instructions)

  * [Backend Setup](#backend-setup)
  * [Frontend Setup](#frontend-setup)
  * [MySQL Setup](#mysql-setup)
  * [Cohere API Setup](#cohere-api-setup)
  * [Slack Webhook Setup](#slack-webhook-setup)
* [Running the Application](#running-the-application)
* [API Endpoints](#api-endpoints)
* [Testing](#testing)
* [Troubleshooting](#troubleshooting)
* [Architecture Decisions](#architecture-decisions)
* [Development Best Practices](#development-best-practices)
* [Deployment](#deployment)
* [Future Enhancements](#future-enhancements)
* [Contributing](#contributing)
* [License](#license)

---

## Architecture

### Frontend (React)

* Dynamic UI using functional components (TodoForm, TodoList).
* Axios for API interactions.
* Styled with basic CSS; scalable with Tailwind CSS.

### Backend (Spring Boot)

* RESTful APIs with layered architecture (Controller, Service, Repository, Model).
* Integrates with Cohere API and Slack Webhook.
* Database interactions via Spring Data JPA.

### Database (MySQL)

* Table: `todos`
* Fields: `id`, `title`, `description`, `status`, `created_at`

### External Services

* **Cohere:** LLM summarization.
* **Slack:** Notifications via Incoming Webhooks.

---

## Prerequisites

* Java 17+
* Maven 3.6+
* Node.js 16+, npm 8+
* MySQL 8.0+
* Postman
* Git
* Cohere API key
* Slack Webhook URL

---

## Setup Instructions

### Backend Setup

```bash
git clone <repository-url>
cd todo-summary-assistant/backend
```

Install dependencies:

```bash
mvn clean install
```

Configure `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/todos_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
llm.api.key=your_cohere_api_key
llm.api.url=https://api.cohere.ai/v1/generate
slack.webhook.url=https://hooks.slack.com/services/xxx/yyy/zzz
```

Enable CORS in `WebConfig.java` if needed.

### Frontend Setup

```bash
cd ../frontend
npm install
```

Create `.env`:

```env
REACT_APP_API_URL=http://localhost:8080
```

### MySQL Setup

Create database:

```sql
CREATE DATABASE todos_db;
```

Spring Boot will auto-create the `todos` table.

### Cohere API Setup

* Get your API key from [cohere.com](https://cohere.com).
* Add the key to `application.properties`.

### Slack Webhook Setup

1. Create a Slack app.
2. Enable Incoming Webhooks.
3. Generate a Webhook URL.
4. Add it to `application.properties`.

---

## Running the Application

1. **Start MySQL**
2. **Run Backend**

```bash
cd backend
mvn spring-boot:run
```

3. **Run Frontend**

```bash
cd frontend
npm start
```

Visit: [http://localhost:3000](http://localhost:3000)

---

## API Endpoints

| Method | Endpoint    | Description                 |
| ------ | ----------- | --------------------------- |
| GET    | /todos      | Fetch all to-dos            |
| POST   | /todos      | Create a to-do              |
| PUT    | /todos/{id} | Update a to-do              |
| DELETE | /todos/{id} | Delete a to-do              |
| POST   | /summarize  | Summarize and post to Slack |

---

## Testing

### Backend Testing

Use Postman to test each endpoint with appropriate headers and bodies.

### Frontend Testing

* Manual Testing via UI.
* Unit tests with Jest:

```bash
npm test
```

---

## Troubleshooting

### Backend Issues

* **DB Connection:** Check MySQL is running, credentials correct.
* **RestTemplate Errors:** Ensure it's declared as a `@Bean`.
* **JSON Errors:** Sanitize and serialize prompts.

### Frontend Issues

* **CORS Errors:** Update `WebConfig.java`.
* **API Errors:** Check `REACT_APP_API_URL`.

### Cohere Issues

* **401 Unauthorized:** Check API key.
* **429 Too Many Requests:** Add retry logic.

### Slack Issues

* **no\_service Error:** Regenerate Webhook URL.
* **Private Channels:** Invite the app explicitly.

---

## Architecture Decisions

* **React**: Simplicity and scalability.
* **Spring Boot**: Robust backend and JPA integration.
* **MySQL**: Reliable relational storage.
* **Cohere API**: Easy LLM integration.
* **Slack Webhooks**: Quick notifications.

---

## Development Best Practices

* Use Git for version control.
* Maintain clean architecture.
* Log API calls and errors.
* Store secrets in `.env`/`application.properties`, not in version control.
* Validate user input.

---

## Deployment

### Backend (Render)

```bash
cd backend
mvn clean package
```

Upload the JAR and set environment variables.

### Frontend (Vercel)

```bash
cd frontend
npm run build
```

Push to GitHub and connect repository to Vercel.

---

## Future Enhancements

* Add authentication (Spring Security).
* Use Redis for caching summaries.
* Add deadline/reminder features.
* Expand Slack integration with buttons/actions.

---

## Contributing

Feel free to fork the repo, create a branch, and submit PRs. We welcome improvements, bug fixes, and new features.

---

## License

MIT License. See `LICENSE` file for details.
