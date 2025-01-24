import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class WebsocketService {
  private sockets: { [key: string]: WebSocket } = {};
  private webSocketReturns: { [key: string]: BehaviorSubject<any> } = {};

  constructor() {}

  connect(url: string, key: string): void {
    if (this.sockets[key]) {
      console.log('Already connected to', key);
      return;
    }

    const socket = new WebSocket(url);

    this.sockets[key] = socket;

    this.webSocketReturns[key] = new BehaviorSubject<string>('');

    socket.onmessage = (event) => {
      this.webSocketReturns[key].next(event.data); 
    };

    socket.onopen = () => {
      console.log(`WebSocket connected to ${key}`);
    };

    // socket.onclose = () => {
    //   console.log(`WebSocket disconnected from ${key}`);
    // };

    // socket.onerror = (error) => {
    //   console.error(`WebSocket error on ${key}:`, error);
    // };
  }

  getMessages(key: string) {
    console.log(key,"this.webSocketReturns",this.webSocketReturns)
    return this.webSocketReturns[key]?.asObservable();
  }

  closeConnection(key: string) {
    if (this.sockets[key]) {
      this.sockets[key].close();
      delete this.sockets[key]; 
      delete this.webSocketReturns[key]; 
    }
  }
}
