var $removePartnerDialog, $projectPPAPartners;
var canUpdatePPAPartners, allPPAInstitutions, partnerPersonTypes, leaderType, coordinatorType, defaultType;
var projectLeader;
var lWordsResp = 100;

$(document).ready(init);

function init() {
  // Setting global variables
  $removePartnerDialog = $('#partnerRemove-dialog');
  $partnersBlock = $('#projectPartnersBlock');
  $projectPPAPartners = $('#projectPPAPartners');
  allPPAInstitutions = JSON.parse($('#allPPAInstitutions').val());
  canUpdatePPAPartners = ($("#canUpdatePPAPartners").val() === "true");
  leaderType = 'PL';
  coordinatorType = 'PC';
  defaultType = 'CP';
  partnerPersonTypes = [
      coordinatorType, leaderType, defaultType, '-1'
  ];
  if(editable) {

    // Getting the actual project leader
    projectLeader = jQuery.extend({}, getProjectLeader());
    // Remove PPA institutions from partner institution list when there is not privileges to update PPA Partners
    if(!canUpdatePPAPartners) {
      removePPAPartnersFromList('#projectPartner-template .institutionsList');
    }
    // Update initial project CCAFS partners list for each partner
    updateProjectPPAPartnersLists();

    // Activate the chosen to the existing partners
    addSelect2();

  }
  
  addUser = function(composedName,userId) {
    var $contact = $elementSelected.parents('.contactsPerson ').find('input[value="'+userId+'"]');
    
    if (!$contact.exists()){
      $elementSelected.parents('.userField ').find("input.userName").val(composedName).addClass('animated flash');
      $elementSelected.parents('.userField ').find("input.userId").val(userId);
    }else{
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = 'Contact person cannot be repeated';
      noty(notyOptions);
    }
    dialog.dialog("close");
  }
  
  // This function enables launch the pop up window
  popups();
  // Attaching listeners
  attachEvents();
  if(($partnersBlock.find('.projectPartner').length == 0) && editable) {
    $("a.addProjectPartner").trigger('click');
    var person = new PartnerPersonObject($partnersBlock.find('.contactPerson'));
    person.setPartnerType(leaderType);
    person.changeType();
    var partner = new PartnerObject($partnersBlock.find('.projectPartner'));
    partner.changeType();
  }
  $('.loadingBlock').hide().next().fadeIn(500, function() {
    // Missing fields in parter person
    $("form .projectPartner ").each(function(i,e) {
      verifyMissingFields(e);
    });
    showHelpText();
    setViewMore();
  });

  $("textarea[id!='justification']").autoGrow();
}

