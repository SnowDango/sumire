import React from "react";
import {
  Avatar,
  Button,
  Card, CardActionArea,
  CardActions,
  CardContent, CardHeader,
  CardMedia,
  Grid, Icon,
  makeStyles,
  Typography
} from "@material-ui/core";
import {darkThemeColor} from "../theme";

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

export default () => {
  const classes = useStyles();
  const themeClasses = darkThemeColor();
  return (<Grid item xs={12} sm={6} md={4}>
      <Card className={`${classes.card} ${themeClasses.cardBody}`}>
        <CardMedia
          className={classes.media}
          image="https://news.1242.com/wp-content/uploads/2020/04/S__94969859RS.jpg"
          title="Contemplative Reptile"
        />
        <CardHeader title={
          <Typography component="h1" className={themeClasses.darkTitle}>Lizard</Typography>
        } avatar={
          <Avatar src={"http://localhost:3000/resource/images/hieda.jpg"} />
        }/>
        <CardContent>
          <Typography component="p" className={themeClasses.darkText}>
            Lizards are a widespread group of squamate reptiles, with over 6,000
            species, ranging across all continents except Antarctica
          </Typography>
        </CardContent>
        <CardActions>
          <Button size="small" className={themeClasses.linkText}>
            Share
          </Button>
          <Button size="small" className={themeClasses.linkText}>
            Learn More
          </Button>
        </CardActions>
      </Card>
    </Grid>
  );
};