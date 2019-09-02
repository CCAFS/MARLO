var dialogOptions = {};
$(document).ready(function() {

  $('form select').select2({
    width: "100%"
  });

  var $projectList = $('table.projectsList');

  var table = $projectList.DataTable({
      "bPaginate": true, // This option enable the table pagination
      "bLengthChange": true, // This option disables the select table size option
      "bFilter": true, // This option enable the search
      "bSort": true, // this option enable the sort of contents by columns
      "bAutoWidth": false, // This option enables the auto adjust columns width
      "iDisplayLength": 50, // Number of rows to show on the table
      "fnDrawCallback": function() {
        // This function locates the add activity button at left to the filter box
        var table = $(this).parent().find("table");
        if($(table).attr("id") == "currentActivities") {
          $("#currentActivities_filter").prepend($("#addActivity"));
        }
      },
      aoColumnDefs: [
          {
              bSortable: false,
              aTargets: [
                -1
              ]
          }, {
              sType: "natural",
              aTargets: [
                0
              ]
          }
      ]
  });

  $projectList.on('draw.dt', function() {
    $("a.removeProject").on("click", removeProject);
  });

  // Add Button click (Adding state)
  $('a.addButton').on('click', function(e) {
    if($(this).hasClass('disabled')) {
      e.preventDefault();
      return;
    }
    // Turn add button in adding button
    $(this).addClass('disabled animated flipInY');
    $(this).find('.glyphicon').hide();
    $(this).find('.saveText').html('Adding ... <img src="' + baseUrl + '/global/images/loading_3.gif" />&nbsp;');

  });

  $('.loadingBlock').hide().next().fadeIn(1000);

  addJustificationPopUp();

  // Search input - Institutions filter
  $(".searchInstitutions").on("keyup", function() {
    var value = $(this).val().toLowerCase();
    $(".filter-items li").filter(function() {
      $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
    });
  });

  // Clear All Institutions
  $('#clearAllInstitutions').on('click', function() {
    console.log("test");
    $('input[type="checkbox"]').prop("checked", false)
    $('#institutionsID').val(0);
  });

  // Select All Institutions
  $('#selectAllInstitutions').on('click', function() {
    console.log("test");
    $('input[type="checkbox"]').prop("checked", true)
    $('#institutionsID').val(0);
  });

  // Filter multiple institution IDs
  $('input[type="checkbox"]').on('change', function() {
    var institutions = $('input[type="checkbox"]:checked').map(function() {
      return this.value;
    }).get().join(',');
    $('#institutionsID').val(institutions);
  }).change();

});

// Justification popup global vars
var dialog, projectId;
var $dialogContent;

function addJustificationPopUp() {
  $dialogContent = $("#dialog-justification");
  // Initializing justification dialog
  dialog = $dialogContent.dialog({
      autoOpen: false,
      closeText: "",
      height: 200,
      width: 400,
      modal: true,
      buttons: {
          Cancel: function() {
            $(this).dialog("close");
          },
          Remove: function() {
            var $justification = $dialogContent.find("#justification");
            if($justification.val().length > 0) {
              $justification.removeClass('fieldError');
              $dialogContent.find("form").submit();
            } else {
              $justification.addClass('fieldError');
            }
          }
      },
  });
  // Event to open dialog to remove deliverable
  $("a.removeProject").on("click", removeProject);
}

function removeProject(e) {
  e.preventDefault();
  $dialogContent.find("#justification").val('').removeClass('fieldError');
  // Getting deliverable ID and setting input hidden to remove that deliverable
  $dialogContent.find('input.nameId').val($(e.target).parent().attr('id').split('-')[1]);
  dialog.dialog("open");
}

/**
 * Pop up to add a new Funding Source
 */
