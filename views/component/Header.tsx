import {AppBar, Button, makeStyles, Toolbar, Typography} from "@material-ui/core";
import React from "react";
import {darkThemeColor} from "../theme";

const useStyles = makeStyles({
  logoutButton: {
    marginLeft: 'auto',
  },
});

interface HeaderProps {
  title: string
}

export default (props: HeaderProps) => {
  const classes = useStyles()
  const themeClasses = darkThemeColor();
  return(<AppBar position="static">
      <Toolbar className={themeClasses.darkHeader}>
        <Typography variant="h6" className={themeClasses.darkHeaderText}>
          {props.title}
        </Typography>
        <Button className={`${classes.logoutButton} ${themeClasses.darkHeaderText}`} href={"/logout"}>Logout</Button>
      </Toolbar>
    </AppBar>
  )
}