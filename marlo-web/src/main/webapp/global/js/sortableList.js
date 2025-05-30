const CustomSortableList = (container, options = {}) => {
    if(!container) {
        console.warn("Container element is required for CustomSortableList.");
        return;
    }

    $(container).sortable({
        start: function(event, ui) {
            console.log("Event",event);
            console.log("UI",ui);
            console.log("Container",container);
            console.log("Sorting started. Initial order:", $(container).sortable('toArray'));
        }, 
        stop: function(event, ui) {
            console.log("Event",event);
            console.log("UI",ui);
            console.log("Container",container);
            console.log("Sorting stopped. New order:", $(container).sortable('toArray'));
        }
    });

} 