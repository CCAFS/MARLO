import type { Components, JSX } from "../types/components";

interface MalMultiselect extends Components.MalMultiselect, HTMLElement {}
export const MalMultiselect: {
    prototype: MalMultiselect;
    new (): MalMultiselect;
};
/**
 * Used to define this component and all nested components recursively.
 */
export const defineCustomElement: () => void;
