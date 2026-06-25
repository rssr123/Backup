import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GlobalService {

  //new code
  private globalValueSubject: BehaviorSubject<any>;
  globalValue$: Observable<any>;

  constructor(private router: Router) { 
    const storedValue = localStorage.getItem('globalValue') || 'en';
    this.globalValueSubject = new BehaviorSubject<any>(storedValue);
    this.globalValue$ = this.globalValueSubject.asObservable();

  }

  setGlobalValue(value: any): void {
    console.log('Setting global value to: ', value);
    //add new line to store the value in local storage
    localStorage.setItem('globalValue', value);
    this.globalValueSubject.next(value);
  }

  getGlobalValue(): any {
    return this.globalValueSubject.value;
  }
}