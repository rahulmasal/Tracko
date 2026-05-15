# Build stage
FROM node:20-alpine AS build

WORKDIR /app

# Build context is ./web, so paths are relative to that
COPY admin-portal/package.json admin-portal/package-lock.json ./
RUN npm ci

COPY admin-portal/tsconfig.json admin-portal/tsconfig.node.json admin-portal/vite.config.ts admin-portal/index.html ./
COPY admin-portal/src ./src
COPY shared ./shared

RUN npm run build

# Runtime stage
FROM nginx:alpine

RUN apk add --no-cache curl \
    && addgroup -S tracko && adduser -S tracko -G tracko

COPY --from=build /app/dist /usr/share/nginx/html

COPY admin-portal/nginx.conf /etc/nginx/conf.d/default.conf

RUN chown -R tracko:tracko /usr/share/nginx/html \
    && chown -R tracko:tracko /etc/nginx/conf.d

USER tracko

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
