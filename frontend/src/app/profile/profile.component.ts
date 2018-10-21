import { Component, OnInit } from '@angular/core';
import { asyncRun, shortDelay } from '../shared/util';
import { MatDialog } from '@angular/material';
import { LoadingOverlayService } from '../shared/overlay/loading-overlay.service';
import { GoogleUserService } from '../shared/google-user.service';
import { appCardData, ProjectCardData } from '../shared/project-card-data';
import { GlobalDataService } from '../shared/global-data.service';
import {
  allTimeIntervals,
  dummyInitData,
  FreeTimeInterval,
  StudentClass,
  StudentProfile,
  timeIntervalIdToStr
} from '../shared/data';
import { ProfileNetworkService } from './profile-network.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  readonly allIntervals: number[] = allTimeIntervals;
  readonly possibleStudentClasses: StudentClass[] = ['FRESHMAN', 'SOPHOMORE', 'JUNIOR', 'SENIOR'];

  readonly appIntro: ProjectCardData = appCardData;
  /**
   * Whether the user has logged in.
   */
  isUserLoggedIn = false;

  profile: StudentProfile = dummyInitData.profile;

  constructor(private dataService: GlobalDataService,
              private googleUserService: GoogleUserService,
              private loadingService: LoadingOverlayService,
              private networkService: ProfileNetworkService,
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
      this.profile = this.dataService.initData.profile;
      ref.close();
    });
  }

  signIn(): void {
    this.googleUserService.signIn();
  }

  get isInitialized(): boolean {
    return !this.dataService.initData.isNotInitialized;
  }

  saveChanges() {
    asyncRun(async () => {
      const ref = this.loadingService.open();
      const profile = <StudentProfile>{ ...this.profile, freeTimes: this.filterFreeTimes() };
      await this.networkService.update(profile);
      this.dataService.initData.profile = this.profile;
      ref.close();
    });
  }

  // noinspection JSMethodCanBeStatic
  getIntervalName(interval: number): string {
    return timeIntervalIdToStr(interval);
  }

  addNew() {
    this.profile.freeTimes.push(<FreeTimeInterval>{ start: 0, end: 1 });
  }

  private filterFreeTimes(): FreeTimeInterval[] {
    return this.profile.freeTimes.filter(interval => interval.start < interval.end);
  }

  removeInterval(interval: FreeTimeInterval) {
    this.profile.freeTimes = this.profile.freeTimes
      .filter(i => i.start !== interval.start || i.end !== interval.end);
  }

}
