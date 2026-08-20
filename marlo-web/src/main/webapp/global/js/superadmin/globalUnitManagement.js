$(document).ready(function() {
  attachAccordionEvents();
  attachAddGlobalUnitEvent();
  attachRemoveElementEvent();
  attachLogoUploadEvents();
  attachCrpAdminTeamEvents();
  attachRequiredFieldsValidation();
  attachAcronymValidation();
  initInstitutionSelect2($(".globalUnits-list"));
  highlightServerValidationErrors();
  addUser = addCrpAdminUser;
  $(".globalUnits-list .crp-admin-team-block").each(function() {
    updateCrpAdminTeamIndexes($(this));
  });
});

function guMsg(id, params) {
  let text = $.trim($("#" + id).text() || "");
  if (!params || !params.length) {
    return text;
  }
  for (let i = 0; i < params.length; i++) {
    text = text.replace(new RegExp("\\{" + i + "\\}", "g"), params[i]);
  }
  return text;
}

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
    if ($item.attr("data-current-global-unit") === "true") {
      notificationError(guMsg("msg-gu-deleteCurrentSession"));
      return;
    }

    const unitLabel = ($item.find(".blockTitle").text() || "").trim();

    // Store the item to be removed so we can access it in the modal button handler
    $("#confirm-delete-modal").data("itemToRemove", $item).data("unitLabel", unitLabel);

    // Update modal with unit label
    $("#delete-unit-label").text(unitLabel || guMsg("msg-gu-thisItem"));

    // Show the modal
    $("#confirm-delete-modal").modal("show");
  });

  // Handle the confirm delete button in the modal
  $("#confirm-delete-btn").on("click", function() {
    const $modal = $("#confirm-delete-modal");
    const $item = $modal.data("itemToRemove");

    if ($item?.length) {
      if ($item.attr("data-current-global-unit") === "true") {
        $modal.modal("hide");
        notificationError(guMsg("msg-gu-deleteCurrentSession"));
        return;
      }
      $modal.modal("hide");
      registerDeletedGlobalUnitId($item);
      $item.hide("slow", function() {
        $item.remove();
        updateIndexes();
      });
    }
  });
}

/**
 * Records the database id of an existing Global Unit that the user removed, so the backend deletes only these
 * explicit ids instead of inferring deletions from missing rows. New (unsaved) Global Units have no id and are
 * simply removed from the DOM.
 */
function registerDeletedGlobalUnitId($item) {
  const globalUnitId = $.trim($item.find(".gu-id-input").val() || "");
  if (!globalUnitId) {
    return;
  }

  const $deletedField = $("#deleted-global-unit-ids");
  const deletedIds = ($deletedField.val() || "").split(",")
    .map(function(id) {
      return $.trim(id);
    })
    .filter(function(id) {
      return id.length > 0;
    });

  if (deletedIds.indexOf(globalUnitId) === -1) {
    deletedIds.push(globalUnitId);
    $deletedField.val(deletedIds.join(","));
  }
}

