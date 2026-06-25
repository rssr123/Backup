// action-tracker.decorator.ts
import { AuditLogService } from './audit-log.service';

export function ActionTracker(action: string) {
  return function(target: any, key: string, descriptor: PropertyDescriptor) {
    const originalMethod = descriptor.value;

    descriptor.value = function(...args: any[]) {
      const result = originalMethod.apply(this, args);
      const auditLogService = new AuditLogService(); // Inject AuditLogService using dependency injection if needed
      auditLogService.logAction(action, args);
      return result;
    };

    return descriptor;
  };
}
