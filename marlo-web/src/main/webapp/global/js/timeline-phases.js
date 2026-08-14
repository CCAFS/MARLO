/**
 * Reporting phase selector.
 *
 * Open phases render as pills; the rest live in the "All phases" panel, which
 * groups by year and can be filtered. Selecting any phase goes through
 * setPhaseID(), which is the unchanged contract with the server.
 */
(function () {
  'use strict';

  document.addEventListener('DOMContentLoaded', function () {
    var selector = document.getElementById('timelineScroll');
    if (!selector || !selector.classList.contains('phaseSelector')) {
      return;
    }

    var toggle = document.getElementById('allPhasesToggle');
    var panel = document.getElementById('allPhasesPanel');
    var search = document.getElementById('phaseSearchInput');
    var empty = panel ? panel.querySelector('.phasePanel__empty') : null;

    /* ---- Phase switching ---- */

    selector.addEventListener('click', function (event) {
      var target = event.target.closest('[data-phase-id]');
      if (!target || target.disabled) {
        return;
      }
      setPhaseID(target.getAttribute('data-phase-id'));
    });

    /* ---- Panel open / close ---- */

    function isOpen() {
      return toggle && toggle.getAttribute('aria-expanded') === 'true';
    }

    function setOpen(open) {
      if (!toggle || !panel) {
        return;
      }
      toggle.setAttribute('aria-expanded', String(open));
      panel.hidden = !open;
      if (open && search) {
        search.focus();
      }
      if (!open) {
        resetSearch();
      }
    }

    if (toggle && panel) {
      toggle.addEventListener('click', function (event) {
        event.stopPropagation();
        setOpen(!isOpen());
      });

      document.addEventListener('click', function (event) {
        if (isOpen() && !panel.contains(event.target) && !toggle.contains(event.target)) {
          setOpen(false);
        }
      });

      document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape' && isOpen()) {
          setOpen(false);
          toggle.focus();
        }
      });
    }

    /* ---- Search ---- */

    function resetSearch() {
      if (!search) {
        return;
      }
      search.value = '';
      applySearch('');
    }

    /**
     * Filter rows by name, then hide any year heading left without matches.
     */
    function applySearch(term) {
      if (!panel) {
        return;
      }
      var needle = term.trim().toLowerCase();
      var matches = 0;
      var headings = panel.querySelectorAll('.phasePanel__year');

      Array.prototype.forEach.call(headings, function (heading) {
        var visibleInGroup = 0;
        var node = heading.nextElementSibling;

        while (node && !node.classList.contains('phasePanel__year')) {
          if (node.classList.contains('phaseRow')) {
            var haystack = node.getAttribute('data-phase-search') || '';
            var hit = needle === '' || haystack.indexOf(needle) !== -1;
            node.hidden = !hit;
            if (hit) {
              visibleInGroup++;
            }
          }
          node = node.nextElementSibling;
        }

        heading.hidden = visibleInGroup === 0;
        matches += visibleInGroup;
      });

      if (empty) {
        empty.hidden = matches !== 0;
      }
    }

    if (search) {
      search.addEventListener('input', function () {
        applySearch(search.value);
      });
    }
  });
})();

/**
 * Execute an AJAX that change the phase in the session
 *
 * @param phaseID
 * @returns
 */
function setPhaseID(phaseID) {
  var currentURL = new Uri(window.location.href);
  // Update Phase ID
  currentURL.deleteQueryParam('phaseID').addQueryParam('phaseID', phaseID);
  // Clean transaction ID
  currentURL.deleteQueryParam('transactionId');

  // Execute a change of phase
  $.ajax({
      url: baseURL + '/changePhase.do',
      method: 'POST',
      data: {
        phaseID: phaseID
      },
      beforeSend: function() {
        $('.timeline-loader').fadeIn();
      },
      success: function(data) {
        // $('.timeline-loader').fadeOut();
      },
      complete: function() {
        window.location.href = currentURL;
      }
  });
}
