import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private http: HttpClient) { }
  baseUrl = 'http://localhost:8080';

  sendData(text: string) {
    const url = this.baseUrl + '/test/reverse_text';
    return this.http.post(url, { text });
  }

  register(username: string, password: string) {
    const url = this.baseUrl + '/auth/register'
    return this.http.post(url, { username, password} )
  }
  
}
