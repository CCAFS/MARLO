$(document).ready(function() {
  attachAccordionEvents();
  attachAddGlobalUnitEvent();
  attachRemoveElementEvent();
  attachLogoUploadMappingEvents();
  initInstitutionSelect2($(".globalUnits-list"));
});

function attachAccordionEvents() {
  $(document).on("click", ".blockTitle", function() {
    const $title = $(this);
    if ($title.hasClass("closed")) {
      $(".blockContent").slideUp();
      $(".blockTitle").removeClass("opened").addClass("closed");
      $title.removeClass("closed").addClass("opened");
    } else {
      $title.removeClass("opened").addClass("closed");
    }
    $title.next().slideToggle("slow");
  });
}

function attachAddGlobalUnitEvent() {
  $(document).on("click", ".addGlobalUnit", function() {
    const $itemsList = $(this).siblings(".globalUnits-list");
    const $item = $("#globalUnit-template").clone(true).removeAttr("id");
    $itemsList.append($item);
    $item.slideDown("slow");
    updateIndexes();
    initInstitutionSelect2($item);
    $item.find(".blockTitle").trigger("click");
  });
}

function attachRemoveElementEvent() {
  $(document).on("click", ".remove-element", function() {
    const $item = $(this).closest(".globalUnit");
    $item.hide("slow", function() {
      $item.remove();
      updateIndexes();
    });
  });
}

function attachLogoUploadMappingEvents() {
  $(document).on("change", ".logo-file-input", function() {
    const $fileInput = $(this);
    syncLogoAcronymSlot($fileInput.closest(".globalUnit"));
  });

  $(document).on("input change", ".acronym-input", function() {
    const $acronymInput = $(this);
    syncLogoAcronymSlot($acronymInput.closest(".globalUnit"));
  });
}

function syncLogoAcronymSlot($globalUnit) {
  if (!$globalUnit?.length) {
    return;
  }

  const $fileInput = $globalUnit.find(".logo-file-input").first();
  const $slotInput = $globalUnit.find(".logo-files-acronym-slot").first();
  const $acronymInput = $globalUnit.find(".acronym-input").first();

  if (!$fileInput.length || !$slotInput.length || !$acronymInput.length) {
    return;
  }

  const hasSelectedFile = ($fileInput[0].files && $fileInput[0].files.length > 0)
    || ($fileInput.val() && $fileInput.val().length > 0);
  const acronymValue = (($acronymInput.val() || "").trim()).toUpperCase();

  if (hasSelectedFile && acronymValue.length > 0) {
    $slotInput.val(acronymValue);
    $slotInput.prop("disabled", false);
  } else {
    $slotInput.val("");
    $slotInput.prop("disabled", true);
  }
}

function updateIndexes() {
  $(".globalUnits-list .globalUnit").each(function(i, block) {
    $(block).setNameIndexes(1, i);

    const $title = $(block).find(".blockTitle strong");
    if ($title.length) {
      $title.text("Global Unit " + (i + 1) + ": ");
    }
  });
}

function initInstitutionSelect2($container) {
  const $scope = $container || $(".globalUnits-list");
  const $selects = $scope.find(".institution-select");

  $selects.each(function() {
    const $select = $(this);
    if ($select.hasClass("select2-hidden-accessible")) {
      return;
    }

    $select.select2({
      placeholder: "Search or select institution...",
      allowClear: true,
      width: "100%"
    });
  });
}
