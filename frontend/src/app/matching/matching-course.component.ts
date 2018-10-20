import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { appCardData, ProjectCardData } from '../shared/project-card-data';
import { GlobalDataService } from '../shared/global-data.service';
import { GoogleUserService } from '../shared/google-user.service';
import { LoadingOverlayService } from '../shared/overlay/loading-overlay.service';
import { MatDialog } from '@angular/material';
import { asyncRun, shortDelay } from '../shared/util';
import { ActivatedRoute } from '@angular/router';
import { MatchingNetworkService } from './matching-network.service';
import { PartnerInvitation, StudentPublicInfo, TimeStatus } from '../shared/data';
import { PartnerNetworkService } from '../partner/partner-network.service';
import { AlertComponent } from '../shared/alert/alert.component';

@Component({
  selector: 'app-matching-course',
  templateUrl: './matching-course.component.html',
  styleUrls: ['./matching-course.component.scss']
})
export class MatchingCourseComponent implements OnInit {

  readonly appIntro: ProjectCardData = appCardData;
  /**
   * Whether the user has logged in.
   */
  isUserLoggedIn = false;

  partnerList: StudentPublicInfo[] = [];
  courseId = '';
  status: TimeStatus = 'CURRENT';

  constructor(private dataService: GlobalDataService,
              private googleUserService: GoogleUserService,
              private networkService: MatchingNetworkService,
              private partnerNetworkService: PartnerNetworkService,
              private loadingService: LoadingOverlayService,
              private route: ActivatedRoute,
              private location: Location,
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
      const courseId = this.route.snapshot.paramMap.get('id');
      this.status = <TimeStatus>this.route.snapshot.paramMap.get('status');
      this.courseId = courseId;
      if (courseId == null) {
        this.location.back();
        ref.close();
        return;
      }
      await this.dataService.initializeApp();
      this.partnerList = await this.networkService.getRanking(courseId);
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

  invitePartner(student: StudentPublicInfo) {
    asyncRun(async () => {
      const ref = this.loadingService.open();
      const canInvite = await this.partnerNetworkService.invite(<PartnerInvitation>{
        inviterId: this.dataService.initData.profile.key,
        invitedId: student.id,
        courseId: this.courseId,
        timeStatus: this.status
      });
      ref.close();
      const message = canInvite ? 'Invitation succeeds.' : 'You cannot invite because you are already partners.';
      this.dialog.open(AlertComponent, { data: message });
    });
  }

}
