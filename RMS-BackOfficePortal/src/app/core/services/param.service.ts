import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ParamData } from '../models/param.interface';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class ParamService {
  constructor(private http: HttpClient) {}

  public getStates(paramPage: string = '',paramSize: string = '',paramCd: string = '', paramGrpNm: string = ''): Observable<ParamData[]> {
    const url = environment.apiUrl + '/api/rms/v1/getparam';
    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json'
    });
    const requestBody = { i_page: paramPage, i_size: paramSize, i_param_cd: paramCd, i_param_grp_nm: paramGrpNm };

    return this.http.post<ParamData[]>(url, requestBody, { headers });
  }
}