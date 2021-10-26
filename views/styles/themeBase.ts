export interface ThemeBase {
  lay1: {
    background: string,
    color: string,
    backgroundColor: string,
  },
  lay2:{
    background: string,
    color: string,
    backgroundColor: string,
  },
  lay3: {
    background: string,
    color: string,
    backgroundColor: string,
  },
  head: {
    background: string,
    color: string,
    backgroundColor: string,
  },
  title: {
    background: string,
    color: string,
    backgroundColor: string,
  },
  text: {
    background: string,
    color: string,
    backgroundColor: string,
  },
  link: {
    background: string,
    color: string,
    backgroundColor: string
  },
}

export interface Theme extends Record<"lay1" | "lay2" | "lay3" | "head" | "title" | "text" |"link", string>{}