# Build stage
FROM node:20-alpine AS build

WORKDIR /app

# Build context is ./web, so paths are relative to that
COPY manager-portal/package.json manager-portal/package-lock.json ./
RUN npm ci

COPY manager-portal/tsconfig.json manager-portal/tsconfig.node.json manager-portal/vite.config.ts manager-portal/index.html ./
COPY manager-portal/src ./src
COPY shared ./shared

RUN npm run build

# Runtime stage
FROM nginx:alpine

RUN apk add --no-cache curl \
    && addgroup -S tracko && adduser -S tracko -G tracko

COPY --from=build /app/dist /usr/share/nginx/html

COPY manager-portal/nginx.conf /etc/nginx/conf.d/default.conf

RUN chown -R tracko:tracko /usr/share/nginx/html \
    && chown -R tracko:tracko /etc/nginx/conf.d

USER tracko

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
