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
    const unitLabel = ($item.find(".blockTitle").text() || "").trim();

    // Store the item to be removed so we can access it in the modal button handler
    $("#confirm-delete-modal").data("itemToRemove", $item).data("unitLabel", unitLabel);

    // Update modal with unit label
    $("#delete-unit-label").text(unitLabel || "This Global Unit");

    // Show the modal
    $("#confirm-delete-modal").modal("show");
  });

  // Handle the confirm delete button in the modal
  $("#confirm-delete-btn").on("click", function() {
    const $modal = $("#confirm-delete-modal");
    const $item = $modal.data("itemToRemove");

    if ($item?.length) {
      $modal.modal("hide");
      $item.hide("slow", function() {
        $item.remove();
        updateIndexes();
      });
    }
  });
}

function attachLogoUploadEvents() {
  // Initialize fileupload on any existing .logo-file-input elements on page load
  $(".logo-file-input").each(function() {
    initLogoFileUpload($(this));
  });

  $(document).on("click", ".logo-file-browse-btn", function() {
    const $dropZone = $(this).closest(".logo-drop-zone");
    $dropZone.find(".logo-file-input").trigger("click");
  });

  $(document).on("click", ".logo-drop-zone", function(event) {
    const clickedBrowseButton = $(event.target).closest(".logo-file-browse-btn").length > 0;
    const clickedInput = $(event.target).closest(".logo-file-input").length > 0;
    if (!clickedBrowseButton && !clickedInput) {
      $(this).find(".logo-file-input").trigger("click");
    }
  });
}

function initLogoFileUpload($fileInput) {
  if (typeof $fileInput.fileupload !== "function") {
    const $gu = $fileInput.closest(".globalUnit");
    $gu.find(".logo-upload-status").html("<span style='color:red'>Upload library is not loaded on this page.</span>");
    return;
  }

  const $dropZone = $fileInput.closest(".logo-drop-zone");
  const $selectedFileLabel = $dropZone.find(".logo-selected-file");

  $fileInput.fileupload({
    dataType: "json",
    dropZone: $dropZone,
    add: function(e, data) {
      const $gu = $(e.target).closest(".globalUnit");
      const selectedFile = data.files?.length ? data.files[0] : null;
      const fileName = selectedFile?.name || "";
      const mimeType = (selectedFile?.type || "").toLowerCase();
      const isPngByMime = mimeType === "image/png";
      const isPngByExt = /\.png$/i.test(fileName);

      if (!selectedFile || (!isPngByMime && !isPngByExt)) {
        $gu.find(".logo-upload-status").html("<span style='color:orange'>Only PNG files are allowed.</span>");
        $selectedFileLabel.text("No file selected.");
        $dropZone.removeClass("is-dragover");
        return;
      }

      $selectedFileLabel.text("Selected file: " + fileName);

      // Show a local preview immediately and keep it while upload completes.
      const $previewBlock = $gu.find(".logo-preview-block");
      const reader = new FileReader();
      reader.onload = function(loadEvent) {
        const previewSrc = loadEvent?.target?.result;
        if (!previewSrc) {
          return;
        }
        $dropZone.data("localPreviewSrc", previewSrc);
        $previewBlock.find(".logo-preview-img").attr("src", previewSrc);
        if (!$previewBlock.find(".help-block").length) {
          $previewBlock.prepend("<small class='help-block'></small>");
        }
        $previewBlock.find(".help-block").html("Preview: <strong>" + fileName + "</strong>.");
        $previewBlock.show();
      };
      reader.readAsDataURL(selectedFile);

      data.submit();
    },
    start: function(e) {
      const $gu = $(e.target).closest(".globalUnit");
      $gu.find(".logo-upload-status").html("<em>Uploading...</em>");
      $selectedFileLabel.text("Uploading...");
    },
    stop: function(e) {
      const $gu = $(e.target).closest(".globalUnit");
      $gu.find(".logo-upload-status").html("");
      $dropZone.removeClass("is-dragover");
    },
    done: function(e, data) {
      const r = data.result;
      const $gu = $(e.target).closest(".globalUnit");
      $gu.find(".logo-upload-status").html("");
      $dropZone.removeClass("is-dragover");
      if (r?.saved) {
        const logoSrc = r.logoUrl + "?t=" + Date.now();
        const uploadedName = (r.acronym || "logo") + ".png";
        const $previewBlock = $gu.find(".logo-preview-block");
        const localPreviewSrc = $dropZone.data("localPreviewSrc");
        if (localPreviewSrc) {
          $previewBlock.find(".logo-preview-img").attr("src", localPreviewSrc).attr("alt", r.acronym + " logo");
        } else {
          $previewBlock.find(".logo-preview-img").attr("src", logoSrc).attr("alt", r.acronym + " logo");
        }
        $previewBlock.find(".help-block").html("Logo: <strong>" + r.acronym + "</strong>.");
        $previewBlock.show();
        $selectedFileLabel.text("Uploaded: " + uploadedName);
        $dropZone.removeData("localPreviewSrc");
      } else {
        const errorMessage = r?.message || "Upload failed. Check the acronym is set and the file is a valid image.";
        $gu.find(".logo-upload-status").html("<span style='color:red'>" + errorMessage + "</span>");
        $selectedFileLabel.text("No file selected.");
        $dropZone.removeData("localPreviewSrc");
      }
    },
    fail: function(e) {
      const $gu = $(e.target).closest(".globalUnit");
      $gu.find(".logo-upload-status").html("<span style='color:red'>Upload error.</span>");
      $selectedFileLabel.text("No file selected.");
      $dropZone.removeClass("is-dragover");
    }
  });

  $dropZone.on("dragenter dragover", function() {
    $dropZone.addClass("is-dragover");
  });

  $dropZone.on("dragleave drop", function() {
    $dropZone.removeClass("is-dragover");
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
