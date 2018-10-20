import { Injectable } from '@angular/core';
import { AuthenticatedNetworkService } from '../shared/authenticated-network-service';
import { HttpClient } from '@angular/common/http';
import { GoogleUserService } from '../shared/google-user.service';
import { StudentFreeTimeRecord, StudentProfile } from '../shared/data';

@Injectable({
  providedIn: 'root'
})
export class FreeTimeNetworkService extends AuthenticatedNetworkService {

  constructor(http: HttpClient, private googleUserService: GoogleUserService) {
    super(http, '/apis/free-time');
  }

  async update(studentFreeTimeRecord: StudentFreeTimeRecord): Promise<StudentFreeTimeRecord> {
    this.firebaseAuthToken = await this.googleUserService.afterSignedIn();
    return this.postData<StudentFreeTimeRecord>('/update', studentFreeTimeRecord);
  }

}
