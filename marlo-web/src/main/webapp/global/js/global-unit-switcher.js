/**
 * Global unit switcher — click-toggled popover in the top bar.
 *
 * The menu used to open on :hover. The panel is wider than its trigger and
 * sits a few pixels below it, so the pointer left the trigger before it
 * reached the panel and the menu closed before anything could be clicked.
 * Opening on click removes the problem entirely and makes the control
 * keyboard reachable.
 */
(function () {
  'use strict';

  function init() {
    var trigger = document.getElementById('globalUnitSwitcherTrigger');
    var panel = document.getElementById('globalUnitSwitcherPanel');
    if (!trigger || !panel) {
      return;
    }

    var root = document.getElementById('globalUnitSwitcher');

    function isOpen() {
      return trigger.getAttribute('aria-expanded') === 'true';
    }

    function items() {
      return Array.prototype.slice.call(panel.querySelectorAll('.guItem'));
    }

    function open() {
      panel.hidden = false;
      trigger.setAttribute('aria-expanded', 'true');
      if (root) {
        root.classList.add('is-open');
      }
      // Bring the selected unit into view when the list scrolls.
      var current = panel.querySelector('.guItem--current');
      if (current && typeof current.scrollIntoView === 'function') {
        current.scrollIntoView({ block: 'nearest' });
      }
    }

    function close(refocus) {
      panel.hidden = true;
      trigger.setAttribute('aria-expanded', 'false');
      if (root) {
        root.classList.remove('is-open');
      }
      if (refocus) {
        trigger.focus();
      }
    }

    trigger.addEventListener('click', function (event) {
      event.preventDefault();
      event.stopPropagation();
      if (isOpen()) {
        close(false);
      } else {
        open();
      }
    });

    // Keep clicks inside the panel from bubbling to the document handler that
    // closes it; links still navigate normally.
    panel.addEventListener('click', function (event) {
      event.stopPropagation();
    });

    document.addEventListener('click', function () {
      if (isOpen()) {
        close(false);
      }
    });

    document.addEventListener('keydown', function (event) {
      if (!isOpen()) {
        return;
      }
      if (event.key === 'Escape' || event.key === 'Esc') {
        close(true);
        return;
      }
      if (event.key !== 'ArrowDown' && event.key !== 'ArrowUp') {
        return;
      }
      var list = items();
      if (!list.length) {
        return;
      }
      event.preventDefault();
      var index = list.indexOf(document.activeElement);
      if (event.key === 'ArrowDown') {
        index = index < 0 ? 0 : (index + 1) % list.length;
      } else {
        index = index <= 0 ? list.length - 1 : index - 1;
      }
      list[index].focus();
    });

    trigger.addEventListener('keydown', function (event) {
      if (event.key === 'ArrowDown' && !isOpen()) {
        event.preventDefault();
        open();
        var list = items();
        if (list.length) {
          list[0].focus();
        }
      }
    });
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
