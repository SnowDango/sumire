import {ReactDOM} from "react";
import Helmet from "react-helmet";

export const htmlTemp = (reactDom: ReactDOM, helmetData: Helmet, title: string) => {
  return (`
<!DOCTYPE html>
  <html lang="ja">
    <head>
    <meta charset="utf-8">
    ${ helmetData.title.toString() }
    ${ helmetData.meta.toString() }
    <title>
    ${title}
    </title>
  <link rel="stylesheet" type="text/css" href="./styles.css" />
</head>
  <body>
  <div id="app">
    ${ reactDom }
  </div>
  <script>

  </script>
  <script src="./app.bundle.js"></script>
  </body>
</html>
`)
};