function attachLogoUploadEvents() {
  // Initialize fileupload on any existing .logo-file-input elements on page load
  $(".logo-file-input").each(function() {
    const $input = $(this);
    const isTemplateInput = $input.closest(".globalUnit").attr("id") === "globalUnit-template";
    if (isTemplateInput) {
      return;
    }
    initLogoFileUpload($input);
  });

  $(document).on("input", ".acronym-input", function() {
    updateLogoAcronymWarning($(this).closest(".globalUnit"));
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

function attachAcronymValidation() {
  $(document).on("input", ".acronym-input", function() {
    validateAcronymField($(this), true);
    updateLogoAcronymWarning($(this).closest(".globalUnit"));
  });

  $(document).on("change", ".acronym-input", function() {
    validateAcronymField($(this), false);
  });

  $(".acronym-input").each(function() {
    validateAcronymField($(this), false);
  });
}

function validateAcronymField($input, showMessage) {
  const rawValue = ($input.val() || "").toString();
  const sanitizedValue = rawValue.replace(/[\s-]+/g, "");
  const hadWhitespace = /\s/.test(rawValue);
  const hadDash = /-/.test(rawValue);

  if (rawValue !== sanitizedValue) {
    $input.val(sanitizedValue);
  }

  const hasValue = $.trim(sanitizedValue).length > 0;
  const isValid = hasValue;
  const $formGroup = $input.closest(".form-group");
  const $message = $formGroup.find(".acronym-validation-message");

  setRequiredFieldState($input, hasValue);

  if (showMessage && (hadWhitespace || hadDash)) {
    showTemporaryAcronymMessage($message, guMsg("msg-gu-acronymWhitespace"));
  } else if (!hadWhitespace && !hadDash) {
    $message.text("").hide();
  }

  if (!isValid) {
    $input.addClass("fieldError");
    $formGroup.addClass("has-error");
  } else {
    $input.removeClass("fieldError");
    $formGroup.removeClass("has-error");
  }

  return isValid;
}

function showTemporaryAcronymMessage($message, text) {
  const existingTimer = $message.data("acronymTimer");
  if (existingTimer) {
    clearTimeout(existingTimer);
  }

  $message.text(text).show();
  $message.data("acronymTimer", setTimeout(function() {
    $message.text("").hide();
    $message.removeData("acronymTimer");
  }, 1400));
}

function updateLogoAcronymWarning($gu) {
  const $dropZone = $gu.find(".logo-drop-zone");
  const uploadedAcronym = ($dropZone.data("uploadedAcronym") || "").trim().toUpperCase();
  const currentAcronym = ($gu.find(".acronym-input").val() || "").trim().toUpperCase();
  const $warning = $gu.find(".logo-acronym-warning");

  if (uploadedAcronym && uploadedAcronym !== currentAcronym) {
    $warning.text(guMsg("msg-gu-logoAcronymMismatch", [uploadedAcronym])).show();
    return;
  }

  $warning.text("").hide();
}

function initLogoFileUpload($fileInput) {
  if ($fileInput.data("guLogoUploadBound") === true) {
    return;
  }

  if (typeof $fileInput.fileupload !== "function") {
    const $gu = $fileInput.closest(".globalUnit");
    $gu.find(".logo-upload-status").html("<span style='color:red'>" + guMsg("msg-gu-logoLibraryMissing") + "</span>");
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
        $gu.find(".logo-upload-status").html("<span style='color:orange'>" + guMsg("msg-gu-logoOnlyPng") + "</span>");
        $selectedFileLabel.text(guMsg("msg-gu-logoNoFileSelected"));
        $dropZone.removeClass("is-dragover");
        return;
      }

      $selectedFileLabel.text(guMsg("msg-gu-logoSelectedFile", [fileName]));

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
        $previewBlock.find(".help-block").html(guMsg("msg-gu-logoPreview", ["<strong>" + fileName + "</strong>"]));
        $previewBlock.show();
      };
      reader.readAsDataURL(selectedFile);

      data.submit();
    },
    start: function(e) {
      const $gu = $(e.target).closest(".globalUnit");
      const uploadingText = guMsg("msg-gu-logoUploading");
      $gu.find(".logo-upload-status").html("<em>" + uploadingText + "</em>");
      $selectedFileLabel.text(uploadingText);
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
        $dropZone.data("uploadedAcronym", (r.acronym || "").trim().toUpperCase());
        updateLogoAcronymWarning($gu);
        const logoSrc = r.logoUrl + "?t=" + Date.now();
        const uploadedName = (r.acronym || "logo") + ".png";
        const $previewBlock = $gu.find(".logo-preview-block");
        const localPreviewSrc = $dropZone.data("localPreviewSrc");
        if (localPreviewSrc) {
          $previewBlock.find(".logo-preview-img").attr("src", localPreviewSrc).attr("alt", r.acronym + " logo");
        } else {
          $previewBlock.find(".logo-preview-img").attr("src", logoSrc).attr("alt", r.acronym + " logo");
        }
        $previewBlock.find(".help-block").html(guMsg("msg-gu-logoExisting", ["<strong>" + r.acronym + "</strong>"]));
        $previewBlock.show();
        $selectedFileLabel.text(guMsg("msg-gu-logoUploaded", [uploadedName]));
        $dropZone.removeData("localPreviewSrc");
      } else {
        const errorMessage = r?.message || guMsg("msg-gu-logoUploadFailed");
        $gu.find(".logo-upload-status").html("<span style='color:red'>" + errorMessage + "</span>");
        $selectedFileLabel.text(guMsg("msg-gu-logoNoFileSelected"));
        $dropZone.removeData("localPreviewSrc");
      }
    },
    fail: function(e) {
      const $gu = $(e.target).closest(".globalUnit");
      $gu.find(".logo-upload-status").html("<span style='color:red'>" + guMsg("msg-gu-logoUploadError") + "</span>");
      $selectedFileLabel.text(guMsg("msg-gu-logoNoFileSelected"));
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
      $gu.find(".logo-upload-status").html("<span style='color:orange'>" + guMsg("msg-gu-logoAcronymRequired") + "</span>");
      return false;
    }
    data.formData = { acronym: acronymValue };
  });

  $fileInput.data("guLogoUploadBound", true);
}

function updateIndexes() {
  $(".globalUnits-list .globalUnit").each(function(i, block) {
    $(block).setNameIndexes(1, i);
    const $teamBlock = $(block).find(".crp-admin-team-block");
    $teamBlock.attr("data-list-name", "globalUnits[" + i + "].crpAdminTeam");
    updateCrpAdminTeamIndexes($teamBlock);

    const $title = $(block).find(".blockTitle strong");
    if ($title.length) {
      $title.text(guMsg("msg-gu-itemTitle", [i + 1]) + " ");
    }
  });
}

function attachCrpAdminTeamEvents() {
  $(document).on("click", ".remove-crp-admin-user", function() {
    const $teamBlock = $(this).closest(".crp-admin-team-block");
    $(this).closest(".userItem").remove();
    updateCrpAdminTeamIndexes($teamBlock);
  });
}

