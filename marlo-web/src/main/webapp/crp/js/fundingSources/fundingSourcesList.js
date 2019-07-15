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

  /**
   * Pop up to add a new Funding Source
   */
  (function() {
    // Testing VUE JS
    var app = new Vue({
        el: '#vueApp',
        data: {
            isValid: false,
            fundingSources: []
        },
        methods: {

        }
    });

    var $instPartnersSelect = $('select[class*="elementType-institution"]');
    var $institutionLeadSelect = $('select.institutionLead');
    var $statusSelect = $('select.agreementStatus');
    var $financeCode = $('input.financeCode');
    var $institutionIDs = $('input[name="partnerIDs"]');
    var keyupTimer = null;

    $instPartnersSelect.on("addElement removeElement", function(event,id,name) {
      if(event.type == "addElement") {
        $('select.institutionLead').addOption(id, name);
      } else if(event.type == "removeElement") {
        $('select.institutionLead').removeOption(id);
      }
      var institutionsIDs = jQuery.map($('input[name="ins"]'), function(el) {
        return $(el).val()
      });
      $institutionIDs.val(institutionsIDs.join(','));
      validateForm();
    });

    $institutionLeadSelect.on("change", validateForm);
    $statusSelect.on("change", validateForm);

    $('#fundingSourceAddPopup').on('hidden.bs.modal', function(e) {
      app.fundingSources = [];
      $statusSelect.val("-1").trigger('change.select2');
      $institutionLeadSelect.val("-1").trigger('change.select2');
      $financeCode.val("");

      validateForm();
    })

    $financeCode.on("keyup", function() {
      $financeCode.addClass('input-loading');
      app.isValid = false;
      if(keyupTimer) {
        clearTimeout(keyupTimer);
        keyupTimer = null;
      }
      keyupTimer = setTimeout(searchDuplicated, 2000);
    });

    function searchDuplicated() {
      var code = $.trim($financeCode.val());
      $.ajax({
          'url': baseURL + '/FundingSourceService.do',
          'data': {
              financeCode: code,
              phaseID: phaseID
          },
          beforeSend: function() {
            app.fundingSources = [];
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
          error: function() {
          }
      });
    }

    function validateForm() {
      app.isValid = false;
      var instPartners = $instPartnersSelect.parents('.panel-body').find('ul li').length;
      var statusValue = $statusSelect.val();
      var leadValue = $institutionLeadSelect.val();
      if((instPartners > 0) && (statusValue > 0) && (leadValue > 0) && (app.fundingSources.length == 0)) {
        app.isValid = true;
      }
      return app.isValid;
    }

  })();

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

//Select All Institutions
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
    console.log(institutions);
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