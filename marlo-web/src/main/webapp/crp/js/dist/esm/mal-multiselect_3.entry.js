import { r as registerInstance, c as createEvent, g as getElement, h, H as Host } from './index-D_qDHNVP.js';

const malMultiselectCss = ":host{display:block}";

const MalMultiselect = class {
    constructor(hostRef) {
        registerInstance(this, hostRef);
        this.valueChange = createEvent(this, "valueChange");
    }
    get el() { return getElement(this); }
    /**
     * The name of the multiselect component.
     */
    name = '';
    /**
     * The reference of the multiselect component.
     */
    reference = '';
    /**
     * The data for the multiselect options.
     */
    data = [];
    value;
    /**
     * The label for the multiselect component.
     */
    label = '';
    /**
     * The options for the virtual scroller.
     */
    virtualScrollerOptions = { itemSize: 38 };
    /**
     * Whether to show the separate container for selected items.
     */
    showSelectedContainer = false;
    /**
     * Event emitted when the values changes.
     */
    valueChange;
    vueApp = null;
    onPropsChange() {
        this.updateVueComponent();
    }
    updateVueComponent() {
        if (this.vueApp) {
            // Update the existing Vue app instead of recreating it
            const vueInstance = this.vueApp._instance.proxy;
            vueInstance.selectedValues = this.value || [];
            vueInstance.options = this.data || [];
        }
    }
    initializeMultiSelectVue() {
        const { createApp } = window.Vue;
        if (!createApp) {
            console.error('Vue 3 is not available');
            return;
        }
        const PrimeVue = window.PrimeVue;
        if (!PrimeVue || !PrimeVue.MultiSelect) {
            console.error('PrimeVue or MultiSelect component is not available');
            return;
        }
        const container = this.el.querySelector('#multi-select-container');
        if (!container) {
            console.error('Container not found');
            return;
        }
        // Properly unmount existing app
        if (this.vueApp) {
            this.vueApp.unmount();
            this.vueApp = null;
        }
        // Clear previous content
        container.innerHTML = '';
        // Store reference to Stencil component
        const stencilComponent = this;
        try {
            this.vueApp = createApp({
                data() {
                    return {
                        selectedValues: stencilComponent.value || [],
                        options: stencilComponent.data || [],
                        showSelectedContainer: stencilComponent.showSelectedContainer || false,
                        virtualScrollerOptions: {
                            itemSize: 38,
                            showLoader: true,
                            loading: false,
                            numToleratedItems: 10,
                        }
                    };
                },
                methods: {
                    onSelectionChange(event) {
                        console.log('Event:', event);
                        this.selectedValues = event.value;
                        stencilComponent.value = event.value;
                        stencilComponent.valueChange.emit(event.value);
                    },
                    onLazyLoad(_event) {
                        this.loading = false;
                    },
                    getLabelForValue(val) {
                        const found = this.options.find(opt => opt.value === val);
                        return found ? found.label : val;
                    }
                },
                template: `
          <div class="p-component">
            <MultiSelect 
              v-model="selectedValues"
              :options="options"
              optionLabel="label"
              optionValue="value"
              :placeholder="'${stencilComponent.label || 'Select options'}'"
              @change="onSelectionChange"
              display="chip"
              :virtualScrollerOptions="virtualScrollerOptions"
              :filter="true"
              filterPlaceholder="Search options..."
              class="w-full p-multiselect"
              :maxSelectedLabels="showSelectedContainer ? 0 : 3"
              :selectedItemTemplate="showSelectedContainer ? () => null : null"
            />

            <div v-if="showSelectedContainer" class="mt-2 flex flex-wrap gap-2">
              <div v-for="(item, index) in selectedValues" :key="index" class="p-chip p-component">
                <input type="hidden" :name="'${stencilComponent.name}[' + index + '].id'" >
                <input type="hidden" :name="'${stencilComponent.name}[' + index + '].${stencilComponent.reference}'" :value="item" >
                <span class="p-chip-text">{{ getLabelForValue(item) }}</span>
                <span class="pi pi-times p-chip-remove-icon"
                      style="cursor:pointer;margin-left:0.5rem"
                      @click="selectedValues = selectedValues.filter(v => v !== item); onSelectionChange({value: selectedValues})">
                </span>
              </div>
            </div>

          </div>
        `
            });
            // Use PrimeVue
            this.vueApp.use(PrimeVue.Config, {
                theme: {
                    preset: 'Aura'
                },
            });
            // Register MultiSelect component
            this.vueApp.component('MultiSelect', PrimeVue.MultiSelect);
            // Mount the this.vueApp
            this.vueApp.mount(container);
        }
        catch (error) {
            console.error('Error mounting MultiSelect:', error);
        }
    }
    componentDidLoad() {
        const checkVue = () => {
            if (window.Vue && window.PrimeVue) {
                this.initializeMultiSelectVue();
            }
            else {
                setTimeout(checkVue, 100);
            }
        };
        checkVue();
    }
    disconnectedCallback() {
        // Properly unmount Vue app
        if (this.vueApp) {
            this.vueApp.unmount();
            this.vueApp = null;
        }
        const container = this.el.querySelector('#multi-select-container');
        if (container) {
            container.innerHTML = '';
        }
    }
    /**
     * Renders the multiselect component.
     * @returns The rendered multiselect component.
     */
    render() {
        return (h(Host, { key: 'fe56f8d9770a8504423c7686939d94c20682da76' }, h("div", { key: 'a0b4a7cc981a981cca2bada215657a018a0a2cb2', id: "multi-select-container" })));
    }
    static get watchers() { return {
        "value": ["onPropsChange"],
        "data": ["onPropsChange"]
    }; }
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
        return (h(Host, { key: '82ebc07899134c30e62615b91b81e0d94d9c8284' }, h("div", { key: '01a2f98aa261c76e283458107afdddd8ea8d7301', id: "react-dropdown" })));
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
};
MyComponent.style = myComponentCss;

export { MalMultiselect as mal_multiselect, MalSelect as mal_select, MyComponent as my_component };
//# sourceMappingURL=mal-multiselect.mal-select.my-component.entry.js.map

//# sourceMappingURL=mal-multiselect_3.entry.js.map