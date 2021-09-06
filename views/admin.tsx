import React from 'react';
import { Head } from '@react-ssr/express';
import CardPanelGrid from "./component/CardPanelGrid";
import Header from "./component/Header";
import {darkThemeColor} from "./theme";

export default () => {
  const classes = darkThemeColor();
  return (
    <React.Fragment>
      <Head>
        <title>admin page</title>
      </Head>
      <body className={classes.darkBody}>
      <Header title={"SnowDango Tools"}/>
      <CardPanelGrid/>
      </body>　
    </React.Fragment>
  );
};