var dialog, notyDialog;
var timeoutID;
var $elementSelected, $dialogContent, $searchInput;
var openSearchDialog, addProject, addUserMessage;
var institutionSelected, selectedPartnerTitle, selectedYear, fileID;
var canAddFunding;

Dropzone.autoDiscover = false;

$(document).ready(
    function() {
      /** Initialize */
      $dialogContent = $("#dialog-searchProjects");
      $searchInput = $('.search-input .input input');
      var dialogOptions = {
          autoOpen: false,
          height: 710,
          width: 550,
          modal: true,
          dialogClass: 'dialog-searchUsers',
          open: function(event,ui) {
            // Reset
            fileID = -1;
            $dialogContent.find("form")[0].reset();
            // $dialogContent.find("#search-users").trigger('click');
            $dialogContent.find(".tickBox-toggle").hide();
            $dialogContent.find('.warning-info').hide();

            $dialogContent.find(".panel-body ul").empty();
            $dialogContent.find(".panel-body .userMessage").show();
            // getData('');
          }
      };

      // Initializing Manage users dialog
      dialog = $dialogContent.dialog(dialogOptions);

      // Loading initial data with all users
      // getData('');

      /** Events */

      // Principal investigator auto-complete
      addContactAutoComplete();

      $(".type").on("change", function() {
        var option = $(this).find("option:selected");
        var url = baseURL + "/institutionsByBudgetType.do";
        var data = {
            budgetTypeID: option.val(),
            phaseID: phaseID
        };
        ajaxService(url, data);

        // Check Agreement status
        onChangeFundingType(option.val());
      });

      onChangeFundingType($(".type").val());

      // Event for manage the accordion function
      $('#create-user').on('click', function() {
        $(this).siblings('.accordion-block').slideUp('slow');
        if($(this).next().is(':visible')) {
          $('#search-users').next().slideDown();
          $(this).removeClass('active');
          $(this).find('span.title').text('Create Funding Source');
          $(this).find('span.glyphicon').removeClass('glyphicon-search').addClass('glyphicon-plus');
        } else {
          $(this).next().slideDown();
          $(this).addClass('active');
          $(this).find('span.title').text('Back to the list');
          $(this).find('span.glyphicon').removeClass('glyphicon-plus').addClass('glyphicon-search');
        }

      });

      $('.close-dialog').on('click', function() {
        dialog.dialog("close");
      });

      // Event to open dialog box and search an contact person
      $(".searchProject").on("click", function(e) {
        e.preventDefault();
        openSearchDialog($(this));
      });

      // Event when the user select the contact person
      $dialogContent.find("span.name, span.select").on("click", function() {
        var $parent = $(this).parent().parent();

        // Add Funding source
        addProject({
            composedName: $parent.find(".name").text(),
            id: $parent.find(".contactId").text(),
            budget: $parent.find(".budget").text(),
            type: $parent.find(".budgetTypeName").text(),
            typeId: $parent.find(".budgetTypeId").text(),
            w1w2: $parent.find(".budgetTypeName .coFinancing:visible"),
            institutionSelected: institutionSelected,
            selectedYear: selectedYear
        });

      });

      // Event to redirect to funding source section
      $dialogContent.find("span.linkIcon").on("click", function() {
        var $parent = $(this).parent().parent();
        var id = $parent.find(".contactId").text();

        var url = baseURL + "/fundingSources/" + currentCrpSession;
        url += "/fundingSource.do?fundingSourceID=" + id + "&edit=true";
        window.open(url, '_blank');

      });

      // Event to find an user according to search field
      $dialogContent.find(".search-content input").on("keyup", searchUsersEvent);

      // Event to search users clicking in "Search" button
      $dialogContent.find(".search-button").on("click", function() {
        getData($searchInput.val());
      });

      // Trigger to open create user section
      $dialogContent.find("span.link").on("click", function() {
        $dialogContent.find("#create-user").trigger('click');
      });

      // Event to Create a funding source clicking in the button
      $dialogContent.find(".create-button").on("click", function() {
        $dialogContent.find('.warning-info').empty().hide();
        var invalidFields = [];
        var project = {
          phaseID: phaseID
        };
        // project.cofundedMode = $dialogContent.find("input[name='cofundedMode']").val().trim();
        project.description = $dialogContent.find("#description").val().trim();
        project.title = $dialogContent.find("#title").val().trim();
        project.startDate = $dialogContent.find("#startDate").val().trim();
        project.fileID = fileID;
        project.endDate = $dialogContent.find("#endDate").val().trim();
        project.financeCode = $dialogContent.find("#financeCode").val().trim();
        project.status = $dialogContent.find("#status").val().trim();
        project.w1w2 = $dialogContent.find("#w1w2-tag-input:checked").length > 0;
        project.budgetType = $dialogContent.find("#budgetType").val().trim();
        project.fileName = $dialogContent.find('input[name="file"]').val();
        project.liaisonInstitution = institutionSelected;
        project.institution = $dialogContent.find("#institution").val().trim();
        project.contactName = $dialogContent.find("#contactName").val().trim();
        project.contactEmail = $dialogContent.find("#contactEmail").val().trim();
        project.selectedYear = selectedYear;
        project.budgets = [];
        $('.budgetByYears .tab-content .tab-pane').each(function(i,e) {
          project.budgets.push({
              year: $(e).attr('id').split('-')[1],
              budget: removeCurrencyFormat($(e).find('input').val())
          });
        });
        project.budgets = JSON.stringify(project.budgets);

        // Object for validate
        var projectValidate = {};
        projectValidate.description = project.description;
        projectValidate.startDate = project.startDate;
        projectValidate.endDate = project.endDate;
        projectValidate.status = project.status;
        projectValidate.contactName = project.contactName;
        if($dialogContent.find("#contactEmail").classParam('validate') === "true") {
          projectValidate.contactEmail = project.contactEmail;
        }
        projectValidate.institution = project.institution;

        // Validate if fields are filled
        $.each(projectValidate, function(key,value) {
          if(value.length < 1) {
            invalidFields.push($('label[for="' + key + '"]').text().trim().replace(':', ''));
          } else if(value == -1) {
            invalidFields.push('Select an option');
          }
        });

        // Validate fileID
        // if(project.fileID == -1) {
        // invalidFields.push('Upload a contract proposal');
        // }

        // Validate Email
        if($dialogContent.find("#contactEmail").classParam('validate') === "true") {
          var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
          if(!emailReg.test(project.contactEmail)) {
            invalidFields.push('valid contact email');
          }
        }

        if(invalidFields.length > 0) {
          var msj = "You must fill " + invalidFields.join(', ');
          $dialogContent.find('.warning-info').text(msj).fadeIn('slow');
        } else {

          $.ajax({
              'url': baseURL + '/fundingSourceAdd.do',
              method: 'POST',
              data: project,
              beforeSend: function() {
                $dialogContent.find('.loading').show();
              },
              success: function(data) {
                if(data.status == "OK") {
                  console.log(data);
                  addProject({
                      composedName: data.title,
                      id: data.id,
                      budget: data.ammount,
                      type: data.type,
                      typeId: data.typeID,
                      w1w2: data.w1w2,
                      institutionSelected: institutionSelected,
                      selectedYear: selectedYear
                  });
                } else {
                  $dialogContent.find('.warning-info').text(data.message).fadeIn('slow');
                }
              },
              complete: function(data) {
                $dialogContent.find('.loading').fadeOut();
              }
          });
        }

      });

      $dialogContent.find("form").on("submit", function(e) {
        event.preventDefault();
      });

      /** Functions * */

      openSearchDialog = function(selected) {

        $elementSelected = $(selected);
        selectedPartnerTitle = $elementSelected.parents('.projectPartner').find('.partnerTitle').text();
        $dialogContent.find('.cgiarCenter').text(selectedPartnerTitle);
        institutionSelected = $elementSelected.parents('.projectPartner').find('.partnerInstitutionId').text();
        selectedYear = $elementSelected.parents('.tab-pane').attr('id').split('-')[1];

        dialog.dialog("open");

        // Verify if has permission to create
        canAddFunding = $elementSelected.hasClass('canAddFunding');

        if(canAddFunding) {
          $('#create-user').show();
        } else {
          $('#create-user').hide();
        }

        // Hide search loader
        $dialogContent.find(".search-loader").fadeOut("slow");

        // Set dates
        date('#startDate', '#endDate');

        // Set file upload (blueimp-tmpl)
        var $uploadBlock = $('.fileUploadContainer');
        var $fileUpload = $uploadBlock.find('.upload')
        $fileUpload.fileupload({
            dataType: 'json',
            start: function(e) {
              $uploadBlock.addClass('blockLoading');
            },
            stop: function(e) {
              $uploadBlock.removeClass('blockLoading');
            },
            done: function(e,data) {
              var r = data.result;
              console.log(r);
              if(r.saved) {
                $uploadBlock.find('.textMessage .contentResult').html(r.fileFileName);
                $uploadBlock.find('.textMessage').show();
                $uploadBlock.find('.fileUpload').hide();
                // Set file ID
                fileID = r.fileID;
              }
            },
            progressall: function(e,data) {
              var progress = parseInt(data.loaded / data.total * 100, 10);
            }
        });

        $uploadBlock.find('.removeIcon').on('click', function() {
          $uploadBlock.find('.textMessage .contentResult').html("");
          $uploadBlock.find('.textMessage').hide();
          $uploadBlock.find('.fileUpload').show();
          fileID = -1;
        });

        // Search initial projects
        getData('');

        // Organization / institution
        $("#institution").select2({
            width: "100%",
            dropdownParent: $('#dialog-searchProjects')
        });

        // Funding Window / Budget type
        $(".type").select2({
            templateResult: function(state) {
              var name = state.text;
              var desc = $('li.budgetTypeDescription-' + state.id).text();
              var $state = $("<span><b>" + name + "</b><br><small class='selectDesc'>" + desc + "</small></span>");
              return $state;
            },
            width: "100%",
            dropdownParent: $('#dialog-searchProjects')
        });

      }

      addProject = function(fundingSource) {
        dialog.dialog("close");
      }

      addUserMessage = function(message) {
        $elementSelected.parent().find('.username-message').remove();
        $elementSelected.after("<p class='username-message note animated flipInX'>" + message + "</p>");
      }

      function searchUsersEvent(e) {
        var query = $(this).val();
        if(query.length > 1) {
          if(timeoutID) {
            clearTimeout(timeoutID);
          }
          // Start a timer that will search when finished
          timeoutID = setTimeout(function() {
            getData(query);
          }, 400);
        } else {
          getData('');
        }

      }

      function getData(query) {
        $.ajax({
            'url': baseURL + '/FundingSourceList.do',
            'data': {
                q: query,
                institutionID: institutionSelected,
                year: selectedYear,
                phaseID: phaseID
            },
            'dataType': "json",
            beforeSend: function(xhr,opts) {
              $dialogContent.find(".search-loader").show();
              $dialogContent.find(".panel-body ul").empty();
            },
            success: function(data) {
              var idsArray = [];
              $(".institution-" + institutionSelected + ".year-" + selectedYear + "").each(function(i,e) {
                idsArray.push(parseInt($(e).val()));
              });
              var usersFound = (data.sources).length;
              if(usersFound > 0) {
                $dialogContent.find(".panel-body .userMessage").hide();
                $.each(data.sources, function(i,source) {
                  var $item = $dialogContent.find("li#userTemplate").clone(true).removeAttr("id");
                  if(source.amount <= 0) {
                    $item.find('.noBudgetMessage').show();
                    $item.find('.noBudgetMessage').attr('title', 'Insufficient funds for ' + selectedYear);
                    // $item.find('.listButton.select').hide();
                  }
                  if(source.w1w2) {
                    $item.find('.name').html(
                        '<strong>' + source.type + ' <small class="text-primary"> (Co-Financing)</small> </strong> - '
                            + source.name);
                  } else {
                    $item.find('.name').html('<strong>' + source.type + '</strong> - ' + source.name);
                  }
                  $item.find(".currentBudget").html(
                      "<br> <strong> Current Budget</strong> - $" + setCurrencyFormat(source.amount));
                  $item.find('.contactId').html(source.id);
                  $item.find('.budget').html(source.amount);
                  $item.find('.budgetTypeName').html(source.type);
                  $item.find('.budgetTypeId').html(source.typeId);
                  if(i == usersFound - 1) {
                    $item.addClass('last');
                  }
                  if(source.canSelect && idsArray.indexOf(source.id) == -1) {
                    $dialogContent.find(".panel-body ul").append($item);
                  }
                });
              } else {
                $dialogContent.find(".panel-body .userMessage").show();
              }

            },
            complete: function() {
              $dialogContent.find(".search-loader").fadeOut("slow");
            }
        });

      }

      // When select center as Funding Window----------
      var lastDonor = -1;
      $("#budgetType").on("change", function() {
        var option = $(this).find("option:selected");
        var institutionSelect = $("#institution");
        console.log(option.val());
        // If the option selected is center
        if(option.val() == 4) {
          if(institutionSelect.val() != "-1") {
            lastDonor = institutionSelect.val();
          }
          institutionSelect.attr("disabled", "disabled");
          institutionSelect.val(institutionSelected);
          $(".note").hide("slow");
        } else {
          $(".note").show("slow");

          if(institutionSelect.attr("disabled") == "disabled") {
            institutionSelect.removeAttr("disabled");
            institutionSelect.val(lastDonor);
          }
        }

      });

    });// End document ready event

