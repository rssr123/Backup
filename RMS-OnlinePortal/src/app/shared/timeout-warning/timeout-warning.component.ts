import { Component, OnDestroy, OnInit, Renderer2, NgZone } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-timeout-warning',
  template: `
    <div *ngIf="visible" class="timeout-overlay">
      <div class="timeout-modal">
        <h3>Session Timeout</h3>
        <p>You have been idle for too long. You will be logged out.</p>
      </div>
    </div>
  `,
  styleUrls: ['./timeout-warning.component.scss']
})
export class TimeoutWarningComponent implements OnInit, OnDestroy {
  visible = false;
  private clickListener?: () => void;

  constructor(
    private router: Router,
    private renderer: Renderer2,
    private zone: NgZone
  ) {}

  ngOnInit(): void {
    window.addEventListener('show-idle-warning', this.showWarning);
  }

  ngOnDestroy(): void {
    window.removeEventListener('show-idle-warning', this.showWarning);
    this.removeClickListener();
  }

  showWarning = () => {
    this.zone.run(() => {
      this.visible = true;

      // Attach global click listener
      this.removeClickListener();
      this.clickListener = this.renderer.listen('document', 'click', () => {
        this.router.navigate(['/logout']);
      });
    });
  };

  private removeClickListener() {
    if (this.clickListener) {
      this.clickListener();
      this.clickListener = undefined;
    }
  }
}
