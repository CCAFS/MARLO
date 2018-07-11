var dialog, capDevDialog;
var $dialogContent, $dialogCapDevCategory;
var timeoutID;
var $contactInput;
var $elementSelected;

$(document).ready(init);

function init() {

  addUser = function(composedName,userId,user) {
    console.log(user);
    var $userField = $elementSelected.parents('.userField');
    $userField.find("input.userName").val(composedName).addClass('animated flash');
    $userField.find("input.userId").val(userId);
    $userField.find("input.ctFirsName").val(user.fName);
    $userField.find("input.ctLastName").val(user.lName);
    $userField.find("input.ctEmail").val(user.email);

    dialog.dialog("close");
  }

  $('form select').select2({
    width: "100%"
  });

  $('.numParticipants').integerInput();
  $('.numMen').integerInput();
  $('.numMen').integerInput();
  $('.participant-code').integerInput();
  $('.capdevDuration').integerInput();

  // Set Date pickers
  setDatePickers();

  // Display individual or group intervention form
  (function() {
    var value = $(".radioButton").val();

    if(value == 1) {
      $(".fullForm").show();
      $(".individualparticipantForm").show();
      $(".grupsParticipantsForm ").hide();
      $(".individual ").show();
      $(".group").hide();
    } else if(value == 2) {
      $(".fullForm").show();
      $(".grupsParticipantsForm ").show();
      $(".individualparticipantForm").hide();
      $(".group").show();
    }
  })();

  // Set value for gender field
  (function() {
    var gender = $(".genderInput").val();
    var genderValue;
    $(".genderSelect select option").each(function() {
      if($(this).val() == gender) {
        $(this).attr("selected", "selected");
        genderValue = $(this).val();
      }
    });

    // Set value for gender when is hostory is active
    var p = $(".genderSelect .selectList p");
    if(gender == "M") {
      p.html("Male")
    }
    if(gender == "F") {
      p.html("Female")
    }
    if(gender == "Other") {
      p.html("Other")
    }

    var citizenship = $(".citizenshipInput").val();
    $(".pCitizenshipcountriesList select option").each(function() {
      if($(this).val() == citizenship) {
        $(this).attr("selected", "selected");
      }
    });

    var countryOfInstitucion = $(".countryOfInstitucionInput").val();
    $(".pcountryOfInstitucionList select option").each(function() {
      if($(this).val() == countryOfInstitucion) {
        $(this).attr("selected", "selected");
      }
    });
  })();

  (function() {
    // this capdev has a regional dimension
    var valueSelected = $(".regional .onoffswitch-radio").val();
    if(valueSelected == 'true') {
      // $(".regionsBox").show("slow");
    }

    var regional = $(".regional p")
    var pValue = regional.html();

    if(pValue === ' Yes') {
      $(".regionsBox").show("slow");
    }
  })();

  (function() {
    if($('input[type="checkbox"][name="capdev.participant.otherInstitution"]').is(":checked")) {
      $(".suggestInstitution").show();
    }
  })();

  $("#syncBoton").attr('disabled', 'disabled');

  checkParticipantCode();
  $(".participant-code").on("keyup", checkParticipantCode);
  $(".participant-code").on("change", checkParticipantCode);
}

// display suggest text area
$('input[type="checkbox"][name="capdev.participant.otherInstitution"]').change(function() {
  if(this.checked) {
    $(".suggestInstitution").show();
    $(".otherInstcheck").val("1")
  } else {
    $(".suggestInstitution").hide();
    $(".otherInstcheck").val("0")

  }
});

// limit number of caracter entered to the text area
$('.textarea').keypress(function(e) {
  var tval = $('.textarea').val(), tlength = tval.length, set = 200, remain = parseInt(set - tlength);
  /* $('p').text(remain); */
  if(remain <= 0 && e.which !== 0 && e.charCode !== 0) {
    $('.textarea').val((tval).substring(0, tlength - 1))
  }
})

// event to download participants template

