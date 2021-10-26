import {Request, Response, Router} from "express";
import {requiresAuth} from "express-openid-connect";
import {StatusCodes,getReasonPhrase} from "http-status-codes";
import {ErrorProps} from "../../views/error";
import {getLogger} from "../logger";

const router: Router = Router();

router.get('/',requiresAuth(),(((req: Request, res: Response) => {
  console.log(req.get("host"));
  res.render("admin");
})));

// logout
router.get("/logout",(((_req: Request, res: Response) => {
  res.oidc.logout().then(data => {
    console.log(data)
    res.redirect("/");
  })
})));

// error page
router.use("/error/:errorId", ((req: Request, res: Response) => {
  const data: ErrorProps = {
    statusCode: req.params.errorId,
    describe: getReasonPhrase(req.params.errorId)
  }
  if(req.accepts("html")){
    res.render("error",data);
  }else if(req.accepts("json")){
    res.status(parseInt(req.params.errorId));
    res.send(data);
  }
}));

router.use("*",((req, res) => {
  res.redirect("/error/"+StatusCodes.NOT_FOUND.toString());
}));

export default router;

