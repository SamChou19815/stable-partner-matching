/**
 * [ProjectCardExternalResource] defines the format of an external resource.
 */
export interface ProjectCardExternalResource {
  /**
   * Prompt to display.
   */
  readonly prompt: string;
  /**
   * Link of the resource.
   */
  readonly link: string;
}

/**
 * [ProjectCardData] defines all the necessary data to display a project in card.
 */
export interface ProjectCardData {
  /**
   * Required name of the project.
   */
  readonly name: string;
  /**
   * Required introduction to the project.
   */
  readonly intro: string;
  /**
   * The optional router link.
   */
  readonly routerLink?: string;
  /**
   * The optional external resource object.
   */
  readonly externalResource?: ProjectCardExternalResource;
}

export const appCardData = <ProjectCardData>{
  name: 'Login', intro: 'Stable Course Partner Matching'
};
