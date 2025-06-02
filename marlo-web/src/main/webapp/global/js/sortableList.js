const CustomSortableList = (container, options = {}) => {
    if(!container) {
        console.warn("Container element is required for CustomSortableList.");
        return;
    }

    const initialOrderElementId = [];

        $(container).find('li.relationElement').each(function(index, item) {
        const $item = $(item);
        const $elementInput = $item.find("input.elementID")[0];
        const $elementID = $elementInput ? $elementInput.value : '';

        console.log("elementID: ", $item.find("input.elementID"));
        console.log("Initial element ID at index " + index + ": " + $elementID);
        
        initialOrderElementId.push($elementID);
    });

    const initialOrderElementIdLength = initialOrderElementId.length;

    $(container).sortable({
        start: function(event, ui) {

            // Add placeholder settings
            $(ui.placeholder).css({
                'background-color': '#f7f7f70a',
                'border': '1px dashed #ccc',
                'visibility': 'visible',
                'height': `${$(ui.item).outerHeight()}px`,
                'width': `${$(ui.item).outerWidth()}px`
            });

            // Check if the initial order element ID length matches the current order element ID length
            const currentOrderElementIdLength = $(container).find('li.relationElement').length;
            if (currentOrderElementIdLength === initialOrderElementIdLength) {
                return;
            } else {
                //add empty values to initialOrderElementId to match the current length
                if( currentOrderElementIdLength > initialOrderElementIdLength) {
                    for (let i = currentOrderElementIdLength; i < currentOrderElementIdLength; i++) {
                        initialOrderElementId.push('');
                    }
                }
            }
        },
        stop: function(event, ui) {
            $(container).find('li.relationElement').each(function(index, item) {
                const $item = $(item);
                const $elementInput = $item.find("input.elementID")[0];
                
                if ($elementInput && initialOrderElementId[index] !== undefined) {
                    $elementInput.value = initialOrderElementId[index];
                }

                //Order value in Form save
                $item.setNameIndexes(1, index);

                $item.find('label').each(function(_i,e) {
                    let newForValue = $(e).prev('input').attr('id');
                    $(e).attr('for', newForValue);
                })
            });
        }
    });

} 