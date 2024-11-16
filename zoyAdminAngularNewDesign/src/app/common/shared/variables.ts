import { InjectionToken } from '@angular/core';
import { DefMenu } from './def-menu';

export const BASE_PATH =new InjectionToken<string>('basePath');

export const defMenuEnable =new InjectionToken<DefMenu>('defMenuEnable');
