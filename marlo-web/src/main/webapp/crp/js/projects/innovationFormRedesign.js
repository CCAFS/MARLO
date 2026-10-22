/**
 * Project / cluster innovation form (A2-2613 redesign).
 *
 * Presentation only: projectInnovations.js keeps every behaviour and every selector it relies on, and
 * deliverableFormRedesign.js (A2-2607) draws the save state, the labels and the word counters. This file adds
 * what the innovation form draws on top of them.
 */
$(document).ready(function() {
  var $root = $('.dlf.inf');
  if(!$root.length) {
    return;
  }

  // ---- Header: the bundle chip follows the Innovation/Bundle answer ----
  var $bundleChip = $root.find('[data-inf-bundle-chip]');
  $root.on('change', 'input[name="innovation.projectInnovationInfo.innovationBundle"]', function() {
    $bundleChip.prop('hidden', this.value !== 'true');
  });

  // ---- Header: the shared clusters count is highlighted once there is one ----
  var counter = document.getElementById('modalCounterShared');
  if(counter) {
    var refreshCounter = function() {
      $(counter).toggleClass('is-on', (parseInt(counter.textContent, 10) || 0) > 0);
    };
    refreshCounter();
    if(window.MutationObserver) {
      new MutationObserver(refreshCounter).observe(counter, {
          childList: true,
          characterData: true,
          subtree: true
      });
    }
  }

  // ---- Annual Report year: where the innovation will be listed after saving ----
  var $yearHint = $root.find('[data-inf-year-hint]');
  var $year = $root.find('select[name="innovation.projectInnovationInfo.year"]');
  function refreshYearHint() {
    var year = parseInt($year.val(), 10);
    var phaseYear = parseInt($yearHint.attr('data-phase-year'), 10);
    if(!year || !phaseYear) {
      $yearHint.text('');
      return;
    }
    $yearHint.text($yearHint.attr(year >= phaseYear ? 'data-current' : 'data-previous'));
  }
  if($yearHint.length && $year.length) {
    refreshYearHint();
    $year.on('change', refreshYearHint);
  }
});
