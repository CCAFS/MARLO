import { p as promiseResolve, b as bootstrapLazy } from './index-D_qDHNVP.js';
export { s as setNonce } from './index-D_qDHNVP.js';
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
  return bootstrapLazy([["mal-multiselect_3",[[0,"mal-multiselect",{"name":[1],"data":[16],"value":[1032],"label":[1],"virtualScrollerOptions":[8,"virtual-scroller-options"],"showSelectedContainer":[4,"show-selected-container"]},null,{"value":["onPropsChange"],"data":["onPropsChange"]}],[0,"mal-select",{"name":[1],"data":[16],"value":[1032]},null,{"data":["onPropsChange"],"value":["onPropsChange"]}],[1,"my-component",{"count":[32]}]]],["mal-input",[[1,"mal-input"]]]], options);
});
//# sourceMappingURL=marlo-stencil-components.js.map

//# sourceMappingURL=marlo-stencil-components.js.map