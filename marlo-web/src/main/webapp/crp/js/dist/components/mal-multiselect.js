import { p as proxyCustomElement, H, d as createEvent, h, c as Host } from './p-CY-b_VSy.js';

const malMultiselectCss = ":host{display:block}";

const MalMultiselect$1 = /*@__PURE__*/ proxyCustomElement(class MalMultiselect extends H {
    constructor() {
        super();
        this.__registerHost();
        this.valueChange = createEvent(this, "valueChange");
    }
    get el() { return this; }
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
     * Whether to show the checkbox all inside the header of the multiselect.
     */
    showToggleAll = false;
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
                        showToggleAll: stencilComponent.showToggleAll || false,
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
              :showToggleAll="showToggleAll"
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
        return (h(Host, { key: '1d7dd56f8e82909f24ca9ba6203e4e8fafa88c28' }, h("div", { key: '69913eb2f84f1f26e938737d8363a1e8f9ff37dc', id: "multi-select-container" })));
    }
    static get watchers() { return {
        "value": ["onPropsChange"],
        "data": ["onPropsChange"]
    }; }
    static get style() { return malMultiselectCss; }
}, [0, "mal-multiselect", {
        "name": [1],
        "reference": [1],
        "data": [16],
        "value": [1032],
        "label": [1],
        "virtualScrollerOptions": [8, "virtual-scroller-options"],
        "showSelectedContainer": [4, "show-selected-container"],
        "showToggleAll": [4, "show-toggle-all"]
    }, undefined, {
        "value": ["onPropsChange"],
        "data": ["onPropsChange"]
    }]);
function defineCustomElement$1() {
    if (typeof customElements === "undefined") {
        return;
    }
    const components = ["mal-multiselect"];
    components.forEach(tagName => { switch (tagName) {
        case "mal-multiselect":
            if (!customElements.get(tagName)) {
                customElements.define(tagName, MalMultiselect$1);
            }
            break;
    } });
}
defineCustomElement$1();

const MalMultiselect = MalMultiselect$1;
const defineCustomElement = defineCustomElement$1;

export { MalMultiselect, defineCustomElement };
//# sourceMappingURL=mal-multiselect.js.map

//# sourceMappingURL=mal-multiselect.js.map