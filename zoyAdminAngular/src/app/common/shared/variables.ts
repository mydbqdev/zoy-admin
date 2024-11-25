import { InjectionToken } from '@angular/core';
import { DefMenu } from './def-menu';

export const BASE_PATH =new InjectionToken<string>('basePath');
export const BASE_PATH_EXTERNAL_SERVER =new InjectionToken<string>('basePathExternal');

export const defMenuEnable =new InjectionToken<DefMenu>('defMenuEnable');
