import type { Components, JSX } from "../types/components";

interface MalInput extends Components.MalInput, HTMLElement {}
export const MalInput: {
    prototype: MalInput;
    new (): MalInput;
};
/**
 * Used to define this component and all nested components recursively.
 */
export const defineCustomElement: () => void;
