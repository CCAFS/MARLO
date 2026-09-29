/*
 * Cluster contribution to performance indicators - A2-2439 redesign.
 * Only the disaggregated-target accordion: the period tabs are plain Bootstrap tabs.
 */
$(document).ready(function () {

  function setOpen($head, open) {
    $head.attr('aria-expanded', open ? 'true' : 'false');
  }

  // Open the first disaggregated target of each period so the pane never reads as empty.
  $('.cpi-pane').each(function () {
    var $first = $(this).find('.cpi-dt__head').first();
    if ($first.length) {
      setOpen($first, true);
      $('#' + $first.attr('data-cpi-toggle')).show();
    }
  });

  function toggle($head) {
    var $body = $('#' + $head.attr('data-cpi-toggle'));
    setOpen($head, $head.attr('aria-expanded') !== 'true');
    $body.slideToggle(140);
  }

  $('.cpi-dt__head').on('click', function () {
    toggle($(this));
  });

  // The head is a role="button" div, so it has to answer the keys a button does.
  $('.cpi-dt__head').on('keydown', function (e) {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      toggle($(this));
    }
  });

});
