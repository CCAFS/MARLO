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

jQuery.fn.integerInput = function() {
  var $inputs = $(this);
  $inputs.on("keypress keyup blur", function(event) {
    $(this).val($(this).val().replace(/[^\d].+/, ""));
    if((event.which < 48 || event.which > 57)) {
      event.preventDefault();
    }
  });
}

jQuery.fn.percentageInput = function() {
  var $inputs = $(this);
  $inputs.addClass('percentageInput');
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

jQuery.fn.currencyInput = function() {
  var $inputs = $(this);
  $inputs.addClass('currencyInput');
  $inputs.on("keydown", isNumber);
  $inputs.on("focusout", setCurrency);
  $inputs.on("focus", removeCurrency);
  $inputs.on("keyup", function(e) {
    isNumber(e);
  });
  $inputs.on("click", function() {
    $(this).select();
  });
  // Active initial currency format to all inputs
  $inputs.attr("autocomplete", "off").trigger("focusout");

  $("form").submit(function(event) {
    $inputs.each(function() {
      $(this).attr("readonly", true);
      $(this).val(removeCurrencyFormat($(this).val() || "0"));
    });
    return;
  });
};

$.fn.scrollBottom = function() {
  return $(document).height() - this.scrollTop() - this.height();
};

/* Serialize object */
$.fn.serializeObject = function() {
  var o = {};
  var a = this.serializeArray();
  $.each(a, function() {
    if(o[this.name] !== undefined) {
      if(!o[this.name].push) {
        o[this.name] = [
          o[this.name]
        ];
      }
      o[this.name].push(this.value || '');
    } else {
      o[this.name] = this.value || '';
    }
  });
  return o;
};

jQuery.fn.setNameIndex = function(level,index) {
  var re = /\[.*?\]/g;
  var name = $(this).attr('name');
  var levels = 0;
  $(this).attr('name', name.replace(re, function(match,pos,original) {
    levels++;
    return (levels == level) ? "[" + index + "]" : match;
  }));
  $(this).attr('id', $(this).attr('name'));
};

jQuery.fn.setNameIndexes = function(level,index) {
  $(this).find('[name]').each(function(i,e) {
    $(e).setNameIndex(level, index);
  });
};

/* Color picker widget */
var colors =
    [
        '#1abc9c', '#16a085', '#2ecc71', '#27ae60', '#3498db', '#2980b9', '#9b59b6', '#8e44ad', '#34495e', '#2c3e50',
        '#f1c40f', '#f39c12', '#e67e22', '#d35400', '#e74c3c', '#c0392b', '#ecf0f1', '#bdc3c7', '#95a5a6', '#7f8c8d'
    ]

$.fn.colorPicker = function() {
  $(this).each(function(i,pickerElement) {
    var defaultColor;
    console.log($(pickerElement).find('input').val());
    if($(pickerElement).find('input').val() == "") {
      defaultColor = getRandomColor();
    } else {
      defaultColor = $(pickerElement).find('input').val();
    }
    console.log(defaultColor);
    var picker = vanillaColorPicker(pickerElement);
    picker.set('customColors', colors);
    picker.set('defaultColor', defaultColor);
    pickerElement.style.backgroundColor = defaultColor;
    $(pickerElement).find('input').val(defaultColor);
    picker.on('colorChosen', function(color,targetElem) {
      targetElem.style.backgroundColor = color;
      $(targetElem).find('input').val(color);
    });
  });
}

// Animate.css - https://github.com/daneden/animate.css
$.fn.extend({
  animateCss: function(animationName) {
    var animationEnd = 'webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend';
    $(this).addClass('animated ' + animationName).one(animationEnd, function() {
      $(this).removeClass('animated ' + animationName);
    });
  }
});

function getRandomColor() {
  var letters = '0123456789ABCDEF'.split('');
  var color = '#';
  for(var i = 0; i < 6; i++) {
    color += letters[Math.floor(Math.random() * 16)];
  }
  return color;
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

function isProjectSection() {
  var url = window.location.href;
  return url.includes("/projects/");
}

function isImpactPathwaySection() {
  var url = window.location.href;
  return url.includes("/centerImpactPathway/");
}

function isMonitoringSection() {
  var url = window.location.href;
  return url.includes("/monitoring/");
}

/**
 * Search from url that has GET parameters
 */
function getParameterByName(name,url) {
  if(!url) {
    url = window.location.href;
  }
  name = name.replace(/[\[\]]/g, "\\$&");
  var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"), results = regex.exec(url);
  if(!results) {
    return null;
  }
  if(!results[2]) {
    return '';
  }
  return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Get Parameter from a class, example:
 * <p class="parameter-100">, the function will return 100
 * 
 * @param selector
 *          <p class="parameter-100">
 *          </p>
 * @param cssName parameter
 * @returns
 */
function getClassParameter(selector,cssName) {
  var check = cssName + "-";
  var className = $(selector).attr('class') || '';
  var type = $.map(className.split(' '), function(val,i) {
    if(val.indexOf(check) > -1) {
      return val.slice(check.length, val.length);
    }
  });
  return((type.join(' ')) || 'none');
}

jQuery.fn.classParam = function(cssName) {
  return getClassParameter(this, cssName)
};

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
  return result;
}

function setCurrency(event) {
  var $input = $(event.target);
  if($input.val().length == 0) {
    $input.val("0");
  }
  $input.val(setCurrencyFormat($input.val()));
}

function removeCurrency(event) {
  var $input = $(event.target);
  if($input.val().length == 0) {
    $input.val("0");
  }
  $input.val(removeCurrencyFormat($input.val()));
  if($input.val() == "0") {
    $input.val("");
  }
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

/**
 * Functions for selects
 */
function setOption(val,name) {
  return "<option value='" + val + "'>" + name + "</option>";
}

jQuery.fn.addOption = function(val,name) {
  if(!($(this).find('option[value=' + val + ']').exists())) {
    $(this).append("<option value='" + val + "'>" + name + "</option>");
  }
};

function removeOption(select,val) {
  $(select).find('option[value=' + val + ']').remove();
}

jQuery.fn.removeOption = function(val) {
  $(this).find('option[value=' + val + ']').remove();
};

jQuery.fn.clearOptions = function(arrIds) {
  if((arrIds.length == 1) && (arrIds[0] == "")) {

  } else {
    for(var i = 0, len = arrIds.length; i < len; i++) {
      $(this).removeOption(arrIds[i]);
    }
  }
};

/**
 * Escape HTML text
 */

function escapeHtml(text) {
  var map = {
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#039;'
  };
  return text.replace(/[&<>"']/g, function(m) {
    return map[m];
  });
}

/**
 * Javascript: array.indexOf() fix for IE8 and below
 */

if(!Array.prototype.indexOf) {
  Array.prototype.indexOf = function(searchElement /* , fromIndex */) {
    'use strict';
    if(this == null) {
      throw new TypeError();
    }
    var n, k, t = Object(this), len = t.length >>> 0;

    if(len === 0) {
      return -1;
    }
    n = 0;
    if(arguments.length > 1) {
      n = Number(arguments[1]);
      if(n != n) { // shortcut for verifying if it's NaN
        n = 0;
      } else if(n != 0 && n != Infinity && n != -Infinity) {
        n = (n > 0 || -1) * Math.floor(Math.abs(n));
      }
    }
    if(n >= len) {
      return -1;
    }
    for(k = n >= 0 ? n : Math.max(len - Math.abs(n), 0); k < len; k++) {
      if(k in t && t[k] === searchElement) {
        return k;
      }
    }
    return -1;
  };
}

function urlify(text) {
  var urlRegex = /(https?:\/\/[^\s]+)/g;
  return text.replace(urlRegex, function(url) {
    var l = getLocation(url);
    return '<a href="' + url + '">' + l.hostname + '</a>';
  })
  // or alternatively
  // return text.replace(urlRegex, '<a href="$1">$1</a>')
}

var getLocation = function(href) {
  var l = document.createElement("a");
  l.href = href;
  return l;
};

/* Add a char counter to a specific text area */
function applyCharCounter($textArea,charCount) {
  $textArea.parent().append("<p class='charCount'>(<span>" + charCount + "</span> characters remaining)</p>");
  $textArea.next(".charCount").find("span").text(charCount - $textArea.val().length);
  $textArea.on("keyup", function(event) {
    if($(event.target).val().length > charCount) {
      $(event.target).val($(event.target).val().substr(0, charCount));
    }
    $(event.target).next(".charCount").find("span").text(charCount - $(event.target).val().length);
  });
  $textArea.trigger("keyup");
}

/* Add a word counter to a specific text area */
function applyWordCounter($textArea,wordCount) {
  var message = "<p class='charCount'>(<span>" + wordCount + "</span> words remaining of " + wordCount + ")</p>";
  $textArea.parent().append(message);
  $textArea.parent().find(".charCount").find("span").text(wordCount - word_count($textArea));
  $textArea.on("keyup", function(event) {
    var valueLength = $(event.target).val().length;
    var $charCount = $(event.target).parent().find(".charCount");
    if((word_count($(event.target)) > wordCount)
        || ((valueLength == 0) && $(event.target).hasClass("required") && $('.hasMissingFields').exists() && $(
            event.target).attr('id') != 'justification')) {
      $(event.target).addClass('fieldError');
      $charCount.addClass('fieldError');
    } else {
      $(event.target).removeClass('fieldError');
      $charCount.removeClass('fieldError');
    }
    // Set count value
    $charCount.find("span").text(wordCount - word_count($(event.target)));

  });
  $textArea.trigger("keyup");
}

function word_count(field) {
  var value = $.trim($(field).val());
  if(typeof value === "undefined" || value.length == 0) {
    return 0;
  } else {
    var regex = /\s+/gi;
    return value.replace(regex, ' ').split(' ').length;
  }
}

function validateField($input) {
  if($input.length) {
    var valid = ($.trim($input.val()).length > 0) ? true : false;
    return valid;
  } else {
    return true;
  }
}

function getCookie(cname) {
  var name = cname + "=";
  var ca = document.cookie.split(';');
  for(var i = 0; i < ca.length; i++) {
    var c = ca[i];
    while(c.charAt(0) == ' ') {
      c = c.substring(1);
    }
    if(c.indexOf(name) == 0) {
      return c.substring(name.length, c.length);
    }
  }
  return "";
}

/**
 * @summary DataTables
 * @description Paginate, search and sort HTML tables
 * @version 1.9.0 /* Natural Sort algorithm for Javascript - Version 0.6 - Released under MIT license Author: Jim Palmer
 *          (based on chunking idea from Dave Koelle) Contributors: Mike Grier (mgrier.com), Clint Priest, Kyle Adams,
 *          guillermo
 */
(function() {

  function naturalSort(a,b) {
    var re = /(^-?[0-9]+(\.?[0-9]*)[df]?e?[0-9]?$|^0x[0-9a-f]+$|[0-9]+)/gi, sre = /(^[ ]*|[ ]*$)/g, dre =
        /(^([\w ]+,?[\w ]+)?[\w ]+,?[\w ]+\d+:\d+(:\d+)?[\w ]?|^\d{1,4}[\/\-]\d{1,4}[\/\-]\d{1,4}|^\w+, \w+ \d+, \d{4})/, hre =
        /^0x[0-9a-f]+$/i, ore = /^0/,
    // convert all to strings and trim()
    x = a.toString().replace(sre, '') || '', y = b.toString().replace(sre, '') || '',
    // chunk
    xN = x.replace(re, '\0$1\0').replace(/\0$/, '').replace(/^\0/, '').split('\0'), yN =
        y.replace(re, '\0$1\0').replace(/\0$/, '').replace(/^\0/, '').split('\0'),
    // numeric, hex or date detection
    xD = parseInt(x.match(hre), 10) || (xN.length !== 1 && x.match(dre) && Date.parse(x)), yD =
        parseInt(y.match(hre), 10) || xD && y.match(dre) && Date.parse(y) || null;

    // first try and sort Hex codes or Dates
    if(yD) {
      if(xD < yD) {
        return -1;
      } else if(xD > yD) {
        return 1;
      }
    }

    // natural sorting through split numeric strings and default strings
    for(var cLoc = 0, numS = Math.max(xN.length, yN.length); cLoc < numS; cLoc++) {
      // find floats not starting with '0', string or 0 if not defined
      var oFxNcL = !(xN[cLoc] || '').match(ore) && parseFloat(xN[cLoc], 10) || xN[cLoc] || 0;
      var oFyNcL = !(yN[cLoc] || '').match(ore) && parseFloat(yN[cLoc], 10) || yN[cLoc] || 0;
      // handle numeric vs string comparison - number < string
      if(isNaN(oFxNcL) !== isNaN(oFyNcL)) {
        return (isNaN(oFxNcL)) ? 1 : -1;
      }
      // rely on string comparison if different types - i.e. '02' < 2 != '02' < '2'
      else if(typeof oFxNcL !== typeof oFyNcL) {
        oFxNcL += '';
        oFyNcL += '';
      }
      if(oFxNcL < oFyNcL) {
        return -1;
      }
      if(oFxNcL > oFyNcL) {
        return 1;
      }
    }
    return 0;
  }

  if(jQuery.fn.dataTableExt) {
    jQuery.extend(jQuery.fn.dataTableExt.oSort, {
        "natural-asc": function(a,b) {
          return naturalSort(a, b);
        },

        "natural-desc": function(a,b) {
          return naturalSort(a, b) * -1;
        }
    });
  }

}());