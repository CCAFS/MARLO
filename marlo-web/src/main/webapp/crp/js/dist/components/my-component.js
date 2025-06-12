import { p as proxyCustomElement, H, h } from './p-LfeK-xMb.js';

const myComponentCss = ":host{display:block}";

const MyComponent$1 = /*@__PURE__*/ proxyCustomElement(class MyComponent extends H {
    constructor() {
        super();
        this.__registerHost();
        this.__attachShadow();
    }
    get el() { return this; }
    count = 0;
    componentDidLoad() {
        // Wait for Vue to be available and initialize the app
        const checkVue = () => {
            if (window.Vue) {
                this.initializeVue();
            }
            else {
                setTimeout(checkVue, 100);
            }
        };
        checkVue();
    }
    initializeVue() {
        const Vue = window.Vue;
        const app = Vue.createApp({
            data() {
                return {
                    count: 0
                };
            },
            methods: {
                increment() {
                    this.count++;
                },
                decrement() {
                    this.count--;
                }
            },
            template: `
        <div class="counter-container">
          <h2>Vue Counter in Stencil</h2>
          <div class="counter">
            <button @click="decrement">-</button>
            <span>{{ count }}</span>
            <button @click="increment">+</button>
          </div>
        </div>
      `
        });
        // Mount Vue app to the container
        const container = this.el.shadowRoot.querySelector('#vue-counter');
        if (container) {
            app.mount(container);
        }
    }
    render() {
        return (h("div", { key: '99173880fecac94ae1283a424248dbc44612c6cb' }, h("div", { key: 'c8370401d0ceaa4569ad671a6bffa2d8fa5309dd', id: "vue-counter" }), h("style", { key: '8e675f030c236659db49ed243dfac8265c6b9c95' }, `
          .counter-container {
            padding: 20px;
            text-align: center;
            font-family: Arial, sans-serif;
          }
          .counter {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            margin-top: 20px;
          }
          button {
            padding: 8px 16px;
            font-size: 18px;
            cursor: pointer;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
          }
          button:hover {
            background-color: #45a049;
          }
          span {
            font-size: 24px;
            font-weight: bold;
            min-width: 40px;
          }
        `)));
    }
    static get style() { return myComponentCss; }
}, [1, "my-component", {
        "count": [32]
    }]);
function defineCustomElement$1() {
    if (typeof customElements === "undefined") {
        return;
    }
    const components = ["my-component"];
    components.forEach(tagName => { switch (tagName) {
        case "my-component":
            if (!customElements.get(tagName)) {
                customElements.define(tagName, MyComponent$1);
            }
            break;
    } });
}
defineCustomElement$1();

const MyComponent = MyComponent$1;
const defineCustomElement = defineCustomElement$1;

export { MyComponent, defineCustomElement };
//# sourceMappingURL=my-component.js.map

//# sourceMappingURL=my-component.js.map