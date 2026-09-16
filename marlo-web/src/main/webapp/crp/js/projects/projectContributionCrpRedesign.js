/*
 * Cluster contribution to performance indicators - A2-2439 redesign.
 * Only the disaggregated-target accordion: the period tabs are plain Bootstrap tabs.
 */
$(document).ready(function () {

  // Open the first disaggregated target of each period so the pane never reads as empty.
  $('.cpi-pane').each(function () {
    var $first = $(this).find('.cpi-dt__head').first();
    if ($first.length) {
      $first.attr('aria-expanded', 'true');
      $('#' + $first.attr('data-cpi-toggle')).show();
    }
  });

  $('.cpi-dt__head').on('click', function () {
    var $head = $(this);
    var $body = $('#' + $head.attr('data-cpi-toggle'));
    var isOpen = $head.attr('aria-expanded') === 'true';
    $head.attr('aria-expanded', isOpen ? 'false' : 'true');
    $body.slideToggle(140);
  });

});
