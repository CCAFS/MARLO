$(document).ready(init);

function init() {
  /* Init Select2 plugin */
  $('form select').select2({
    width: '100%'
  });
  $('.outcome').select2({
      templateResult: formatState,
      templateSelection: formatState,
      width: '100%'
  });

  // select name
  $(".outcome").attr("name", "deliverable.crpProgramOutcome.id");
  $(".keyOutput").attr("name", "deliverable.crpClusterKeyOutput.id");
  /* Events select */
  subTypes();
  keyOutputs();

  justificationByStatus($(".status").find("option:selected").val());
  validateCurrentDate();

  $(".addPartner").on("click", addPartnerEvent);
  $(".removeElement").on("click", removePartnerEvent);
  // Update value of responsible person
  $(".responsible").on(
      "change",
      function() {
        var option = $(this).find("option:selected");
        // validate if exists this person in contact person list
        var validation =
            $(this).parents(".fullBlock").parent().find(".personList").find("input[value=" + option.val() + "]");
        if(option.val() != "-1") {
          if(validation.exists()) {
            // Remove from contact person list
            validation.parent().hide("slow", function() {
              $(this).remove();
              updatePartners();
            })
            // Show message
            var text = option.html() + ' was removed from contact persons list';
            notify(text);
            $(this).parents(".responsiblePartner").find(".id").val(option.val());
          } else {
            $(this).parents(".responsiblePartner").find(".id").val(option.val());
          }
        } else {
          $(this).parents(".responsiblePartner").find(".id").val(-1);
        }

      });
  // Update value of partner
  $(".partner").on(
      "change",
      function() {
        var option = $(this).find("option:selected");
        // validate if exists this person in contact person list
        var validation =
            $(this).parents(".partnerWrapper").find(".responsibleWrapper").find("input[value=" + option.val() + "]");
        if(validation.exists()) {
          option.parent().val(-1);
          option.parent().trigger("change.select2");
          $(this).parents(".deliverablePartner").find(".id").val(-1);
          // Show message
          var text = option.html() + ' is the responsible person of this deliverable';
          notify(text);
        } else {
          $(this).parents(".deliverablePartner").find(".id").val(option.val());
        }
      });

  // CHANGE STATUS
  $(".status").on("change", function() {
    var option = $(this).find("option:selected");
    justificationByStatus(option.val());
  });

  $(".yearExpected").on("change", validateCurrentDate);

  if(!reportingActive) {
    var statusSelect = $("form .status");
    statusSelect.find("option").each(function(i,e) {
      e.remove();
    });
    $(statusSelect).append("<option value='-1'>Select an option...</option><option value='2'>On-going</option>");
  }
}

function validateCurrentDate() {
  if(reportingActive) {
    var yearSelect = $(".yearExpected");
    var statusSelect = $("form .status");
    var option = $(yearSelect).find("option:selected");
    if(option.val() <= currentCycleYear) {
      console.log("holi");
      if($(statusSelect).val() === "2") {
        console.log("holi");
        statusSelect.val("-1");
        statusSelect.trigger("change.select2");
        justificationByStatus("-1");
      }
      statusSelect.find("option[value='2']").remove();

    } else {
      $(statusSelect).find("option[value='2']").remove();
      $(statusSelect).append("<option value='2'>On-going</option>")
    }
  }
}

function justificationByStatus(optionValue) {
  var justification = $(".justificationContent");
  if(optionValue == "-1" || optionValue == "3") {
    justification.hide("slow");
  }
  if(optionValue == "2") {
    justification.find("label").html(
        "Describe overall deliverable or progress made during this reporting cycle:" + "<span class='red'>*</span>");
    justification.show("slow");
  }
  if(optionValue == "4") {
    justification.find("label").html(
        "Please provide a justification and new expected date of completion:" + "<span class='red'>*</span>");
    justification.show("slow");
  }
  if(optionValue == "5") {
    justification.find("label").html(
        "Please provide a justification for cancelling the deliverable:" + "<span class='red'>*</span>");
    justification.show("slow");
  }
}

// Add a new person element
function addPartnerEvent() {
  var $list = $(".personList");
  var $item = $("#deliverablePartner-template").clone(true).removeAttr("id");
  $list.append($item);
  $item.find('select').select2({
    width: "100%"
  });
  $item.show('slow');
  checkItems($list);
  updatePartners();
}

// Remove person element
function removePartnerEvent() {
  var $list = $(this).parents('.personList');
  var $item = $(this).parents('.deliverablePartner');
  $item.hide(1000, function() {
    $item.remove();
    checkItems($list);
    updatePartners();
  });

}

function updatePartners() {
  var name = "deliverable.otherPartners";
  $(".personList").find('.deliverablePartner').each(function(i,item) {

    var customName = name + '[' + i + ']';
    $(item).find('span.index').html(i + 1);
    $(item).find('.id').attr('name', customName + '.projectPartnerPerson.id');
    $(item).find('.type').attr('name', customName + '.projectPartnerPerson.type');
    $(item).find('.element').attr('name', customName + '.id');
  });
}

function subTypes() {
  var url = baseURL + "/deliverableSubType.do";
  var typeSelect = $(".typeSelect");
  var subTypeSelect = $(".subTypeSelect");
  typeSelect.on("change", function() {

    subTypeSelect.empty();
    subTypeSelect.append("<option value='-1' >Select an option... </option>");
    subTypeSelect.trigger("change.select2");
    var option = $(this).find("option:selected");

    if(option.val() != "-1") {
      var data = {
        deliverableTypeId: option.val()
      }
      $.ajax({
          url: url,
          type: 'GET',
          dataType: "json",
          data: data
      }).success(
          function(m) {
            for(var i = 0; i < m.deliverableSubTypes.length; i++) {
              subTypeSelect.append("<option value='" + m.deliverableSubTypes[i].id + "' >"
                  + m.deliverableSubTypes[i].name + "</option>");
            }
          });
    }
  });
}

function keyOutputs() {
  var url = baseURL + "/keyOutputList.do";
  var outcomeSelect = $(".outcome");
  var keyOutputSelect = $(".keyOutput");
  outcomeSelect.on("change", function() {

    keyOutputSelect.empty();
    keyOutputSelect.append("<option value='-1' >Select an option... </option>");
    keyOutputSelect.trigger("change.select2");
    var option = $(this).find("option:selected");
    var data = {
      clusterActivityID: option.val()
    }
    $.ajax({
        url: url,
        type: 'GET',
        dataType: "json",
        data: data
    }).success(
        function(m) {
          console.log(m);
          for(var i = 0; i < m.keyOutputs.length; i++) {
            keyOutputSelect.append("<option value='" + m.keyOutputs[i].id + "' >" + m.keyOutputs[i].description
                + "</option>");
          }
        });
  });
}

function checkItems(block) {
  console.log(block);
  var items = $(block).find('.deliverablePartner').length;
  if(items == 0) {
    $(block).parent().find('p.emptyText').fadeIn();
  } else {
    $(block).parent().find('p.emptyText').fadeOut();
  }
}

function notify(text) {
  var notyOptions = jQuery.extend({}, notyDefaultOptions);
  notyOptions.text = text;
  notyOptions.type = 'alert';
  noty(notyOptions);
}

function formatState(state) {
  console.log(state.text);
  if(state.id != "-1") {
    var text = state.text.split(/:(.+)?/);
    var $state = $("<span><strong>" + text[0] + ":</strong> " + text[1] + "</span>");
    return $state;
  } else {
    var $state = $("<span>" + state.text + "</span>");
    return $state;
  }

};