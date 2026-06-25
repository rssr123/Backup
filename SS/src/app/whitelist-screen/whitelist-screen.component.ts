import { Component } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-whitelist-screen',
  templateUrl: './whitelist-screen.component.html',
  styleUrls: ['./whitelist-screen.component.scss']
})
export class WhitelistScreenComponent {
  i_ss_cd = '';
  i_ip = '';
  i_remark = '';
  isSubmitting = false;
  message: string | null = null;
  isSuccess = false;

  private readonly apiUrl = `${environment.apiUrl}/api/rms/v1/inswhiteip`;

  constructor(private http: HttpClient) {}

  onSubmit() {

    this.isSubmitting = true;
    this.message = null;

    const headers = new HttpHeaders({
      Authorization: environment.authKey,
      'Content-Type': 'application/json',
    });

    const body = {
      i_ss_cd: this.i_ss_cd,
      i_ip: this.i_ip,
      i_remark: this.i_remark
    };

    this.http.post<any>(this.apiUrl, body, {headers}).subscribe({
      next: (res) => {
        this.isSubmitting = false;
        if (res?.data === 0) {
          this.isSuccess = false;
          this.message = 'IP address already exists in whitelist.';
        } else {
          this.isSuccess = true;
          this.message = 'Whitelist IP successfully inserted.';
          this.i_ss_cd = '';
          this.i_ip = '';
          this.i_remark = '';
        }
      },
      error: (err) => {
        this.isSubmitting = false;
        this.isSuccess = false;
        this.message = 'Error occurred while inserting whitelist IP.';
      }
    });
  }
}
