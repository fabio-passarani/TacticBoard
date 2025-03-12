import { fabric } from 'fabric';

declare module 'fabric' {
  interface Object {
    data?: {
      id: string;
      type: string;
      [key: string]: any;
    };
  }

  interface IObjectOptions {
    data?: {
      id: string;
      type: string;
      [key: string]: any;
    };
  }
}