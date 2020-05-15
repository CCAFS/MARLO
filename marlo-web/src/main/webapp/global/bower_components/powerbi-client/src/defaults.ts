/** @hidden */  
import * as models from 'powerbi-models';

/** @hidden */  
export abstract class Defaults {
  public static defaultSettings: models.ISettings = {
    filterPaneEnabled: true
  };

  public static defaultQnaSettings: models.IQnaSettings = {
    filterPaneEnabled: false
  };
}