$(document).ready(init);

function init() {

  // Select2
  $('select').select2({
    width: '100%'
  });

  // Set visible publications questions
  $('.publicationMetadataBlock').show();

  // Attaching events
  attachEvents();
}

function attachEvents() {

  // YES/NO Event
  $(".button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    var $input = $(this).parent().find('input');
    $input.val(valueSelected);
    $(this).parent().find("label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
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

  // Have the publication adopted a license
  $(".license .button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    if(!valueSelected) {
      $(".licenseOptions-block").hide("slow");
    } else {
      $(".licenseOptions-block").show("slow");
    }
  });

  // Other license type
  $(".licenseOptions input").on("change", function() {
    if($(this).val() == "OTHER") {
      $(".licence-modifications").show("slow");
    } else {
      $(".licence-modifications").hide("slow");
    }
  });

  /** Gender Levels * */
  $(".genderLevelsSelect").on("change", function() {
    var option = $(this).find("option:selected");
    if(option.val() != "-1") {
      addGenderLevel(option);
    }
    // Remove option from select
    option.remove();
    $(this).trigger("change.select2");
  });
  $(".removeGenderLevel").on("click", removeGenderLevel);

  // Validate if funding source exists in select
  $("form .genderLevel").each(function(i,e) {
    var options = $(".genderLevelsSelect option");
    options.each(function(iOption,eOption) {
      if($(e).find(".fId").val() == $(eOption).val()) {
        $(eOption).remove();
      }
    });
  });

  /** Gender questions * */

  $('input#gender').on('change', function() {
    if($(this).is(':checked')) {
      $('#gender-levels').slideDown();
    } else {
      $('#gender-levels').slideUp();
    }
  });

  $('input#gender, input#youth, input#capacity').on('change', function() {
    $('input#na').prop("checked", false);
  });

  $('input#na').on('change', function() {
    $('input#gender, input#youth, input#capacity').prop("checked", false);
    $('#gender-levels').slideUp();
  });

  // Add authors
  $(".addAuthor").on("click", addAuthor);

  // Remove a author
  $('.removeAuthor').on('click', removeAuthor);

  // Select an open restriction
  $("input.openAccessRestrictionRadio").on("change", openAccessRestriction);

  // Other license type
  $("input.licenseRadio").on("change", function() {
    console.log($(this).val());
    if($(this).val() == "OTHER") {
      $(".licence-modifications").show("slow");
    } else {
      $(".licence-modifications").hide("slow");
    }
  });
}

// Open access restriction period
function openAccessRestriction() {
  if($(this).val() == "restrictedUseAgreement") {
    $(".restrictionDate-block").find("label").text("Restricted access until:*");
    $("#restrictionDate").attr("name", "deliverable.dissemination.restrictedAccessUntil");
    $(".restrictionDate-block").show("slow");
  } else if($(this).val() == "effectiveDateRestriction") {
    $(".restrictionDate-block").find("label").text("Restricted embargoed date:*");
    $("#restrictionDate").attr("name", "deliverable.dissemination.restrictedEmbargoed");
    $(".restrictionDate-block").show("slow");
  } else {
    $(".restrictionDate-block").hide("slow");
  }
}

/** Add gender level * */

function addGenderLevel(option) {
  var canAdd = true;
  console.log(option.val());
  if(option.val() == "-1") {
    canAdd = false;
  }

  var $list = $(option).parents(".select").parents("#genderLevelsList").find(".list");
  var $item = $("#glevelTemplate").clone(true).removeAttr("id");
  var v = $(option).text().length > 80 ? $(option).text().substr(0, 80) + ' ... ' : $(option).text();

  // Check if is already selected
  $list.find('.genderLevel').each(function(i,e) {
    if($(e).find('input.fId').val() == option.val()) {
      canAdd = false;
      return;
    }
  });
  if(!canAdd) {
    return;
  }

  // Set funding source parameters
  $item.find(".name").attr("title", $(option).text()).tooltip();
  $item.find(".name").html(v);
  $item.find(".fId").val(option.val());
  $list.append($item);
  $item.show('slow');
  updateGenderLevels($list);
  checkGenderItems($list);

  // Reset select
  $(option).val("-1");
  $(option).trigger('change.select2');

}

function removeGenderLevel() {
  var $list = $(this).parents('.list');
  var $item = $(this).parents('.genderLevel');
  var value = $item.find(".fId").val();
  var name = $item.find(".name").attr("title");
  console.log(name + "-" + value);
  var $select = $(".genderLevelsSelect");
  $item.hide(800, function() {
    $item.remove();
    checkGenderItems($list);
    updateGenderLevels($list);
  });
  // Add funding source option again
  $select.addOption(value, name);
  $select.trigger("change.select2");
}

/* Authors */

function addAuthor() {
  if($(".lName").val() != "" && $(".fName").val() != "") {
    $(".lName").removeClass("fieldError");
    $(".fName").removeClass("fieldError");
    $(".oId").removeClass("fieldError");
    var $list = $('.authorsList');
    var $item = $('#author-template').clone(true).removeAttr("id");
    $item.find(".lastName").html($(".lName").val());
    $item.find(".firstName").html($(".fName").val());
    if($(".oId").val() == "") {
      $item.find(".orcidId").html("<b>orcid id:</b> not filled</small>");
      $item.find(".orcidIdInput").val("");
    } else {
      $item.find(".orcidId").html($(".oId").val());
      $item.find(".orcidIdInput").val($(".oId").val());
    }

    $item.find(".lastNameInput").val($(".lName").val());
    $item.find(".firstNameInput").val($(".fName").val());
    $list.append($item);
    $item.show('slow');
    updateAuthor();
    checkNextAuthorItems($list);

    $(".lName").val("");
    $(".fName").val("");
    $(".oId").val("");
  } else {
    $(".lName").addClass("fieldError");
    $(".fName").addClass("fieldError");
    $(".oId").addClass("fieldError");
  }
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