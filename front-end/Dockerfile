FROM node:22-alpine3.19

WORKDIR /app

RUN npm install -g @angular/cli


COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

EXPOSE 4200

CMD ["ng", "serve", "--host", "0.0.0.0"]
