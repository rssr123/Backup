import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ActionMappingService {
  private mapping: { [key: string]: string } = {
   "Pending Finance Admin": "Assign / Query to Finance Admin",
    "Pending SME": "Assign / Query to SME",
    "Pending BYM": "Approved by Finance Admin / SME",
    "Pending FSM/FHOD": "Assign / Query to FSM/FHOD",
    "Pending DCEO": "Assign / Query to DCEO",
    "Pending CEO": "Assign / Query to CEO",
    "Refund Request": "Refund Request",
    "THRESHOLD ASSIGN": "Threshold Assign",
    "Pending RG": "Pending Refund Slip Generated ",
    "Job Pick Up": "Job Pick Up",
    "Pending Refund Slip Generated": "Approval by Threshold User Role",
    "Refund Bank Info Submitted": "Refund Bank Info Submitted",
    "Refund Rejected": "Refund Rejected",
    "Pending PG": "Assign / Query to PG",
    "Preview Refund Submission": "Assign to PG preview the refund submission",
    "Refund Submitted": "Refund Submitted",
    "Refund Info Update": "Refund Info Update",
    "Pending Email Refund Submitted": "Approved by Finance Admin",
    "Refund Slip Generated": "Refund Slip Generated",
    "Refund Email Sent": "Refund Email Sent",
    "Return to Public Task List": "Return to Public Task List",
    "Bank Error": "Bank Error",
    "Refund Expired": "Refund Expired"
  };

  getMapping(): { [key: string]: string } {
    return this.mapping;
  }
}
