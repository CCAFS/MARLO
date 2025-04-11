import { p as proxyCustomElement, H, h, c as Host } from './p-LfeK-xMb.js';

const malInputCss = ":host{display:block}";

const MalInput$1 = /*@__PURE__*/ proxyCustomElement(class MalInput extends H {
    constructor() {
        super();
        this.__registerHost();
        this.__attachShadow();
    }
    render() {
        return (h(Host, { key: '452b946638723877bdccc1ddc318e9d26dac657f' }, h("slot", { key: '2eee4d7341085ec90ba24dc9885fb79b2e020713' })));
    }
    static get style() { return malInputCss; }
}, [1, "mal-input"]);
function defineCustomElement$1() {
    if (typeof customElements === "undefined") {
        return;
    }
    const components = ["mal-input"];
    components.forEach(tagName => { switch (tagName) {
        case "mal-input":
            if (!customElements.get(tagName)) {
                customElements.define(tagName, MalInput$1);
            }
            break;
    } });
}
defineCustomElement$1();

const MalInput = MalInput$1;
const defineCustomElement = defineCustomElement$1;

export { MalInput, defineCustomElement };
//# sourceMappingURL=mal-input.js.map

//# sourceMappingURL=mal-input.js.map