var fundingSourcePopupModule = (function() {
  // Testing VUE JS
  var app = new Vue({
      el: '#vueApp',
      data: {
          isValid: false,
          fields: -1,
          missingFields: -1,
          fundingSources: [],
          messages: []
      },
      methods: {
        progress: function() {
          if(this.missingFields == 0) {
            return 100;
          } else {
            return Math.abs(((this.missingFields / this.fields) - 1) * 100);
          }
        }
      }
  });

  var $instPartnersSelect = $('select[class*="elementType-institution"]');
  var $institutionLeadSelect = $('select.institutionLead');
  var $budgetTypeSelect = $('select.budgetType');
  var $statusSelect = $('select.agreementStatus');
  var $financeCode = $('input.financeCode');
  var $institutionIDs = $('input[name="partnerIDs"]');
  var keyupTimer = null;

  $instPartnersSelect.on("addElement removeElement", function(event,id,name) {
    var $institutionLead = $('select.institutionLead');
    if(event.type == "addElement") {
      $institutionLead.addOption(id, name);
    } else if(event.type == "removeElement") {
      $institutionLead.removeOption(id);
    }

    // Automatically select the unique on option selected
    if($institutionLead.find('option').length == 2) {
      $($institutionLead.find('option')[1]).prop('selected', true);
    }

    // Set hidden input of institutions
    var institutionsIDs = jQuery.map($('input[name="ins"]'), function(el) {
      return $(el).val()
    });
    $institutionIDs.val(institutionsIDs.join(','));

    // Search if institutions are removed
    searchDuplicated();
  });

  $institutionLeadSelect.on("change", validateForm);
  $statusSelect.on("change", validateForm);
  $budgetTypeSelect.on("change", validateForm);

  $('#fundingSourceAddPopup').on('hidden.bs.modal', function(e) {
    app.fundingSources = [];
    app.messages = [];
    $statusSelect.val("-1").trigger('change.select2');
    $institutionLeadSelect.val("-1").trigger('change.select2');
    $budgetTypeSelect.val("-1").trigger('change.select2');
    $financeCode.val("");

    validateForm();
  })

  $financeCode.on("keyup", function() {
    $financeCode.addClass('input-loading');
    app.isValid = false;
    app.messages = [];
    if(keyupTimer) {
      clearTimeout(keyupTimer);
      keyupTimer = null;
    }
    keyupTimer = setTimeout(searchDuplicated, 800);
  });

  function searchDuplicated() {
    var code = $.trim($financeCode.val());
    var institutionID = $.trim($institutionLeadSelect.val());
    app.fundingSources = [];
    app.messages = [];
    if(code) {
      $.ajax({
          'url': baseURL + '/FundingSourceService.do',
          'data': {
              financeCode: code,
              institutionID: institutionID,
              phaseID: phaseID
          },
          beforeSend: function() {
            $financeCode.addClass('input-loading');
          },
          success: function(data) {
            if(data.sources.length) {
              app.fundingSources = data.sources;
            }
          },
          complete: function() {
            validateForm();
            $financeCode.removeClass('input-loading');
          },
          error: function(error) {
            app.messages.push({
                type: "danger",
                title: error.status + " - " + error.statusText
            });
          }
      });
    } else {
      $financeCode.removeClass('input-loading');
      validateForm();
    }

  }

  function requireCodeByStatus(id) {
    // 1 -> Concept Note/Pipeline
    // 2 -> On-going
    // 3 -> Complete
    // 4 -> Extended
    // 5 -> Cancelled
    // 7 -> Informally Confirmed
    console.log(id);
    if((id == 1) || (id == 7) || (id == 5)) {
      return false;
    }
    return true;
  }

  function validateForm() {
    app.isValid = false;
    var instPartners = $instPartnersSelect.parents('.panel-body').find('ul li').length;
    var budgetType = $budgetTypeSelect.val();
    var statusValue = $statusSelect.val();
    var leadValue = $institutionLeadSelect.val();
    var financeCode = $financeCode.val();
    app.fields = 5;
    app.missingFields = 0;

    // Institution Partners, at least one
    if(instPartners <= 0) {
      app.missingFields += 1;
    }

    // Institution lead selected
    if(leadValue <= 0) {
      app.missingFields += 1;
    }

    // Status
    if(statusValue <= 0) {
      app.missingFields += 1;
    } else {
      if(requireCodeByStatus(statusValue)) {
        $financeCode.parent().find('span.requiredTag').show();
        // Finance code
        if(!financeCode) {
          app.missingFields += 1;
        }
      } else {
        $financeCode.parent().find('span.requiredTag').hide();
      }
    }

    // Validate selection of budget type (Funding Window)
    if(budgetType <= 0) {
      app.missingFields += 1;
    }

    // Duplicates funding sources with the same code
    if(app.fundingSources.length != 0) {
      app.missingFields += 1;
    }

    app.isValid = (app.missingFields == 0);
    return app.isValid;
  }

})();