$(".dowmloadTemplate").on("click", function() {
  $.ajax({
      'url': baseURL + '/template/dowmloadTemplate',

      beforeSend: function() {
        console.log("estoy preparando todo")
      },
      success: function() {
        console.log("todo salio bien")

      },
      error: function() {
        console.log("Pailas algo paso")
      },

      complete: function() {
        console.log("Listo papito!")
      }
  });
});

//
function uploadFile() {
  var file = document.getElementById('uploadFile').files[0];
  if(file) {

    $.ajax({
        'url': baseURL + '/previewParticipants.do',
        'data': file,
        type: 'POST',
        enctype: 'multipart/form-data',
        'dataType': "json",
        contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        processData: false,
        beforeSend: function() {
        },
        success: function(data) {

        },
        error: function() {
        },
        complete: function() {
        }
    });
  }
}

// PREVIEW participants

$('#btnDisplay').click(function() {
  $("#participantsTable").empty();
  $("#filewarning").empty();

  var file = document.getElementById('uploadFile').files[0];

  if(file) {
    if(file.name.indexOf("xls") > 0 || file.name.indexOf("xlsx") > 0) {
      $.ajax({
          'url': baseURL + '/previewParticipants.do',
          'data': file,
          type: 'POST',
          enctype: 'multipart/form-data',
          'dataType': "json",
          contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
          processData: false,
          beforeSend: function() {

            $(".loader").show();
          },
          success: function(data) {

            if(data.length > 0) {
              var mytable = $('<table></table>').attr("class", "table table-bordered");
              var rows = data[0].content.length;
              var cols = data[0].headers.length;
              var tr = [];
              var header = $('<tr></tr>').appendTo(mytable);

              for(var i = 0; i < cols; i++) {
                $('<th></th>').text(data[0].headers[i]).appendTo(header);
              }

              for(var i = 0; i < rows; i++) {
                var row = $('<tr></tr>').appendTo(mytable);
                for(var j = 0; j < cols; j++) {
                  $('<td></td>').text(data[0].content[i][data[0].headers[j]]).appendTo(row);
                }
              }

              mytable.appendTo("#participantsTable");
              $("#filewarning").hide();
              $('#myModal').modal('show')
            }

            else {
              $("#filewarning").html("<p> Wrong file. </p>");
              $("#filewarning").show();
            }

          },
          error: function() {

            $("#filewarning").html("<p> An error has occurred by displaying the preview </p>");
            $("#filewarning").show();

          },
          complete: function() {
            // $("#filewarning").hide();
            $(".loader").hide();
          }
      });
    } else {
      $("#filewarning").html("<p> You should upload only excel files. </p>");
      $("#filewarning").show();
    }
  } else {
    $("#filewarning").html("<p> You haven't uploaded any file. </p>");
    $("#filewarning").show();
  }

});

// CAPACITY DEVELOPMENT functions

// add Objective
$(".addObjective").on("click", function() {
  var $list = $(".objectivesList");
  var $item = $("#objective-template").clone(true).removeAttr("id");
  $list.append($item);
  $item.show('slow', function() {
    width: "100%"
  });

  checkItems($list);
  updateFundingSource();

});

// remove Objective
$(".removeObjective").on("click", function() {
  var $list = $(this).parents('.objectivesList');
  var $item = $(this).parents('.objective');
  $item.hide(1000, function() {
    $item.remove();
    checkItems($list);
    updateFundingSource();
  });

});

// add country
$(".capdevCountriesSelect").change(function() {
  var option = $(this).find("option:selected");

  if(option.val() != "-1") {
    addCountry(option);
  }
  // Remove option from select
  option.remove();
  $(this).trigger("change.select2");
});

// remove country
$(".removeCountry").on("click", removeCountry);

// remove country action
// $(".removeCountry-action").on("click", removeCountryAction);

// add region
// REGION item
$(".capdevRegionsSelect").on("change", function() {
  var option = $(this).find("option:selected");
  if(option.val() != "-1") {
    addRegion(option);
  }

  // Remove option from select
  option.remove();
  // option.prop('disabled', true);
  $(this).trigger("change.select2");
  // $('.capdevRegionsSelect').select2();

});

