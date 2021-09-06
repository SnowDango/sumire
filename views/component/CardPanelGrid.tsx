import React from "react";
import {Grid, makeStyles} from "@material-ui/core";
import CardPanel from "./CardPanel";

const useStyle = makeStyles({
  gridContainer: {
    paddingLeft: "8px",
    paddingRight: "8px"
  }
});

export default () => {
  const classes = useStyle();
  return (
    <Grid container direction="row" className={classes.gridContainer}>
      <Grid item sm={1} />
      <Grid container　alignItems="center" item spacing={2} xs={12} sm={10}>
        <CardPanel/>
        <CardPanel/>
        <CardPanel/>
        <CardPanel/>
        <CardPanel/>
      </Grid>
      <Grid item sm={1} />
    </Grid>
  );
}