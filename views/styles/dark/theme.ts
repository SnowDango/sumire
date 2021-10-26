import {makeStyles} from "@material-ui/core";
import {ThemeBase} from "../themeBase";

/*
base by MaterialUi color
https://material.io/design/color/dark-theme.html
*/

const darkTheme: ThemeBase = {
  lay1: {
    background: "#121212",
    color: "#121212",
    backgroundColor: "#121212",
  },
  lay2:{
    background: "#212121",
    color: "#212121",
    backgroundColor: "#212121",
  },
  lay3: {
    background: "#303030",
    color: "#303030",
    backgroundColor: "#303030",
  },
  head:{
    background: "#303030",
    color: "#303030",
    backgroundColor: "#303030"
  },
  title: {
    background: "#03DAC6",
    color: "#03DAC6",
    backgroundColor: "#03DAC6",
  },
  text: {
    background: "#FFFFFF",
    color: "#FFFFFF",
    backgroundColor: "#FFFFFF",
  },
  link: {
    background: "#BB86FC",
    color: "#BB86FC",
    backgroundColor: "#BB86FC"
  },
};
export const useDarkTheme = makeStyles(darkTheme);