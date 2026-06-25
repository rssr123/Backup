import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { environment } from 'src/environments/environment';

type DataStructure = Array<{
    col1: number;
    col2: number;
}>;

@Injectable({
  providedIn: 'root'
})
export class ApiServiceService {

  private apiUrl = environment.apiUrl + '/api/mft/v1/getTableData';

  

  constructor(private http: HttpClient) { }

   

  fetchData(): Observable<ApiResponse<DataStructure>> {

    const headers = new HttpHeaders({
      'Authorization': environment.authKey,
      'Content-Type': 'application/x-www-form-urlencoded'
    });

    return this.http.get<ApiResponse<DataStructure>>(this.apiUrl, { headers });

}

}