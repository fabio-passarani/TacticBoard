import { FabricObject } from 'fabric';

// Estendi FabricObject per includere la proprietà 'data'
declare module 'fabric' {
  interface FabricObject {
    data?: {
      id: string;
      type: string;
      [key: string]: any;
    };
  }
}