$(document).ready(init);

function init() {
  // Attaching events
  attachEvents();
}

function attachEvents() {
  /**
   * General:
   */
  $(".requestPopUp").on("click", function(e) {
    e.preventDefault();
    openDialog();
  });

  $('.close-dialog').on('click', function() {
    $("#popUp").dialog("close");
  });

  $("#sendRequest").on("click", requestService);

}

function openDialog() {
  $("#popUp").dialog({
      resizable: false,
      width: 400,
      modal: true,
      dialogClass: 'dialog-searchUsers',
      show: {
          effect: "blind",
          duration: 500
      },
      hide: {
          effect: "fadeOut",
          duration: 500
      },
      open: function(event,ui) {
      }
  });
}

function requestService() {
  $.ajax({
      url: baseURL + "/targetUnitRequest.do?",
      type: 'GET',
      data: {
        targetUnitName: $(".newTargetUnit").val()
      },
      success: function(m) {
        console.log(m);
      },
      error: function(e) {
        console.log(e);
      }
  });
}