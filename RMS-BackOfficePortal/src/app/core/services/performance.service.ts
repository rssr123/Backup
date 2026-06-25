import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PerformanceService {

  constructor() { }

  measurePerformanceAsync(func: () => Observable<any> | Promise<any> | void, funcName: string): void {
    const start = performance.now();
    const result = func();

    if (result instanceof Observable) {
      // Handle Observable
      result.subscribe(
        () => this.logPerformance(start, funcName),
        (error) => console.error('Error in operation:', error)
      );
    } else if (result instanceof Promise) {
      // Handle Promise
      result.then(
        () => this.logPerformance(start, funcName),
        (error) => console.error('Error in operation:', error)
      );
    } else {
      // Handle synchronous operation
      this.logPerformance(start, funcName);
    }
  }

  private logPerformance(start: number, funcName: string): void {
    const end = performance.now();
    console.log(`${funcName} took ${end - start} milliseconds.`);
  }
}
