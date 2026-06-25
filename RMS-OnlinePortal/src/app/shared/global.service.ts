import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GlobalService {

  private globalValueSubject: BehaviorSubject<any> = new BehaviorSubject<any>("en");
  globalValue$: Observable<any> = this.globalValueSubject.asObservable();

  constructor() { }

  setGlobalValue(value: any): void {
    console.log('Setting global value to: ', value);
    this.globalValueSubject.next(value);
  }

  getGlobalValue(): any {
    return this.globalValueSubject.value;
  }
}