// remove region
$(".removeRegion").on("click", removeRegion);

// remove region action
$(".removeRegion-action").on("click", removeRegionAction);

function checkItems(block) {
  var items = $(block).find('.objective').length;
  if(items == 0) {
    $(block).parent().find('p.inf').fadeIn();
  } else {
    $(block).parent().find('p.inf').fadeOut();
  }
}

function updateFundingSource() {
  $(".objectivesList").find('.objective').each(function(i,e) {
    // Set indexes
    $(e).setNameIndexes(1, i);
  });
}

function updateDisciplineList(block) {
  var items = $(block).find('.approach').length;
  if(items == 0) {
    $(block).parent().find('p.inf').fadeIn();
  } else {
    $(block).parent().find('p.inf').fadeOut();
  }
}

function updateCountryList(block) {
  var items = $(block).find('.country').length;
  if(items == 0) {
    $(block).parent().find('p.inf').fadeIn();
  } else {
    $(block).parent().find('p.inf').fadeOut();
  }
}

function updateRegionList(block) {
  var items = $(block).find('.region').length;
  if(items == 0) {
    $(block).parent().find('p.inf').fadeIn();
  } else {
    $(block).parent().find('p.inf').fadeOut();
  }
}

function updateOutComesList(block) {
  var items = $(block).find('.outcome').length;
  if(items == 0) {
    $(block).parent().find('p.inf').fadeIn();
  } else {
    $(block).parent().find('p.inf').fadeOut();
  }
}

/** COUNTRIES SELECT FUNCTIONS * */
// Add a new country element
function addCountry(option) {

  var canAdd = true;
  console.log(option.val());
  if(option.val() == "-1") {
    canAdd = false;
  }

  var $list = $("#capdevCountriesList").find(".list");
  var $item = $("#capdevCountryTemplate").clone(true).removeAttr("id");
  var v = $(option).text().length > 12 ? $(option).text().substr(0, 12) + ' ... ' : $(option).text();

  // Check if is already selected
  $list.find('.capdevCountry').each(function(i,e) {
    if($(e).find('input.cId').val() == option.val()) {
      canAdd = false;
      return;
    }
  });
  if(!canAdd) {
    return;
  }

  // Set country parameters
  $item.find(".name").attr("title", $(option).text());
  var $state = $('<span>' + v + '</span>');
  $item.find(".name").html($state);
  $item.find(".cId").val(option.val());
  // $item.find(".id").val(-1);
  $list.append($item);
  $item.show('slow');
  updateCountryList($list);
  checkCountryList($list);

  // Reset select
  $(option).val("-1");
  $(option).trigger('change.select2');

}

function removeCountry() {
  var $list = $(this).parents('.list');
  var $item = $(this).parents('.capdevCountry');
  var value = $item.find(".cId").val();
  var name = $item.find(".name").attr("title");

  var $select = $(".capdevCountriesSelect");
  $item.hide(300, function() {
    $item.remove();
    checkCountryList($list);
    updateCountryList($list);
  });
  // Add country option again
  $select.addOption(value, name);
  $select.trigger("change.select2");
}

function updateCountryList($list) {

  $($list).find('.capdevCountry').each(function(i,e) {
    // Set country indexes
    $(e).setNameIndexes(1, i);
  });
}

function checkCountryList(block) {
  var items = $(block).find('.capdevCountry').length;
  if(items == 0) {
    $(block).parent().find('p.emptyText').fadeIn();
  } else {
    $(block).parent().find('p.emptyText').fadeOut();
  }
}

