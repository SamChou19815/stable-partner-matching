/**
 * [NavIconType] defines a set of all supported types of icons.
 */
export type IconType = 'material' | 'font-awesome';

/**
 * [NavIcon] defines the structure of a icon definition.
 */
export interface Icon {
  /**
   * Name of the icon in the icon provider.
   */
  readonly name: string;
  /**
   * Type of the icon in the icon provider.
   */
  readonly type: IconType;
}

export namespace Icon {

  /**
   * Returns an icon from material icon collection.
   *
   * @param {string} name name of the icon.
   * @returns {Icon} an icon from material icon collection.
   */
  export function ofMaterial(name: string): Icon {
    return <Icon>{ name, type: 'material' };
  }

  /**
   * Returns an icon from FontAwesome icon collection.
   *
   * @param {string} name name of the icon.
   * @returns {Icon} an icon from FontAwesome icon collection.
   */
  export function ofFontAwesome(name: string): Icon {
    return <Icon>{ name, type: 'font-awesome' };
  }

  /**
   * The dummy icon. Do not use this except for dummy initialization.
   * @type {Icon}
   */
  export const ofDummy: Icon = ofMaterial('');

}
