'use strict';

var index = require('./index-BDMyP6ST.js');

const malInputCss = ":host{display:block}";

const MalInput = class {
    constructor(hostRef) {
        index.registerInstance(this, hostRef);
    }
    render() {
        return (index.h(index.Host, { key: '452b946638723877bdccc1ddc318e9d26dac657f' }, index.h("slot", { key: '2eee4d7341085ec90ba24dc9885fb79b2e020713' })));
    }
};
MalInput.style = malInputCss;

exports.mal_input = MalInput;
//# sourceMappingURL=mal-input.entry.cjs.js.map

//# sourceMappingURL=mal-input.cjs.entry.js.map