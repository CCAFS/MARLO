$(document).ready(function() {
  attachAccordionEvents();
  attachAddGlobalUnitEvent();
  attachRemoveElementEvent();
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
