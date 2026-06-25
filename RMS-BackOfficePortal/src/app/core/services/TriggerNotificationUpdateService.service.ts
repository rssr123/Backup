import {Injectable} from '@angular/core';
import {Observable, Subject} from "rxjs";

@Injectable()
export class TriggerNotificationUpdateService{
  private mySubject: Subject<any> = new Subject<string>();
  public readonly messageReceived$: Observable<string> = this.mySubject.asObservable();

  emitEvent(data: any) {
    this.mySubject.next(data);
  }
}