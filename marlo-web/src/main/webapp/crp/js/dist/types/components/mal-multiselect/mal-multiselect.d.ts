import { EventEmitter } from '../../stencil-public-runtime';
export declare class MalMultiselect {
    /**
     * The host element for the multiselect component.
     */
    el: HTMLElement;
    /**
     * The name of the multiselect component.
     */
    name: string;
    /**
     * The reference of the multiselect component.
     */
    reference: string;
    /**
     * The data for the multiselect options.
     */
    data: any[];
    value: any;
    /**
     * The label for the multiselect component.
     */
    label: string;
    /**
     * The options for the virtual scroller.
     */
    virtualScrollerOptions: any;
    /**
     * Whether to show the separate container for selected items.
     */
    showSelectedContainer: boolean;
    /**
     * Whether to show the checkbox all inside the header of the multiselect.
     */
    showToggleAll: boolean;
    /**
     * Event emitted when the values changes.
     */
    valueChange: EventEmitter<any>;
    private vueApp;
    onPropsChange(): void;
    private updateVueComponent;
    private initializeMultiSelectVue;
    componentDidLoad(): void;
    disconnectedCallback(): void;
    /**
     * Renders the multiselect component.
     * @returns The rendered multiselect component.
     */
    render(): any;
}
