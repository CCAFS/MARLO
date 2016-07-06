$(document).ready(init);
function init() {
  initJreject();

  $('.crpGroup ul li').on('click', function() {
    // Add 'selected' class and removing sibling's class if any
    $(this).addClass('selected').siblings().removeClass('selected');
    // Setting up the CRP value into a hidden input
    $('#crp').val($(this).attr('id').split('-')[1]);
    // Show Second Form (Email, password & login button)
    $(this).parents('.loginForm').find('.secondForm').slideDown();
  });
}

function initJreject() {
  $.reject({
      reject: {
          msie: false,
          msie5: true,
          msie6: true,
          msie7: true, // Microsoft Internet Explorer
          firefox: false,
          firefox1: true,
          firefox2: true,
          firefox3: true, // Mozilla firefox
          opera: false,
          opera7: true,
          opera8: true,
          opera9: true, // Opera
          safari: false,
          safari2: true,
          safari3: true,
          safari4: true
      // Safari
      }, // Reject all renderers for demo

      closeCookie: true, // Set cookie to remmember close for this session
      display: [
          'firefox', 'chrome', 'opera', 'msie', 'safari'
      ]
  });
}