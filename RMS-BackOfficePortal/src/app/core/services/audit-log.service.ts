// audit-log.service.ts
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuditLogService {

  constructor() {}

  logAction(action: string, data: any): void {
    const timestamp = new Date().toISOString();
    console.log(`[${timestamp}] Action: ${action}, Data:`, data);
  }
}