/**
 * Check Agreement status
 * 
 * @param {number} typeID - Funding budget type
 */
function onChangeFundingType(typeID) {
  var W1W2 = 1;
  var ON_GOING = 2;
  // Change Agreement Status when is (W1W2 Type => 1)
  var $agreementStatus = $('select#status');
  // 3 => Concept Note/Pipeline
  // 4 => Informally Confirmed
  var $options = $agreementStatus.find("option[value='3'], option[value='4']");
  if(typeID == W1W2) {
    $agreementStatus.val(ON_GOING); // On-going
    $options.remove();
  } else {
    if($options.length == 0) {
      $agreementStatus.addOption("3", "Concept Note/Pipeline");
      $agreementStatus.addOption("4", "Informally Confirmed");
    }
  }

  // Check W1/W2 - Tag
  if(typeID == W1W2) {
    $('.w1w2-tag').show();
  } else {
    $('.w1w2-tag').hide();
  }
}

function date(start,end) {
  var dateFormat = "yy-mm-dd";
  var from = $(start).datepicker({
      dateFormat: dateFormat,
      minDate: MIN_DATE,
      maxDate: $(end).val() || MAX_DATE,
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth, 1)
        $(this).datepicker('setDate', selectedDate);
        if(selectedDate != "") {
          $(end).datepicker("option", "minDate", selectedDate);
        }
        getYears();
      }
  }).on("change", function() {
    getYears();
  }).on("click", function() {
    if(!$(this).val()) {
      $(this).datepicker('setDate', new Date());
      getYears();
    }
  });

  var to = $(end).datepicker({
      dateFormat: dateFormat,
      minDate: $(start).val() || MIN_DATE,
      maxDate: MAX_DATE,
      changeMonth: true,
      numberOfMonths: 1,
      changeYear: true,
      onChangeMonthYear: function(year,month,inst) {
        var selectedDate = new Date(inst.selectedYear, inst.selectedMonth + 1, 0)
        $(this).datepicker('setDate', selectedDate);
        if(selectedDate != "") {
          $(start).datepicker("option", "maxDate", selectedDate);
        }
        getYears();
      }
  }).on("change", function() {
    getYears();
  }).on("click", function() {
    if(!$(this).val()) {
      $(this).datepicker('setDate', new Date());
      getYears();
    }
  });

  function getYears() {
    var endYear = (new Date(to.val())).getFullYear();
    var years = [];
    var startYear = (new Date(from.val())).getFullYear() || 2015;

    $('.budgetByYears .nav-tabs').empty();
    $('.budgetByYears .tab-content').empty();
    $('.budgetByYears .fundingTotalAmount').html(setCurrencyFormat(0));

    while(startYear <= endYear) {
      var state = '';
      if(selectedYear == startYear) {
        state = 'active';
      }
      $('.budgetByYears .nav-tabs').append(
          '<li class="' + state + '"><a href="#fundingYear-' + startYear + '" data-toggle="tab">' + startYear
              + '</a></li>');
      $('.budgetByYears .tab-content').append(
          '<div id="fundingYear-' + startYear + '" class="tab-pane col-md-4 ' + state + '">'
              + '<label for="">Budget for ' + startYear
              + ':</label> <input type="text" class="currencyInput form-control input-sm col-md-4" />' + '</div>');

      years.push(startYear++);
    }

    if(years.indexOf(parseInt(selectedYear)) == -1) {
      $('.budgetByYears .nav-tabs li').last().addClass('active');
      $('.budgetByYears .tab-content .tab-pane').last().addClass('active');
    }

    // Set currency format
    $dialogContent.find('.currencyInput').currencyInput();

    $dialogContent.find('.currencyInput').on('keyup', function() {
      var total = 0;
      $dialogContent.find('input.currencyInput').each(function(i,e) {
        total += removeCurrencyFormat($(e).val());
      });
      console.log(total);
      $('.budgetByYears .fundingTotalAmount').html(setCurrencyFormat(total));
    });

  }

  function getDate(element) {
    var date;
    try {
      date = $.datepicker.parseDate(dateFormat, element.value);
    } catch(error) {
      date = null;
    }
    return date;
  }
}