function addCrpAdminUser(composedName, userId) {
  const $teamBlock = $elementSelected.closest(".crp-admin-team-block");
  const duplicate = $teamBlock.find(".userItem .user").filter(function() {
    return String($(this).val()) === String(userId);
  }).length > 0;
  if (duplicate) {
    notificationError(guMsg("msg-gu-crpAdminDuplicate"));
    dialog.dialog("close");
    return;
  }

  const $item = $("#crp-admin-user-template").clone(true).removeAttr("id");
  $item.find(".name").text(composedName);
  $item.find(".user").val(userId);
  $item.find(".id").val("");
  $teamBlock.find(".items-list ul").append($item);
  $item.show("slow");
  updateCrpAdminTeamIndexes($teamBlock);
  dialog.dialog("close");
}

function updateCrpAdminTeamIndexes($teamBlock) {
  const listName = $teamBlock.attr("data-list-name");
  $teamBlock.find(".userItem").each(function(index) {
    $(this).find(".user").attr("name", listName + "[" + index + "].user.id");
    $(this).find(".id").attr("name", listName + "[" + index + "].id");
  });
  const hasUsers = $teamBlock.find(".userItem").length > 0;
  $teamBlock.find(".usersMessage").toggle(!hasUsers);
  $teamBlock.toggleClass("fieldError", !hasUsers);
  $teamBlock.closest(".form-group").toggleClass("has-error", !hasUsers);
}

function attachRequiredFieldsValidation() {
  $(document).on("input change select2:select select2:clear select2:unselect", ".globalUnit .required", function() {
    const $field = $(this);
    setRequiredFieldState($field, $.trim($field.val() || "").length > 0);
  });

  $(".button-save").on("click", function(event) {
    const $saveButton = $(this);
    const $invalidFields = validateRequiredGlobalUnitFields();

    if (!$invalidFields.length) {
      $(".globalUnits-list .logo-file-input").prop("disabled", true);
      return;
    }

    event.preventDefault();
    event.stopImmediatePropagation();
    if (typeof closeLoadPage === "function") {
      closeLoadPage();
    }
    if (typeof turnSavingStateOff === "function") {
      turnSavingStateOff($saveButton);
    }
    openGlobalUnitWithInvalidField($invalidFields.first());
    notificationError(guMsg("msg-gu-validationRequired"));
  });
}

function validateRequiredGlobalUnitFields() {
  const $invalidFields = $();

  $(".globalUnits-list .globalUnit").each(function() {
    const $globalUnit = $(this);
    $globalUnit.find(".required").each(function() {
      const $field = $(this);
      const isAcronymField = $field.hasClass("acronym-input");
      const isValid = isAcronymField ? validateAcronymField($field, false) : $.trim($field.val() || "").length > 0;
      setRequiredFieldState($field, isValid);
      if (!isValid) {
        $invalidFields.push(this);
      }
    });
    const $teamBlock = $globalUnit.find(".crp-admin-team-block");
    const hasCrpAdmin = $teamBlock.find(".userItem").length > 0;
    $teamBlock.toggleClass("fieldError", !hasCrpAdmin);
    $teamBlock.closest(".form-group").toggleClass("has-error", !hasCrpAdmin);
    if (!hasCrpAdmin) {
      $invalidFields.push($teamBlock.get(0));
    }
  });

  return $invalidFields;
}

function setRequiredFieldState($field, isValid) {
  $field.toggleClass("fieldError", !isValid);
  $field.closest(".form-group").toggleClass("has-error", !isValid);

  if ($field.hasClass("institution-select")) {
    const $select2Container = $field.next(".select2-container");
    $select2Container.toggleClass("fieldError", !isValid);
    $select2Container.find(".select2-selection").toggleClass("missingSelect2", !isValid);
  }
}

function openGlobalUnitWithInvalidField($field) {
  const $globalUnit = $field.closest(".globalUnit");
  const $title = $globalUnit.find(".blockTitle");
  if ($title.hasClass("closed")) {
    $title.trigger("click");
  }
  $field.trigger("focus");
}

function highlightServerValidationErrors() {
  const $serverErrors = $(".globalUnits-list .fieldError").filter(function() {
    return $.trim($(this).text()).length > 0;
  });

  if (!$serverErrors.length) {
    return;
  }

  let $firstInvalidField = $();
  $serverErrors.each(function() {
    const $field = $(this).siblings(".required").first();
    if (!$field.length) {
      return;
    }
    setRequiredFieldState($field, false);
    if (!$firstInvalidField.length) {
      $firstInvalidField = $field;
    }
  });

  if ($firstInvalidField.length) {
    openGlobalUnitWithInvalidField($firstInvalidField);
  }
  notificationError(guMsg("msg-gu-validationRequired"));
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
      placeholder: guMsg("msg-gu-institutionPlaceholder"),
      allowClear: true,
      width: "100%"
    });
  });
}