function attachEvents() {

  /**
   * General
   */

  $('.blockTitle.closed').on('click', function() {
    if($(this).hasClass('closed')) {
      $('.blockContent').slideUp();
      $('.blockTitle').removeClass('opened').addClass('closed');
      $(this).removeClass('closed').addClass('opened');
    } else {
      $(this).removeClass('opened').addClass('closed');
    }
    $(this).next().slideToggle('slow', function() {
      $(this).find('textarea').autoGrow();
      $(this).find(".errorTag").hide();
      $(this).find(".errorTag").css("left", $(this).outerWidth());
      $(this).find(".errorTag").fadeIn(1000);
    });
  });

  /**
   * Project partner Events
   */
  // Add a project partner Event
  $(".addProjectPartner").on('click', addPartnerEvent);
  // Remove a project partner Event
  $(".removePartner").on('click', removePartnerEvent);
  // When Partner Type change
  $("select.partnerTypes, select.countryList").change(updateOrganizationsList);
  // When organization change
  $("select.institutionsList").on("change", function(e) {
    var partner = new PartnerObject($(this).parents('.projectPartner'));
    // Update Partner Title
    partner.updateBlockContent();

    $.ajax({
        url: baseURL + "/institutionBranchList.do",
        data: {
          institutionID: $(this).val()
        },
        beforeSend: function() {
          partner.startLoader();
        },
        success: function(data) {
          partner.clearCountries();
          $(partner.countriesSelect).empty();
          $(partner.countriesSelect).addOption(-1, "Select a country...");
          $.each(data.branches, function(index,branch) {
            
            if ((branch.name).indexOf("HQ") != "-1"){
              partner.addCountry({
                iso: branch.iso,
                name: branch.name
              });  
            }else{
              $(partner.countriesSelect).addOption(branch.iso, branch.name);
            }
            
          });
          $(partner.countriesSelect).trigger("change.select2");
        },
        complete: function() {
          partner.stopLoader();
        }
    });

    // Update PPA Partners List
    updateProjectPPAPartnersLists(e);
  });
  // Partners filters
  $(".filters-link span").on("click", filterInstitutions);

  // Location Elements events
  $(".countriesList").on('change', addLocElementCountry);
  $('.removeLocElement').on('click', removeLocElement);

  // Request country office
  $('#requestModal').on('show.bs.modal', function (event) {
    $.noty.closeAll(); 
    var partner = new PartnerObject($(event.relatedTarget).parents('.projectPartner'));

    var $modal = $(this);
    // Show Form & button
    $modal.find('form, .requestButton').show();
    $modal.find('.messageBlock').hide();
    $modal.find('input.institution_id').val(partner.institutionId);
    $modal.find('select.countriesRequest').val(null).trigger('select2:change');
    $modal.find('select.countriesRequest').trigger('change');
    $modal.find('.modal-title').html('Request Country office(s) <br /><small>('+ partner.institutionName + ')</small>');
  });
  $('#requestModal button.requestButton').on('click', function(){
    var $modal = $(this).parents('.modal');
    if ($modal.find('select.countriesRequest').val() == null ){
      return
    }
    
    $.ajax({
      url: baseURL + '/requestCountryOffice.do',
      data: $('#requestModal form').serialize(),
      beforeSend: function(data) {
        $modal.find('.loading').fadeIn();
      },
      success: function(data) {
        console.log(data);
        if(data.sucess.result == "1"){
          // Hide Form & button
          $modal.find('form, .requestButton').hide();
          $modal.find('.messageBlock').show();
          
          // Noty Message
          /*
           * var message = $modal.find('.messageBlock .notyMessage').html(); var notyOptions = jQuery.extend({},
           * notyDefaultOptions); notyOptions.text = message; notyOptions.type = 'info'; notyOptions.timeout = 5000;
           * notyOptions.animation = { open: 'animated fadeIn', // Animate.css class names close: 'animated fadeOut', //
           * Animate.css class names easing: 'swing', // unavailable - no need speed: 400 // unavailable - no need };
           * notyOptions.callback = { onClose : function(){ $modal.modal('hide'); } }; $modal.find('.messageBlock
           * .notyMessage').noty(notyOptions);
           */
        }
      },
      complete: function() {
        $modal.find('.loading').fadeOut();
      }
    });
  });
  
  
  /**
   * CCAFS Partners list events
   */
  $('.ppaPartnersList select').on('change', function(e) {
    addItemList($(this).find('option:selected'));
  });
  $('ul li .remove').on('click', function(e) {
    removeItemList($(this).parents('li'));
  });

  /**
   * Partner Person Events
   */
  // Add partner Person Event
  $(".addContact a.addLink").on('click', addContactEvent);
  // Remove partner person event
  $(".removePerson").on('click', removePersonEvent);
  // When partnerPersonType change
  $("select.partnerPersonType").on("change", changePartnerPersonType);
  // Event to open dialog box and search an contact person
  $(".searchUser, input.userName").on("click", changePersonEmail);
  // Event when click in a relation tag of partner person
  $(".tag").on("click", showPersonRelations);

}

function showHelpText() {
  $('.helpMessage').show();
  $('.helpMessage').addClass('animated flipInX');
}

function getProjectLeader() {
  var contactLeader = {};
  $partnersBlock.find('.contactPerson').each(function(i,partnerPerson) {
    var contact = new PartnerPersonObject($(partnerPerson));
    if(contact.isLeader()) {
      contactLeader = jQuery.extend({}, contact);
    }
  });
  return contactLeader;
}

function setProjectLeader(obj) {
  projectLeader = jQuery.extend({}, obj);
}

function filterInstitutions(e) {
  var $filterContent = $(e.target).parent().next();
  if($filterContent.is(":visible")) {
    updateOrganizationsList(e);
  }
  $filterContent.slideToggle();
}

