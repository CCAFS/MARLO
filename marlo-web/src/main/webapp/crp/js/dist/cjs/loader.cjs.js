'use strict';

var index = require('./index-CI1S0Ic_.js');
var appGlobals = require('./app-globals-V2Kpy_OQ.js');

const defineCustomElements = async (win, options) => {
  if (typeof window === 'undefined') return undefined;
  await appGlobals.globalScripts();
  return index.bootstrapLazy([["mal-multiselect_3.cjs",[[0,"mal-multiselect",{"name":[1],"reference":[1],"data":[16],"value":[1032],"label":[1],"virtualScrollerOptions":[8,"virtual-scroller-options"],"showSelectedContainer":[4,"show-selected-container"]},null,{"value":["onPropsChange"],"data":["onPropsChange"]}],[0,"mal-select",{"name":[1],"data":[16],"value":[1032]},null,{"data":["onPropsChange"],"value":["onPropsChange"]}],[1,"my-component",{"count":[32]}]]],["mal-input.cjs",[[1,"mal-input"]]]], options);
};

exports.setNonce = index.setNonce;
exports.defineCustomElements = defineCustomElements;
//# sourceMappingURL=loader.cjs.js.map

//# sourceMappingURL=loader.cjs.js.map