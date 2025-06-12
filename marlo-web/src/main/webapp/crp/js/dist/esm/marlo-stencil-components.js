import { p as promiseResolve, b as bootstrapLazy } from './index-CZWClHa_.js';
export { s as setNonce } from './index-CZWClHa_.js';
import { g as globalScripts } from './app-globals-DQuL1Twl.js';

/*
 Stencil Client Patch Browser v4.29.0 | MIT Licensed | https://stenciljs.com
 */

var patchBrowser = () => {
  const importMeta = import.meta.url;
  const opts = {};
  if (importMeta !== "") {
    opts.resourcesUrl = new URL(".", importMeta).href;
  }
  return promiseResolve(opts);
};

patchBrowser().then(async (options) => {
  await globalScripts();
  return bootstrapLazy([["mal-multiselect_3",[[1,"mal-multiselect"],[0,"mal-select",{"name":[1],"data":[16],"value":[1032]},null,{"data":["onPropsChange"],"value":["onPropsChange"]}],[1,"my-component",{"count":[32]}]]],["mal-input",[[1,"mal-input"]]]], options);
});
//# sourceMappingURL=marlo-stencil-components.js.map

//# sourceMappingURL=marlo-stencil-components.js.map