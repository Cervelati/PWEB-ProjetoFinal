FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .

ARG VITE_BRAND_NAME=Escarapela
ARG VITE_DEV_TOOLS=false
ARG VITE_DEV_WEBHOOK_SECRET=troque-este-segredo
ENV VITE_BRAND_NAME=$VITE_BRAND_NAME \
    VITE_DEV_TOOLS=$VITE_DEV_TOOLS \
    VITE_DEV_WEBHOOK_SECRET=$VITE_DEV_WEBHOOK_SECRET

RUN npm run build


FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
