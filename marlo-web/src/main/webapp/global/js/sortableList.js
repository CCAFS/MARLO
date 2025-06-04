const CustomSortableList = (container) => {
    if(!container) {
        console.warn("Container element is required for CustomSortableList.");
        return;
    }

    const initialOrderElementId = [];

        $(container).find('li.relationElement').each(function(index, item) {
        const $item = $(item);
        const $elementInput = $item.find("input.elementID")[0];
        const $elementID = $elementInput ? $elementInput.value : '';
        
        initialOrderElementId.push($elementID);
    });

    const initialOrderElementIdLength = initialOrderElementId.length;

    let placeholderInitialized = false;

    $(container).sortable({
        create: function(_event, _ui) {

            //Add icon of dragabble to items
            const $items = $(container).find('li.relationElement');
            $items.each(function(index, item) {
                const $item = $(item);
                if ($item.find('.sortElement').css('display') === 'none') {
                    $item.find('.sortElement').css('display', 'block');
                }
            });

            //Add icon of dragabble to template item
            const $itemsTemplate = $(container).parents('.elementsListComponent').find('li.relationElement-template');

            if( $itemsTemplate.length > 0 || $itemsTemplate.find('.sortElement').css('display') === 'none') {
                $itemsTemplate.find('.sortElement').css('display', 'block');
            }

        },
        start: function(_event, ui) {

            // Add placeholder settings
            if (!placeholderInitialized) {
            $(ui.placeholder).css({
                'background-color': '#f7f7f70a',
                'border': '1px dashed #ccc',
                'visibility': 'visible',
                'height': `${$(ui.item).outerHeight()}px`,
                'width': `${$(ui.item).outerWidth()}px`
            });

            placeholderInitialized = true;
            }

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
        stop: function(_event, _ui) {
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