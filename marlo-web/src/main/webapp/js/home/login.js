$(document).ready(function() {
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

});

function workflowModal() {
  console.log('asdsa');
  $("#showPandRWorkflowDialog").dialog({
      modal: true,
      width: 700,
      height: 770,
      buttons: {
        Ok: function() {
          $(this).dialog("close");
        }
      }
  });
  return false;
}