$(document).ready(init);

function init() {
  /* Init Select2 plugin */
  $('.disseminationChannel').select2({
    width: '50%'
  });
  $(".accessible .no-button-label").addClass("radio-checked");
  $(".findable .yes-button-label").addClass("radio-checked");
  $(".license .yes-button-label").addClass("radio-checked");
  // Validations

  // accessible
  $(".accessible .no-button-label").on("click", function() {
    $(".accessible .yes-button-label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
    $(".openAccessOptions").show("slow");
  });
  $(".accessible .yes-button-label").on("click", function() {
    $(".accessible .no-button-label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
    $(".openAccessOptions").hide("slow");
  });

// findable
  $(".findable .no-button-label").on("click", function() {
    $(".findable .yes-button-label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
    $(".findableOptions").hide("slow");
    $(".dataSharing").show("slow");
  });
  $(".findable .yes-button-label").on("click", function() {
    $(".findable .no-button-label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
    $(".findableOptions").show("slow");
    $(".dataSharing").hide("slow");
  });

// acknowledge
  $(".acknowledge .no-button-label").on("click", function() {
    $(".acknowledge .yes-button-label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
  });
  $(".acknowledge .yes-button-label").on("click", function() {
    $(".acknowledge .no-button-label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
  });

// license
  $(".license .no-button-label").on("click", function() {
    $(".license .yes-button-label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
    $(".licenseOptions").hide("slow");
  });
  $(".license .yes-button-label").on("click", function() {
    $(".license .no-button-label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
    $(".licenseOptions").show("slow");
  });

  $("#deliverableMetadataDate").datepicker({
      dateFormat: "yy-mm-dd",
      minDate: '2015-01-01',
      maxDate: '2030-12-31',
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth, 1)
        $(this).datepicker('setDate', selectedDate);
      }
  });

  $(".addAuthor").on("click", addAuthor);

// Remove a author
  $('.removeAuthor').on('click', removeAuthor);

  // Change dissemination channel
  $(".disseminationChannel").on('change', changeDisseminationChannel);

  $("#fillMetadata").on("click", function() {
    var url = $(".deliverableDisseminationUrl").val();
    // Validate url

    if(/(http(s)?:\/\/.)?(www\.)?cgspace.cgiar\.org\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/g.test(url)) {
      // get data from url
      var urlSplit = pageId = url.split("/");
      var pageId = urlSplit[urlSplit.length - 2] + urlSplit[urlSplit.length - 1];

      // Ajax to service
      var data = {
          pageID: "cgspace",
          metadaID: pageId
      }
      $.ajax({
          url: baseURL + "/metadataByLink.do",
          type: 'GET',
          dataType: "json",
          data: data
      }).done(function(m) {
        console.log(m);
      });
    }

  });

}

function changeDisseminationChannel() {
  var channel = $(".disseminationChannel").val();
  if(channel != "-1") {
    if(channel == "2") {
      $("#fillMetadata").slideDown("slow");
    } else {
      $("#fillMetadata").slideUp("slow");
    }
    $('#disseminationUrl').slideDown("slow");
  } else {
    $('#disseminationUrl').slideUp("slow");
  }
}

function addAuthor() {
  var $list = $('.authorsList');
  var $item = $('#author-template').clone(true).removeAttr("id");
  $list.append($item);
  $item.show('slow');
  updateAuthor();
  checkNextAuthorItems($list);
}

function removeAuthor() {
  var $list = $(this).parents('.authorsList');
  var $item = $(this).parents('.author');
  $item.hide(function() {
    $item.remove();
    checkNextAuthorItems($list);
    updateAuthor();
  });
}

function updateAuthor() {
  $(".authorsList").find('.author').each(function(i,e) {
    // Set activity indexes
    $(e).setNameIndexes(1, i);
  });
}

function checkNextAuthorItems(block) {
  var items = $(block).find('.author ').length;
  if(items == 0) {
    $(block).parent().find('p.emptyText').fadeIn();
  } else {
    $(block).parent().find('p.emptyText').fadeOut();
  }
}