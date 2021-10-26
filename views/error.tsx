import React from "react";
import { Head } from '@react-ssr/express';
import {Typography} from "@material-ui/core";

export interface ErrorProps {
  statusCode: string,
  describe: string
}

export default (props: ErrorProps) => {
  return(
    <React.Fragment>
      <Head>
        <title>{props.statusCode}</title>
      </Head>
      <body>
      <h2>{props.statusCode}</h2>
      <p>{props.describe}</p>
      </body>
    </React.Fragment>
  );
}