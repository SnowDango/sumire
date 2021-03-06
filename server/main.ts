import express, {NextFunction, Request, Response} from 'express';
import register from '@react-ssr/express/register';
import methodOverride from "method-override";
import {auth} from "express-openid-connect";
import {load} from "ts-dotenv";
import http from "http";
import log4js from "log4js";

import router from "./router/router";
import * as path from "path";
import {getLogger} from "./logger";
import {ErrorProps} from "../views/error";

const env = load({
  CLIENT_ID: String,
  PORT: Number,
  ISSUER_BASE_URL: String,
  SECRET: String,
  BASE_URL: String,
  SECRET_SESSION: String
});

const app = express();

app.use(express.json());
app.use(auth({
  authRequired: false,
  secret: env.SECRET_SESSION,
  clientSecret: env.SECRET,
  clientID: env.CLIENT_ID,
  baseURL: env.BASE_URL,
  issuerBaseURL: env.ISSUER_BASE_URL,
  auth0Logout: true,
  idpLogout: true,
}));

(async () => {
  await register(app);

  app.use(methodOverride());
  app.use("/resource",express.static(path.join(__dirname, '../resource')));
  app.use((err: any, _req: Request, res: Response, _next: NextFunction) => {
    const logger = getLogger("debug");
    logger.error(err.toString());
    logger.error(err.typeof);
    res.redirect("/error/401");

  });
  app.use(router);

  http.createServer(app).listen(env.PORT);
  console.log('> Ready on http://localhost:3000');
})();
