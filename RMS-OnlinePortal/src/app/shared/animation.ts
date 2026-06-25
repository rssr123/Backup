// src/app/shared/animations.ts
import { trigger, transition, style, animate, keyframes } from '@angular/animations';
import { environment } from 'src/environments/environment';

export const fadeInOut = trigger('fadeInStayOut', [
  transition(':enter', [
    animate(`${environment.fadeInOutDuration + 1000}ms`, keyframes([
      style({ opacity: 0, offset: 0 }),                               // start fully transparent
      style({ opacity: 1, offset: 0.1 }),                             // fade in (10% of total time)
      style({ opacity: 1, offset: 0.9 }),                             // stay visible (80% of total time)
      style({ opacity: 0, offset: 1 })                                // fade out (last 10% of total time)
    ]))
  ]),
  transition(':leave', [
    animate('1500ms', style({ opacity: 0 })) // quick fade out when element leaves
  ]),
]);


