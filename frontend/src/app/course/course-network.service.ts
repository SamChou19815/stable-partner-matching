import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { GoogleUserService } from '../shared/google-user.service';
import { StudentCourse, StudentProfile } from '../shared/data';
import { AuthenticatedNetworkService } from '../shared/authenticated-network-service';

@Injectable({
  providedIn: 'root'
})
export class CourseNetworkService extends AuthenticatedNetworkService {

  constructor(http: HttpClient, private googleUserService: GoogleUserService) {
    super(http, '/apis/courses');
  }

  async edit(studentCourse: StudentCourse): Promise<StudentCourse> {
    this.firebaseAuthToken = await this.googleUserService.afterSignedIn();
    return this.postData<StudentCourse>('/edit', studentCourse);
  }

  async delete(studentCourse: StudentCourse): Promise<void> {
    this.firebaseAuthToken = await this.googleUserService.afterSignedIn();
    const okText = await this.postDataForText('/delete', studentCourse);
    if (okText === 'OK') {
      return;
    } else {
      throw new Error();
    }
  }
}
