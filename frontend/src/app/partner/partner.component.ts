import { Component, OnInit } from '@angular/core';
import { appCardData, ProjectCardData } from '../shared/project-card-data';
import { GlobalDataService } from '../shared/global-data.service';
import { GoogleUserService } from '../shared/google-user.service';
import { LoadingOverlayService } from '../shared/overlay/loading-overlay.service';
import { MatDialog } from '@angular/material';
import { asyncRun, shortDelay } from '../shared/util';
import { PartnerInvitation, StudentCourse, StudentPartnership } from '../shared/data';
import { PartnerNetworkService } from './partner-network.service';

@Component({
  selector: 'app-partner',
  templateUrl: './partner.component.html',
  styleUrls: ['./partner.component.scss']
})
export class PartnerComponent implements OnInit {

  readonly appIntro: ProjectCardData = appCardData;
  /**
   * Whether the user has logged in.
   */
  isUserLoggedIn = false;

  invitations: PartnerInvitation[] = [];

  private allMyPartners: StudentPartnership[] = [];
  pastPartners: StudentPartnership[] = [];
  currentPartners: StudentPartnership[] = [];
  futurePartners: StudentPartnership[] = [];

  constructor(private dataService: GlobalDataService,
              private googleUserService: GoogleUserService,
              private networkService: PartnerNetworkService,
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
      this.invitations = this.dataService.initData.partnerInvitations;
      this.constructPartnershipData(this.dataService.initData.partners);
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

  private constructPartnershipData(allMyPartners: StudentPartnership[]) {
    this.allMyPartners = allMyPartners;
    this.dataService.initData.partners = allMyPartners;
    const pastPartners = [];
    const currentPartners = [];
    const futurePartners = [];
    for (const partner of allMyPartners) {
      switch (partner.timeStatus) {
        case 'PAST':
          pastPartners.push(partner);
          break;
        case 'CURRENT':
          currentPartners.push(partner);
          break;
        case 'FUTURE':
          futurePartners.push(partner);
          break;
      }
    }
    this.pastPartners = pastPartners;
    this.currentPartners = currentPartners;
    this.futurePartners = futurePartners;
  }

  removePartner(studentPartnership: StudentPartnership) {
    asyncRun(async () => {
      const ref = this.loadingService.open();
      await this.networkService.removePartner(studentPartnership);
      this.constructPartnershipData(this.allMyPartners.filter(p => p.key !== studentPartnership.key));
      ref.close();
    });
  }

  acceptInvitation(invitation: PartnerInvitation) {
    asyncRun(async () => {
      const ref = this.loadingService.open();
      const partnership = await this.networkService.acceptInvitation(invitation);
      this.constructPartnershipData([...this.allMyPartners, partnership]);
      this.invitations = this.invitations.filter(i => i.key !== invitation.key);
      ref.close();
    });
  }

  rejectInvitation(invitation: PartnerInvitation) {
    asyncRun(async () => {
      const ref = this.loadingService.open();
      await this.networkService.rejectInvitation(invitation);
      this.invitations = this.invitations.filter(i => i.key !== invitation.key);
      ref.close();
    });
  }

}
