// src/app/shared/animations.ts
import { trigger, transition, style, animate, keyframes } from '@angular/animations';
import { environment } from 'src/environments/environment';
//fadeInStayOut
export const fadeInOut = trigger('fadeInOut', [
  transition(':enter', [
    style({ position: 'relative', zIndex: '99999', opacity: 0  }),  // Ensure z-index is applied when the element enters
    animate('7000ms', keyframes([
      style({ opacity: 0, offset: 0 , zIndex: 99999}),                               // start fully transparent
      style({ opacity: 1, offset: 0.1 , zIndex: 99999}),                             // fade in (10% of total time)
      style({ opacity: 1, offset: 0.9 , zIndex: 99999}),                             // stay visible (80% of total time)
      style({ opacity: 0, offset: 1 , zIndex: 99999})                                // fade out (last 10% of total time)
    ]))
  ]),
  transition(':leave', [
    style({ zIndex: 99999 }),
    animate('5000ms', style({ opacity: 0, zIndex: 99999})) // quick fade out when element leaves
  ]),
]);


