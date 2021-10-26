import React from 'react';
import {
  Document,
  Main,
} from '@react-ssr/express';

export default class extends Document {
  render() {
    return (
      <html lang="en">
        <body>
          <Main />
        </body>
      </html>
    );
  }
};
