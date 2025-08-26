import { p as proxyCustomElement, H, d as createEvent, h, c as Host } from './p-CY-b_VSy.js';

const malSelectCss = ":host{display:block}";

const MalSelect$1 = /*@__PURE__*/ proxyCustomElement(class MalSelect extends H {
    constructor() {
        super();
        this.__registerHost();
        this.valueChange = createEvent(this, "valueChange");
    }
    get el() { return this; }
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
        return (h(Host, { key: '82ebc07899134c30e62615b91b81e0d94d9c8284' }, h("div", { key: '01a2f98aa261c76e283458107afdddd8ea8d7301', id: "react-dropdown" })));
    }
    static get watchers() { return {
        "data": ["onPropsChange"],
        "value": ["onPropsChange"]
    }; }
    static get style() { return malSelectCss; }
}, [0, "mal-select", {
        "name": [1],
        "data": [16],
        "value": [1032]
    }, undefined, {
        "data": ["onPropsChange"],
        "value": ["onPropsChange"]
    }]);
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
function defineCustomElement$1() {
    if (typeof customElements === "undefined") {
        return;
    }
    const components = ["mal-select"];
    components.forEach(tagName => { switch (tagName) {
        case "mal-select":
            if (!customElements.get(tagName)) {
                customElements.define(tagName, MalSelect$1);
            }
            break;
    } });
}
defineCustomElement$1();

const MalSelect = MalSelect$1;
const defineCustomElement = defineCustomElement$1;

export { MalSelect, defineCustomElement };
//# sourceMappingURL=mal-select.js.map

//# sourceMappingURL=mal-select.js.map