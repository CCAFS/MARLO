import React from "react";
import { createRoot } from "react-dom/client";
export const createReactComponent = (container, component, props, children) => {
    const root = createRoot(container);
    root.render(React.createElement(component, props, children));
    return () => {
        root.unmount();
    };
};
//# sourceMappingURL=react-wrapper.js.map
