import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DataService {

private wf_id: number | null = null;
private task_id: string | null = null;
private fee_detail_id: string | null = null;
private fee_grp_id: number | null = null;
private fee_grp_nm_en: string | null = null;
private fee_grp_nm_bm: string | null = null;
private fee_detail_nm_en: string | null = null;
private fee_detail_nm_bm: string | null = null;
private fee_amt: number | null = null;
private promo_startdt: Date | null = null;
private promo_enddt: Date | null = null;
private promo_fee: number | null = null;
private tax_cd_id: number | null = null;
private tax_cd: string | null = null;
private allow_otc: number | null = null;
private ll_parent_id: string | null = null;
private ll_start_day: number | null = null;
private ll_start_mth: number | null = null;
private ll_end_day: number | null = null;
private ll_end_mth: number | null = null;
private ledger_cd: string | null = null;
private ss_cd: string | null = null;
private ss_nm: string | null = null;
private effective_date: Date | null = null;
private dt_created: Date | null = null;
private dt_modified: Date | null = null;
private created_by: string | null = null;
private created_by_nm: string | null = null;
private modified_by: string | null = null;
private modified_by_nm: string | null = null;
private status: string | null = null;
private status_en: string | null = null;
private status_bm: string | null = null;
private assign_to: string | null = null;
private assign_to_nm: string | null = null;
private action: string | null = null;
private total: number | null = null;
private showUpdateAlert:boolean = false;
private unit_fee:number | null = null;
private showInsertAlert:boolean = false;
private alertMessage:string | null = null;

// Getters and setters for all data fields

setWfId(wf_id: number) {
  this.wf_id = wf_id;
}

getWfId() {
  return this.wf_id;
}

setTaskId(task_id: string) {
  this.task_id = task_id;
}

getTaskId() {
  return this.task_id;
}

setFeeDetailId(fee_detail_id: string) {
  this.fee_detail_id = fee_detail_id;
}

getFeeDetailId() {
  return this.fee_detail_id;
}

setFeeGrpId(fee_grp_id: number) {
  this.fee_grp_id = fee_grp_id;
}

getFeeGrpId() {
  return this.fee_grp_id;
}

setFeeGrpNmEn(fee_grp_nm_en: string) {
  this.fee_grp_nm_en = fee_grp_nm_en;
}

getFeeGrpNmEn() {
  return this.fee_grp_nm_en;
}

setFeeGrpNmBm(fee_grp_nm_bm: string) {
  this.fee_grp_nm_bm = fee_grp_nm_bm;
}

getFeeGrpNmBm() {
  return this.fee_grp_nm_bm;
}

setFeeDetailNmEn(fee_detail_nm_en: string) {
  this.fee_detail_nm_en = fee_detail_nm_en;
}

getFeeDetailNmEn() {
  return this.fee_detail_nm_en;
}

setFeeDetailNmBm(fee_detail_nm_bm: string) {
  this.fee_detail_nm_bm = fee_detail_nm_bm;
}

getFeeDetailNmBm() {
  return this.fee_detail_nm_bm;
}

setFeeAmt(fee_amt: number) {
  this.fee_amt = fee_amt;
}

getFeeAmt() {
  return this.fee_amt;
}

setPromoStartDt(promo_startdt: Date) {
  this.promo_startdt = promo_startdt;
}

getPromoStartDt() {
  return this.promo_startdt;
}

setPromoEndDt(promo_enddt: Date) {
  this.promo_enddt = promo_enddt;
}

getPromoEndDt() {
  return this.promo_enddt;
}

setPromoFee(promo_fee: number) {
  this.promo_fee = promo_fee;
}

getPromoFee() {
  return this.promo_fee;
}

setTaxCdId(tax_cd_id: number) {
  this.tax_cd_id = tax_cd_id;
}

getTaxCdId() {
  return this.tax_cd_id;
}

setTaxCd(tax_cd: string) {
  this.tax_cd = tax_cd;
}

getTaxCd() {
  return this.tax_cd;
}

setAllowOtc(allow_otc: number) {
  this.allow_otc = allow_otc;
}

getAllowOtc() {
  return this.allow_otc;
}

setLlParentId(ll_parent_id: string) {
  this.ll_parent_id = ll_parent_id;
}

getLlParentId() {
  return this.ll_parent_id;
}

setLlStartDay(ll_start_day: number) {
  this.ll_start_day = ll_start_day;
}

getLlStartDay() {
  return this.ll_start_day;
}

setLlStartMth(ll_start_mth: number) {
  this.ll_start_mth = ll_start_mth;
}

getLlStartMth() {
  return this.ll_start_mth;
}

setLlEndDay(ll_end_day: number) {
  this.ll_end_day = ll_end_day;
}

getLlEndDay() {
  return this.ll_end_day;
}

setLlEndMth(ll_end_mth: number) {
  this.ll_end_mth = ll_end_mth;
}

getLlEndMth() {
  return this.ll_end_mth;
}

setLedgerCd(ledger_cd: string) {
  this.ledger_cd = ledger_cd;
}

getLedgerCd() {
  return this.ledger_cd;
}

setSsCd(ss_cd: string) {
  this.ss_cd = ss_cd;
}

getSsCd() {
  return this.ss_cd;
}

setSsNm(ss_nm: string) {
  this.ss_nm = ss_nm;
}

getSsNm() {
  return this.ss_nm;
}

setEffectiveDate(effective_date: Date) {
  this.effective_date = effective_date;
}

getEffectiveDate() {
  return this.effective_date;
}

setDtCreated(dt_created: Date) {
  this.dt_created = dt_created;
}

getDtCreated() {
  return this.dt_created;
}

setDtModified(dt_modified: Date) {
  this.dt_modified = dt_modified;
}

getDtModified() {
  return this.dt_modified;
}

setCreatedBy(created_by: string) {
  this.created_by = created_by;
}

getCreatedBy() {
  return this.created_by;
}

setModifiedBy(modified_by: string) {
  this.modified_by = modified_by;
}

getModifiedBy() {
  return this.modified_by;
}

setModifiedByNm(modified_by_nm: string) {
  this.modified_by_nm = modified_by_nm;
}

getModifiedByNm() {
  return this.modified_by_nm;
}

setStatusEn(status_en: string) {
  this.status_en = status_en;
}

getStatusEn() {
  return this.status_en;
}

setStatusBm(status_bm: string) {
  this.status_bm = status_bm;
}

getStatusBm() {
  return this.status_bm;
}

setAssignTo(assign_to: string) {
  this.assign_to = assign_to;
}

getAssignTo() {
  return this.assign_to;
}

setAssignToNm(assign_to_nm: string) {
  this.assign_to_nm = assign_to_nm;
}

getAssignToNm() {
  return this.assign_to_nm;
}

setAction(action: string) {
  this.action = action;
}

getAction() {
  return this.action;
}

setTotal(total: number) {
  this.total = total;
}

getTotal() {
  return this.total;
}

setShowUpdateAlert(showUpdateAlert: boolean) {
  this.showUpdateAlert = showUpdateAlert;
}

getShowUpdateAlert() {
  return this.showUpdateAlert;
}

setUnitFee(unit_fee: number) {
  this.unit_fee = unit_fee;
}

getUnitFee() {
  return this.unit_fee;
}

setCreatedByNm(created_by_nm: string) {
  this.created_by_nm = created_by_nm;
}

getCreatedByNm() {
  return this.created_by_nm;
}

setStatus(status: string) {
  this.status = status;
}

getStatus() {
  return this.status;
}

setShowInsertAlert(showInsertAlert: boolean) {
  this.showInsertAlert = showInsertAlert;
}

getShowInsertAlert() {
  return this.showInsertAlert;
}

setAlertMessage(alertMessage: string | null) {
  this.alertMessage = alertMessage;
}

getAlertMessage() {
  return this.alertMessage;
}

}
