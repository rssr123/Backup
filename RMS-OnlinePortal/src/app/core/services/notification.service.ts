import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, map, Observable, of } from 'rxjs';
import { environment } from 'src/environments/environment';
import { WebSocketSubject } from 'rxjs/webSocket';
import { Client, Message } from '@stomp/stompjs';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  // private socket$: WebSocketSubject<any>;
  // private client: Client;
  //private socket: WebSocket;
  private notificationMyTaskSubject = new BehaviorSubject<number>(0);
  private notificationCreatedTaskSubject = new BehaviorSubject<number>(0);
  $notificationCreatedTask = this.notificationCreatedTaskSubject.asObservable();
  $notificationMyTask = this.notificationMyTaskSubject.asObservable();
  username = this.authService.username;

  constructor(private http: HttpClient, private authService: AuthService) {
   // this.socket = new WebSocket('wss://localhost:8080/notifications');
   // this.initializeWebSocket();
  }


  // initializeWebSocket() {


  //   this.socket.onopen = () => {
  //     console.log('WebSocket connection established');
  //     const payload = {
  //       username: this.username
  //     };
  //     const payloadString = JSON.stringify(payload);
  //     console.log('Payload stringified:', payloadString);
  //     this.socket.send(payloadString);
  //   };

  //   this.socket.onmessage = (event: MessageEvent) => {
    
  //     console.log('Message received from WebSocket:', event.data);

     
  //     try {
  //       const message = JSON.parse(event.data);
  //       if (message.type === 'notificationUpdate') {
  //         this.notificationMyTaskSubject.next(message.myTaskCount);
  //         this.notificationCreatedTaskSubject.next(message.createdTaskCount);
  //       }
  //     } catch (e) {
  //       console.error('Error parsing WebSocket message:', e);
  //     }
  //   };

  //   this.socket.onerror = (error: Event) => {
  //     console.error('WebSocket error:', error);
  //   };

  //   this.socket.onclose = (event: CloseEvent) => {
  //     console.log('WebSocket connection closed:', event);
  //     // Optionally try to reconnect after a delay
  //     setTimeout(() => this.initializeWebSocket(), 5000);
  //   };

  //   this.socket.onerror = function (event) {
  //     console.error('WebSocket error observed:', event);
  //   };
  //}

  // getMyTaskNotificationCount(): any {

  //   const url = environment.apiUrl + '/api/mftwf/v1/getmytaskunreadcount';

  //   // Set your authorization header
  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json'
  //   });

  //   const body: any = {
  //     i_assign_to: this.username,
  //   };

  //   return this.http.post<number>(url, body, { headers }).pipe(
  //     map((response: any) => {
  //       console.log("response " + response)
  //       if (response.header.statusCode === '00') {
  //         this.notificationMyTaskSubject.next(response.data);
  //       }
  //       else {
  //         this.notificationMyTaskSubject.next(0);
  //       }
  //     }),
  //   ).subscribe();
  // }

  // getCreatedTaskNotificationCount(): any {

  //   const urlMftWF = environment.apiUrl + '/api/mftwf/v1/getmasterfeetableworkflow';

  //   // Set your authorization header
  //   const headers = new HttpHeaders({
  //     Authorization: environment.authKey,
  //     'Content-Type': 'application/json'
  //   });

  //   const body: any = {
  //     i_page: 1,
  //     i_size: 1000,
  //     i_created_by: this.username,
  //     i_wf_is_in_prg: "lf"
  //   };

  //   return this.http.post<number>(urlMftWF, body, { headers }).pipe(
  //     map((response: any) => {
  //       console.log("response " + response)
  //       if (response.data && response.data.length > 0) {
  //         this.notificationCreatedTaskSubject.next(response.data[0].total);
  //       }
  //       else {
  //         this.notificationCreatedTaskSubject.next(0);
  //       }
  //     }),
  //   ).subscribe();
  // }




}
