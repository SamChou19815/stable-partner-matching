import { Icon } from '../shared/icon';

/**
 * [NavData] defines the common attribute shared by all nav related items.
 */
interface NavData {
  /**
   * Name of the nav item displayed to the user.
   */
  readonly name: string;
  /**
   * Material Icon of the nav item (material-icon) displayed to the user.
   */
  readonly icon: Icon;
}

/**
 * [NavItem] defines the properties of a navigation item.
 */
export interface NavItem extends NavData {
  /**
   * The actual link of the item, which can be internal or external.
   */
  readonly link: string;
}

/**
 * [NavGroup] defines the properties of a navigation group, which contains a list of [NavItem]
 * as its children.
 */
export interface NavGroup extends NavData {
  /**
   * A list of child nav items. The first one is the default nav-item in this group.
   */
  readonly children: NavItem[];
}

/**
 * [NavDataList] is a wrapper for a list of [NavData] and a collection of helper methods.
 */
export class NavDataList {

  /**
   * The map that maps urls to names.
   * @type {Map<string, string>}
   */
  private nameMap: Map<string, string> = this.computeNames();

  constructor(public readonly list: NavGroup[]) {
  }

  /**
   * Computes and returns the map from urls to names.
   *
   * @returns {Map<string, string>} the map from urls to names.
   */
  private computeNames(): Map<string, string> {
    const map = new Map<string, string>();
    for (const group of this.list) {
      for (const child of group.children) {
        map.set(child.link, `${group.name} - ${child.name}`);
      }
    }
    return map;
  }

  /**
   * Returns the name of the nav data with respect to the url.
   *
   * @param {string} url url to search.
   * @returns {string} the name of the nav data with respect to the url.
   */
  getNameByUrl(url: string): string {
    const nameOpt = this.nameMap.get(url);
    return nameOpt ? nameOpt : 'Developer Sam';
  }

}
