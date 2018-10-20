import { Injectable } from '@angular/core';
import { AuthenticatedNetworkService } from './authenticated-network-service';
import { HttpClient } from '@angular/common/http';
import { dummyInitData, InitData } from './data';
import { GoogleUserService } from './google-user.service';

@Injectable({
  providedIn: 'root'
})
export class GlobalDataService extends AuthenticatedNetworkService {

  private _data: InitData = dummyInitData;

  constructor(http: HttpClient, private googleUserService: GoogleUserService) {
    super(http, '/apis');
  }

  async initializeApp(): Promise<void> {
    if (!this._data.isNotInitialized) {
      return;
    }
    this.firebaseAuthToken = await this.googleUserService.afterSignedIn();
    const rawData = await this.getData<InitData>('/load');
    this._data = <InitData>{ ...rawData, isNotInitialized: false };
  }

  get initData(): InitData {
    return this._data;
  }


}
