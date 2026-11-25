$(document).ready(init);

function init() {
  var $page = $('.prq');

  /* Init Select2 plugin. The dropdown is mounted inside .prq so that the page styles reach it. */
  $page.find('form select').not('.institutionTypes').select2({
    width: '100%',
    dropdownParent: $page
  });

  $("select.institutionTypes").select2({
    width: '100%',
    dropdownParent: $page,
    templateResult: function(state) {
      if (state.id == -1) {
        return state.text;
      }
      var $description = $('#institutionType-' + state.id);
      if ($description.length === 0) {
        return state.text;
      }
      return $("<span>" + $description.html() + "</span>");
    }
  });

  // Select2 draws its own focusable box: bind it to the label of the field so a screen reader names it
  $page.find('form select').each(function() {
    var $select = $(this);
    var labelId = $('label[for="' + this.id + '"]').attr('id');
    var $box = $select.next('.select2-container').find('.select2-selection');
    if (labelId && $box.length) {
      $box.attr('aria-labelledby', labelId + ' ' + $box.find('.select2-selection__rendered').attr('id'));
    }
    if ($select.attr('aria-invalid') === 'true') {
      $box.attr('aria-invalid', 'true');
      if ($select.attr('aria-describedby')) {
        $box.attr('aria-describedby', $select.attr('aria-describedby'));
      }
    }
  });

  // One send per click: the button is marked disabled but not disabled for real, because a disabled submit button
  // is left out of the form data and the action only saves when the "save" parameter arrives.
  var submitting = false;
  $page.find('form').on('submit', function(event) {
    if (submitting) {
      event.preventDefault();
      return;
    }
    submitting = true;
    $(this).find('button[type="submit"]').attr('aria-disabled', 'true');
  });
  // Coming back to the page through the browser history shows the form again: allow a new send
  $(window).on('pageshow', function() {
    submitting = false;
    $page.find('button[type="submit"]').removeAttr('aria-disabled');
  });

  // The first field with an error takes the focus, so the keyboard user starts where the problem is
  var $firstInvalid = $page.find('[aria-invalid="true"]').first();
  if ($firstInvalid.length) {
    if ($firstInvalid.is('select')) {
      $firstInvalid.next('.select2-container').find('.select2-selection').trigger('focus');
    } else {
      $firstInvalid.trigger('focus');
    }
  }

  // Close button of the success card (the page opens in a popup)
  $page.on('click', '[data-prq-close]', function() {
    window.close();
  });
}
