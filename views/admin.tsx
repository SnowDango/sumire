import React from 'react';
import { Head } from '@react-ssr/express';
import CardPanelGrid from "./component/CardPanelGrid";
import Header from "./component/Header";
import { Theme } from "./styles/themeBase";
import {useDarkTheme} from "./styles/dark/theme";

export default () => {
  let theme: Theme = useDarkTheme();

  return (
    <React.Fragment>
      <Head>
        <title>admin page</title>
      </Head>
      <body className={theme.lay1}>
      <Header title={"SnowDango Tools"}/>
      <CardPanelGrid/>
      </body>
    </React.Fragment>
  );
};