/** REGIONS SELECT FUNCTIONS * */
// Add a new region element
function addRegion(option) {
  var canAdd = true;
  if(option.val() == "-1") {
    canAdd = false;
  }
  var optionValue = option.val().split("-")[0];
  var optionScope = option.val().split("-")[1];

  var $list = $("#capdevRegionsList").find(".list");
  var $item = $("#capdevRegionTemplate").clone(true).removeAttr("id");
  var v = $(option).text().length > 16 ? $(option).text().substr(0, 16) + ' ... ' : $(option).text();

  // Check if is already selected
  $list.find('.capdevRegion').each(function(i,e) {
    if($(e).find('input.rId').val() == optionValue) {
      canAdd = false;
      return;
    }
  });
  if(!canAdd) {
    return;
  }

  // Set region parameters
  $item.find(".name").attr("title", $(option).text());
  $item.find(".name").html(v);
  $item.find(".rId").val(optionValue);

  // $item.find(".id").val(-1);
  $list.append($item);
  $item.show('slow');
  updateRegionList($list);
  checkRegionList($list);

}

function removeRegion() {
  var $list = $(this).parents('.list');
  var $item = $(this).parents('.capdevRegion');
  var value = $item.find(".rId").val();
  var name = $item.find(".name").attr("title");

  var $select = $(".capdevRegionsSelect");
  $item.hide(300, function() {
    $item.remove();
    checkRegionList($list);
    updateRegionList($list);
  });
  var option = $select.find("option[value='" + value + "']");
  // console.log(option);
  // option.prop('disabled', false);
  $select.append(option);
  $('.capdevRegionsSelect').append('<option value= ' + value + '>' + name + '</option>');
  // $('.capdevRegionsSelect').select2();

}

function updateRegionList($list) {

  $($list).find('.capdevRegion').each(function(i,e) {
    // Set regions indexes
    $(e).setNameIndexes(1, i);
  });
  verifiedRegionList($list)
}

function checkRegionList(block) {
  var items = $(block).find('.capdevRegion').length;
  if(items == 0) {
    $(block).parent().find('p.emptyText').fadeIn();
  } else {
    $(block).parent().find('p.emptyText').fadeOut();
  }
}

function removeRegionAction() {
  console.log("removeRegionAction");
  var $item = $(this).parents('.capdevRegion');
  var value = $item.find(".id").val();
  $.ajax({
      'url': baseURL + '/deleteRegion.do',
      'data': {
        q: value
      },
      beforeSend: function() {
        console.log("antes de enviar el ajax")
      },
      success: function(data) {
      },
      error: function() {
        console.log("algun error")
      },
      complete: function() {
        console.log("terminado todo")
      }
  });

}

function removeCountryAction() {
  console.log("removeCountryAction");
  var $item = $(this).parents('.capdevCountry');
  var value = $item.find(".id").val();
  $.ajax({
      'url': baseURL + '/deleteCountry.do',
      'data': {
        q: value
      },
      beforeSend: function() {
        console.log("antes de enviar el ajax")
      },
      success: function(data) {
      },
      error: function() {
        console.log("algun error")
      },
      complete: function() {
        console.log("terminado todo")
      }
  });

}

function verifiedRegionList(regionsList) {
  var length = regionsList.find('.capdevRegion').length;
  $('.capdevCountriesSelect').empty();
  $('.capdevCountriesSelect').append('<option value= -1>select option... </option>');
  if(length > 0) {
    regionsList.find('.capdevRegion').each(function(i,e) {
      var regionID = $(e).find('input.rId').val();
      filterCountry(regionID);
    });
  } else {
    filterCountry(-1);
  }
}

// filter countries according region selected
function filterCountry(regionID) {
  $.ajax({
      'url': baseURL + '/filterCountry.do',
      'data': {
        q: regionID
      },
      'dataType': "json",
      beforeSend: function() {
      },
      success: function(data) {
        var length = data.length;
        for(var i = 0; i < length; i++) {
          $('.capdevCountriesSelect').append(
              '<option value=' + data[i]['countryID'] + '>' + data[i]['countryName'] + '</option>');
        }
      },
      error: function() {
      },
      complete: function() {

      }
  })

}

