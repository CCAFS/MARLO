$(document).ready(function() {
  $textAreas = $("textarea");
  $textAreas.autoGrow();
  $('#mogsTabs').tabs({
      show: {
          effect: "fadeIn",
          duration: 500
      },
      hide: {
          effect: "fadeOut",
          duration: 300
      }
  });
});