function changePersonEmail(e) {
  var person = new PartnerPersonObject($(e.target).parents('.contactPerson'));
  // Validate if the person has any activity related for be changed
  if(!person.canEditEmail) {
    e.stopImmediatePropagation();
    var messages = '';
    messages +=
        '<li>This contact cannot be changed due to is currently the Activity Leader for '
            + person.getRelationsNumber('activities') + ' activity(ies)';
    messages += '<ul>';
    messages += person.getRelations('activities');
    messages += '</ul>';
    messages += '</li>';
    // Show a pop up with the message
    $("#contactChange-dialog").find('.messages').append(messages);
    $("#contactChange-dialog").dialog({
        modal: true,
        closeText: "",
        width: 500,
        buttons: {
          Close: function() {
            $(this).dialog("close");
          }
        },
        close: function() {
          $(this).find('.messages').empty();
        }
    });
  }
}

function showPersonRelations(e) {
  var $relations = $(this).next().html();
  $('#relations-dialog').dialog({
      modal: true,
      closeText: "",
      width: 500,
      buttons: {
        Close: function() {
          $(this).dialog("close");
        }
      },
      open: function() {
        $(this).html($relations);
      },
      close: function() {
        $(this).empty();
      }
  });
}

function changePartnerPersonType(e) {
  var $contactPerson = $(e.target).parents('.contactPerson');
  var contact = new PartnerPersonObject($contactPerson);
  // Set as unique contact type in the project
  if((contact.type == leaderType)) {
    setPartnerTypeToDefault(contact.type);
  }
  // Change partner person type
  contact.changeType();
  // Change parent partner type
  var partner = new PartnerObject($contactPerson.parents('.projectPartner'));
  partner.changeType();
  // If the contact type selected is PL
  if(contact.type == leaderType) {
    // If there is a PL previous selected
    if(!jQuery.isEmptyObject(projectLeader)) {
      var previousLeaderName = projectLeader.contactInfo;
      var messages = '<li>Please note that there can only be one project leader per project. <br/>';
      messages += 'Therefore <strong>' + previousLeaderName + '</strong> was assigned a Project collaborator/partner role.</li>';
      // Show a pop up with the message
      $("#contactChangeType-dialog").find('.messages').append(messages);
      $("#contactChangeType-dialog").dialog({
          modal: true,
          closeText: "",
          width: 500,
          buttons: {
            Close: function() {
              $(this).dialog("close");
            }
          },
          close: function() {
            $(this).find('.messages').empty();
          }
      });
    }
  }
  // Update project leader contact person
  setProjectLeader(getProjectLeader());
}

function updateOrganizationsList(e) {
  var $parent = $(e.target).parents('.projectPartner');
  var partner = new PartnerObject($parent);
  var $selectInstitutions = $parent.find("select.institutionsList"); // Institutions list
  var optionSelected = $selectInstitutions.find('option:selected').val(); // Institution selected
  var source = baseURL + "/institutionsByTypeAndCountry.do";

  if($(e.target).parent().attr("class") != "filters-link") {
    var partnerTypes = $parent.find("select.partnerTypes").find('option:selected').val() || -1; // Type value
    var countryList = $parent.find("select.countryList").find('option:selected').val() || -1; // Value value
    source += "?institutionTypeID=" + partnerTypes + "&countryID=" + countryList;
  } else {
    source += "?institutionTypeID=-1&countryID=-1";
  }
  $.ajax({
      url: source,
      beforeSend: function() {
        partner.startLoader();
        $selectInstitutions.empty().append(setOption(-1, "Select an option"));
      },
      success: function(data) {
        $.each(data.institutions, function(index,institution) {
          $selectInstitutions.append(setOption(institution.id, institution.name));
        });
        if(!canUpdatePPAPartners) {
          removePPAPartnersFromList($selectInstitutions);
        }
      },
      complete: function() {
        partner.stopLoader();
        $selectInstitutions.val(optionSelected);
        $selectInstitutions.trigger("change.select2");
      }
  });
}

function removePPAPartnersFromList(list) {
  for(var i = 0, len = allPPAInstitutions.length; i < len; i++) {
    $(list).find('option[value=' + allPPAInstitutions[i] + ']').remove();
  }
  $(list).trigger("change.select2");
}

