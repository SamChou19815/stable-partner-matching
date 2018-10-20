import { Component, OnInit } from '@angular/core';
import {
  allTimeIntervals,
  FreeTimeInterval,
  StudentFreeTimeRecord,
  timeIntervalIdToStr,
  TimeStatus
} from '../shared/data';
import { appCardData, ProjectCardData } from '../shared/project-card-data';
import { GlobalDataService } from '../shared/global-data.service';
import { GoogleUserService } from '../shared/google-user.service';
import { LoadingOverlayService } from '../shared/overlay/loading-overlay.service';
import { MatDialog } from '@angular/material';
import { asyncRun, shortDelay } from '../shared/util';
import { FreeTimeNetworkService } from './free-time-network.service';

@Component({
  selector: 'app-free-time',
  templateUrl: './free-time.component.html',
  styleUrls: ['./free-time.component.scss']
})
export class FreeTimeComponent implements OnInit {

  readonly allIntervals: number[] = allTimeIntervals;

  readonly appIntro: ProjectCardData = appCardData;
  /**
   * Whether the user has logged in.
   */
  isUserLoggedIn = false;

  record: StudentFreeTimeRecord;

  constructor(private dataService: GlobalDataService,
              private googleUserService: GoogleUserService,
              private networkService: FreeTimeNetworkService,
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
      this.record = this.dataService.initData.freeTimes;
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
  getIntervalName(interval: number): string {
    return timeIntervalIdToStr(interval);
  }

  addNew() {
    this.record.record.push(<FreeTimeInterval>{ start: 0, end: 1 });
  }

  private filterRecord(): StudentFreeTimeRecord {
    return <StudentFreeTimeRecord>{
      ...this.record, record: this.record.record.filter(interval => interval.start < interval.end)
    };
  }

  removeInterval(interval: FreeTimeInterval) {
    this.record.record = this.record.record.filter(i => i.start !== interval.start || i.end !== interval.end);
  }

  saveChanges() {
    asyncRun(async () => {
      const ref = this.loadingService.open();
      this.record = await this.networkService.update(this.filterRecord());
      this.dataService.initData.freeTimes = this.record;
      ref.close();
    });
  }

}
