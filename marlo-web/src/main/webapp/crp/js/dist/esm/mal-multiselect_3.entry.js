import { r as registerInstance, h, c as createEvent, g as getElement, H as Host } from './index-CZWClHa_.js';

const malMultiselectCss = ":host{display:block}";

const MalMultiselect = class {
    constructor(hostRef) {
        registerInstance(this, hostRef);
    }
    render() {
        return h("h1", { key: '867c2b602281f5373d82dc5ce382e644eda998d2' }, "Hello World");
    }
};
MalMultiselect.style = malMultiselectCss;

const malSelectCss = ":host{display:block}";

const MalSelect = class {
    constructor(hostRef) {
        registerInstance(this, hostRef);
        this.valueChange = createEvent(this, "valueChange");
    }
    get el() { return getElement(this); }
    /**
     * The name of the dropdown
     */
    name = '';
    /**
     * The data for the dropdown options
     */
    data = [];
    /**
     * The currently selected value
     */
    value;
    /**
     * Event emitted when the selection changes
     */
    valueChange;
    reactApp = null;
    componentDidLoad() {
        // Wait for React and PrimeReact to be available, then initialize
        const checkReact = () => {
            if (window.React && window.ReactDOM && window.primereact) {
                this.initializeReactDropdown();
            }
            else {
                setTimeout(checkReact, 100);
            }
        };
        checkReact();
    }
    disconnectedCallback() {
        // Clean up React component when element is removed
        if (this.reactApp && window.ReactDOM) {
            window.ReactDOM.unmountComponentAtNode(this.el.querySelector('#react-dropdown'));
        }
    }
    onPropsChange() {
        // Re-render when props change
        this.initializeReactDropdown();
    }
    initializeReactDropdown() {
        const React = window.React;
        const ReactDOM = window.ReactDOM;
        // Check if primereact is available
        if (!React || !ReactDOM) {
            console.error('React or ReactDOM not found');
            return;
        }
        // Ensure primereact global object exists
        const primereact = window.primereact || {};
        // Create a complete mock of the style system if needed
        const createEmptyStyleHook = () => ({
            bind: () => { },
            unbind: () => { },
            value: {}
        });
        // Create or extend core if needed
        if (!primereact.core) {
            primereact.core = {};
        }
        // Setup complete style system mocks
        primereact.core.useStyle = primereact.core.useStyle || createEmptyStyleHook;
        primereact.core.useMountEffect = primereact.core.useMountEffect || function (fn) { setTimeout(fn, 0); };
        primereact.core.ObjectUtils = primereact.core.ObjectUtils || {
            equals: (a, b) => JSON.stringify(a) === JSON.stringify(b),
            isEmpty: (value) => value === null || value === undefined || value === ''
        };
        // Get PrimeReact Dropdown from either individual component or full bundle
        const PrimeDropdown = primereact.dropdown?.Dropdown || primereact.Dropdown;
        if (!PrimeDropdown) {
            console.error('PrimeReact Dropdown not found');
            return;
        }
        // Use simple props to avoid style system dependencies
        const dropdown = React.createElement(PrimeDropdown, {
            name: this.name,
            options: this.data,
            value: this.value,
            onChange: (e) => {
                this.value = e.value;
                this.valueChange.emit(e.value);
            },
            optionLabel: 'label',
            className: 'w-full',
            placeholder: 'Select an option',
            filter: true,
            filterPlaceholder: 'Search...',
            filterInputAutoFocus: true,
            virtualScrollerOptions: {
                itemSize: 40,
                showLoader: true,
                loadingTemplate: loadingTemplate,
                numToleratedItems: 10,
            },
        });
        // Find container element
        const container = this.el.querySelector('#react-dropdown');
        if (container) {
            // Use try/catch to handle potential render errors
            try {
                ReactDOM.render(dropdown, container);
            }
            catch (err) {
                console.error('Error rendering PrimeReact dropdown:', err);
            }
        }
    }
    render() {
        return (h(Host, { key: '56a93e3518dae75f744f8b21faf21e1672e86a65' }, h("div", { key: '08cdeeb6a33209bb555ddc9a056aedf69881464f', id: "react-dropdown" })));
    }
    static get watchers() { return {
        "data": ["onPropsChange"],
        "value": ["onPropsChange"]
    }; }
};
const loadingTemplate = (options) => {
    const React = window.React;
    const ReactDOM = window.ReactDOM;
    // Check if primereact is available
    if (!React || !ReactDOM) {
        console.error('React or ReactDOM not found');
        return;
    }
    // Ensure primereact global object exists
    const primereact = window.primereact || {};
    const Skeleton = primereact.skeleton?.Skeleton || primereact.Skeleton;
    if (!Skeleton) {
        console.error('PrimeReact Skeleton not found');
        return;
    }
    // Use React.createElement instead of JSX to avoid Stencil compilation
    return React.createElement('div', {
        className: 'flex align-items-center p-2 justify-content-center',
        style: {
            height: '25px',
            backgroundColor: options.odd ? '#fff' : '#fafafa', // Use actual color values instead of Tailwind classes
        }
    }, React.createElement(Skeleton, {
        style: {
            width: '50%',
            borderRadius: '0.375rem',
            padding: '0.25rem 0.25rem',
            margin: '0.25rem 0',
        }
    }));
};
MalSelect.style = malSelectCss;

const myComponentCss = ":host{display:block}";

const MyComponent = class {
    constructor(hostRef) {
        registerInstance(this, hostRef);
    }
    get el() { return getElement(this); }
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
};
MyComponent.style = myComponentCss;

export { MalMultiselect as mal_multiselect, MalSelect as mal_select, MyComponent as my_component };
//# sourceMappingURL=mal-multiselect.mal-select.my-component.entry.js.map

//# sourceMappingURL=mal-multiselect_3.entry.js.map