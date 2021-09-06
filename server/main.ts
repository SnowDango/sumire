import express, {NextFunction, Request, Response} from 'express';
import register from '@react-ssr/express/register';
import methodOverride from "method-override";
import {auth, requiresAuth} from "express-openid-connect";
import {load} from "ts-dotenv";
import log4js from "log4js";
import http from "http";
import * as path from "path";

const logger = log4js.getLogger();
logger.level = "debug";

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
  app.use(express.static("../resource"));
  app.use((err: any, _req: Request, res: Response, _next: NextFunction) => {
    logger.debug(err.toString());
    res.redirect("/failed");
  });

  app.use('*',requiresAuth(),(((_req: Request, res: Response) => {
    res.render('admin');
  })));

  app.use("/logout",(((_req: Request, res: Response) => {
    res.oidc.logout().then(data => {
      console.log(data)
      res.redirect("/");
    })
  })))

  http.createServer(app).listen(env.PORT);
  console.log('> Ready on http://localhost:3000');
})();
