import { EventEmitter } from '../../stencil-public-runtime';
export declare class MalSelect {
    el: HTMLElement;
    /**
     * The name of the dropdown
     */
    name: string;
    /**
     * The data for the dropdown options
     */
    data: any[];
    /**
     * The currently selected value
     */
    value: any;
    /**
     * Event emitted when the selection changes
     */
    valueChange: EventEmitter<any>;
    private reactApp;
    componentDidLoad(): void;
    disconnectedCallback(): void;
    onPropsChange(): void;
    private initializeReactDropdown;
    render(): any;
}
