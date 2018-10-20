import { Injectable } from '@angular/core';
import { AuthenticatedNetworkService } from './authenticated-network-service';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class GlobalDataService extends AuthenticatedNetworkService {

  constructor(http: HttpClient) {
    super(http, '/apis/');
  }


}
