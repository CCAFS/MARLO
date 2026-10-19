/**
 * Project / cluster deliverable form (A2-2607 redesign).
 *
 * Presentation only: the deliverable scripts (deliverableInfo.js, deliverableDissemination.js,
 * deliverableQualityCheck.js) keep every behaviour and every selector they rely on. This file adds what
 * the redesign draws on top of them: the save bar's state, and the label and counter text the design
 * writes differently from customForm.
 */
$(document).ready(function() {
  var $root = $('.dlf');
  if(!$root.length) {
    return;
  }

  // ---- Save bar ----
  var $saveBar = $root.find('.dlf-saveBar');

  // buttons-deliverables.ftl prints the last-edit message after the buttons; it reads as the state's caption.
  var $message = $saveBar.find('.dlf-saveBar__actions #lastUpdateMessage');
  if($message.length) {
    $saveBar.find('.dlf-saveBar__info').append($message);
  }

  // The design's outline save icon in place of the shared glyphicon.
  $saveBar.find('.button-save .glyphicon').replaceWith(
      '<svg width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true">'
          + '<path d="M3 3h7.2L13 5.8V13H3V3Z" stroke="#fff" stroke-width="1.5" stroke-linejoin="round"></path>'
          + '<path d="M5.6 3v3.4h4.2V3M5.6 13v-3.2h4.8V13" stroke="#fff" stroke-width="1.4" stroke-linejoin="round"></path>'
          + '</svg>');

  // The state compares the form with the one the page loaded, so undoing an edit brings "All changes saved"
  // back. The snapshot waits for every ready handler: the deliverable scripts rewrite some values on load
  // (select2, the trainees totals, the remainingPending flag), and loading alone must not read as an edit.
  var $form = $saveBar.closest('form');
  var $state = $saveBar.find('[data-dlf-save-state]');
  var loadedForm = null;

  function refreshSaveState() {
    if(loadedForm === null || !$state.length) {
      return;
    }
    var dirty = $form.serialize() !== loadedForm;
    $saveBar.toggleClass('is-dirty', dirty);
    $state.text($state.attr(dirty ? 'data-unsaved' : 'data-saved'));
  }

  if($state.length && $form.length) {
    setTimeout(function() {
      loadedForm = $form.serialize();
    }, 0);
    $form.on('input change', 'input, textarea, select', refreshSaveState);
    // List components and the yes/no switches change hidden inputs without firing change on them.
    $form.on('click', '.button-label, .removeElement, .removeActivity, .removeFundingSource, .removePartnerItem',
        function() {
          setTimeout(refreshSaveState, 0);
        });
  }

  // ---- Labels ----
  // customForm ends every label with a colon ("Title:*"); the design's labels carry none. Only the text node
  // before the required mark changes, so the mark, the help text and the feedback bubble inside the label stay.
  function dropLabelColons($scope) {
    $scope.find('.dlf-card label, .dlf-card .yesNoLabel').each(function() {
      $(this).contents().filter(function() {
        return this.nodeType === 3;
      }).each(function() {
        this.nodeValue = this.nodeValue.replace(/:\s*$/, ' ').replace(/^\s+/, '');
      });
    });
  }
  dropLabelColons($root);

  // ---- Word counters ----
  // applyWordCounter() (utils.js) prints "(20 words remaining of 20)"; the design drops the brackets. Only the
  // outer text changes, so the count it keeps updating is untouched.
  $root.find('.charCount').each(function() {
    $(this).contents().filter(function() {
      return this.nodeType === 3;
    }).each(function() {
      this.nodeValue = this.nodeValue.replace(/[()]/g, '');
    });
  });
});
