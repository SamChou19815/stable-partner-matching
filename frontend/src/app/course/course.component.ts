import { Component, OnInit, ViewChild } from '@angular/core';
import { appCardData, ProjectCardData } from '../shared/project-card-data';
import { GlobalDataService } from '../shared/global-data.service';
import { GoogleUserService } from '../shared/google-user.service';
import { LoadingOverlayService } from '../shared/overlay/loading-overlay.service';
import { MatDialog } from '@angular/material';
import { asyncRun, shortDelay } from '../shared/util';
import { SimplifiedCourseInfo, StudentCourse, TimeStatus } from '../shared/data';
import { CourseNetworkService } from './course-network.service';

@Component({
  selector: 'app-course',
  templateUrl: './course.component.html',
  styleUrls: ['./course.component.scss']
})
export class CourseComponent implements OnInit {

  allCourses: SimplifiedCourseInfo[] = [];
  readonly allPossibleTimeStatus: TimeStatus[] = ['PAST', 'CURRENT', 'FUTURE'];

  readonly appIntro: ProjectCardData = appCardData;
  /**
   * Whether the user has logged in.
   */
  isUserLoggedIn = false;

  private allMyCourses: StudentCourse[] = [];
  pastCourses: StudentCourse[] = [];
  currentCourses: StudentCourse[] = [];
  futureCourses: StudentCourse[] = [];

  courseToEdit: StudentCourse | null = null;

  @ViewChild('drawer') drawer: any | undefined;

  constructor(private dataService: GlobalDataService,
              private googleUserService: GoogleUserService,
              private networkService: CourseNetworkService,
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
      this.allCourses = this.dataService.initData.allCourses;
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

  getCourseName(key: string): string {
    return this.dataService.getCourseNameByKey(key);
  }

  private constructCourseData(allMyCourses: StudentCourse[]) {
    this.allMyCourses = allMyCourses;
    this.dataService.initData.courses = allMyCourses;
    const pastCourses = [];
    const currentCourses = [];
    const futureCourses = [];
    for (const course of allMyCourses) {
      switch (course.status) {
        case 'PAST':
          pastCourses.push(course);
          break;
        case 'CURRENT':
          currentCourses.push(course);
          break;
        case 'FUTURE':
          futureCourses.push(course);
          break;
      }
    }
    this.pastCourses = pastCourses;
    this.currentCourses = currentCourses;
    this.futureCourses = futureCourses;
  }

  editCourse(studentCourse?: StudentCourse) {
    this.courseToEdit = studentCourse == null
      ? <StudentCourse> {
        studentId: this.dataService.initData.profile.key,
        courseId: '',
        status: 'CURRENT'
      }
      : <StudentCourse>{ ...studentCourse };
  }

  deleteCourse(studentCourse: StudentCourse) {
    asyncRun(async () => {
      const ref = this.loadingService.open();
      await this.networkService.delete(studentCourse);
      this.constructCourseData(this.allMyCourses.filter(c => c.key !== studentCourse.key));
      ref.close();
    });
  }

  submit() {
    asyncRun(async () => {
      const ref = this.loadingService.open();
      const course = this.courseToEdit;
      if (course == null) {
        throw new Error();
      }
      const newCourse = await this.networkService.edit(course);
      if (course.key == null) {
        if (this.allMyCourses.find(c => c.courseId === newCourse.courseId)) {
          this.constructCourseData(this.allMyCourses.map(c => {
            return (c.courseId === newCourse.courseId) ? newCourse : c;
          }));
        } else {
          this.constructCourseData([...this.allMyCourses, newCourse]);
        }
      } else {
        this.constructCourseData(this.allMyCourses.map(c => {
          return (c.courseId === newCourse.courseId) ? newCourse : c;
        }));
      }
      ref.close();
    });
  }

}
