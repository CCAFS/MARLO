$(document).ready(init);

function init() {

  $("a[href='#deliverable-mainInformation']").on('shown.bs.tab', function(e) {
    // $("textarea").autogrow();
    $("a[href='#deliverable-mainInformation']").removeClass("hideInfo");
  });
  $(".dateMetadata").attr("id", "deliverableMetadataDate");
  $(".restrictionDate").attr("id", "restrictionDate");
  $("#deliverableMetadataDate, #restrictionDate").datepicker({
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

  // Is this deliverable Open Access
  $(".accessible .button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    var $input = $(this).parent().find('input');
    $input.val(valueSelected);
    $(this).parent().find("label").removeClass("radio-checked");
    $(this).addClass("radio-checked");

    if(!valueSelected) {
      $(".openAccessOptions").show("slow");
    } else {
      $(".openAccessOptions").hide("slow");
    }
    checkFAIRCompliant();
  });

  // Is this deliverable already disseminated
  $(".findable .button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    var $input = $(this).parent().find('input');
    $input.val(valueSelected);
    $(this).parent().find("label").removeClass("radio-checked");
    $(this).addClass("radio-checked");

    if(!valueSelected) {
      $(".findableOptions").hide("slow");
      $(".dataSharing").show("slow");
    } else {
      $(".findableOptions").show("slow");
      $(".dataSharing").hide("slow");
    }
    checkFAIRCompliant();

  });

  // Does the publication acknowledge
  $(".acknowledge .button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    var $input = $(this).parent().find('input');
    $input.val(valueSelected);
    $(this).parent().find("label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
  });

  // Have you adopted a license
  $(".license .button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    var $input = $(this).parent().find('input');
    $input.val(valueSelected);
    $(this).parent().find("label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
    checkFAIRCompliant();
  });

  // Does this license allow modifications?
  $(".licenceModifications .button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    var $input = $(this).parent().find('input');
    $input.val(valueSelected);
    $(this).parent().find("label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
    checkFAIRCompliant();
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

  $("#fillMetadata").on("click", loadAndFillMetadata);

  $("input[name='deliverable.dissemination.type']").on("change", openAccessRestriction);

  // Check handle and doi urls
  $(".handleMetadata").on("change keyup", checkHandleUrl);
  $(".doiMetadata").on("change keyup", checkDoiUrl);

}

function checkHandleUrl() {
  var input = $(this);
  $(input).removeClass("fieldError");
  var inputData = $.trim(input.val());
  if(inputData != "") {
    if(inputData.indexOf("handle") == -1) {
      $(input).addClass("fieldError");
    } else {
      $(input).removeClass("fieldError");
    }
  }
}

function checkDoiUrl() {
  var input = $(this);
  $(input).removeClass("fieldError");
  var inputData = $.trim(input.val());
  if(inputData != "") {
    if(inputData.indexOf("doi") == -1) {
      $(input).addClass("fieldError");
    } else {
      $(input).removeClass("fieldError");
    }
  }
}

function openAccessRestriction() {
  if($(this).val() == "restrictedAccess") {
    $(".restrictionDate-block").find("label").text("Restricted access until");
    $(".restrictionDate-block").show("slow");
  } else if($(this).val() == "embargoedPeriods") {
    $(".restrictionDate-block").find("label").text("Restricted embargoed date");
    $(".restrictionDate-block").show("slow");
  } else {
    $(".restrictionDate-block").hide("slow");
  }
}

function setMetadata(data) {
  $("a[href='#deliverable-mainInformation']").addClass("hideInfo");
  if($(".citationMetadata").val() == "") {
    $(".citationMetadata").val(data.citation).autoGrow();
  }
  if($("#deliverableMetadataDate").val() == "") {
    $("#deliverableMetadataDate").datepicker('setDate', data.publicationDate);
  }
  if($(".languageMetadata").val() == "") {
    $(".languageMetadata").val(data.languaje);
  }
  if($(".descriptionMetadata").val() == "") {
    $(".descriptionMetadata").val(data.description).autoGrow();
  }
  if($(".handleMetadata").val() == "") {
    $(".handleMetadata").val(data.handle);
  }
  if($(".doiMetadata").val() == "") {
    $(".doiMetadata").val(data.doi);
  }
  if($(".countryMetadata").val() == "") {
    $(".countryMetadata").val(data.country);
  }

}

function changeDisseminationChannel() {
  var channel = $(".disseminationChannel").val();
  $('#disseminationUrl').find("input").val("");
  $("#metadata-output").empty();
  $(".exampleUrl-block").hide();
  if(channel != "-1") {
    // CGSpace or Dataverse
    if((channel == "2") || channel == "3") {
      $("#fillMetadata").slideDown("slow");
      $(".exampleUrl-block.channel-" + channel).slideDown("slow");
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

/* Load Metadata and fill fields */
function loadAndFillMetadata() {
  var channel = $(".disseminationChannel").val();
  var url = $.trim($(".deliverableDisseminationUrl").val());
  // jsUri Library (https://github.com/derek-watson/jsUri)
  var uri = new Uri(url);

  // Validate URL
  if(url == "") {
    return;
  }

  if(channel == "2") {
    // Get CGSpace Metadata from MARLO server
    getCGSpaceMetadata(channel, url, uri);
  } else if(channel == "3") {
    // Get DataversE Metadata from native API
    getDataverseMetadata(channel, url, uri);
  }

}

function getCGSpaceMetadata(channel,url,uri) {
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
            sendDataJson.country = m.metadata['coverage.country'];
            setMetadata(sendDataJson);

            jsonTest = {
              authors: [
                  {
                      lastName: "lastTest",
                      firstName: "firstTest",
                      orcidId: 546
                  }, {
                      lastName: "lastTest2",
                      firstName: "firstTest2",
                      orcidId: 5462
                  }, {
                      lastName: "lastTest3",
                      firstName: "firstTest3",
                      orcidId: 546435
                  }
              ]
            };
            authorsByService(jsonTest);

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
}

function getDataverseMetadata(channel,url,uri) {
  /**
   * Dataverse metadata is harvest using swagger https://services.dataverse.harvard.edu/static/swagger-ui/
   */

  var data = {
      key: 'c1580888-185f-4250-8f44-b98ca5e7b01b',
      persistentId: uri.getQueryParamValue('persistentId')
  }

  $.ajax({
      // url: 'https://dataverse.harvard.edu/api/datasets/:persistentId/',
      url: 'https://services.dataverse.harvard.edu/miniverse/metrics/v1/datasets/by-persistent-id',
      data: data,
      beforeSend: function() {
        $(".deliverableDisseminationUrl").addClass('input-loading');
        $('#metadata-output').html("Searching ... " + data.persistentId);
      },
      success: function(m) {
        console.log("success");
        if(m.status == "OK") {
          // Getting metadata
          var sendDataJson = {
              citation: '',
              publicationDate: m.data.timestamps.publicationdate,
              languaje: '',
              description: function() {
                var output = "";
                $.each(m.data.metadata_blocks.citation.dsDescription, function(i,element) {
                  output += element.dsDescriptionValue;
                })
                return output;
              },
              handle: '',
              doi: data.persistentId,
              authors: m.data.metadata_blocks.citation.author
          }
          console.log(sendDataJson);

          // Setting metadata
          setMetadata(sendDataJson);
        } else {
          $('#metadata-output').empty().append("Invalid URL for searching metadata");
        }

      },
      complete: function() {
        $(".deliverableDisseminationUrl").removeClass('input-loading');
      },
      error: function() {
        $('#metadata-output').empty().append("Invalid URL for searching metadata");
      }
  });

}

/** FAIR Functions* */

function checkFiandable() {
  $('.fairCompliant.findable').addClass('achieved');
}

function checkAccessible() {
  $('.fairCompliant.accessible').addClass('achieved');
}

function checkInteroperable() {
  $('.fairCompliant.interoperable').addClass('achieved');
}

function checkReusable() {
  $('.fairCompliant.reusable').addClass('achieved');
}

function checkFAIRCompliant() {
  console.log('Check FAIR compliant');
  checkFiandable();
  checkAccessible();
  checkInteroperable();
  checkReusable();
}

// Add author by service
function authorsByService(json) {
  var $list = $('.authorsList');
  for(var i = 0; i < json.authors.length; i++) {
    var $item = $('#author-template').clone(true).removeAttr("id");
    $($item).find(".lastNameInput").val(json.authors[i].lastName);
    $($item).find(".firstNameInput").val(json.authors[i].firstName);
    $($item).find(".orcidIdInput").val(json.authors[i].orcidId);
    $list.append($item);
    $item.show('slow');
    updateAuthor();
    checkNextAuthorItems($list);
  }
}