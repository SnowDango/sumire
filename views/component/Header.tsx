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

export default (props: HeaderProps) => {
  const classes = useStyles()
  return(<AppBar position="static">
      <Toolbar>
        <Typography variant="h6">
          {props.title}
        </Typography>
        <Button color="inherit" className={classes.logoutButton} href={"/logout"}>Logout</Button>
      </Toolbar>
    </AppBar>
  )
}