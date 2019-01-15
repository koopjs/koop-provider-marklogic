FROM node:8.12.0

RUN mkdir /usr/src/app

# Create app directory
WORKDIR /usr/src/app

COPY package.json ./
RUN npm install
COPY . .

EXPOSE 8099
ENV NODE_ENV=docker
CMD [ "node", "server.js" ]
