$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();
  
  addSelect2();
  applySelect2Filters();
  
}

function attachEvents() {

  $('#feedbackPermissionFilter').on('change', function () {
    const selectedId = $(this).val();
    $('.srfSlo').each(function () {
      const permissionId = $(this).find('.feedbackPermission').val();
      if (!selectedId || selectedId === permissionId) {
        $(this).show();
      } else {
        $(this).hide();
      }
    });
  });
  
  $('#clearFeedbackPermissionFilter').on('click', function () {
    $('#feedbackPermissionFilter').val(null).trigger('change');
  });
  
  const newEntry = document.querySelector(".new-entry");
    if (newEntry) {
      newEntry.scrollIntoView({
        behavior: "smooth",
        block: "center"
      });
    }
    
  $('.addSlo').on('click', addIdo);

  $('.addIndicator').on('click', addIndicator);

  $('.addTargets').on('click', addTargets);

  $('.addCrossCuttingIssue').on('click', addCrossCuttingIssue);

  $('.remove-element').on('click', removeElement);

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
    });
  });
  
  $('#feedbackPermissionFilter').on('change', function () {
  const selectedPermissionId = $(this).val();

    $('.srfSlo').not('.is-template').each(function () {
      const itemPermissionId = $(this).data('permission-id') + "";
      const shouldShow = !selectedPermissionId || selectedPermissionId === itemPermissionId;
      $(this).toggle(shouldShow);
    });
  });
  
}

function addIdo() {
  console.log("add ido");
  var $itemsList = $(this).parent().find('.slos-list');
  var $item = $("#srfSlo-template").clone(true).removeAttr("id");
  $item.find('.blockTitle').trigger('click');
  $itemsList.append($item);
  $item.slideDown('slow');
  updateIndexes();
  $item.trigger('addComponent');
}




function addIndicator() {
  console.log("addIndicator");
  var $itemsList = $(this).parent().parent().find('.srfIndicators-list');
  var $item = $("#srfSloIndicator-template").clone(true).removeAttr("id");

  $itemsList.append($item);
  $item.slideDown('slow');
  updateIndexes();
  $item.trigger('addComponent');
}

function addTargets() {
  console.log("addTargets");
  var $itemsList = $(this).parent().parent().find('.targetsList');
  var $item = $("#targetIndicator-template").clone(true).removeAttr("id");
  $itemsList.append($item);
  $item.show('slow');
  updateIndexes();
  $item.trigger('addComponent');
}

function addCrossCuttingIssue() {
  console.log("addCrossCuttingIssue");
  var $itemsList = $(this).parent().find('.issues-list');
  var $item = $("#srfCCIssue-template").clone(true).removeAttr("id");

  $itemsList.append($item);
  $item.slideDown('slow');
  updateIndexes();
  $item.trigger('addComponent');
}

function removeElement() {
  $item = $(this).parent();
  $item.hide('slow', function() {
    $item.remove();
    updateIndexes();
    $(document).trigger('removeComponent');
  });
}

function updateIndexes() {
  $('.slos-list .srfSlo').each(function(i,slo) {
    // Updating indexes
    $(slo).setNameIndexes(1, i);
    $(slo).find('.srfSloIndicator').each(function(subIdoIndex,subIdo) {
      // Updating indexes
      $(subIdo).setNameIndexes(2, subIdoIndex);
    });
  });

  $('.issues-list .srfCCIssue').each(function(i,crossCutting) {
    // Updating indexes
    $(crossCutting).setNameIndexes(1, i);

  });
}

function addSelect2() {
  $("form select").select2();
}

function applySelect2Filters() {
  $('.select2-filter').select2({
    theme: 'bootstrap',
    width: '100%',
    allowClear: true,
    placeholder: function () {
      return $(this).data('placeholder') || 'Type to search...';
    },
    language: {
      searching: function () {
        return "Searching...";
      },
      noResults: function () {
        return "No results found";
      }
    }
  });
}


