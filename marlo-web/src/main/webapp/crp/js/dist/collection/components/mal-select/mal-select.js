import { Host, h } from "@stencil/core";
export class MalSelect {
    el;
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
    static get is() { return "mal-select"; }
    static get originalStyleUrls() {
        return {
            "$": ["mal-select.css"]
        };
    }
    static get styleUrls() {
        return {
            "$": ["mal-select.css"]
        };
    }
    static get properties() {
        return {
            "name": {
                "type": "string",
                "attribute": "name",
                "mutable": false,
                "complexType": {
                    "original": "string",
                    "resolved": "string",
                    "references": {}
                },
                "required": false,
                "optional": false,
                "docs": {
                    "tags": [],
                    "text": "The name of the dropdown"
                },
                "getter": false,
                "setter": false,
                "reflect": false,
                "defaultValue": "''"
            },
            "data": {
                "type": "unknown",
                "attribute": "data",
                "mutable": false,
                "complexType": {
                    "original": "any[]",
                    "resolved": "any[]",
                    "references": {}
                },
                "required": false,
                "optional": false,
                "docs": {
                    "tags": [],
                    "text": "The data for the dropdown options"
                },
                "getter": false,
                "setter": false,
                "defaultValue": "[]"
            },
            "value": {
                "type": "any",
                "attribute": "value",
                "mutable": true,
                "complexType": {
                    "original": "any",
                    "resolved": "any",
                    "references": {}
                },
                "required": false,
                "optional": false,
                "docs": {
                    "tags": [],
                    "text": "The currently selected value"
                },
                "getter": false,
                "setter": false,
                "reflect": false
            }
        };
    }
    static get events() {
        return [{
                "method": "valueChange",
                "name": "valueChange",
                "bubbles": true,
                "cancelable": true,
                "composed": true,
                "docs": {
                    "tags": [],
                    "text": "Event emitted when the selection changes"
                },
                "complexType": {
                    "original": "any",
                    "resolved": "any",
                    "references": {}
                }
            }];
    }
    static get elementRef() { return "el"; }
    static get watchers() {
        return [{
                "propName": "data",
                "methodName": "onPropsChange"
            }, {
                "propName": "value",
                "methodName": "onPropsChange"
            }];
    }
}
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
//# sourceMappingURL=mal-select.js.map
