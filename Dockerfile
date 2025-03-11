FROM postgres:16.8-bookworm

ENV POSTGRES_PASSWORD=""
ENV POSTGRES_DB="ecommerce"
ENV POSTGRES_HOST_AUTH_METHOD="trust"

COPY database.sql /docker-entrypoint-initdb.d/

EXPOSE 5432:5432