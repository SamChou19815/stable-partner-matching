/**
 * [GoogleUser] represents a [GoogleUser] in the system.
 */
export interface GoogleUser {
  /**
   * Used to uniquely identifiers a user.
   */
  readonly key: string;
  /**
   * Name of the user.
   */
  readonly name: string;
  /**
   * Email of the user.
   */
  readonly email: string;
  /**
   * Picture url of the user.
   */
  readonly picture: string;
}

export namespace GoogleUser {

  /**
   * The default dummy Google user.
   * @type {GoogleUser}
   */
  export const defaultDummyUser: GoogleUser = {
    key: '_DUMMY_KEY', name: 'Anonymous', email: 'example@example.com', picture: ''
  };

}