function updateProjectPPAPartnersLists(e) {
  var projectInstitutions = [];
  // Clean PPA partners from hidden select
  $projectPPAPartners.empty();
  // Loop for all projects partners
  $partnersBlock.find('.projectPartner').each(function(i,projectPartner) {
    var partner = new PartnerObject($(projectPartner));
    // Collecting partners institutions
    projectInstitutions.push(parseInt(partner.institutionId));
    // Validating if the partners is PPA Partner
    if(partner.isPPA()) {
      partner.hidePPAs();
      // Collecting list CCAFS partners from all project partners
      $projectPPAPartners.append(setOption(partner.institutionId, partner.institutionName));
    } else {
      if(partner.institutionId == -1) {
        partner.hidePPAs();
      } else {
        partner.showPPAs();
      }
    }
  });
  $projectPPAPartners.trigger("change.select2");

  // Validating if the institution chosen is already selected
  if(e) {
    var $fieldError = $(e.target).parents('.partnerName').find('p.fieldErrorInstitutions');
    $fieldError.text('');
    var count = 0;
    // Verify if the partner is already selected
    for(var i = 0; i < projectInstitutions.length; ++i) {
      if(projectInstitutions[i] == e.target.value) {
        count++;
      }
    }
    // If there is one selected , show an error message
    if(count > 1) {
      var institutionName = $(e.target).find('option[value="' + e.target.value + '"]').text();
      $fieldError.text(institutionName + ' is already selected').animateCss('flipInX');
      e.target.value = -1;
    }
  }

  // Filling CCAFS partners lists for each project partner cooment
  $partnersBlock.find('.projectPartner').each(function(i,partner) {
    var $select = $(partner).find('select.ppaPartnersSelect');
    $select.empty().append(setOption(-1, "Select an option"));
    $select.append($projectPPAPartners.html());
    // Removing of the list CCAFS partners previously selected by project partner
    $(partner).find('li input.id').each(function(i_id,id) {
      $select.find('option[value=' + $(id).val() + ']').remove();
    });
    $select.trigger("change.select2");
  });

}

function setPartnerTypeToDefault(type) {
  $partnersBlock.find('.projectPartner').each(function(i,partner) {
    var projectPartner = new PartnerObject($(partner));
    $(partner).find('.contactPerson').each(function(i,partnerPerson) {
      var contact = new PartnerPersonObject($(partnerPerson));
      if(contact.type == type) {
        $(partnerPerson).removeClass(partnerPersonTypes.join(' ')).addClass(defaultType);
        contact.setPartnerType(defaultType);
      }
    });
    projectPartner.changeType();
  });
}

function removePartnerEvent(e) {
  e.preventDefault();
  var partner = new PartnerObject($(e.target).parent().parent());
  var messages = "";
  var activities = partner.getRelationsNumber('activities');
  var deliverables = partner.getRelationsNumber('deliverables');
  var partnerContributions = partner.hasPartnerContributions();
  var removeDialogOptions = {
      modal: true,
      width: 500,
      buttons: {},
      close: function() {
        $(this).find('.messages').empty();
      }
  };
  // The budget related with this partner will be deleted
  if(partner.id != -1) {
    // messages += '<li>Note that the budget affected to this partner will also be deleted.</li>';
    removeDialogOptions.buttons = {
        "Remove partner": function() {
          partner.remove();
          $(this).dialog("close");
        },
        Close: function() {
          $(this).dialog("close");
        }
    };
  }
  // Validate if there are any deliverables linked to any contact persons from this partner
  if(deliverables > 0) {
    messages +=
        '<li>Please bear in mind that if you delete this partner, ' + deliverables
            + ' deliverables relations will be deleted</li>';
    removeDialogOptions.buttons = {
        "Remove partner": function() {
          partner.remove();
          $(this).dialog("close");
        },
        Close: function() {
          $(this).dialog("close");
        }
    };
  }
  // Validate if the CCAFS partner is currently contributing has any contributions to another partner
  if(partner.isPPA() && (partnerContributions.length > 0)) {
    messages += '<li>' + partner.institutionName + ' is currently allocating budget to the following partner(s):';
    messages += '<ul>';
    for(var i = 0, len = partnerContributions.length; i < len; i++) {
      messages += '<li>' + partnerContributions[i] + '</li>';
    }
    messages += '</ul> </li>';
    removeDialogOptions.buttons = {
      Close: function() {
        $(this).dialog("close");
      }
    };
  }
  // Validate if the project partner has any project leader assigned
  if(partner.hasLeader()) {
    messages +=
        '<li>Please indicate another project leader from a different partner before deleting this partner.</li>';
    removeDialogOptions.buttons = {
      Close: function() {
        $(this).dialog("close");
      }
    };
  }
  // Validate if the user has privileges to remove CCAFS Partners
  if(partner.isPPA() && !canUpdatePPAPartners) {
    messages += '<li>You don\'t have enough privileges to delete CCAFS Partners.</li>';
    removeDialogOptions.buttons = {
      Close: function() {
        $(this).dialog("close");
      }
    };
  }
  // Validate if there are any activity linked to any contact person of this partner
  if(activities > 0) {
    messages +=
        '<li>This partner cannot be deleted because at least one or more contact persons is leading ' + activities
            + ' activity(ies)</li>';
    messages +=
        '<li>If you want to proceed with the deletion, please go to the activities and change the activity leader</li>';
    removeDialogOptions.buttons = {
      Close: function() {
        $(this).dialog("close");
      }
    };
  }

  if(messages === "") {
    // Remove partner if there is not any problem
    partner.remove();
  } else {
    // Show pop up if there are any message
    $("#partnerRemove-dialog").find('.messages').append(messages);
    $("#partnerRemove-dialog").dialog(removeDialogOptions);
  }
}

