FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
COPY .npmrc ./
RUN npm ci
COPY . .
ARG VITE_API_URL=
ENV VITE_API_URL=${VITE_API_URL}
RUN npm run build

FROM nginx:alpine
ARG GIT_SHA=unknown
ARG BUILD_TIME=unknown
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /tmp/nginx.conf
RUN SHORT_SHA=$(printf '%s' "$GIT_SHA" | cut -c1-7) && \
    sed "s|__GIT_SHA__|${GIT_SHA}|g; s|__GIT_SHORT_SHA__|${SHORT_SHA}|g; s|__BUILD_TIME__|${BUILD_TIME}|g" \
      /tmp/nginx.conf > /etc/nginx/conf.d/default.conf
EXPOSE 80
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD wget -qO- http://localhost/ || exit 1
