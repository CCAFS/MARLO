$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();

  /* Overwritten event from global.js */
  yesnoEvent = function(target) {
    // var isChecked = $(this).is(':checked');
    $t = $(target);
    var isChecked = ($t.val() === "true");
    $t.siblings().removeClass('radio-checked');
    $t.next().addClass('radio-checked');
    var array = $t.attr('name').split('.');
    var $aditional = $t.parents('.locationLevel').find('.aditional-' + array[array.length - 1]);
    if($t.hasClass('inverse')) {
      isChecked = !isChecked;
    }
    if(isChecked) {
      $aditional.slideDown("slow");
    } else {
      $aditional.slideUp("slow");
      $aditional.find('input:text,textarea').val('');
    }
  }
}

function attachEvents() {

}