function addPartnerEvent(e) {
  var $newElement = $("#projectPartner-template").clone(true).removeAttr("id");
  $(this).before($newElement);
  $newElement.find('.blockTitle').trigger('click');
  $newElement.show("slow", function() {
    // Update component
    $(document).trigger('updateComponent');
  });

  // Activate the select2 plugin for new partners created
  // Organization
  $newElement.find("select.institutionsList").select2(searchInstitutionsOptions(canUpdatePPAPartners));
  $newElement.find("select.institutionsList").parent().find("span.select2-selection__placeholder")
      .text(placeholderText);

  // Role Selection
  $newElement.find("select.partnerPersonType").select2({
      templateResult: formatState,
      width: "100%"
  });

  // Branch
  $newElement.find("select.branchesSelect ").select2({
      placeholder: "Select the branches where the project is working on...",
      width: '100%'
  });

  $newElement.find('select.countriesList').select2({
      placeholder: "Select a country office",
      templateResult: formatStateCountries,
      templateSelection: formatStateCountries,
      width: '100%'
  });

  // Update indexes
  setProjectPartnersIndexes();
}

function addContactEvent(e) {
  e.preventDefault();
  var $newElement = $("#contactPerson-template").clone(true).removeAttr("id");
  var contact = new PartnerPersonObject($newElement);
  var partner = new PartnerObject($(this).parents('.projectPartner'));
  $(e.target).parent().before($newElement);
  $newElement.show("slow");
  // applyWordCounter($newElement.find("textarea.resp"), lWordsResp);
  // Activate the select2 plugin for new partners created
  $newElement.find("select").select2({
      templateResult: formatState,
      width: '100%'
  });

  // Update indexes
  setProjectPartnersIndexes();
}

function removePersonEvent(e) {
  e.preventDefault();
  var person = new PartnerPersonObject($(e.target).parent());
  var messages = "";
  var activities = person.getRelationsNumber('activities');
  var deliverables = person.getRelationsNumber('deliverables');
  var removeDialogOptions = {
      modal: true,
      closeText: "",
      width: 500,
      buttons: {},
      close: function() {
        $(this).find('.messages').empty();
      }
  };
  // Validate if there are any deliverable linked to this person
  if(deliverables > 0) {
    messages +=
        '<li>Please bear in mind that if you delete this contact, ' + deliverables
            + ' deliverables relations will be deleted.</li>';
    removeDialogOptions.buttons = {
        "Remove Person": function() {
          person.remove();
          $(this).dialog("close");
        },
        Close: function() {
          $(this).dialog("close");
        }
    };
  }
  // Validate if the person type is PL
  if(person.isLeader()) {
    messages +=
        '<li>There must be one project leader per project. Please select another project leader before deleting this contact.</li>';
    removeDialogOptions.buttons = {
      Close: function() {
        $(this).dialog("close");
      }
    };
  }
  // Validate if there are any activity linked to this person
  if(activities > 0) {
    messages += '<li>This contact person cannot be deleted because he/she is leading activity(ies)';
    messages +=
        '<li>If you want to proceed with the deletion, please go to the following activities and change the activity leader</li>';
    messages += '<ul>';
    messages += person.getRelations('activities');
    messages += '</ul>';
    messages += '</li>';
    removeDialogOptions.buttons = {
      Close: function() {
        $(this).dialog("close");
      }
    };
  }
  if(messages === "") {
    // Remove person if there is not any message
    person.remove();
  } else {
    // Show a pop up with the message
    $("#contactRemove-dialog").find('.messages').append(messages);
    $("#contactRemove-dialog").dialog(removeDialogOptions);
  }
}

