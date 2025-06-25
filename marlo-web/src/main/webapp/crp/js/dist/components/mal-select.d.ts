import type { Components, JSX } from "../types/components";

interface MalSelect extends Components.MalSelect, HTMLElement {}
export const MalSelect: {
    prototype: MalSelect;
    new (): MalSelect;
};
/**
 * Used to define this component and all nested components recursively.
 */
export const defineCustomElement: () => void;
