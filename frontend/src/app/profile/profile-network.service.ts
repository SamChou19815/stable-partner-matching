import { Injectable } from '@angular/core';
import { AuthenticatedNetworkService } from '../shared/authenticated-network-service';
import { HttpClient } from '@angular/common/http';
import { StudentProfile, StudentPublicInfo } from '../shared/data';
import { GoogleUserService } from '../shared/google-user.service';

@Injectable({
  providedIn: 'root'
})
export class ProfileNetworkService extends AuthenticatedNetworkService {

  constructor(http: HttpClient, private googleUserService: GoogleUserService) {
    super(http, '/apis/profile');
  }

  async update(studentProfile: StudentProfile): Promise<void> {
    this.firebaseAuthToken = await this.googleUserService.afterSignedIn();
    const okText = await this.postDataForText('/update', studentProfile);
    if (okText === 'OK') {
      return;
    } else {
      throw new Error();
    }
  }

  async loadPublic(studentId: string): Promise<StudentPublicInfo> {
    this.firebaseAuthToken = await this.googleUserService.afterSignedIn();
    return this.getData<StudentPublicInfo>(`/load_public?student_id=${studentId}`);
  }

}