function setProjectPartnersIndexes() {
  $partnersBlock.find(".projectPartner").each(function(index,element) {
    var partner = new PartnerObject($(element));
    partner.setIndex($('#partners-name').val(), index);
  });
}

/**
 * Items list functions
 */

function removeItemList($item) {
  // Adding option to the select
  var $select = $item.parents('.panel').find('select');
  $select.append(setOption($item.find('.id').val(), $item.find('.name').text()));
  $select.trigger("change.select2");
  // Removing from list
  $item.hide("slow", function() {
    $item.remove();
    setProjectPartnersIndexes();
  });
}

function addItemList($option) {
  var $select = $option.parent();
  var $list = $option.parents('.panel').find('ul.list');
  // Adding element to the list
  var $li = $("#ppaListTemplate").clone(true).removeAttr("id");
  $li.find('.id').val($option.val());
  $li.find('.name').html($option.text());
  $li.appendTo($list).hide().show('slow');
  // Removing option from select
  $option.remove();
  $select.trigger("change.select2");
  setProjectPartnersIndexes();
}

// Activate the chosen plugin to the countries, partner types and partners lists.
function addSelect2() {

  // Organization / institution
  $("form select.institutionsList").select2(searchInstitutionsOptions(canUpdatePPAPartners));
  $("form select.institutionsList").parent().find("span.select2-selection__placeholder").text(placeholderText);

  // Role Selection
  $("form select.partnerPersonType").select2({
      templateResult: formatState,
      width: "100%"
  });

  // Branch
  $("form select.branchesSelect ").select2({
      placeholder: "Select the branches where the project is working on...",
      width: '100%'
  });
  $('form select.countriesList, select.countriesRequest').select2({
      placeholder: "Select a country office",
      templateResult: formatStateCountries,
      templateSelection: formatStateCountries,
      width: '100%'
  });

}

/**
 * PartnerObject
 * 
 * @param {DOM} Project partner
 */

