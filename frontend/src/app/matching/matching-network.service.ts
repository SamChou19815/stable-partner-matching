import { Injectable } from '@angular/core';
import { AuthenticatedNetworkService } from '../shared/authenticated-network-service';
import { HttpClient } from '@angular/common/http';
import { GoogleUserService } from '../shared/google-user.service';
import { StudentPublicInfo } from '../shared/data';

@Injectable({
  providedIn: 'root'
})
export class MatchingNetworkService extends AuthenticatedNetworkService {

  constructor(http: HttpClient, private googleUserService: GoogleUserService) {
    super(http, '/apis/matching');
  }

  async getRanking(courseId?: string): Promise<StudentPublicInfo[]> {
    this.firebaseAuthToken = await this.googleUserService.afterSignedIn();
    const url = courseId == null ? '/general' : `/course?course_id=${courseId}`;
    return this.getData<StudentPublicInfo[]>(url);
  }

}
