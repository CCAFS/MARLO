$(function() {
  if(debugMode) {
    $("#debugPanel").draggable();
    $("#accordion").accordion({
      heightStyle: "content"
    });
    $('body').on('click', function() {

      $('.getSerializeForm').html(getSerializeForm());
    });
  }
});

/**
 * Jquery Plugins
 */
jQuery.fn.exists = function() {
  return this.length > 0;
};

jQuery.fn.numericInput = function() {
  $(this).each(function(i,input) {
    if($(input).val() != '') {
      var inputVal = parseFloat($(input).val()) || 0;
      if((inputVal % 1) == 0) {
        $(input).val(parseInt(inputVal));
      }
    }
    $(input).on("keydown", function(e) {
      isNumber(e);
    });
  });
};

jQuery.fn.percentageInput = function() {
  var $inputs = $(this);
  $inputs.on("keydown", isNumber);
  $inputs.on("focusout", setPercentage);
  $inputs.on("focus", removePercentage);
  $inputs.on("keyup", function(e) {
    isPercentage(e);
  });
  $inputs.on("click", function() {
    $(this).select();
  });
  // Active initial currency format to all inputs
  $inputs.attr("autocomplete", "off").trigger("focusout");

  $("form").submit(function(event) {
    $inputs.each(function() {
      $(this).attr("readonly", true);
      $(this).val(removePercentageFormat($(this).val() || "0"));
    });
    return;
  });
};

$.fn.scrollBottom = function() {
  return $(document).height() - this.scrollTop() - this.height();
};

/* Color picker widget */
var colors =
    [
        '#1abc9c', '#16a085', '#2ecc71', '#27ae60', '#3498db', '#2980b9', '#9b59b6', '#8e44ad', '#34495e', '#2c3e50',
        '#f1c40f', '#f39c12', '#e67e22', '#d35400', '#e74c3c', '#c0392b', '#ecf0f1', '#bdc3c7', '#95a5a6', '#7f8c8d'
    ]
$.fn.colorPicker = function() {
  $(this).each(function(i,pickerElement) {
    var picker = vanillaColorPicker(pickerElement);
    picker.set('customColors', colors);
    picker.set('defaultColor', $(pickerElement).find('input').val());
    picker.on('colorChosen', function(color,targetElem) {
      targetElem.style.backgroundColor = color;
      $(targetElem).find('input').val(color);
    });
  });
}

/*
 * This function takes the links whit popup class and add a click event. That event takes the href and open it in a
 * popUp window This method must be called in ready function
 */
function popups() {
  $("a.popup").click(function(event) {
    event.preventDefault();
    var options = "width=700,height=500,resizable=1,scrollbars=1,location=0";
    nueva = window.open(this.href, '_blank', options);
  });
}

function isNumber(e) {
  if($.inArray(e.keyCode, [
      46, 8, 9, 27, 13, 110, 190, 109, 189
  ]) !== -1 ||
  // Allow: Ctrl + C
  (e.keyCode == 67 && e.ctrlKey === true) ||
  // Allow: Ctrl + V
  (e.keyCode == 86 && e.ctrlKey === true) ||
  // Allow: Ctrl + A
  (e.keyCode == 65 && e.ctrlKey === true) ||
  // Allow: home, end, left, right
  (e.keyCode >= 35 && e.keyCode <= 39)) {
    // let it happen, don't do anything
    return;
  }
  // Ensure that it is a number and stop the keypress
  if((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
    e.preventDefault();
  }
}

function isPercentage(e) {
  // Use keyup/keydown for add/subtract
  if(e.keyCode === 38) {
    e.target.value++;
  } else if(e.keyCode === 40) {
    e.target.value--;
  }
  // Ensure that is a percentage value
  var value = e.target.value;
  if(value >= 100) {
    e.target.value = 100;
  }
  if(value < 0) {
    e.target.value = 0;
  }
}

// checks whether the coordinate is valid
function isCoordinateValid(latitude,longitude) {
  if(latitude > -90 && latitude < 90 && longitude > -180 && longitude < 180) {
    return true;
  } else {
    return false;
  }
}

function printOut() {
  $("form").each(function(indexForm,form) {
    console.log("--------------------------- Form #" + indexForm + "  ------------------------------");
    $(form).find("input,textarea,select,button").each(function(i,input) {
      if($(input).attr("name")) {
        console.log("> " + $(input).attr("name") + ": " + $(input).val() + " (" + input.tagName + ")");
      }
    });
  });
}

function getCrpFromUrl() {
  var url = window.location.href;
  var result = (url.split(baseURL)[1]).split('/')[1]
  if(!(result.indexOf('.do') > -1) && result != "") {
    return result;
  }
}

function getSerializeForm() {
  var result = '';
  $("form").each(function(indexForm,form) {
    result += "<strong> Form #" + indexForm + "</strong></br>";
    $.each($(form).serializeArray(), function(i,a) {
      if(a.value) {
        result += '<p>' + a.name + ' : <span>' + a.value + '</span></p>';
      }
    });
  });
  return result
}

function setPercentage(event) {
  var $input = $(event.target);
  if($input.val().length == 0) {
    $input.val(0);
  }
  $input.val(setPercentageFormat($input.val()));
}

function removePercentage(event) {
  $input = $(event.target);
  $input.val(removePercentageFormat($input.val() || "0"));
}

function setCurrencyFormat(stringNumber) {
  return parseFloat(stringNumber, 10).toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, "$1,").toString();
}

function removeCurrencyFormat(stringNumber) {
  return parseFloat(stringNumber.replace(/,/g, ''));
}

function setPercentageFormat(stringNumber) {
  return stringNumber + "%";
}

function removePercentageFormat(stringNumber) {
  return stringNumber.replace(/%/g, '');
}