function addDropzone() {

  $("div#file-dropzone").dropzone({
      init: function() {
        console.log("init");
      },
      fallback: function() { // Run this function if the browser not support dropzone plugin
        console.log("fallBackDropzone");
      },
      forceFallback: false,
      uploadMultiple: false,
      parallelUploads: 1,
      createImageThumbnails: false,
      paramName: "file", // The name that will be used to transfer the file
      addRemoveLinks: true,
      params: {},
      url: baseURL + '/uploadFile.do',
      maxFilesize: 30,
      accept: function(file,done) {
        console.log(file.name);
        done();
      },
      success: function(file,responseText) {
        console.log("success");
        console.log(file);
      },
      removedfile: function(file) {
        console.log(file);
      }
  });

}

function ajaxService(url,data) {
  var $select = $("#institution");
  $.ajax({
      url: url,
      data: data,
      beforeSend: function() {
        $('#fundingSourceForm').find('.loading').fadeIn();
      },
      success: function(m) {
        $select.empty();
        $select.addOption("-1", "Select an option...");
        $.each(m.institutions, function(i,e) {
          $select.addOptionFast(e.id, e.name);
        });
        console.log(data.budgetTypeID);
        // Set CGIAR Consortium Office if applicable to the direct donor
        if(data.budgetTypeID == "1" && $select.find("option:selected").val() == "-1") {
          $select.val($(".cgiarConsortium").text());
        }
        $select.trigger("change.select2");
        $('#fundingSourceForm').find('.loading').fadeOut();
      },
      error: function(e) {
        console.log(e);
      },
      complete: function() {
        $('#fundingSourceForm').find('.loading').fadeOut();
      }
  });
}

function addContactAutoComplete() {
  var autocompleteOptions = {
      source: searchSource,
      minLength: 2,
      select: selectUser
  }

  function searchSource(request,response) {
    $.ajax({
        url: baseURL + '/searchUsers.do',
        data: {
            q: request.term,
            phaseID: phaseID
        },
        success: function(data) {
          response(data.users);
        }
    });
  }

  function selectUser(event,ui) {
    $("input.contactName").val(ui.item.name);
    $("input.contactEmail").val(ui.item.email);
    return false;
  }

  function renderItem(ul,item) {
    return $("<li>").append("<div>" + escapeHtml(item.composedName) + "</div>").appendTo(ul);
  }

  // Auto-complete
  $("input.contactName").autocomplete(autocompleteOptions).autocomplete("instance")._renderItem = renderItem;
  $("input.contactEmail").autocomplete(autocompleteOptions).autocomplete("instance")._renderItem = renderItem;
}
