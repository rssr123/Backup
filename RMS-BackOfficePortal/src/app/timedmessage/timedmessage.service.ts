// import { Injectable } from '@angular/core';
// import { MatDialog } from '@angular/material/dialog';
// import { TimedMessageComponent } from './timedmessage.component';

// @Injectable({
//   providedIn: 'root'
// })
// export class TimedMessageService {

//   constructor(private dialog: MatDialog) { }

//   schedulePopUpAtNoon(): void {
//     const now = new Date();
//     const nextNoon = new Date();
//     nextNoon.setHours(12, 0, 0, 0);

//     if (now.getTime() > nextNoon.getTime()) {
//       nextNoon.setDate(now.getDate() + 1);
//     }

//     const timeUntilNoon = nextNoon.getTime() - now.getTime();

//     setTimeout(() => {
//       this.openDialog();
//       this.schedulePopUpAtNoon();
//     }, timeUntilNoon);
//   }

//   private openDialog(): void {
//     this.dialog.open(TimedMessageComponent, {
//       width: '300px',
//       data: { message: 'Please check out' }
//     });
//   }
// }