function PartnerObject(partner) {
  var types = [];
  this.id = parseInt($(partner).find('.partnerId').val());
  this.institutionId = parseInt($(partner).find('.institutionsList').val());
  this.institutionName =
      $('#projectPartner-template .institutionsList option[value=' + this.institutionId + ']').text()
          || $(partner).find('.partnerTitle').text();
  this.ppaPartnersList = $(partner).find('.ppaPartnersList');
  this.persons = $(partner).find('.contactsPerson .contactPerson');
  this.countriesSelect = $(partner).find('.countriesList');
  this.setIndex = function(name,index) {
    var elementName = name + "[" + index + "].";

    // Updating indexes
    $(partner).setNameIndexes(1, index);

    // Update index for project Partner
    
    $(partner).find("> .blockTitle .index_number").html(index + 1);

    // Update index for CCAFS Partners
    $(partner).find('.ppaPartnersList ul.list li').each(function(li_index,li) {
      $(li).setNameIndexes(2, li_index);
    });
    // Update index for partner persons
    $(partner).find('.contactPerson').each(function(i,partnerPerson) {
      var contact = new PartnerPersonObject($(partnerPerson));
      contact.setIndex(elementName, index, i);
    });
    
 // Update index for locations
    $(partner).find('.locElement').each(function(i,element) {
      $(element).setNameIndexes(2, i);
    });
  };
  this.updateBlockContent = function() {
    $(partner).find('.partnerTitle').text(this.institutionName);
  };
  this.hasPartnerContributions = function() {
    var partners = [];
    var institutionId = this.institutionId;
    $partnersBlock.find(".projectPartner").each(function(index,element) {
      var projectPartner = new PartnerObject($(element));
      $(element).find('.ppaPartnersList ul.list li input.id').each(function(i_id,id) {
        if($(id).val() == institutionId) {
          partners.push(projectPartner.institutionName);
        }
      });
    });
    return partners;
  };
  this.hasLeader = function() {
    var result = false;
    $(partner).find('.contactPerson').each(function(i,partnerPerson) {
      var contact = new PartnerPersonObject($(partnerPerson));
      if(contact.isLeader()) {
        result = true;
      }
    });
    return result;
  };
  this.isPPA = function() {
    var instID = parseInt($(partner).find('.institutionsList').val());
    if(instID == -1) {
      $(partner).find("> .blockTitle .index").removeClass('ppa').text('Partner');
      return false;
    }
    if(allPPAInstitutions.indexOf(instID) != -1) {
      $(partner).find("> .blockTitle .index").addClass('ppa').text('Managing / PPA Partner');
      return true;
    } else {
      $(partner).find("> .blockTitle .index").removeClass('ppa').text('Partner');
      return false;
    }
  };
  this.getRelationsNumber = function(relation) {
    var count = 0;
    $(partner).find('.contactPerson').each(function(i,partnerPerson) {
      var contact = new PartnerPersonObject($(partnerPerson));
      count += contact.getRelationsNumber(relation);
    });
    return count;
  };
  this.checkLeader = function() {
    if($(partner).find('.contactPerson.PL').length == 0) {
      $(partner).removeClass('leader');
      $(partner).find('.type-leader').hide();
    } else {
      $(partner).find('.type-leader').show();
      $(partner).addClass('leader');
      types.push('Leader');
    }
  };
  this.checkCoordinator = function() {
    if($(partner).find('.contactPerson.PC').length == 0) {
      $(partner).removeClass('coordinator');
      $(partner).find('.type-coordinator').hide();
    } else {
      $(partner).addClass('coordinator');
      $(partner).find('.type-coordinator').show();
      types.push('Coordinator');
    }
  };
  this.changeType = function() {
    types = [];
    this.checkLeader();
    this.checkCoordinator();
    if(types.length != 0) {
      $(partner).find('strong.type').text(' (' + types.join(", ") + ')');
    } else {
      $(partner).find('strong.type').text('');
    }
  };
  this.remove = function() {
    $(partner).hide("slow", function() {
      $(partner).remove();
      updateProjectPPAPartnersLists();
      setProjectPartnersIndexes();
    });
  };
  this.clearCountries = function(){
    var $list = $(partner).find(".countries-list.items-list ul");
    $list.empty();
  };
  this.addCountry = function(country){
    var contryISO = country.iso;
    var countryName = country.name;
    if(contryISO == "-1"){
      return
    }
    
    var $list = $(partner).find(".items-list ul");

    var selectedCountries = $list.find('.locElement').map(function(){
      return $(this).find('input.locElementCountry').val();
    }).get();

    if (selectedCountries.indexOf(contryISO) != -1){
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = 'Countries office cannot be repeated';
      noty(notyOptions);
      return
    }
    
    var $item = $('#locElement-template').clone(true).removeAttr('id');
    
    // Fill item values
    $item.find('span.name').text(countryName);
    $item.find('span.coordinates').text("");
    $item.find('input.locElementName').val(countryName);
    $item.find('input.locElementCountry').val(contryISO);
    
    // Add Flag
    var $flag = $item.find('.flag-icon');
    var flag = '<i class="flag-sm flag-sm-' + contryISO.toUpperCase() + '"></i>';
    $flag.html(flag);
    // Remove coordinates span
    $item.find('.coordinates').remove();
    // Adding item to the list
    $list.append($item);
    // Update Locations Indexes
    setProjectPartnersIndexes();
    // Show item
    $item.show('slow');
    // Remove message
    $list.parent().find('p.message').hide();
    
    // Reset select
    $(this.countriesSelect).removeOption(contryISO)
    $(this.countriesSelect).val('-1');
    $(this.countriesSelect).trigger('select2:change');
  };
  this.showPPAs = function() {
    $(this.ppaPartnersList).slideDown();
  };
  this.hidePPAs = function() {

    $(this.ppaPartnersList).slideUp();
  };
  this.startLoader = function() {
    $(partner).find('.loading').fadeIn();
  };
  this.stopLoader = function() {
    $(partner).find('.loading').fadeOut();
  };
}

