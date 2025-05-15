import { Host, h } from "@stencil/core";
export class MalInput {
    render() {
        return (h(Host, { key: '452b946638723877bdccc1ddc318e9d26dac657f' }, h("slot", { key: '2eee4d7341085ec90ba24dc9885fb79b2e020713' })));
    }
    static get is() { return "mal-input"; }
    static get encapsulation() { return "shadow"; }
    static get originalStyleUrls() {
        return {
            "$": ["mal-input.css"]
        };
    }
    static get styleUrls() {
        return {
            "$": ["mal-input.css"]
        };
    }
}
//# sourceMappingURL=mal-input.js.map
