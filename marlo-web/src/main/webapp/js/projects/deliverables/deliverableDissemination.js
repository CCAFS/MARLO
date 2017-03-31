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
  /* Init Select2 plugin */

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

    if(!valueSelected) {
      $(".licenseOptions-block").hide("slow");
    } else {
      $(".licenseOptions-block").show("slow");
    }
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

  // Type a dissemination channel url
  $('input.deliverableDisseminationUrl, input.otherLicense').on("change", function() {
    checkFAIRCompliant();
  })

  // Check handle and doi urls
  $(".handleMetadata").on("change keyup", checkHandleUrl);
  $(".doiMetadata").on("change keyup", checkDoiUrl);

  // Ohter license type
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

  // remove flagship
  $(".removeFlagship ").on("click", removeFlagship);

  if(editable) {
    $('.lastName').dblclick(function() {
      var spantext = $(this).text();
      $(this).empty().html('<input type="text" value="' + spantext + '">').find('input').focus();
    }).keypress(function(e) {
      if(e.keyCode == 13) {
        var text = $('input', this).val();
        if(text == "") {
          text = "Last Name";
        } else {
          $(this).parents(".author").find(".lastNameInput").val(text);
        }
        $(this).html(text);
      }
    });
    $('.firstName').dblclick(function() {
      var spantext = $(this).text();
      $(this).empty().html('<input type="text" value="' + spantext + '">').find('input').focus();
    }).keypress(function(e) {
      if(e.keyCode == 13) {
        var text = $('input', this).val();
        if(text == "") {
          text = "First Name";
        } else {
          $(this).parents(".author").find(".firstNameInput").val(text);
        }
        $(this).html(text);
      }
    });
    $('.orcidId').dblclick(function() {
      var spantext = $(this).text();
      $(this).empty().html('<input type="text" value="' + spantext + '">').find('input').focus();
    }).keypress(function(e) {
      if(e.keyCode == 13) {
        var text = $('input', this).val();
        if(text == "") {
          text = "orcid Id";
        } else {
          $(this).parents(".author").find(".orcidIdInput").val(text);
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

function setMetadata(data) {
  // $("a[href='#deliverable-mainInformation']").addClass("hideInfo");
  if($(".citationMetadata").val() == "") {
    $(".citationMetadata").val(data.citation).autoGrow();
  }
  if($("#deliverableMetadataDate").val() == "") {
    $("#deliverableMetadataDate").datepicker({
      dateFormat: 'yyyy-mm-dd'
    }).datepicker('setDate', data.publicationDate);
  }

  if($(".languageMetadata").val() == "") {
    $(".languageMetadata").val(data.languaje);
  }

  if($(".titleMetadata").val() == "") {
    $(".titleMetadata").val(data.title);
  }

  if($(".descriptionMetadata").val() == "") {
    $(".descriptionMetadata").val(data.description).autoGrow();
  }

  if($(".HandleMetadata").val() == "") {
    $(".HandleMetadata").val(data.handle);
  }
  if($(".DOIMetadata").val() == "") {
    $(".DOIMetadata").val(data.doi);
  }
  if($(".countryMetadata").val() == "") {
    $(".countryMetadata").val(data.country);
  }

  if($(".keywordsMetadata ").val() == "") {
    $(".keywordsMetadata ").val(data.keywords);
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

function addAuthor() {
  if($(".lName").val() != "" && $(".fName").val() != "") {
    $(".lName").removeClass("fieldError");
    $(".fName").removeClass("fieldError");
    $(".oId").removeClass("fieldError");
    var $list = $('.authorsList');
    var $item = $('#author-template').clone(true).removeAttr("id");
    $item.find(".lastName").html($(".lName").val() + ", ");
    $item.find(".firstName").html($(".fName").val());
    if($(".oId").val() == "") {
      $item.find(".orcidId").html("");
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
 * Load Metadata and fill fields
 */
function loadAndFillMetadata() {
  var channel = $(".disseminationChannel").val();
  var url = $.trim($(".deliverableDisseminationUrl").val());
  // jsUri Library (https://github.com/derek-watson/jsUri)
  var uri = new Uri(url);

  // Validate URL
  if(url == "") {
    return;
  }

  if(channel == "cgspace") {
    // Get CGSpace Metadata from MARLO server
    getCGSpaceMetadata(channel, url, uri);
  } else if(channel == "dataverse") {
    // Get Dataverse Metadata from native API
    getDataverseMetadata(channel, url, uri);
  } else if(channel == "ifpri") {
    // Get IFPRI E-BRARY Metadata from MARLO server
    getIfpriMetadata(channel, url, uri);
  } else if(channel == "ilri") {
    // Get IFPRI E-BRARY Metadata from MARLO server
    getIlriMetadata(channel, url, uri);
  }

}

function getIlriMetadata(channel,url,uri) {
  var pathArray = uri.path().split('/');

  var data = {
      pageID: channel,
      metadataID: pathArray[pathArray.indexOf("dataset") + 1]
  }

  // get data from url
  // Ajax to service
  $.ajax({
      'url': baseURL + '/metadataByLink.do',
      'type': "GET",
      'data': data,
      beforeSend: function() {
        $(".deliverableDisseminationUrl").addClass('input-loading');
        $('#metadata-output').html("Searching ... " + data.metadataID);
      },
      success: function(m) {

        m.metadata = JSON.parse(m.metadata);

        console.log(m.metadata);

        if(jQuery.isEmptyObject(m.metadata)) {
          $('#metadata-output').html("Metadata empty");
        } else {
          var result = m.metadata.result;

          // Setting Metadata
          setMetadata({
              title: result.title,
              description: result.notes,
              citation: result.ILRI_actycitation,
              publicationDate: ilriDate(result.ILRI_actydatavailable),
              languaje: '',
              keywords: function() {
                var output = [];
                $.each(result.tags, function(i,element) {
                  output.push(element.display_name);
                })
                return output.join(', ');
              },
              handle: '',
              country: result.ILRI_actycountries.join(', '),
              doi: ''
          });

          function ilriDate(date) {
            var arrayDate = (date).split('/');
            return arrayDate[2] + "-" + arrayDate[1] + "-" + arrayDate[0];
          }

          // Getting authors
          var authors = [];
          var authorsMetadata = result.ILRI_actystaff.split(',');
          $.each(authorsMetadata, function(i,element) {
            var elementArray = $.trim(element).split(' ');

            authors.push({
                lastName: elementArray[1],
                firstName: elementArray[0],
                orcidId: ''
            });
          });
          // Set Authors
          authorsByService(authors);

          $('#metadata-output').empty().append("Found metadata for " + data.metadataID);
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

function getIfpriMetadata(channel,url,uri) {

  // https://www.oclc.org/support/services/contentdm/help/customizing-website-help/other-customizations/contentdm-api-reference.en.html

  var pathArray = uri.path().split('/');
  var itemInfo = {
      collection: pathArray[pathArray.indexOf("collection") + 1],
      id: pathArray[pathArray.indexOf("id") + 1]
  }

  var data = {
      pageID: "ifpri",
      metadataID: "dmGetItemInfo/" + itemInfo.collection + "/" + itemInfo.id + "/json"
  }

  // get data from url
  // Ajax to service
  $.ajax({
      'url': baseURL + '/metadataByLink.do',
      'type': "GET",
      'data': data,
      beforeSend: function() {
        $(".deliverableDisseminationUrl").addClass('input-loading');
        $('#metadata-output').html("Searching ... " + data.metadataID);
      },
      success: function(m) {

        m.metadata = JSON.parse(m.metadata);
        console.log(m.metadata);

        if(jQuery.isEmptyObject(m.metadata)) {
          $('#metadata-output').html("Metadata empty");
        } else {

          // Setting Metadata
          setMetadata({
              title: validateKeyObject(m.metadata.title),
              description: validateKeyObject(m.metadata.descri),
              citation: validateKeyObject(m.metadata.full),
              publicationDate: validateKeyObject(m.metadata.date) + "-01-01",
              languaje: validateKeyObject(m.metadata.langua),
              keywords: validateKeyObject(m.metadata.loc),
              handle: '',
              country: validateKeyObject(m.metadata.contri),
              doi: validateKeyObject(m.metadata.doi)
          });

          function validateKeyObject(Obj) {
            if(typeof Obj === 'object') {
              if(jQuery.isEmptyObject(Obj)) {
                return "";
              }
            } else {
              return Obj;
            }
            return "";
          }

          // Getting authors
          var authors = [];
          var authorsMetadata = m.metadata.orcid.split(';');
          $.each(authorsMetadata, function(i,element) {
            var elementArray = $.trim(element).split(' ');
            var orcid = elementArray[0]
            elementArray.shift();
            var name = elementArray.join('');

            authors.push({
                lastName: name.split(',')[0],
                firstName: name.split(',')[1],
                orcidId: orcid
            });
          });

          // Set Authors
          authorsByService(authors);

          $('#metadata-output').empty().append("Found metadata for " + data.metadataID);
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
        console.log(m);

        if(m.errorMessage) {
          $('#metadata-output').html(data.errorMessage);
        } else {
          m.metadata = JSON.parse(m.metadata);
          if(jQuery.isEmptyObject(m.metadata)) {
            $('#metadata-output').html("Metadata empty");
          } else {
            var fields = [];
            $.each(m.metadata, function(key,value) {
              // console.log(key + "-" + value);
              fields.push(key.charAt(0).toUpperCase() + key.slice(1));
            });

            var sendDataJson = {};
            sendDataJson.title = m.metadata['title'];
            sendDataJson.citation = m.metadata['identifier.citation'];
            var date = m.metadata['date.available'].split("T");
            sendDataJson.publicationDate = date[0];
            sendDataJson.languaje = m.metadata['language.iso'];
            sendDataJson.description = m.metadata['description.abstract'];
            sendDataJson.handle = m.metadata['identifier.uri'];
            sendDataJson.doi = m.metadata['identifier.doi'];
            sendDataJson.country = m.metadata['coverage.country'];
            sendDataJson.keywords = m.metadata['subject'];
            setMetadata(sendDataJson);

            // Getting authors
            var authors = [];
            $.each(m.metadata['contributor.author'], function(i,element) {
              authors.push({
                  lastName: (element).split(',')[0],
                  firstName: (element).split(',')[1]
              });
            });

            // Set Authors
            authorsByService(authors);

            // Open Acces Validation
            var $input = $(".accessible ").parent().find('input');
            if(m.metadata['identifier.status'] == "Open Access") {
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

            $('#metadata-output').empty().append("Found metadata for " + data.metadataID);
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

          console.log(m.data);

          // Setting Metadata
          setMetadata({
              title: m.data.title,
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
              keywords: function() {
                var output = [];
                $.each(m.data.metadata_blocks.citation.keyword, function(i,element) {
                  output.push(element.keywordValue);
                })
                return output.join(', ');
              },
              handle: '',
              doi: data.persistentId
          });

          // Getting authors
          var authors = [];
          $.each(m.data.metadata_blocks.citation.author, function(i,element) {
            authors.push({
                lastName: (element.authorName).split(',')[0],
                firstName: (element.authorName).split(',')[1],
                orcidId: element.authorIdentifier
            });
          });

          // Set Authors
          authorsByService(authors);

          $('#metadata-output').empty().append("Found metadata for " + data.persistentId);

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

function authorsByService(authors) {
  var $list = $('.authorsList');
  for(var i = 0; i < authors.length; i++) {
    var validation = validateAuthors(authors[i].lastName, authors[i].firstName);
    if(validation == false) {
      var $item = $('#author-template').clone(true).removeAttr("id");
      $($item).find(".lastName").text(authors[i].lastName + ", ");
      $($item).find(".firstName").text(authors[i].firstName);
      $($item).find(".orcidId").text(authors[i].orcidId);
      $($item).find(".lastNameInput").val(authors[i].lastName + ", ");
      $($item).find(".firstNameInput").val(authors[i].firstName);
      $($item).find(".orcidIdInput").val(authors[i].orcidId);
      $list.append($item);
      $item.show('slow');
      updateAuthor();
      checkNextAuthorItems($list);
    }
  }
}

function validateAuthors(lastName,firstName) {
  if($(".authorsList").find('.author input.lastNameInput[value="' + lastName + '"]').exists()
      || $(".authorsList").find(".author input.firstNameInput[value='" + firstName + "']").exists()) {
    return true;
  } else {
    return false;
  }

}