/**
 * PartnerPersonObject
 * 
 * @param {DOM} Partner person
 */
function PartnerPersonObject(partnerPerson) {
  this.id = parseInt($(partnerPerson).find('.partnerPersonId').val());
  this.type = $(partnerPerson).find('.partnerPersonType').val();
  this.contactInfo = $(partnerPerson).find('.userName').val();
  this.canEditEmail = ($(partnerPerson).find('input.canEditEmail').val() === "true");
  this.setPartnerType = function(type) {
    this.type = type;
    $(partnerPerson).find('.partnerPersonType').val(type).trigger('change.select2');
  };
  this.getPartnerType = function() {
    return $(partnerPerson).find('.partnerPersonType').val();
  };
  this.changeType = function() {
    $(partnerPerson).removeClass(partnerPersonTypes.join(' ')).addClass(this.type);
    this.setPartnerType(this.type);
  };
  this.getRelationsNumber = function(relation) {
    return parseInt($(partnerPerson).find('.tag.' + relation + ' span').text()) || 0;
  };
  this.getRelations = function(relation) {
    return $(partnerPerson).find('.tag.' + relation).next().find('ul').html();
  };
  this.setIndex = function(name,partnerIndex,index) {
    // Update Indexes
    $(partnerPerson).setNameIndexes(2, index);

    // Update name & id for unused input
    $(partnerPerson).find(".userName").attr("name", "partner-" + partnerIndex + "-person-" + index);
    $(partnerPerson).find(".userName").attr("id", "partner-" + partnerIndex + "-person-" + index);

  };
  this.isLeader = function() {
    return(this.type == leaderType);
  };
  this.remove = function() {
    var partner = new PartnerObject($(partnerPerson).parents('.projectPartner'));
    $(partnerPerson).hide("slow", function() {
      $(partnerPerson).remove();
      partner.changeType(this.type);
      setProjectPartnersIndexes();
    });
  };
}

function formatState(state) {
  var text = "";
  if(state.id == "PC") {
    text =
        "Responsible for helping the Project Leader to fill the information requested by the system. He/she will have the same privileges as the Project Leader with the exception that cannot officially submit the project into the platform.";
  } else if(state.id == "PL") {
    text =
        "Responsible for the entire project. He/she must officially submit the project into the platform. Only one Project Leader per project is allowed.";
  } else if(state.id == "CP") {
    text =
        "This person is a member of the project but does not have access to nor responsibilities in MARLO. It could also be a person who is responsible for a producing a deliverable and/or activity.";
  }
  var $state =
      $("<span><b>"
          + state.text
          + "</b> <br><small style='margin-top:2px; font-size:80%; line-height:13px; display:block; font-style:italic;'>"
          + text + "</small> </span>");
  return $state;

};

function formatStateCountries(state) {
  if(!state.id) {
    return state.text;
  }
  var flag = '<i class="flag-sm flag-sm-' + state.element.value.toUpperCase() + '"></i> ';
  var $state;
  if (state.id != -1){
    $state = $('<span>'+ flag + state.text + '</span>');
  }else{
    $state = $('<span>' + state.text + '</span>');
  }
  return $state;
};

// Locations (Country Offices)

function addLocElementCountry() {
  var $partner = $(this).parents('.projectPartner');
  var partner = new PartnerObject($partner);
  
  var $countrySelected = $(this).find("option:selected");
  partner.addCountry({
    iso: $countrySelected.val(),
    name: $countrySelected.text()
  }); 
}

function removeLocElement() {
  var $parent = $(this).parent();
  var $select = $parent.parents('.countries-list ').find('select.countriesList');
  // Add removed item to the selection list
  $select.addOption($parent.find('input.locElementCountry').val(), $parent.find('span.name').text());
  // Removing item
  $parent.hide('slow', function() {
    $parent.remove();
    setProjectPartnersIndexes();
  });
}

