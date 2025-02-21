
# AutomataExplorer

This is a starting template for the StateMachineExplorer application.

## Features
- Full-stack application with a Spring Boot backend and a React frontend.
- Backend exposed on `http://localhost:8080`.
- Frontend served through Nginx on `http://localhost:3000`.
- Dockerized setup for easy development and deployment.

## Getting Started

### Prerequisites

- Docker (for containerized deployment)
- Node.js (for the frontend build)
- Maven (for the Spring Boot backend build)

### Installation

1. **Clone the repository:**

   ```bash
   git clone https://github.com/yourusername/StateMachineExplorer.git
   cd StateMachineExplorer
   ```

2. **Install dependencies for the backend** (Spring Boot):

   ```bash
   cd backend
   ./mvnw install  # On Windows, use mvnw.cmd
   ```

3. **Install dependencies for the frontend** (React/Vite):

   ```bash
   cd frontend
   npm install
   ```

### Running the Project with Docker

This project uses Docker Compose to build and run both the frontend and backend services.

1. **Build and start both the frontend and backend containers**:

   ```bash
   docker-compose up --build
   ```

2. **Access the frontend** at [http://localhost:3000](http://localhost:3000).

3. **Access the backend** at [http://localhost:8080](http://localhost:8080).

### Stopping the Containers

To stop and remove the containers, use the following command:

```bash
docker-compose down
```

## Project Structure

```plaintext
StateMachineExplorer/
│
├── backend/              # Spring Boot backend project
│   ├── src/              # Backend source code
│   ├── pom.xml           # Maven configuration
│   └── Dockerfile        # Dockerfile for backend
│
├── frontend/             # React frontend project
│   ├── src/              # Frontend source code
│   ├── package.json      # npm configuration
│   └── Dockerfile        # Dockerfile for frontend
│
├── docker-compose.yml    # Docker Compose configuration
├── .gitignore            # Git ignore rules
├── .dockerignore         # Docker ignore rules
└── README.md             # Project documentation
```


## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
