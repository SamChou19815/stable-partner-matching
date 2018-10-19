/**
 * [OpenSourceData] defines all the necessary data to display an open source project in card.
 */
export interface OpenSourceData {
  /**
   * Required name of the project.
   */
  readonly name: string;
  /**
   * Required square logo link of the project.
   */
  readonly logo: string;
  /**
   * Required introduction to the project.
   */
  readonly intro: string;
  /**
   * Required external link to the project.
   */
  readonly link: string;
}
