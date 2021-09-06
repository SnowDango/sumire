import {NextFunction, Request, Response, Router} from "express";
import {requiresAuth} from "express-openid-connect";
import log4js from "log4js";

const router: Router = Router();

router.get('/',requiresAuth(),(((_req: Request, res: Response) => {
  res.render("admin");
})));

// logout
router.get("/logout",(((_req: Request, res: Response) => {
  res.oidc.logout().then(data => {
    console.log(data)
    res.redirect("/");
  })
})));

export default router;

