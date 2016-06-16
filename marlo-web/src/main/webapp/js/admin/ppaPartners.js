$(document).ready(init);
var partnerSelect, partnerContent;

function init() {
  partnerSelect = $("#ccafs_ppaPartners_");
  partnerSelect.attr("data-live-search", "true")
  partnerContent = $("#partnerContent");
  /* Declaring Events */
  attachEvents();
}

function attachEvents() {
  differences();
  updateIndex();
  removePartner();
  var partners = "";
  // Getting event of Select
  partnerSelect.on('changed.bs.select', function() {
    partners = partnerSelect.find(":selected");
    if(partners[0].innerText != "" && partners[0].innerText != null) {
      addPartner(partners, partnerContent);
      differences(partnerContent, partnerSelect);
    }
  });

}

// Add partner item
function addPartner(partners) {
  var $item = $('#institution-template').clone(true).removeAttr('id');
  $item.find('input.id').val(partners.val());
  $item.find('.title').html(partners.text());
  partnerContent.append($item);
  $item.show("slow");
  updateIndex();
}

// Remove partner item
function removePartner() {
  $(".delete").on('click', function() {
    var institution = $(this).parents(".institution");
    $("#partnerSelect option[id='" + institution[0].children[1].innerText + "'] ").attr("disabled", false);
    institution.hide(1000, function() {
      institution.remove();
      updateIndex();
      differences();
    });
  });
}

/*
 * This function creates 2 arrays, these are the list of institutions of select and container. Then the array's
 * positions are compared
 */
function differences() {
  var institutionsContent = [];
  var institutionSelect = [];
  for(var int = 0; int < partnerContent[0].children.length; int++) {
    institutionsContent.push(partnerContent[0].children[int].children[1].innerText);
  }
  for(var int2 = 1; int2 < partnerSelect[0].length; int2++) {
    institutionSelect.push(partnerSelect[0][int2].innerText);
  }
  for(var int3 = 0; int3 < institutionsContent.length; int3++) {
    for(var int4 = 0; int4 < institutionSelect.length; int4++) {
      if(institutionsContent[int3] == institutionSelect[int4]) {
        console.log($("#partnerSelect").find('option:contains("' + institutionSelect[int4] + '")'));
        // $("#partnerSelect option[id='" + institutionSelect[int4] + "'] ").attr('disabled', true);
      } else {
      }
    }
  }
  $('#partnerSelect').selectpicker('refresh');
}

// Update index and position of property name
function updateIndex() {
  $(partnerContent).find('.institution').each(function(i,item) {
    $(this)[0].children[3].name = "institutions[" + i + "]";
    $(this)[0].children[0].innerText = i + 1;
    console.log($(this)[0].children[3].name);
  });
}