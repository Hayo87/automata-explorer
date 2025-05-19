
# Getting Started

The application consist of a a Spring Boot backend and a React frontend. The setup is dockerized for easy deployment and usage. This guide will help you to run the Automata Explorer using Docker for regular users as well as developers.

## 👤 For Users

### Prerequisites

- [Docker](https://www.docker.com/) installed.
- Browser installed. 


### Download docker-compose.yml and run

   ```bash
   curl -O https://raw.githubusercontent.com/Hayo87/automata-explorer/refs/heads/main/docker-compose.yml
   docker compose up
   ```

### Access the app

 Open a browser and go to [http://localhost:3000](http://localhost:3000).

 ### Stop the app

 Use the following command:

   ```bash
   docker-compose down
   ```

## 🛠️ For Developers

Clone the repo and run the app locally for development. 

### Prerequisites

- [Docker](https://www.docker.com/) for containerized deployment
- [Node.js](https://nodejs.org) for the frontend build
- [Maven](https://maven.apache.org) for the Spring Boot backend build

### Installation

1. **Clone the repository:**

   ```bash
   git clone https://github.com/hayo87/AutomataExplorer.git
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
   docker compose -f docker-compose.dev.yml up --build
   ```

2. **Access the frontend** at [http://localhost:3000](http://localhost:3000).

3. **Access the backend** at [http://localhost:8080](http://localhost:8080).

### Stopping the Containers

To stop and remove the containers, use the following command:

   ```bash
   docker compose -f docker-compose.dev.yml down
   ```