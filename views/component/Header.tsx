import {AppBar, Button, makeStyles, Toolbar, Typography} from "@material-ui/core";
import React from "react";

const useStyles = makeStyles({
  logoutButton: {
    marginLeft: 'auto',
  },
});

interface HeaderProps {
  title: string
}

const darkTheme = makeStyles({
  darkButton: {
    color: "#FFFFFF"
  }
})

export default (props: HeaderProps) => {
  const classes = useStyles();
  const darkClasses = darkTheme();
  return(<AppBar position="static">
      <Toolbar>
        <Typography variant="h6">
          {props.title}
        </Typography>
        <Button  className={`${classes.logoutButton} ${darkClasses.darkButton}`} href={"/logout"}>
          Logout
        </Button>
      </Toolbar>
    </AppBar>
  )
}