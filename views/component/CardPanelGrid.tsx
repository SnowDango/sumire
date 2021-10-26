import React from "react";
import {Grid, makeStyles} from "@material-ui/core";
import CardPanel from "./CardPanel";
import {serverRoute} from "../../server_data";

const useStyle = makeStyles({
  gridContainer: {
    paddingLeft: "8px",
    paddingRight: "8px",
  }
});

export default () => {
  const classes = useStyle();
  return (
    <Grid container direction="row" className={classes.gridContainer}>
      <Grid item sm={1} />
      <Grid container　alignItems="center" item spacing={2} xs={12} sm={10}>
        {cards()}
      </Grid>
      <Grid item sm={1} />
    </Grid>
  );
}

const cards = () => {
  return serverRoute.map((data, index) =>
    <CardPanel
      title={data.title}
      describe={data.describe}
      icon={data.iconUrl}
      git={data.githubUrl}
      url={data.url}
      key={index}
    />
  )
}