import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../env/env';
import { Observable, tap } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class LoginService {
  private URL_API: string = environment.ApiUrl + '/facturacion/login';

  constructor(private http: HttpClient) { }



  login(username: string, password: string): Observable<any> {

    const params = { username: username, password: password };

     return this.http.get<any>(this.URL_API, {
      params: params
    }).pipe(
        tap( response => {
          if(response.code == 200){
            localStorage.setItem('token', response.data);
          }

        }
      )
     );

  }
  private BASE_URL: string = environment.ApiUrl;
  register(userData: {username: string, password: string}): Observable<any> {
    return this.http.post(`${this.BASE_URL}/facturacion/register`, userData);
  }


}
