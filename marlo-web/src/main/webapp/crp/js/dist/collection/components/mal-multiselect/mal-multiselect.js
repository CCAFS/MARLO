import { h, Host } from "@stencil/core";
export class MalMultiselect {
    /**
     * The host element for the multiselect component.
     */
    el;
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
    static get is() { return "mal-multiselect"; }
    static get originalStyleUrls() {
        return {
            "$": ["mal-multiselect.css"]
        };
    }
    static get styleUrls() {
        return {
            "$": ["mal-multiselect.css"]
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
                    "text": "The name of the multiselect component."
                },
                "getter": false,
                "setter": false,
                "reflect": false,
                "defaultValue": "''"
            },
            "reference": {
                "type": "string",
                "attribute": "reference",
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
                    "text": "The reference of the multiselect component."
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
                    "text": "The data for the multiselect options."
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
                    "text": ""
                },
                "getter": false,
                "setter": false,
                "reflect": false
            },
            "label": {
                "type": "string",
                "attribute": "label",
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
                    "text": "The label for the multiselect component."
                },
                "getter": false,
                "setter": false,
                "reflect": false,
                "defaultValue": "''"
            },
            "virtualScrollerOptions": {
                "type": "any",
                "attribute": "virtual-scroller-options",
                "mutable": false,
                "complexType": {
                    "original": "any",
                    "resolved": "any",
                    "references": {}
                },
                "required": false,
                "optional": false,
                "docs": {
                    "tags": [],
                    "text": "The options for the virtual scroller."
                },
                "getter": false,
                "setter": false,
                "reflect": false,
                "defaultValue": "{ itemSize: 38 }"
            },
            "showSelectedContainer": {
                "type": "boolean",
                "attribute": "show-selected-container",
                "mutable": false,
                "complexType": {
                    "original": "boolean",
                    "resolved": "boolean",
                    "references": {}
                },
                "required": false,
                "optional": false,
                "docs": {
                    "tags": [],
                    "text": "Whether to show the separate container for selected items."
                },
                "getter": false,
                "setter": false,
                "reflect": false,
                "defaultValue": "false"
            },
            "showToggleAll": {
                "type": "boolean",
                "attribute": "show-toggle-all",
                "mutable": false,
                "complexType": {
                    "original": "boolean",
                    "resolved": "boolean",
                    "references": {}
                },
                "required": false,
                "optional": false,
                "docs": {
                    "tags": [],
                    "text": "Whether to show the checkbox all inside the header of the multiselect."
                },
                "getter": false,
                "setter": false,
                "reflect": false,
                "defaultValue": "false"
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
                    "text": "Event emitted when the values changes."
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
                "propName": "value",
                "methodName": "onPropsChange"
            }, {
                "propName": "data",
                "methodName": "onPropsChange"
            }];
    }
}
//# sourceMappingURL=mal-multiselect.js.map
