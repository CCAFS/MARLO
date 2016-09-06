$(document).ready(init);
var countID = 0;

function init() {
  /* Init Select2 plugin */
  $('form select').select2({
    width: "100%"
  });

  // ids for inputs date
  $(".activitiesOG-content").find(".startDate").each(function(index,item) {
    $(item).attr("id", "startDate-" + index)
    $(item).parent().parent().find(".endDate").attr("id", "endDate-" + index);
    date("startDate-" + index, "endDate-" + index);
    countID = index;
  })

  // Events
  $(".addActivity").on("click", addActivity);
  $(".removeActivity").on("click", removeactivity);
  $(".deliverableList").on("change", addDeliverable);

  $('.blockTitle').on('click', function() {
    if($(this).hasClass('closed')) {
      $(this).parent().find('.blockTitle').removeClass('opened').addClass('closed');
      $(this).removeClass('closed').addClass('opened');
    } else {
      $(this).removeClass('opened').addClass('closed');
    }
    $(this).next().slideToggle('slow', function() {
      $(this).find('textarea').autoGrow();
    });
  });
}

// Add a new activity element
function addActivity() {
  var $list = $(".activitiesOG-content");
  var $item = $("#projectActivity-template").clone(true).removeAttr("id");
  $item.find(".startDate").attr("id", "startDate-" + countID);
  $item.find(".endDate").attr("id", "endDate-" + countID);

  $list.append($item);
  $item.show('slow', function() {
    $item.find("textarea").autoGrow();
    $item.find("select").select2({
      width: "100%"
    });
  });
  checkItems($list);
  updateActivities();
  date("#startDate-" + countID, "#endDate-" + countID);
  countID++;

}

// Remove activity element
function removeactivity() {
  var $list = $(this).parents('.activitiesOG-content');
  var $item = $(this).parents('.projectActivity');
  $item.hide(1000, function() {
    $item.remove();
    checkItems($list);
    updateActivities();
  });

}

// Update activities
function updateActivities() {
  var name = "activities";
  $(".activitiesOG-content").find('.projectActivity').each(function(i,item) {

    var customName = name + '[' + i + ']';
    $(item).find('.activityIndex span b').html("Activity #" + (i + 1));
    $(item).find('.activityTitle').attr('name', customName + '.title');
    $(item).find('.activityDescription').attr('name', customName + '.description');
    $(item).find('.startDate').attr('name', customName + '.startDate');
    $(item).find('.endDate').attr('name', customName + '.endDate');
    $(item).find('.progressDescription').attr('name', customName + '.progressDescription');
  });
}

// check items
function checkItems(block) {
  console.log(block);
  var items = $(block).find('.projectActivity').length;
  if(items == 0) {
    $(block).parent().find('p.emptyText').fadeIn();
  } else {
    $(block).parent().find('p.emptyText').fadeOut();
  }
}

// Add a new deliverable element
function addDeliverable() {
  var $list = $(".deliverableWrapper");
  var $item = $("#deliverableActivity-template").clone(true).removeAttr("id");
  $list.append($item);
  $item.show('slow');
  checkItems($list);
  updateActivities();

}

function date(start,end) {
  var dateFormat = "yy-mm-dd", from = $(start).datepicker({
      dateFormat: dateFormat,
      minDate: '2015-01-01',
      maxDate: '2030-12-31',
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true
  }).on("change", function() {
    to.datepicker("option", "minDate", getDate(this));
  }), to = $(end).datepicker({
      dateFormat: dateFormat,
      minDate: '2015-01-01',
      maxDate: '2030-12-31',
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true
  }).on("change", function() {
    from.datepicker("option", "maxDate", getDate(this));
  });

  function getDate(element) {
    console.log(element);
    var date;
    try {
      date = $.datepicker.parseDate(dateFormat, element.value);
    } catch(error) {
      date = null;
    }

    return date;
  }
}