FROM node:10.18.1-alpine AS base

# the version of the marklogic koop provider
ARG provider_version=1.1.0
#ARG external_port=9000

# tools
RUN apk --update add --no-cache --virtual .deps \
    git curl python make g++ gcc cyrus-sasl krb5-dev

# install Koop
RUN curl -LsS https://github.com/koopjs/koop-provider-marklogic/archive/v${provider_version}.tar.gz | tar zxv
WORKDIR /koop-provider-marklogic-${provider_version}

COPY provider-config.json /koop-provider-marklogic-${provider_version}/config/docker.json
COPY certs/ /koop-provider-marklogic-${provider_version}/certs

RUN npm  --no-optional install

# delete build dependencies to keep the image smaller
#RUN apk del .deps

ENV NODE_ENV=docker

#EXPOSE ${external_port}

CMD node server.js
