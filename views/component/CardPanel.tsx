import React from "react";
import {
  Avatar,
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Grid,
  makeStyles, Typography,
} from "@material-ui/core";

const useStyles = makeStyles({
  card: {
    margin: "120px auto 50px",
    maxWidth: 345,
    overflow: "visible"
  },
  media: {
    margin: "-70px auto 0",
    width: "80%",
    height: 140,
    borderRadius: "4px",
    boxShadow: "0 10px 20px rgba(0, 0, 0, 0.19), 0 6px 6px rgba(0, 0, 0, 0.23)",
    position: "relative",
    zIndex: 1000
  }
});

export interface CardProps {
  title: string,
  describe: string,
  icon: string,
  git: string | null,
  url: string,
}

export default (props: CardProps) => {
  const classes = useStyles();
  return (<Grid item xs={8} sm={6} md={4}>
      <Card className={classes.card}>
        <CardHeader
          title={<Typography>{props.title}</Typography>}
          avatar={<Avatar src={props.icon} />}
        />
        <CardContent>
          <Typography>
            {props.describe}
          </Typography>
        </CardContent>
        <CardActions>
          {urlButton(props.url, "Go")}
          {urlButton(props.git, "GitHub")}
        </CardActions>
      </Card>
    </Grid>
  );
};

const urlButton = (url: string | null, uiText: string) => {
  if (url === null){
    return
  }else{
    return(
      <Button size="small" href={url}>
        {uiText}
      </Button>
    );
  }
}

