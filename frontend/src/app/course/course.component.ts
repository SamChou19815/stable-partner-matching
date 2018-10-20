import { Component, OnInit, ViewChild } from '@angular/core';
import { appCardData, ProjectCardData } from '../shared/project-card-data';
import { GlobalDataService } from '../shared/global-data.service';
import { GoogleUserService } from '../shared/google-user.service';
import { LoadingOverlayService } from '../shared/overlay/loading-overlay.service';
import { MatDialog } from '@angular/material';
import { shortDelay } from '../shared/util';
import { StudentCourse, TimeStatus } from '../shared/data';

@Component({
  selector: 'app-course',
  templateUrl: './course.component.html',
  styleUrls: ['./course.component.scss']
})
export class CourseComponent implements OnInit {

  readonly appIntro: ProjectCardData = appCardData;
  /**
   * Whether the user has logged in.
   */
  isUserLoggedIn = false;

  private allCourses: StudentCourse[] = [];
  pastCourses: StudentCourse[] = [];
  currentCourses: StudentCourse[] = [];
  futureCourses: StudentCourse[] = [];

  @ViewChild('drawer') drawer: any | undefined;

  constructor(private dataService: GlobalDataService,
              private googleUserService: GoogleUserService,
              private loadingService: LoadingOverlayService,
              private dialog: MatDialog) {
  }

  ngOnInit() {
    shortDelay(async () => {
      const ref = this.loadingService.open();
      this.isUserLoggedIn = await this.googleUserService.isSignedInPromise();
      if (!this.isUserLoggedIn) {
        ref.close();
        return;
      }
      await this.dataService.initializeApp();
      this.constructCourseData(this.dataService.initData.courses);
      ref.close();
    });
  }

  signIn(): void {
    this.googleUserService.signIn();
  }

  get isInitialized(): boolean {
    return !this.dataService.initData.isNotInitialized;
  }

  // noinspection JSMethodCanBeStatic
  get onMobile() {
    return window.innerWidth < 800;
  }

  get mode() {
    return this.onMobile ? 'over' : 'side';
  }

  private constructCourseData(allCourses: StudentCourse[]) {
    this.allCourses = allCourses;
    this.pastCourses = [];
    this.currentCourses = [];
    this.futureCourses = [];
    for (const course of allCourses) {
      switch (course.status) {
        case 'PAST':
          this.pastCourses.push(course);
          break;
        case 'CURRENT':
          this.currentCourses.push(course);
          break;
        case 'FUTURE':
          this.futureCourses.push(course);
          break;
      }
    }
  }

}
