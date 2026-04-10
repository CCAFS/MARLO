$(document).ready(function() {
  attachAccordionEvents();
  attachAddGlobalUnitEvent();
  attachRemoveElementEvent();
  attachLogoUploadEvents();
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
    initLogoFileUpload($item.find(".logo-file-input"));
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

function attachLogoUploadEvents() {
  // Initialize fileupload on any existing .logo-file-input elements on page load
  $(".logo-file-input").each(function() {
    initLogoFileUpload($(this));
  });
}

function initLogoFileUpload($fileInput) {
  if (typeof $fileInput.fileupload !== "function") {
    const $gu = $fileInput.closest(".globalUnit");
    $gu.find(".logo-upload-status").html("<span style='color:red'>Upload library is not loaded on this page.</span>");
    return;
  }

  $fileInput.fileupload({
    dataType: "json",
    start: function(e) {
      const $gu = $(e.target).closest(".globalUnit");
      $gu.find(".logo-upload-status").html("<em>Uploading...</em>");
    },
    stop: function(e) {
      const $gu = $(e.target).closest(".globalUnit");
      $gu.find(".logo-upload-status").html("");
    },
    done: function(e, data) {
      const r = data.result;
      const $gu = $(e.target).closest(".globalUnit");
      $gu.find(".logo-upload-status").html("");
      if (r?.saved) {
        const logoSrc = r.logoUrl + "?t=" + Date.now();
        const $previewBlock = $gu.find(".logo-preview-block");
        $previewBlock.find(".logo-preview-img").attr("src", logoSrc).attr("alt", r.acronym + " logo");
        $previewBlock.find(".help-block").html("Logo: <strong>" + r.acronym + "</strong>.");
        $previewBlock.show();
      } else {
        const errorMessage = r?.message || "Upload failed. Check the acronym is set and the file is a valid image.";
        $gu.find(".logo-upload-status").html("<span style='color:red'>" + errorMessage + "</span>");
      }
    },
    fail: function(e) {
      const $gu = $(e.target).closest(".globalUnit");
      $gu.find(".logo-upload-status").html("<span style='color:red'>Upload error.</span>");
    }
  });

  $fileInput.bind("fileuploadsubmit", function(e, data) {
    const $gu = $(e.target).closest(".globalUnit");
    const acronymValue = ($gu.find(".acronym-input").val() || "").trim().toUpperCase();
    if (!acronymValue) {
      $gu.find(".logo-upload-status").html("<span style='color:orange'>Set the acronym before uploading a logo.</span>");
      return false;
    }
    data.formData = { acronym: acronymValue };
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
