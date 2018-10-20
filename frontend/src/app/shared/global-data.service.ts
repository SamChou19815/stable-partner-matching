import { Injectable } from '@angular/core';
import { AuthenticatedNetworkService } from './authenticated-network-service';
import { HttpClient } from '@angular/common/http';
import { dummyInitData, InitData, SimplifiedCourseInfo } from './data';
import { GoogleUserService } from './google-user.service';

@Injectable({
  providedIn: 'root'
})
export class GlobalDataService extends AuthenticatedNetworkService {

  private _data: InitData = dummyInitData;
  private _keyToCourseMap: Map<string, SimplifiedCourseInfo> = new Map<string, SimplifiedCourseInfo>();

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
    for (const course of this._data.allCourses) {
      this._keyToCourseMap.set(course.key, course);
    }
  }

  get initData(): InitData {
    return this._data;
  }

  getCourseByKey(key: string): SimplifiedCourseInfo {
    const ans = this._keyToCourseMap.get(key);
    if (ans == null) {
      throw new Error();
    } else {
      return ans;
    }
  }

  getCourseNameByKey(key: string): string {
    const course = this.getCourseByKey(key);
    return `${course.subject}${course.code}: ${course.title}`;
  }

}
