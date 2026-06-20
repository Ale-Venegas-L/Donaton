FROM node:22-alpine AS build
WORKDIR /app
<<<<<<< HEAD
COPY package.json package-lock.json .
RUN npm ci
=======
COPY package.json package-lock.json ./
RUN npm install
>>>>>>> develop
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
