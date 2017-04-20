\$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();

}

function attachEvents() {

  $('.addTargetUnit').on('click', addTargetUnit);

  $('.remove-targetUnit').on('click', removeTargetUnit);
}

function addTargetUnit() {
  var $parent = $(this).parent().parent();
  var $itemsList = $parent.parent().find('.items-list');
  var $item = $("#targetUnit-template").clone(true).removeAttr("id");
  var data = {
      name: $.trim($parent.find('.name-input').val()),
      composedName: function() {
        return this.name;
      }
  }
  if((data.name) != "") {
    $item.find('.composedName').text(data.composedName());
    $item.find('.name').val(data.name);

    $itemsList.find("ul").append($item);
    $item.show('slow');
    updateIndexes();

    $parent.find('input:text').val('');
    $item.trigger('addComponent');
  } else {
    var notyOptions = jQuery.extend({}, notyDefaultOptions);
    notyOptions.text = 'You must fill required fields';
    noty(notyOptions);
  }
}

function removeTargetUnit() {
  $item = $(this).parent();
  $item.hide('slow', function() {
    $item.remove();
    updateIndexes();
    $(document).trigger('removeComponent');
  });
}

function updateIndexes() {
  $('.items-list li').each(function(i,item) {
    $(item).find('[name]').each(function() {
      $(this).setNameIndex(1, i);
    });
  });
}
