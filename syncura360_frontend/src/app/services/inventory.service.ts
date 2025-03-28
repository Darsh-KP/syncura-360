import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, Observable, throwError } from 'rxjs';

export interface inventory {
  ndc?: number;
  name?: string;
  category: string;
  description: string;
  strength?: string;
  ppq: number;
  quantity?: number;
  price?: number;
}

export interface inventoryUpdateDto {
  ndc: number;
  fields: Partial<inventory>;
}

export interface inventoryUpdateRequest {
  updates: inventoryUpdateDto[];
}

@Injectable({
  providedIn: 'root'
})
export class inventoryService {
  private baseUrl = 'http://localhost:8080/drug';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
  }

  getInventory(): Observable<inventory[]> {
    return this.http.get<{ message: string; drugs: inventory[] } | null>(`${this.baseUrl}`, {
      headers: this.getHeaders()
    })
      .pipe(
        map(response => {
          if (response && response.drugs) {
            console.log(response.message);
            return response.drugs;
          } else {
            // Handle 204 or bad response gracefully
            return []; // Empty array instead of throwing error
          }
        }),
        catchError(error => {
          console.error('Error fetching inventory:', error);
          return throwError(() => new Error(error.message || 'Failed to load inventory.'));
        })
      );
  }



  createInventory(drug: inventory): Observable<{ message: string }> {
    const headers = this.getHeaders();


    return this.http.post<{ message: string }>(this.baseUrl, drug, { headers });
  }

  deleteInventory(ndc: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.baseUrl}/${ndc}`, {
      headers: this.getHeaders()
    }).pipe(
        catchError(error => {
          console.error('Error deleting inventory item:', error);
          return throwError(() => new Error(error.message || 'Failed to delete inventory item.'));
        })
    );
  }


  updateInventoryQuantities(updates: inventoryUpdateDto[]): Observable<{ message: string; ndc: string[] }> {
    const requestBody: inventoryUpdateRequest = { updates };

    return this.http.put<{ message: string; ndc: string[] }>(`${this.baseUrl}`, requestBody, { headers: this.getHeaders() });

  }
}
