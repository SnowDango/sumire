import React from 'react';
import { Head } from '@react-ssr/express';
import {Card} from "@material-ui/core";

interface IndexProps {
  user: any;
}

export default (props: IndexProps) => {
  return (
    <React.Fragment>
      <Head>
        <title>admin</title>
      </Head>
      <body>
      <Card> sss </Card>
      </body>
    </React.Fragment>
  );
};
