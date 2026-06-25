import { EventEmitter, Injectable } from "@angular/core";
import { BehaviorSubject, Observable, ReplaySubject, catchError, tap, throwError } from 'rxjs';

@Injectable({
  providedIn: "root"
})
export class CounterCheckInStatus {
  counterIdChanged: EventEmitter<any> = new EventEmitter();
  counterId: any;

  constructor() {}
  get data(): any {
    return this.counterId;
  }
  set data(val: any) {
    this.counterId = val;
    this.counterIdChanged.emit(val);
  }
}