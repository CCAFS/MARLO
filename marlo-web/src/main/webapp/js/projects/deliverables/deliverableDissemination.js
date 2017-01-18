$(document).ready(init);

function init() {

  $('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
    // $("textarea").autogrow();
  });

  $("#deliverableMetadataDate").datepicker({
      dateFormat: "yy-mm-dd",
      minDate: '2012-01-01',
      maxDate: '2030-12-31',
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth, 1)
        $(this).datepicker('setDate', selectedDate);
      }
  });
  /* Init Select2 plugin */
  $('.disseminationChannel').select2({
    width: '90%'
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

  $("#fillMetadata").on(
      "click",
      function() {
        var url = $(".deliverableDisseminationUrl").val();
        var uri = new Uri(url);
        // Validate url
        var data = {
          pageID: "cgspace"
        }

        if(uri.host() == "hdl.handle.net") {
          console.log(uri.path());
          data.metadataID = "oai:cgspace.cgiar.org:" + uri.path().slice(1, uri.path().length);
        } else {
          data.metadataID = "oai:" + uri.host() + ":" + uri.path().slice(8, uri.path().length);
        }
        // get data from url
        // Ajax to service
        $.ajax({
            'url': baseURL + '/metadataByLink.do',
            'type': "GET",
            'data': data,
            'dataType': "json",
            beforeSend: function() {
              $(".deliverableDisseminationUrl").addClass('input-loading');
              $('#metadata-output').html("Searching ... " + data.metadataID);
            },
            success: function(m) {

              if(m.errorMessage) {
                $('#metadata-output').html(data.errorMessage);
              } else {
                m.metadata = JSON.parse(m.metadata);
                if(jQuery.isEmptyObject(m.metadata)) {
                  $('#metadata-output').html("Metadata empty");
                } else {
                  var fields = [];
                  $.each(m.metadata, function(key,value) {
                    console.log(key + "-" + value);
                    fields.push(key.charAt(0).toUpperCase() + key.slice(1));
                  });
                  var sendDataJson = {};
                  sendDataJson.citation = m.metadata['identifier.citation'];
                  var date = m.metadata['date.available'].split("T");
                  sendDataJson.publicationDate = date[0];
                  sendDataJson.languaje = m.metadata['language.iso'];
                  sendDataJson.description = m.metadata['description.abstract'];
                  sendDataJson.handle = m.metadata['identifier.uri'];
                  sendDataJson.doi = m.metadata['identifier.doi'];
                  setMetadata(sendDataJson);

                  $('#metadata-output').empty().append(
                      "Found metadata for " + data.metadataID + " <br /> " + fields.reverse().join(', '));
                }
              }
            },
            complete: function() {
              $(".deliverableDisseminationUrl").removeClass('input-loading');
            },
            error: function() {
              console.log("error");
              $('#metadata-output').empty().append("Invalid URL for searching metadata");
            }
        });
      });

}

function setMetadata(data) {
  $(".citation").val(data.citation).autoGrow();
  $("#deliverableMetadataDate").datepicker('setDate', data.publicationDate);
  $(".language").val(data.languaje);
  $(".metadataDescription").val(data.description).autoGrow();
  $(".handle").val(data.handle);
  $(".doi").val(data.doi);
}

function changeDisseminationChannel() {
  var channel = $(".disseminationChannel").val();
  $('#disseminationUrl').find("input").val("");
  $("#metadata-output").empty();
  if(channel != "-1") {
    if(channel == "2") {
      $("#fillMetadata").slideDown("slow");
      $("#exampleUrl-block").slideDown("slow");
    } else {
      $("#fillMetadata").slideUp("slow");
      $("#exampleUrl-block").slideUp("slow");
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