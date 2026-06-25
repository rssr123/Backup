import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, map, Observable, of } from 'rxjs';
import { environment } from 'src/environments/environment';
import { AuthService } from '../../core/services/auth.service';

// WebSocket imports disabled - not used anymore
// import { WebSocketSubject } from 'rxjs/webSocket';
// import { Client, Message } from '@stomp/stompjs';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  // WebSocket functionality disabled - using HTTP polling instead
  // private socket?: WebSocket;
  // private heartbeatInterval: any;

  // MFT
  private notificationMyTaskSubject = new BehaviorSubject<number>(0);
  private notificationCreatedTaskSubject = new BehaviorSubject<number>(0);
  $notificationCreatedTask = this.notificationCreatedTaskSubject.asObservable();
  $notificationMyTask = this.notificationMyTaskSubject.asObservable();

  // OCT-RC
  private notificationOctRCAssignedTasksSubject = new BehaviorSubject<number>(0);
  private notificationOctRCCreatedTasksSubject = new BehaviorSubject<number>(0);
  $notificationOctRCAssignedTasks = this.notificationOctRCAssignedTasksSubject.asObservable();
  $notificationOctRCCreatedTasks = this.notificationOctRCCreatedTasksSubject.asObservable();

  // Refund
  private notificationRefundAssignedTasksSubject = new BehaviorSubject<number>(0);
  private notificationRefundCreatedTasksSubject = new BehaviorSubject<number>(0);
  $notificationRefundAssignedTasks = this.notificationRefundAssignedTasksSubject.asObservable();
  $notificationRefundCreatedTasks = this.notificationRefundCreatedTasksSubject.asObservable();

  // Billing
  private notificationBillingAssignedTasksSubject = new BehaviorSubject<number>(0);
  private notificationBillingCreatedTasksSubject = new BehaviorSubject<number>(0);
  $notificationBillingAssignedTasks = this.notificationBillingAssignedTasksSubject.asObservable();
  $notificationBillingCreatedTasks = this.notificationBillingCreatedTasksSubject.asObservable();

  //Credit Control
  private notificationCCCAssignedTasksSubject =  new BehaviorSubject<number>(0);
  private notificationCCCCreatedTasksSubject = new BehaviorSubject<number>(0);
  $notificationCCCAssignedTasks = this.notificationCCCAssignedTasksSubject.asObservable();
  $notificationCCCCreatedTasks = this.notificationCCCCreatedTasksSubject.asObservable();

  username: string = '';
  private pollingInterval: any;

  constructor(private http: HttpClient, public authService: AuthService) {
   // WebSocket notifications disabled
   // this.socket = new WebSocket(environment.notificationURL);
   // this.initializeWebSocket();
   console.log('WebSocket notifications are disabled - using HTTP polling for notifications');
   this.username = this.authService.username;
   this.startPolling();
  }

  // Start polling for notification counts every 10 seconds
  private startPolling(): void {
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
    }
    
    this.pollingInterval = setInterval(() => {
      if (this.username) {
        this.getTaskNotificationCounts(this.username);
      }
    }, 10000); // Poll every 10 seconds
  }

  // Stop polling
  stopPolling(): void {
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
      this.pollingInterval = null;
    }
  }

  // Manually refresh notification counts
  refreshNotificationCounts(): void {
    if (this.username) {
      this.getTaskNotificationCounts(this.username);
    }
  }

  // initializeWebSocket() {

  //   this.socket.onopen = () => {
  //     //console.log('WebSocket connection established');
  //     const payload = {
  //       username: this.username
  //     };
  //     const payloadString = JSON.stringify(payload);
  //     //console.log('Payload stringified:', payloadString);
  //     this.socket.send(payloadString);
  //   };

  //   this.socket.onmessage = (event: MessageEvent) => {
    
  //     //console.log('Message received from WebSocket:', event.data);

  //     try {
  //       const message = JSON.parse(event.data);
  //       if (message.type === 'notificationUpdate') {
  //         //mft
  //         this.notificationMyTaskSubject.next(message.myTaskCount);
  //         this.notificationCreatedTaskSubject.next(message.createdTaskCount);
          
  //         //refund                           
  //         this.notificationRefundAssignedTasksSubject.next(message.myTaskAssignedCountRefund);
  //         this.notificationRefundCreatedTasksSubject.next(message.createdTaskCountRefund);
  //         console.log('Notification refund assigned counts updated:', message.myTaskAssignedCountRefund);
  //         console.log('Notification refund created counts updated:', message.createdTaskCountRefund);

  //         //otc-rc
  //         this.notificationOctRCAssignedTasksSubject.next(message.myTaskAssignedCountOTCRC);
  //         this.notificationOctRCCreatedTasksSubject.next(message.createdTaskCountOTCRC);

  //         //billing
  //         this.notificationBillingAssignedTasksSubject.next(message.myTaskAssignedCountBilling);
  //         this.notificationBillingCreatedTasksSubject.next(message.createdTaskCountBilling);

  //         //credit control
  //         this.notificationCCCAssignedTasksSubject.next(message.myTaskAssignedCountCC);
  //         this.notificationCCCCreatedTasksSubject.next(message.createdTaskCountCC);
  //       }
  //     } catch (e) {
  //       console.error('Error parsing WebSocket message:', e);
  //     }
  //   };

  //   this.socket.onerror = (error: Event) => {
  //     console.error('WebSocket error:', error);
  //   };

  //   this.socket.onclose = (event: CloseEvent) => {
  //     //console.log('WebSocket connection closed:', event);
  //     // Optionally try to reconnect after a delay
  //     setTimeout(() => this.initializeWebSocket(), 5000);
  //   };

  //   this.socket.onerror = function (event) {
  //     console.error('WebSocket error observed:', event);
  //   };
  // }

  // WebSocket functionality disabled
  /*
  initializeWebSocket() {
    try {
      this.socket = new WebSocket(environment.notificationURL);
    
      this.socket.onopen = () => {
        const payload = {
          username: this.username
        };
        this.socket.send(JSON.stringify(payload));
        console.log('WebSocket connected successfully');
    
        // start heartbeat for keep web socket alive (without this after sometimes like 5 minutes the the websocket wont update  anymore)
        this.heartbeatInterval = setInterval(() => {
          if (this.socket.readyState === WebSocket.OPEN) {
            this.socket.send(JSON.stringify({ type: 'ping' }));
            //console.log('Sent heartbeat ping');
          }
        }, 30000); // every 30 seconds
      };

      this.socket.onmessage = (event: MessageEvent) => {
        try {
          const message = JSON.parse(event.data);
    
          if (message.type === 'notificationUpdate') {
            //mft
            this.notificationMyTaskSubject.next(message.myTaskCount);
            this.notificationCreatedTaskSubject.next(message.createdTaskCount);
    
             //refund    
            this.notificationRefundAssignedTasksSubject.next(message.myTaskAssignedCountRefund);
            this.notificationRefundCreatedTasksSubject.next(message.createdTaskCountRefund);
            // console.log('Notification refund assigned counts updated:', message.myTaskAssignedCountRefund);
            // console.log('Notification refund created counts updated:', message.createdTaskCountRefund);

           // console.log('Updated refund notifications');
    
            //otc-rc
            this.notificationOctRCAssignedTasksSubject.next(message.myTaskAssignedCountOTCRC);
            this.notificationOctRCCreatedTasksSubject.next(message.createdTaskCountOTCRC);
    
            //billing
            this.notificationBillingAssignedTasksSubject.next(message.myTaskAssignedCountBilling);
            this.notificationBillingCreatedTasksSubject.next(message.createdTaskCountBilling);
    
            //credit control
            this.notificationCCCAssignedTasksSubject.next(message.myTaskAssignedCountCC);
            this.notificationCCCCreatedTasksSubject.next(message.createdTaskCountCC);
          }
    
        } catch (e) {
          console.error('Error parsing WebSocket message:', e);
        }
      };

      this.socket.onerror = (error) => {
        console.warn('WebSocket connection failed - notifications will not work in real-time:', error);
        // Don't throw error, just log warning
      };

      this.socket.onclose = (event) => {
        console.warn('WebSocket connection closed - notifications will not work in real-time');
        if (this.heartbeatInterval) {
          clearInterval(this.heartbeatInterval);
        }
        // Don't attempt to reconnect to avoid spam
      };

    } catch (error) {
      console.warn('Failed to initialize WebSocket - notifications will not work in real-time:', error);
    }
  }
  */

  // MFT
  getMyTaskNotificationCount(): any {

    const url = environment.apiUrl + '/api/mftwf/v1/getmytaskactivetaskcount';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const body: any = {
      i_assign_to: this.username,
    };

    return this.http.post<number>(url, body, { headers }).pipe(
      map((response: any) => {
        //console.log("response " + response)
        if (response.header.statusCode === '00') {
          this.notificationMyTaskSubject.next(response.data);
        }
        else {
          this.notificationMyTaskSubject.next(0);
        }
      }),
    ).subscribe();
  }

  getCreatedTaskNotificationCount(): any {

    const urlMftWF = environment.apiUrl + '/api/mftwf/v1/getcreatedtaskactivetaskcount';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const body: any = {
      i_created_by: this.username
    };

    return this.http.post<number>(urlMftWF, body, { headers }).pipe(
      map((response: any) => {
        //console.log("response " + response)
        if (response.header.statusCode === '00') {
          this.notificationCreatedTaskSubject.next(response.data);
        }
        else {
          this.notificationCreatedTaskSubject.next(0);
        }
      }),
    ).subscribe();
  }

  // OCT-RC
  getOtcRCAssignedTaskNotificationCount(): any {

    const url = environment.apiUrl + '/api/otcrcptccl/v1/getotcrcassignedtaskcount';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const body: any = {
      i_assign_to: this.username,
    };

    return this.http.post<number>(url, body, { headers }).pipe(
      map((response: any) => {
        //console.log("response " + response)
        if (response.header.statusCode === '00') {
          this.notificationOctRCAssignedTasksSubject.next(response.data);
        }
        else {
          this.notificationOctRCAssignedTasksSubject.next(0);
        }
      }),
    ).subscribe();
  }

  getOtcRCCreatedTaskNotificationCount(): any {
    
    const url = environment.apiUrl + '/api/otcrcptccl/v1/getotcrccreatedtaskcount';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const body: any = {
      i_created_by: this.username,
    };

    return this.http.post<number>(url, body, { headers }).pipe(
      map((response: any) => {
        //console.log("response " + response)
        if (response.header.statusCode === '00') {
          this.notificationOctRCCreatedTasksSubject.next(response.data);
        }
        else {
          this.notificationOctRCCreatedTasksSubject.next(0);
        }
      }),
    ).subscribe();
  }

  // Refund
  getRefundAssignedTaskNotificationCount(): any {
    
    const url = environment.apiUrl + '/api/rtt/v1/getrefundassignedtaskcount';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const body: any = {
      i_assigned_to: this.username,
    };

    return this.http.post<number>(url, body, { headers }).pipe(
      map((response: any) => {
        //console.log("Refund getrefundassignedtaskcount response: ");
        //console.log(response);
        if (response.header.statusCode === '00') {
          this.notificationRefundAssignedTasksSubject.next(response.data);
        }
        else {
          this.notificationRefundAssignedTasksSubject.next(0);
        }
      }),
    ).subscribe();
  }

  getRefundCreatedTaskNotificationCount(): any {
    
    const url = environment.apiUrl + '/api/rtt/v1/getrefundcreatedtaskcount';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const body: any = {
      i_created_by: this.username,
    };

    return this.http.post<number>(url, body, { headers }).pipe(
      map((response: any) => {
        //console.log("response " + response)
        if (response.header.statusCode === '00') {
          this.notificationRefundCreatedTasksSubject.next(response.data);
        }
        else {
          this.notificationRefundCreatedTasksSubject.next(0);
        }
      }),
    ).subscribe();
  }

  // Billing
  getBillingAssignedTaskNotificationCount(): any {
    
    const url = environment.apiUrl + '/api/bil/v1/getbillingassignedtaskcount';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const body: any = {
      i_assigned_to: this.username,
    };

    return this.http.post<number>(url, body, { headers }).pipe(
      map((response: any) => {
        //console.log("response " + response)
        if (response.header.statusCode === '00') {
          this.notificationBillingAssignedTasksSubject.next(response.data);
        }
        else {
          this.notificationBillingAssignedTasksSubject.next(0);
        }
      }),
    ).subscribe();
  }

  getBillingCreatedTaskNotificationCount(): any {
    
    const url = environment.apiUrl + '/api/bil/v1/getbillingcreatedtaskcount';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const body: any = {
      i_created_by: this.username,
    };

    return this.http.post<number>(url, body, { headers }).pipe(
      map((response: any) => {
        //console.log("response " + response)
        if (response.header.statusCode === '00') {
          this.notificationBillingCreatedTasksSubject.next(response.data);
        }
        else {
          this.notificationBillingCreatedTasksSubject.next(0);
        }
      }),
    ).subscribe();
  }

  // Credit Control
  getCCCAssignedTaskNotificationCount(): any {
    
    const url = environment.apiUrl + '/api/cc/v1/getassignedtaskcount';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const body: any = {
      i_assigned_to: this.username,
    };

    return this.http.post<number>(url, body, { headers }).pipe(
      map((response: any) => {
        //console.log("response " + response)
        if (response.header.statusCode === '00') {
          this.notificationCCCAssignedTasksSubject.next(response.data);
        }
        else {
          this.notificationCCCAssignedTasksSubject.next(0);
        }
      }),
    ).subscribe();
  }

  getCCCCreatedTaskNotificationCount(): any {
    
    const url = environment.apiUrl + '/api/cc/v1/getcreatedtaskcount';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const body: any = {
      i_created_by: this.username,
    };

    return this.http.post<number>(url, body, { headers }).pipe(
      map((response: any) => {
        if (response.header.statusCode === '00') {
          this.notificationCCCCreatedTasksSubject.next(response.data);
        }
        else {
          this.notificationCCCCreatedTasksSubject.next(0);
        }
      }),
    ).subscribe();
  }

  getTaskNotificationCounts(usernm: string): any {
    this.username = this.authService.username;
    if(this.username == null || this.username == '')
      return of(null);

    const url = environment.apiUrl + '/api/mytasks/v1/getnotificationcount';

    // Set your authorization header
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });

    const body: any = {
      i_assigned_to: this.username,
    };

    return this.http.post<number>(url, body, { headers }).pipe(
      map((response: any) => {
        if (response.header.statusCode === '00') {
          //console.log(response.data)
          this.notificationMyTaskSubject.next(response.data.mft_a);
          this.notificationCreatedTaskSubject.next(response.data.mft_c);

          this.notificationOctRCAssignedTasksSubject.next(response.data.otc_rc_a);
          this.notificationOctRCCreatedTasksSubject.next(response.data.otc_rc_c);

          this.notificationRefundAssignedTasksSubject.next(response.data.rtt_a);
          this.notificationRefundCreatedTasksSubject.next(response.data.rtt_c);

          this.notificationBillingAssignedTasksSubject.next(response.data.bil_a);
          this.notificationBillingCreatedTasksSubject.next(response.data.bil_c);

          this.notificationCCCAssignedTasksSubject.next(response.data.cc_a);
          this.notificationCCCCreatedTasksSubject.next(response.data.cc_c);
        }
        else {
          this.notificationMyTaskSubject.next(0);
          this.notificationCreatedTaskSubject.next(0);
          
          this.notificationOctRCAssignedTasksSubject.next(0);
          this.notificationOctRCCreatedTasksSubject.next(0);

          this.notificationRefundAssignedTasksSubject.next(0);
          this.notificationRefundCreatedTasksSubject.next(0);

          this.notificationBillingAssignedTasksSubject.next(0);
          this.notificationBillingCreatedTasksSubject.next(0);

          this.notificationCCCAssignedTasksSubject.next(0);
          this.notificationCCCCreatedTasksSubject.next(0);
        }
      }),
      catchError(error => {
        console.error('Error loading notification counts:', error);
        // Set all to zero on error to prevent freezing
        this.notificationMyTaskSubject.next(0);
        this.notificationCreatedTaskSubject.next(0);
        this.notificationOctRCAssignedTasksSubject.next(0);
        this.notificationOctRCCreatedTasksSubject.next(0);
        this.notificationRefundAssignedTasksSubject.next(0);
        this.notificationRefundCreatedTasksSubject.next(0);
        this.notificationBillingAssignedTasksSubject.next(0);
        this.notificationBillingCreatedTasksSubject.next(0);
        this.notificationCCCAssignedTasksSubject.next(0);
        this.notificationCCCCreatedTasksSubject.next(0);
        return of(null);
      })
    ).subscribe();
  }
}