import React from 'react';
import { Head } from '@react-ssr/express';
import CardPanelGrid from "./component/CardPanelGrid";
import Header from "./component/Header";

export default () => {
  return (
    <React.Fragment>
      <Head>
        <title>admin page</title>
      </Head>
      <body>
      <Header title={"SnowDango Tools"}/>
      <CardPanelGrid/>
      </body>
    </React.Fragment>
  );
};