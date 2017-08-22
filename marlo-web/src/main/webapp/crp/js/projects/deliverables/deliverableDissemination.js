$(document).ready(init);

function init() {

  $("a[data-toggle='tab']").on('shown.bs.tab', function(e) {
    $("#indexTab").val($(this).attr("index"));
    $(".radio-block").each(function(i,e) {
      showHiddenTags(e);
    });
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

  // YES/NO Event for deliverables
  $(".button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    var $input = $(this).parent().find('input');
    $input.val(valueSelected);
    $(this).parent().find("label").removeClass("radio-checked");
    $(this).addClass("radio-checked");

    checkFAIRCompliant();
  });

  // Is this deliverable Open Access
  $(".accessible .button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    if(!valueSelected) {
      $(".openAccessOptions").show("slow");
    } else {
      $(".openAccessOptions").hide("slow");
    }
  });

  // Is this deliverable already disseminated
  $(".findable .button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    if(!valueSelected) {
      $(".findableOptions").hide("slow");
      $(".dataSharing").show("slow");
      unSyncDeliverable();
    } else {
      $(".findableOptions").show("slow");
      $(".dataSharing").hide("slow");
    }

  });

  // Does the publication acknowledge
  $(".acknowledge .button-label").on("click", function() {
    // Do Something
  });

  // Have you adopted a license
  $(".license .button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    if(!valueSelected) {
      $(".licenseOptions-block").hide("slow");
    } else {
      $(".licenseOptions-block").show("slow");
    }
  });

  // Does this license allow modifications?
  $(".licenceModifications .button-label").on("click", function() {
    // Do Something
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

  // Add Author
  $(".addAuthor").on("click", addAuthorElement);

  // Remove a author
  $('.removeAuthor').on('click', removeAuthor);

  // Change dissemination channel
  $(".disseminationChannel").on('change', changeDisseminationChannel);

  // Harvest metadata from URL
  $("#fillMetadata .checkButton, #fillMetadata .updateButton").on("click", syncMetadata);

  // Unsync metadata
  $("#fillMetadata .uncheckButton").on("click", unSyncDeliverable);

  $("input[name='deliverable.dissemination.type']").on("change", openAccessRestriction);

  // Type a dissemination channel url
  $('input.deliverableDisseminationUrl, input.otherLicense').on("change", function() {
    checkFAIRCompliant();
  })

  // Check handle and doi urls
  $(".handleMetadata").on("change keyup", checkHandleUrl);
  $(".doiMetadata").on("change keyup", checkDoiUrl);

  // Other license type
  $("input[name='deliverable.license']").on("change", function() {
    if($(this).val() == "OTHER") {
      $(".licence-modifications").show("slow");
    } else {
      $(".licence-modifications").hide("slow");
    }
    checkFAIRCompliant();
  });

  // Add many flagships
  $(".flaghsipSelect").on("change", function() {
    var option = $(this).find("option:selected");
    if(option.val() != "" && option.val() != "-1") {
      if($(".flagshipList").find(".flagships input.idFlagship[value='" + option.val() + "']").exists()) {
      } else {
        var composedText = currentCrpSession.toUpperCase() + " - " + option.text();
        var v = composedText.length > 60 ? composedText.substr(0, 60) + ' ... ' : composedText;
        addFlagship(option.val(), v, composedText, "");
      }
    }
  });
  $(".crpSelect").on("change", function() {
    var option = $(this).find("option:selected");
    if(option.val() != "" && option.val() != "-1") {
      if($(".flagshipList").find(".flagships input.idCrp[value='" + option.val() + "']").exists()) {
      } else {
        var composedText = option.text().toUpperCase();
        var v = composedText.length > 60 ? composedText.substr(0, 60) + ' ... ' : composedText;
        addCrp("", v, composedText, option.val());
      }
    }
  });

  // Remove flagship
  $(".removeFlagship ").on("click", removeFlagship);

  if(editable) {
    $('.lastName').dblclick(function() {
      var spantext = $(this).text();
      $(this).empty().html('<input type="text" value="' + spantext + '">').find('input').focus();
    }).keypress(function(e) {
      if((e.keyCode == 13) || (e.keyCode == 27)) {
        var text = $('input', this).val();
        if(text == "") {
          text = "Last Name";
        } else {
          $(this).parents(".author").find(".lastNameInput").val(text);
          $(this).parents(".author").find(".id").val("");
        }
        $(this).html(text);
      }
    });
    $('.firstName').dblclick(function() {
      var spantext = $(this).text();
      $(this).empty().html('<input type="text" value="' + spantext + '">').find('input').focus();
    }).keypress(function(e) {
      if((e.keyCode == 13) || (e.keyCode == 27)) {
        var text = $('input', this).val();
        if(text == "") {
          text = "First Name";
        } else {
          $(this).parents(".author").find(".firstNameInput").val(text);
          $(this).parents(".author").find(".id").val("");
        }
        $(this).html(text);
      }
    });
    $('.orcidId').dblclick(function() {
      var spantext = $(this).text();
      $(this).empty().html('<input type="text" value="' + spantext + '">').find('input').focus();
    }).keypress(function(e) {
      if((e.keyCode == 13) || (e.keyCode == 27)) {
        var text = $('input', this).val();
        if(text == "") {
          text = "";
        } else {
          $(this).parents(".author").find(".orcidIdInput").val(text);
          $(this).parents(".author").find(".id").val("");
        }
        $(this).html(text);
      }
    });
  }
}

function addFlagship(id,text,title,crpId) {
  var $list = $('.flagshipList');
  var $item = $('#flagship-template').clone(true).removeAttr("id");
  $item.find(".name").text(text);
  $item.find(".name").attr("title", title);
  $item.find(".idElemento").val("-1");
  $item.find(".idCrp").val(crpId);
  $item.find(".idFlagship").val(id);
  $list.append($item);
  $item.show('slow');
  checkNextFlagshipItems($list);
  updateFlagship();
}

function addCrp(id,text,title,crpId) {
  var $list = $('.flagshipList');
  var $item = $('#flagship-template').clone(true).removeAttr("id");
  $item.find(".name").text(text);
  $item.find(".name").attr("title", title);
  $item.find(".idElemento").val("-1");
  $item.find(".idCrp").val(crpId);
  $item.find(".idFlagship").val(id);
  $list.append($item);
  $item.show('slow');
  checkNextFlagshipItems($list);
  updateFlagship();
}

function removeFlagship() {
  var $list = $(this).parents('.flagshipList');
  var $item = $(this).parents('.flagships');
  $item.hide(function() {
    $item.remove();
    checkNextFlagshipItems($list);
    updateFlagship();
  });
}

function updateFlagship() {
  $(".flagshipList").find('.flagships').each(function(i,e) {
    // Set activity indexes
    $(e).setNameIndexes(1, i);
  });
}

function checkNextFlagshipItems(block) {
  var items = $(block).find('.flagships ').length;
  if(items == 0) {
    $(block).parent().find('p.emptyText').fadeIn();
  } else {
    $(block).parent().find('p.emptyText').fadeOut();
  }
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
  if($(this).val() == "restrictedUseAgreement") {
    $(".restrictionDate-block").find("label").text("Restricted access until");
    $("#restrictionDate").attr("name", "deliverable.dissemination.restrictedAccessUntil");
    $(".restrictionDate-block").show("slow");
  } else if($(this).val() == "effectiveDateRestriction") {
    $(".restrictionDate-block").find("label").text("Restricted embargoed date");
    $("#restrictionDate").attr("name", "deliverable.dissemination.restrictedEmbargoed");
    $(".restrictionDate-block").show("slow");
  } else {
    $(".restrictionDate-block").hide("slow");
  }
}

function changeDisseminationChannel() {
  var channel = $(".disseminationChannel").val();
  $('#disseminationUrl').find("input").val("");
  $("#metadata-output").empty();
  $(".exampleUrl-block").hide();

  var channelsList = [
      "cgspace", "dataverse", "ifpri", "ilri"
  ];

  if(channel != "-1") {
    $('#disseminationUrl').slideDown("slow");
    if(channelsList.indexOf(channel) != -1) {
      $("#fillMetadata").slideDown("slow");
      $(".exampleUrl-block.channel-" + channel).slideDown("slow");
    } else {
      $("#fillMetadata").slideUp("slow");
    }
  } else {
    $('#disseminationUrl').slideUp("slow");
  }
  checkFAIRCompliant();
}

function addAuthorElement() {
  var firstName = $(".fName").val();
  var lastName = $(".lName").val();
  var orcidId = $(".oId").val();

  // Check if inputs are filled out
  if(firstName && lastName) {
    $(".lName, .fName, .oId").removeClass("fieldError");

    // Add a new author
    addAuthor({
        lastName: lastName,
        firstName: firstName,
        orcidId: orcidId
    });

    // Clean add inputs
    $(".lName, .fName, .oId").val("");
  } else {
    $(".lName, .fName, .oId").addClass("fieldError");
  }
}

function addAuthor(author) {

  var $list = $('.authorsList');
  var $item = $('#author-template').clone(true).removeAttr("id");

  // Last Name
  $item.find(".lastName").html(author.lastName);
  $item.find(".lastNameInput").val(author.lastName);

  // First name
  $item.find(".firstName").html(author.firstName);
  $item.find(".firstNameInput").val(author.firstName);

  // ORCID
  if(author.orcidId) {
    author.orcidId = (author.orcidId).replace(/^https?\:\/\//i, "");
    $item.find(".orcidId strong").html(author.orcidId);
    $item.find(".orcidIdInput").val(author.orcidId);
  }

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
    // Set indexes
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

/**
 * Set the metadata in the interface
 * 
 * @param {Object} data
 * @returns
 */
function setMetadata(data) {
  console.log(data);

  // Text area & Inputs fields
  $.each(data, function(key,value) {
    var $parent = $('.metadataElement-' + key);
    var $input = $parent.find(".metadataValue");
    var $hide = $parent.find('.hide');
    if(value) {
      $input.val(value);
      $parent.find('textarea').autoGrow();
      $input.attr('readOnly', true);
      $hide.val("true");
    } else {
      $input.attr('readOnly', false);
      $hide.val("false");
    }
  });

  // Set Authors
  if(typeof (data.authors) != 'undefined') {
    if(data.authors.length > 0) {
      // Clear Authors
      $('.authorsList').empty();
      // Add Authors from data source
      $.each(data.authors, function(i,author) {
        addAuthor(author);
      });
      // Hide authors
      $('.author').addClass('hideAuthor');
      $('.authorVisibles').hide();
      $('.metadataElement-authors .hide').val("true");
    } else {
      // Show authors
      $('.author').removeClass('hideAuthor');
      $('.authorVisibles').show();
      $('.metadataElement-authors .hide').val("false");
    }
  }

  // Open Access Validation
  var $input = $(".accessible ").parent().find('input');
  if(data.openacess == "Open Access") {
    $input.val(true);
    $(".accessible ").parent().find("label").removeClass("radio-checked");
    $(".openAccessOptions").hide("slow");
    $(".accessible .yes-button-label ").addClass("radio-checked");
  } else {
    $input.val(false);
    $(".accessible ").parent().find("label").removeClass("radio-checked");
    $(".openAccessOptions").show("slow");
    $(".accessible .no-button-label ").addClass("radio-checked");
  }

  syncDeliverable();

}

/**
 * Sync the deliverable in the interface and set as synced
 * 
 * @returns
 */
function syncDeliverable() {
  // Hide Sync Button & dissemination channel
  $('#fillMetadata .checkButton, .disseminationChannelBlock').hide('slow');
  // Show UnSync & Update Button
  $('#fillMetadata .unSyncBlock').show();
  // Set hidden inputs
  $('#fillMetadata input:hidden').val(true);
  // Dissemination URL
  $('.deliverableDisseminationUrl').attr('readOnly', true);
  // Check Fair
  checkFAIRCompliant();
  // Update component
  $(document).trigger('updateComponent');
}

/**
 * UnSync the deliverable in the interface
 * 
 * @returns
 */
function unSyncDeliverable() {
  // Show metadata
  $('.metadataElement').each(function(i,e) {
    var $parent = $(e);
    var $input = $parent.find('.metadataValue');
    var $hide = $parent.find('.hide');
    $input.attr('readOnly', false);
    $hide.val("false");
  });

  // Show authors
  $('.author').removeClass('hideAuthor');
  $('.authorVisibles').show();
  $('.metadataElement-authors .hide').val("false");

  // Show Sync Button & dissemination channel
  $('#fillMetadata .checkButton, .disseminationChannelBlock').show('slow');
  // Hide UnSync & Update Button
  $('#fillMetadata .unSyncBlock').hide();
  // Set hidden inputs
  $('#fillMetadata input:hidden').val(false);
  // Dissemination URL
  $('.deliverableDisseminationUrl').attr('readOnly', false);
  // Check Fair
  checkFAIRCompliant();
  // Update component
  $(document).trigger('updateComponent');
}

/**
 * Load Metadata and fill fields
 */
function syncMetadata() {
  var channel = $(".disseminationChannel").val();
  var url = $.trim($(".deliverableDisseminationUrl").val());

  // Validate URL
  if(url == "") {
    return;
  }

  // Get metadata
  getMetadata(channel, url);

}

/**
 * Get Deliverable metadata from different repositories using ajax
 * 
 * @param {string} channel - Repository whrere the metadata is hosted (e.g. CGSpace, Dataverse etc.)
 * @param {string} url - Repositori URL (e.g. https://cgspace.cgiar.org/handle/10568/79435)
 * @returns the ajax return a metadata object
 */
function getMetadata(channel,url) {
  var data = {
      pageID: channel,
      metadataID: url
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
      success: function(data) {
        data = data.metadata;
        if(jQuery.isEmptyObject(data)) {
          $('#metadata-output').html("Metadata empty");
        } else {
          // Setting Metadata
          setMetadata(data);

          $('#metadata-output').empty().append("Found metadata in " + channel);
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

/**
 * Validate duplicated authors
 * 
 * @param {string} lastName
 * @param {string} firstName
 * @returns {boolean} True if is duplicated.
 */
function validateAuthors(lastName,firstName) {
  if($(".authorsList").find('.author input.lastNameInput[value="' + lastName + '"]').exists()
      || $(".authorsList").find(".author input.firstNameInput[value='" + firstName + "']").exists()) {
    return true;
  } else {
    return false;
  }

}
