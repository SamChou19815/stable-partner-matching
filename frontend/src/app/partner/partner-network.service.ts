import { Injectable } from '@angular/core';
import { AuthenticatedNetworkService } from '../shared/authenticated-network-service';
import { HttpClient } from '@angular/common/http';
import { GoogleUserService } from '../shared/google-user.service';
import { PartnerInvitation, StudentPartnership, StudentPublicInfo } from '../shared/data';

@Injectable({
  providedIn: 'root'
})
export class PartnerNetworkService extends AuthenticatedNetworkService {

  constructor(http: HttpClient, private googleUserService: GoogleUserService) {
    super(http, '/apis/partners');
  }

  async invite(partnerInvitation: PartnerInvitation): Promise<boolean> {
    this.firebaseAuthToken = await this.googleUserService.afterSignedIn();
    const resp = await this.postDataForText('/invite', partnerInvitation);
    return resp === 'OK';
  }

  private async respondInvitation(
    partnerInvitation: PartnerInvitation, accepted: boolean
  ): Promise<StudentPartnership | null> {
    this.firebaseAuthToken = await this.googleUserService.afterSignedIn();
    return this.postData(`/respond_invitation?accepted=${accepted}`, partnerInvitation);
  }

  async acceptInvitation(partnerInvitation: PartnerInvitation): Promise<StudentPartnership> {
    const resp = await this.respondInvitation(partnerInvitation, true);
    if (resp == null) {
      throw new Error();
    }
    return resp;
  }

  async rejectInvitation(partnerInvitation: PartnerInvitation): Promise<void> {
    await this.respondInvitation(partnerInvitation, false);
  }

  async getRanking(courseId?: string): Promise<StudentPublicInfo[]> {
    this.firebaseAuthToken = await this.googleUserService.afterSignedIn();
    const url = courseId == null ? '/general' : `/course?course_id=${courseId}`;
    return this.getData<StudentPublicInfo[]>(url);
  }

}
