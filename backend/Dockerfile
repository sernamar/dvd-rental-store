FROM clojure:temurin-17-tools-deps-alpine

# Install some utilities
RUN apk add bash

# Set working directory
WORKDIR /backend

# Prepare deps
COPY deps.edn ./
RUN clojure -P

# Add sources
COPY ./ ./

# Run server
CMD clojure -M -m dvd-rental-store.server
