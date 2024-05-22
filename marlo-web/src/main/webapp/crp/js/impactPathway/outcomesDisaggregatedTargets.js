$(document).ready(init);
var countID = 0;
let AuxcurrentActivity=undefined;
function init() {
  /* Init Select2 plugin */
  $('form select').select2({
    width: "100%"
  });

  $("form .deliverableList").select2({
      templateResult: formatState,
      templateSelection: formatState,
      width: "100%"
  });
  
  // Events
  $(".addActivity").on("click", addActivity);
  $(".removeActivity").on("click", removeactivity);
  $(".removeActivityBtnInList").on("click", getAndSaveLocallyTitle);
  $(".deliverableList").on("change", addDeliverable);
  $(".activityTitle").on("change", changeTitle);
  $(".activityTitle").on("keyup", changeTitle);
  $(".removeDeliverable").on("click", removeDeliverable);
  // $('select.activityTitle').on("change", updateActivityList);

  // Missing fields in activities
  $("form .projectActivity").each(function(i,e) {
    verifyMissingFields(e);
  });

  $('.blockTitle').on('click', function() {
    if($(this).hasClass('closed')) {
      $(this).parent().find('.blockTitle').removeClass('opened').addClass('closed');
      $(this).removeClass('closed').addClass('opened');
    } else {
      $(this).removeClass('opened').addClass('closed');
    }
    $(this).next().slideToggle('slow', function() {
      $(this).find('textarea').autoGrow();
      $(this).find(".errorTag").hide();
      $(this).find(".errorTag").css("left", $(this).find(".deliverableWrapper").outerWidth());
      $(this).find(".errorTag").fadeIn(2000);
    });
  });

  // Missing fields in activities
  $("form .projectActivity").each(function(i,e) {
    verifyMissingFields(e);
  });

  // Change status
  $('select.activityStatus').on("change", function() {
    var statusId = $(this).val();
    $statusDescription = $(this).parents('.activityStatusBlock').find('.statusDescriptionBlock');

    $statusDescription.hide().show(400);
    $statusDescription.find('label').html($('#status-' + statusId).html());

    $statusDescription.find('textarea').val('');
  });

  // Open Current Activity from url
  var currentActivity = window.location.hash.substring(1);
  if(currentActivity != '') {
    $('.activitiesOG-content #' + currentActivity + ' .blockTitle').click();
  }
}

function getAndSaveLocallyTitle() {
  $('#activityName').html($(this).parents('.projectActivity').find('.blockTitle').text().trimStart().trimEnd() );
  AuxcurrentActivity = this;
}
// change title
function changeTitle() {
  var $blockTitle = $(this).parents(".projectActivity").find(".blockTitle");
  $blockTitle.html($(this).val());
  if($blockTitle.html() == "" || $blockTitle.html() == " ") {
    $blockTitle.html("New Activity");
  }
}

// Add a new activity element
function addActivity() {
  countID++;
  //var $list = $(".activitiesOG-content");
  var $item = $("#projectActivity-template").clone(true).removeAttr("id");
  console.log(countID);
// Set indexes
  $item.setNameIndexes(1, countID);
  $item.find(".blockTitle").removeClass("closed").addClass("opened");
  $item.find(".blockContent").css("display", "block");
  $item.find(".index").html(countID);

  //$list.append($item);
  $item.show('slow', function() {
    $item.find("textarea").autoGrow();
    $item.find("select").select2({
        templateResult: formatState,
        templateSelection: formatState,
        width: "100%"
    });
  });
  //checkItems($list);

}

// Remove activity element
function removeactivity() {
  console.log("removeactivity");
  var $list = $(AuxcurrentActivity).parents('.activitiesOG-content');
  var $item = $(AuxcurrentActivity).parents('.projectActivity');
  $item.hide(1000, function() {
    $item.remove();
    checkItems($list);
  });

}

// Add a new deliverable element
function addDeliverable() {
  var option = $(this).find("option:selected");
  var canAdd = true;
  if(option.val() == "-1") {
    canAdd = false;
  }

  var $list = $(this).parents(".select").parent().parent().find(".deliverableWrapper");
  var $item = $("#deliverableActivity-template").clone(true).removeAttr("id");
  var v = $(option).text().length > 80 ? $(option).text().substr(0, 80) + ' ... ' : $(option).text();

  // Check if is already selected
  $list.find('.deliverableActivity').each(function(i,e) {
    if($(e).find('input.id').val() == option.val()) {
      canAdd = false;
      return;
    }
  });

  // Reset select
  $(this).val("-1");
  $(this).trigger('change.select2');

  if(!canAdd) {
    return;
  }

  // Set deliverable parameters
  $item.find(".name").attr("title", $(option).text());
  $item.find(".name").html(v);
  $item.find(".id").val(option.val());
  $list.append($item);
  $item.show('slow');
  var $activity = $list.parents(".projectActivity");
  var activityIndex = $activity.find(".index").html();
  updateDeliverable($activity, activityIndex);
  checkItems($list);

}

// Remove a new deliverable element
function removeDeliverable() {
  var $list = $(this).parents('.deliverableWrapper');
  var $item = $(this).parents('.deliverableActivity');
  var $activity = $list.parents(".projectActivity");
  var activityIndex = $activity.find(".index").html();
  $item.hide(1000, function() {
    $item.remove();
    checkItems($list);
    updateDeliverable($activity, activityIndex);
  });

}

function updateDeliverable(item,activityIndex) {
  $(item).find('.deliverableActivity').each(function(indexDeliverable,deliverableItem) {
    // Set activity indexes
    $(deliverableItem).setNameIndexes(1, activityIndex);
    // Set indexes
    $(deliverableItem).setNameIndexes(2, indexDeliverable);
  });
}

function checkItems(block) {
  console.log(block);
  var items = $(block).find('.deliverableActivity').length;
  if(items == 0) {
    $(block).parent().find('p.inf').fadeIn();
  } else {
    $(block).parent().find('p.inf').fadeOut();
  }
}

function formatState(state) {
  var $state = $("<span>" + state.text + "</span>");
  return $state;

};