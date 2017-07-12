var caseStudiesName;
var $elementsBlock;

$(document).ready(init);

function init() {
  // Set initial variables
  $elementsBlock = $('#caseStudiesBlock');
  caseStudiesName = $('#caseStudiesName').val();

  // Add events for project next users section
  attachEvents();

  // Add select2
  addSelect2();

}

function attachEvents() {
  /**
   * Upload files functions
   */
  $('.fileUpload .remove').on('click', function(e) {
    var context = $(this).attr('id').split('-')[1];
    var $parent = $(this).parent();
    var $inputFile = $('[id$=' + context + '-template]').clone(true).removeAttr("id");
    console.log($inputFile);
    $parent.empty().append($inputFile);
    $inputFile.hide().fadeIn('slow');
    setElementsIndexes();
  });

  $(".projects").on("change", function() {
    var option = $(this).find("option:selected");
    if(option.val() != "-1") {
      addProject(option);
    }
    // Remove option from select
    option.remove();
    $(this).trigger("change.select2");
  });

  $(".removeProject").on("click", removeProject);
}

function addProject(option) {
  var canAdd = true;
  console.log(option.val());
  if(option.val() == "-1") {
    canAdd = false;
  }

  var $list = $(option).parents(".select").parents("#myProjectsList").find(".list");
  var $item = $("#sharedProject-template").clone(true).removeAttr("id");
  var v = $(option).text().length > 80 ? $(option).text().substr(0, 80) + ' ... ' : $(option).text();

  // Check if is already selected
  $list.find('.sharedProject').each(function(i,e) {
    if($(e).find('input.projectId').val() == option.val()) {
      canAdd = false;
      return;
    }
  });
  if(!canAdd) {
    return;
  }

  // Set funding source parameters
  $item.find(".name").attr("title", $(option).text());
  $item.find(".name").html(v);
  $item.find(".projectId").val(option.val());
  $item.find(".id").val(-1);
  $list.append($item);
  $item.show('slow');
  updateProjects($list);
  checkProjects($list);

  // Reset select
  $(option).val("-1");
  $(option).trigger('change.select2');

}

function updateProjects($list) {
  $($list).find('.sharedProject').each(function(i,e) {
    // Set funding sources indexes
    $(e).setNameIndexes(1, i);
  });
}

function removeProject() {
  var $list = $(this).parents('.list');
  var $item = $(this).parents('.sharedProject');
  var value = $item.find(".projectId").val();
  var name = $item.find(".name").attr("title");
  console.log(name + "-" + value);
  var $select = $(".projects");
  $item.hide(1000, function() {
    $item.remove();
    checkProjects($list);
    updateProjects($list);
  });
  // Add funding source option again
  $select.addOption(value, name);
  $select.trigger("change.select2");
}

function checkProjects(block) {
  console.log(block);
  var items = $(block).find('.sharedProject').length;
  console.log(items);
  if(items == 0) {
    $(block).parent().find('p.emptyText').fadeIn();
  } else {
    $(block).parent().find('p.emptyText').fadeOut();
  }
}

function addSelect2() {
  $('form select').select2();
}
