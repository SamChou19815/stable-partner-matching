import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

/**
 * [HttpClientConfig] is the type for HttpClient config.
 */
export interface HttpClientConfig {
  /**
   * The header config.
   */
  [p: string]: string | string[];
}

/**
 * [AuthenticatedNetworkService] is designed to be a superclass that provides a set of convenience
 * methods for a network service that involves auth to backend.
 */
export class AuthenticatedNetworkService {

  /**
   * [_firebaseAuthToken] is the token stored in the service.
   */
  private _firebaseAuthToken: string | undefined;

  /**
   * Construct itself by Angular's HTTP Client.
   *
   * @param {HttpClient} http Angular's HTTP Client.
   * @param {string} baseUrl the base URL.
   */
  constructor(protected http: HttpClient, private baseUrl: string) {
  }

  /**
   * Automatically handles the error from http client.
   *
   * @param {HttpErrorResponse} error the error to handle.
   */
  private static handleHttpError<T>(error: HttpErrorResponse): Observable<never> {
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      console.log('ERROR: An error occurred:', error.error.message);
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong,
      console.log(`ERROR: Backend returned code ${error.status} with body:\n${error.error}`);
    }
    return throwError('Something bad happened; please try again later.');
  }

  /**
   * Sets the currently used [firebaseAuthToken] to [token].
   *
   * @param {string} token the new token.
   */
  set firebaseAuthToken(token: string) {
    this._firebaseAuthToken = token;
  }

  /**
   * Returns the header for auth.
   *
   * @returns {HttpClientConfig} the header for auth.
   */
  protected get firebaseAuthHeader(): HttpClientConfig {
    if (this._firebaseAuthToken == null) {
      throw new Error();
    }
    return { 'Firebase-Auth-Token': this._firebaseAuthToken };
  }

  /**
   * Returns the promise of data of type T.
   *
   * @param {string} url url to fetch.
   * @param {HttpClientConfig} params params with get, which is optional.
   * @returns {Promise<T>} the promise of data of type T.
   */
  protected async getData<T>(url: string, params: HttpClientConfig = {}): Promise<T> {
    return this.http.get<T>(this.baseUrl + url, {
      params: params, withCredentials: true, headers: this.firebaseAuthHeader
    }).pipe(catchError(AuthenticatedNetworkService.handleHttpError)).toPromise();
  }

  /**
   * Returns the promise of data after posting the specified data.
   *
   * @param {string} url url to post.
   * @param data data to post.
   * @param {HttpClientConfig} params optional params to post.
   * @returns {Promise<T>} the promise of data after posting data.
   */
  protected async postData<T>(url: string, data: any, params?: HttpClientConfig): Promise<T> {
    return this.http.post<T>(this.baseUrl + url, data, {
      withCredentials: true, params: params, headers: this.firebaseAuthHeader
    }).pipe(catchError(AuthenticatedNetworkService.handleHttpError)).toPromise();
  }

  /**
   * Returns the promise of text after posting data.
   *
   * @param {string} url url to post.
   * @param data the data to post.
   * @returns {Promise<string>} the promise of text after posting data.
   */
  protected async postDataForText(url: string, data: any): Promise<string> {
    return this.http.post(this.baseUrl + url, data, {
      responseType: 'text', withCredentials: true, headers: this.firebaseAuthHeader
    }).pipe(catchError(AuthenticatedNetworkService.handleHttpError)).toPromise();
  }

  /**
   * Asynchronously post a set of params for data.
   *
   * @param {string} url url to post.
   * @param {HttpClientConfig} params params to post.
   * @returns {Promise<string>} the promise of data after finishing.
   */
  protected async postParamsForData<T>(url: string, params: HttpClientConfig): Promise<T> {
    return this.http.post<T>(this.baseUrl + url, '', {
      params: params, withCredentials: true, headers: this.firebaseAuthHeader
    }).pipe(catchError(AuthenticatedNetworkService.handleHttpError)).toPromise();
  }

  /**
   * Asynchronously post a set of params for text.
   *
   * @param {string} url url to post.
   * @param {HttpClientConfig} params params to post.
   * @returns {Promise<string>} the promise of indicating finishing.
   */
  protected async postParamsForText(url: string, params: HttpClientConfig): Promise<string> {
    return this.http.post(this.baseUrl + url, '', {
      responseType: 'text', params: params, withCredentials: true, headers: this.firebaseAuthHeader
    }).pipe(catchError(AuthenticatedNetworkService.handleHttpError)).toPromise();
  }

  /**
   * Returns the promise of text after deletion.
   *
   * @param {string} url the url to send the delete request.
   * @param {HttpClientConfig} params params with delete, which is optional.
   * @returns {Promise<string>} the promise of text after deletion.
   */
  protected async deleteData(url: string, params: HttpClientConfig = {}): Promise<string> {
    return this.http.delete<string>(this.baseUrl + url, {
      params: params, withCredentials: true, headers: this.firebaseAuthHeader
    }).pipe(catchError(AuthenticatedNetworkService.handleHttpError)).toPromise();
  }

}