$(".button-label").on("click", function() {
  var valueSelected = $(this).hasClass('yes-button-label');
  var $input = $(this).parent().find('input');
  if($(this).hasClass("radio-checked")) {
    console.log(valueSelected)
    $(this).removeClass("radio-checked")
    $input.val("");
  } else {
    console.log(valueSelected)
    $input.val(valueSelected);
    $(this).parent().find("label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
  }
});

// Is this capdev has a global dimension
$(".global .button-label").on("click", function() {
  var valueSelected = $(this).hasClass('yes-button-label');
  var isChecekd = $(this).hasClass('radio-checked');
  if(!valueSelected || !isChecekd) {
    // $(".countriesBox").show("slow");
  } else {
    // $(".countriesBox").hide("slow");
  }
});

// Is this capdev has a regional dimension
$(".regional .button-label").on("click", function() {
  var valueSelected = $(this).hasClass('yes-button-label');
  var isChecekd = $(this).hasClass('radio-checked');
  if(!valueSelected || !isChecekd) {
    $(".regionsBox").hide("slow");
  } else {

    $(".regionsBox").show("slow");
  }
});

// sync participant code to get HR information
$(".syncParticipant").on("click", function() {
  var participant_code = $(".participant-code").val();

  $.ajax({
      'url': baseURL + '/syncParticipant.do',
      'data': {
        syncParticipantCode: participant_code
      },
      'dataType': "json",
      beforeSend: function() {
        $('.loading.syncBlock').show();
      },
      success: function(data) {
        setData(data);
      },
      error: function() {
      },
      complete: function() {
        $('.loading.syncBlock').hide();
      }
  })
})

function setData(data) {
  if(!jQuery.isEmptyObject(data.json)) {
    $('.participant-name').val(data.json.firstName);
    $('.participant-lastname').val(data.json.lastName);
    $('.participant-pEmail').val(data.json.email);
    $('.participant-supervisor').val(data.json.supervisor1);

    $(".pCitizenshipcountriesList select option").each(function() {
      if($(this).html() == data.json.cityOfBirth) {
        $(this).attr("selected", "selected");
        $('.pCitizenshipcountriesList .selection .select2-selection__rendered').html(data.json.cityOfBirth);
      }
    });

    switch (data.json.gender) {
      case 'MALE':
        $(".genderSelect select").val("Male");
        $('.genderSelect .selection .select2-selection__rendered').html("Male");
        break;
      case 'FEMALE':
        $(".genderSelect select").val("Female");
        $('.genderSelect .selection .select2-selection__rendered').html("Female");
        break;
    }
  } else {
    $('.participant-name').val("");
    $('.participant-lastname').val("");
    $('.participant-pEmail').val("");
    $('.participant-supervisor').val("");
    $('.pCitizenshipcountriesList .selection .select2-selection__rendered').html("");
    $(".genderSelect select").val("");
    $('.genderSelect .selection .select2-selection__rendered').html("");
    $(".genderSelect select").val("");
    $('.genderSelect .selection .select2-selection__rendered').html("");

  }

}

function checkParticipantCode() {
  console.log("algo")
  var participant_code = $(".participant-code").val();

  if(participant_code == "") {
    $("#syncBoton").css("pointer-events", "none");
    $("#syncBoton").css("background", " #d9d9d9");
  } else {
    $("#syncBoton").css("pointer-events", "");
    $("#syncBoton").css("background", " #7FB06F");
  }
}

function setDatePickers() {
  var datePickerOptions = {
      format: "mmm d, yyyy",
      formatSubmit: "yyyy-mm-dd",
      hiddenName: true,
      selectYears: true,
      selectMonths: true
  }

  $('.datePickersBlock').each(function(i,e) {
    var $startDate = $(e).find('.startDate');
    var $endDate = $(e).find('.endDate');
    var startDatePicker, endDatePicker;

    // Set date pickers
    $startDate.pickadate(datePickerOptions);
    $endDate.pickadate(datePickerOptions);

    // Instance picker component
    startDatePicker = $startDate.pickadate('picker');
    endDatePicker = $endDate.pickadate('picker');

    // Set parameters an events
    startDatePicker.set('max', endDatePicker.get());
    startDatePicker.on('close', function() {
      endDatePicker.set('min', startDatePicker.get());
    });

    endDatePicker.set('min', startDatePicker.get());
    endDatePicker.on('close', function() {
      startDatePicker.set('max', endDatePicker.get());
    })

  });
}