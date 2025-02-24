# Step 1: Build the React app
FROM node:18 AS build
WORKDIR /app

# Copy package.json and package-lock.json to leverage layer caching
COPY package*.json ./
RUN npm install

# Copy the rest of the project files
COPY . . 

# Install dependencies before building
RUN npm install react-router-dom @types/react-router-dom

# Build the React app for production
RUN npm run build

# Step 2: Prepare the runtime image with Nginx
FROM nginx:alpine

# Copy the build artifacts to Nginx's html directory
COPY --from=build /app/dist /usr/share/nginx/html

# Copy the custom NGINX config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose the port Nginx will use
EXPOSE 80

# Run Nginx in the foreground
CMD ["nginx", "-g", "daemon off;"]
