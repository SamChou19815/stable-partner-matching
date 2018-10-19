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
   * Required square logo link of the project.
   */
  readonly logo: string;
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

/**
 * Card data for scheduler.
 * @type {ProjectCardData}
 */
export const schedulerCardData = <ProjectCardData>{
  name: 'Scheduler', logo: '/assets/app-icons/scheduler.png', routerLink: '/scheduler',
  intro: 'A manager and auto-scheduler for your projects and events.'
};

/**
 * Card data for RSS Reader.
 * @type {ProjectCardData}
 */
export const rssReaderCardData = <ProjectCardData>{
  name: 'RSS Reader', logo: '/assets/app-icons/rss-reader.png', routerLink: '/rss-reader',
  intro: 'A reader for subscribed RSS feeds. Source of my potential ML data.'
};

/**
 * Card data for SAMPL.
 * @type {ProjectCardData}
 */
export const samplCardData = <ProjectCardData>{
  name: 'SAMPL', logo: '/assets/app-icons/sampl.png', routerLink: '/playground/sampl',
  externalResource: { prompt: 'GitHub Repo', link: 'https://github.com/SamChou19815/sampl' },
  intro: 'A statically-typed functional programming language with basic type inference.'
};

/**
 * Card data for Chunk Reader.
 * @type {ProjectCardData}
 */
export const chunkReaderCardData = <ProjectCardData>{
  name: 'Chunk Reader', logo: '/assets/app-icons/chunk-reader.png',
  routerLink: '/playground/chunk-reader',
  intro: 'A hackathon-winning service to extract key information and summary from text.'
};

/**
 * Card data for TEN.
 * @type {ProjectCardData}
 */
export const tenCardData = <ProjectCardData>{
  name: 'TEN', logo: '/assets/app-icons/ten.png', routerLink: '/playground/ten',
  externalResource: { prompt: 'GitHub Repo', link: 'https://github.com/SamChou19815/ten' },
  intro: 'An interesting game with simple rules. Powered by an MCTS AI.'
};

/**
 * Card data for more projects.
 * @type {ProjectCardData}
 */
export const moreProjectsCardData = <ProjectCardData>{
  name: 'More Projects', logo: '/assets/app-icons/more-projects.png',
  externalResource: { prompt: 'See Them All', link: 'https://github.com/SamChou19815' },
  intro: 'Other open source projects that are not hosted on this website.'
};
