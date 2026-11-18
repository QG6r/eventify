# Build stage: compile the React app
FROM node:18-alpine AS builder
WORKDIR /app

COPY package*.json ./
RUN npm ci --only=production

COPY . .

# Create React App bakes REACT_APP_* values in at build time, so they must be declared as build args
ARG REACT_APP_API_URL
ARG REACT_APP_USE_MOCKS=false
ENV REACT_APP_API_URL=$REACT_APP_API_URL \
    REACT_APP_USE_MOCKS=$REACT_APP_USE_MOCKS
RUN npm run build

# Runtime stage: serve static files with nginx
FROM nginx:alpine
COPY --from=builder /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
