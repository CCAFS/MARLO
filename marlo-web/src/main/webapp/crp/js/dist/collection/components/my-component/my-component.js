import { h } from "@stencil/core";
export class MyComponent {
    el;
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
        return (h("div", { key: '1a35a1c2199a9c8be63be3d2df70c2fbda7b66d8' }, h("div", { key: 'd72f44361add83840a9e6037c2df812612681eb4', id: "vue-counter" }), h("style", { key: '08f7dd9371ef93df84197ad6435b0c35cac7da03' }, `
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
    static get is() { return "my-component"; }
    static get encapsulation() { return "shadow"; }
    static get originalStyleUrls() {
        return {
            "$": ["my-component.css"]
        };
    }
    static get styleUrls() {
        return {
            "$": ["my-component.css"]
        };
    }
    static get states() {
        return {
            "count": {}
        };
    }
    static get elementRef() { return "el"; }
}
//# sourceMappingURL=my-component.js.map
