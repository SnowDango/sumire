import {makeStyles} from "@material-ui/core";
import {ThemeBase} from "../themeBase";

const lightTheme: ThemeBase = {
  lay1: {
    background: "#FFFFFF",
    color: "#FFFFFF",
    backgroundColor: "#FFFFFF",
  },
  lay2:{
    background: "#FFFFFF",
    color: "#FFFFFF",
    backgroundColor: "#FFFFFF",
  },
  lay3: {
    background: "#FFFFFF",
    color: "#FFFFFF",
    backgroundColor: "#FFFFFF",
  },
  head: {
    background: "#2962FF",
    color: "#2962FF",
    backgroundColor: "#2962FF",
  },
  title: {
    background: "#000000",
    color: "#000000",
    backgroundColor: "#000000",
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

export const useLightTheme = makeStyles(lightTheme);