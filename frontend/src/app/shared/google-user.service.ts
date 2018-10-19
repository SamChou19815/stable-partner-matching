import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { User, auth } from 'firebase';
import { ignore } from './util';
import { isPlatformBrowser } from '@angular/common';
import { AngularFireAuth } from '@angular/fire/auth';

@Injectable({
  providedIn: 'root'
})
export class GoogleUserService {

  /**
   * Whether we are on browser platform.
   */
  private readonly isOnBrowser: boolean;

  constructor(private angularFireAuth: AngularFireAuth,
              @Inject(PLATFORM_ID) private platformId: Object) {
    this.isOnBrowser = isPlatformBrowser(platformId);
  }

  /**
   * Returns the promise of a user from Firebase.
   *
   * @return {Promise<User | null>>} the promise of a user from Firebase.
   */
  private userPromise(): Promise<User | null> {
    if (!this.isOnBrowser) {
      return Promise.resolve(null);
    }
    return new Promise((resolve, reject) =>
      this.angularFireAuth.auth.onAuthStateChanged(resolve, reject)
    );
  }

  /**
   * Returns the promise of whether the user is logged in.
   *
   * @return {Promise<boolean>} the promise of whether the user is logged in.
   */
  isSignedInPromise(): Promise<boolean> {
    return this.userPromise().then(u => u != null);
  }

  /**
   * Let the user sign in.
   */
  signIn(): void {
    this.angularFireAuth.auth.signInWithRedirect(new auth.GoogleAuthProvider()).then(ignore);
  }

  /**
   * Returns the promise of user's token that is guaranteed to resolve.
   *
   * @return {Promise<string>} the promise of user's token that is guaranteed to resolve.
   */
  async afterSignedIn(): Promise<string> {
    const userOpt = await this.userPromise();
    if (userOpt == null) {
      try {
        await this.angularFireAuth.auth.signInWithRedirect(new auth.GoogleAuthProvider());
      } catch (e) {
      }
      return this.afterSignedIn();
    }
    return userOpt.getIdToken(false);
  }

}
