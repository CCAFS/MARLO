'use strict';

var index = require('./index-CI1S0Ic_.js');
var appGlobals = require('./app-globals-V2Kpy_OQ.js');

var _documentCurrentScript = typeof document !== 'undefined' ? document.currentScript : null;
/*
 Stencil Client Patch Browser v4.29.0 | MIT Licensed | https://stenciljs.com
 */

var patchBrowser = () => {
  const importMeta = (typeof document === 'undefined' ? require('u' + 'rl').pathToFileURL(__filename).href : (_documentCurrentScript && _documentCurrentScript.tagName.toUpperCase() === 'SCRIPT' && _documentCurrentScript.src || new URL('marlo-stencil-components.cjs.js', document.baseURI).href));
  const opts = {};
  if (importMeta !== "") {
    opts.resourcesUrl = new URL(".", importMeta).href;
  }
  return index.promiseResolve(opts);
};

patchBrowser().then(async (options) => {
  await appGlobals.globalScripts();
  return index.bootstrapLazy([["mal-multiselect_3.cjs",[[0,"mal-multiselect",{"name":[1],"reference":[1],"data":[16],"value":[1032],"label":[1],"virtualScrollerOptions":[8,"virtual-scroller-options"],"showSelectedContainer":[4,"show-selected-container"],"showToggleAll":[4,"show-toggle-all"]},null,{"value":["onPropsChange"],"data":["onPropsChange"]}],[0,"mal-select",{"name":[1],"data":[16],"value":[1032]},null,{"data":["onPropsChange"],"value":["onPropsChange"]}],[1,"my-component",{"count":[32]}]]],["mal-input.cjs",[[1,"mal-input"]]]], options);
});

exports.setNonce = index.setNonce;
//# sourceMappingURL=marlo-stencil-components.cjs.js.map

//# sourceMappingURL=marlo-stencil-components.cjs.js.map