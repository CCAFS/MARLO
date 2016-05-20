var TIMEOUT = 1000;
var timeoutID, saveData = {}; // Will contain all inputs modified that should be saved.

function setAutoSaveEvents() {
  $("form input").on("keyup", getDataModified);
  console.log($("form input").lenght);
  // The event for textarea elements is called in the initialization
  // of the tinyMCE editor
}

function autoSave(formAction) {
  var data = {};
  data["save"] = true;

  // First, get the values of hidden inputs
  var $hiddenInputs = $("form input[type=hidden]");
  $.each($hiddenInputs, function(index,input) {
    data[$(input).attr("name")] = $(input).val();
  });

  // Second, get the values that were updated
  for( var key in saveData) {
    var $input = saveData[key];

    console.log($input.prop("tagName"));
    data[$input.attr("name")] = $input.val();
  }

  $.ajax({
      type: "POST",
      url: formAction,
      data: data,
      beforeSend: function() {
        $("#autoSavingMessages #saving").show();
      },
      error: function() {
        $("#autoSavingMessages #saving").hide("slow");
        $("#autoSavingMessages #problemSaving").show("slow").delay(1250).hide("slow");
      },
      success: function(result) {
        var saved;
        try {
          result = JSON.parse(result);
          saved = result.dataSaved;
        } catch(err) {
          saved = false;
          console.log(err);
        }

        $("#autoSavingMessages #saving").hide("slow");

        if(saved) {
          $("#autoSavingMessages #saved").show("slow").delay(1250).hide("slow");
        } else {
          $("#autoSavingMessages #problemSaving").show("slow").delay(1250).hide("slow");
        }
      }
  });

  saveData = {};
}

/**
 * This function should be called as a callback when the input elements changes.
 */
function getDataModified(event) {
  console.log("in -------");
  $inputElement = $(event.target);
  // Set property on saveData object and set it equal to the current Jquery element
  saveData[$inputElement.attr("id")] = $inputElement;

  // If a timer was started, clear it because users can still pressing keys
  if(timeoutID) {
    clearTimeout(timeoutID);
  }

  // Start a timer that will fire save when finished
  timeoutID = setTimeout(autoSave, TIMEOUT);
}