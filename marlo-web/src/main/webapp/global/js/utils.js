$(function() {
  if(debugMode) {
    $("#debugPanel").draggable();
    $("#accordion").accordion({
      heightStyle: "content"
    });
    // Update control debug panel
    $('body').on('click', updateSerializeForm);
    $(document).on('updateComponent', updateSerializeForm);
    $('form:first :input').on('keyup change', updateSerializeForm);
  }
});

function updateSerializeForm() {
  $('.getSerializeForm').html(getSerializeForm());
}

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
    } else {
      $(input).val(0);
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

  $inputs.each(function(i,input) {
    $(input).addClass('currencyInput');
    $(input).on("keydown", isNumber);
    $(input).on("focus", removeCurrency);
    $(input).on("keyup", function(e) {
      isNumber(e);
    });
    $(input).on("click", function() {
      $(this).select();
    });
    $(input).on("focusout", setCurrency);
    // Active initial currency format to all inputs
    $(input).attr("autocomplete", "off").trigger("focusout");
  });

  $("form").submit(function(event) {
    $inputs.each(function() {
      $(this).attr("readonly", true);
      $(this).val(removeCurrencyFormat($(this).val() || "0"));
    });
    return;
  });
};

// contains in div or content
jQuery.expr[':'].icontains = function(a,i,m) {
  return jQuery(a).text().toUpperCase().indexOf(m[3].toUpperCase()) >= 0;
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
    if($(pickerElement).find('input').val() == "") {
      defaultColor = getRandomColor();
    } else {
      defaultColor = $(pickerElement).find('input').val();
    }
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
    var options = "width=620,height=550,resizable=1,scrollbars=1,location=0";
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

function isClusterSection() {
  var url = window.location.href;
  return url.includes("/clusters/");
}

function isImpactPathwaySection() {
  var url = window.location.href;
  return url.includes("/impactPathway/");
}

function isCenterImpactPathwaySection() {
  var url = window.location.href;
  return (url.includes("/impactPathway/")) && centerGlobalUnit;
}

function isMonitoringSection() {
  var url = window.location.href;
  return url.includes("/monitoring/");
}

function isCapDevSection() {
  var url = window.location.href;
  return url.includes("/capdev/");
}

function isPOWBSection() {
  var url = window.location.href;
  return url.includes("/powb/");
}

function isPOWB2019Section() {
  var url = window.location.href;
  return url.includes("/powb2019/");
}

function isAnnualReportSection() {
  var url = window.location.href;
  return url.includes("/annualReport/");
}

function isAnnualReport2018Section() {
  var url = window.location.href;
  return url.includes("/annualReport2018/");
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
      // if(a.value) {
      result += '<p>' + a.name + ' : <span>' + a.value + '</span></p>';
      // }
    });
  });
  return result;
}

function setCurrency() {
  this.value = setCurrencyFormat(this.value || "0");
}

function removeCurrency(event) {
  var $input = $(this);
  var inputValue = $input.val() || "0";
  $input.val(removeCurrencyFormat(inputValue));
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
  return (parseFloat(stringNumber)).toCurrencyFormat(2, 3, ',', '.');
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

jQuery.fn.addOptionFast = function(val,name) {
  $(this).append("<option value='" + val + "'>" + name + "</option>");
};

jQuery.fn.addArrayOptions = function(array) {
  var optionsText = "";
  for(var i = 0, len = array.length; i < len; i++) {
    optionsText += "<option value='" + array[i][0] + "'>" + array[i][1] + "</option>";
  }
  $(this).append(optionsText);
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

jQuery.fn.sanitizeInputs = function() {
  return this.each(function() {
    var $input = $(this);
    var text = $input.val();
    if (text) {
      var sanitized = text.normalize("NFKD")
                          .replace(/[\u{1D400}-\u{1D7FF}]/gu, "")
                          .replace(/[^\u0000-\uFFFF]/g, "");
      $input.val(sanitized);
    }
  });
};

function strip(html) {
  var tmp = document.createElement("DIV");
  tmp.innerHTML = html;
  return tmp.textContent || tmp.innerText || "";
}

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
  var urlRegex = /(https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*))/g;
  return text.replace(urlRegex, function(url) {
    var l = getLocation(url);
    return ' <a href="' + url + '">' + l.hostname + '</a>';
  })
  // or alternatively
  // return text.replace(urlRegex, '<a href="$1">$1</a>')
}

function urlifyComplete(text) {
  var urlRegex = /(https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*))/g;
  return text.replace(urlRegex, function(url) {
    var l = getLocation(url);
    return '<a href="' + url + '">' + truncate(url, 45) + '</a>';
  })
  // or alternatively
  // return text.replace(urlRegex, '<a href="$1">$1</a>')
}

function truncate(str,no_words) {
  if(str.length > no_words) {
    return str.slice(0, no_words) + "...";
  } else {
    return str;
  }

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
  var eventType = 'keyup ';
  if($textArea.hasClass('allowTextEditor')) {
    eventType += ' tbwchange tbwfocus tbwblur tbwpaste ';
  }

  var message = "<p class='charCount'>(<span>" + wordCount + "</span> words remaining of " + wordCount + ")</p>";
  $textArea.parent().append(message);
  $textArea.parent().find(".charCount").find("span").text(wordCount - word_count($textArea));
  $textArea.on(eventType, function() {
    var content = $(this).val();
    var valueLength = content.length;
    var $charCount = $(this).closest('.textArea, .input').find(".charCount");
    var hasMissingFields = $('.hasMissingFields').exists();
    var required = $(this).hasClass("required");
    var noJustification = ($(this).attr('id') != 'justification');

    if((word_count($(this)) > wordCount) || ((valueLength == 0) && required && hasMissingFields && noJustification)) {
      $(this).addClass('fieldError');
      $charCount.addClass('fieldError');
    } else {
      $(this).removeClass('fieldError');
      $charCount.removeClass('fieldError');
    }
    // Set count value
    $charCount.find("span").text(wordCount - word_count($(this)));

  });
  $textArea.trigger(eventType);
}

function word_count(field) {
  var value = $.trim($(field).val());

  // Replace Tag P to an space
  value = $.trim(value.replace(/<p[^>]*>/g, ' ').replace(/<\/p>/g, ' '));
  // Replace Tag BR to an space
  value = $.trim(value.replace(/<br[^>]*>/g, ' ').replace(/<\/br>/g, ' '));

  // Remove all tags
  var htmlRegex = /(<([^>]+)>)/ig
  value = $.trim(value.replace(htmlRegex, ""));

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

/**
 * Validate if and URL is valid
 * 
 * @param str
 * @returns
 */
function isValidURL(str) {
  regexp =
      /^(?:(?:https?|ftp):\/\/)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
  if(regexp.test(str)) {
    return true;
  } else {
    return false;
  }
}

// Validate Email
function isEmail(email) {
  var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
  if(!emailReg.test(email)) {
    return false;
  }
  return true;
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

/**
 * Get the current date in String
 * 
 * @returns {string} date
 */
function getDateString() {
  var today = new Date();
  return today.toISOString().split('T')[0] + "_" + today.getHours() + today.getMinutes();
}

/**
 * Number.prototype.toCurrencyFormat(n, x, s, c)
 * 
 * @param integer n: length of decimal
 * @param integer x: length of whole part
 * @param mixed s: sections delimiter
 * @param mixed c: decimal delimiter
 */
Number.prototype.toCurrencyFormat = function(n,x,s,c) {
  var re = '\\d(?=(\\d{' + (x || 3) + '})+' + (n > 0 ? '\\D' : '$') + ')', num = this.toFixed(Math.max(0, ~~n));
  return (c ? num.replace('.', c) : num).replace(new RegExp(re, 'g'), '$&' + (s || ','));
};

/**
 * Function to get a key in an object by its value
 * 
 * @param object
 * @param value
 * @returns key
 */
function getKeyByValue(obj,value) {
  return Object.keys(obj).filter(function(key) {
    return obj[key] == value
  })[0];
}

function postMessageToSlack(messageJson) {
  var xmlhttp = new XMLHttpRequest();
  // Webhook URLs for Your Workspace #marlo-notifications
  // old url "T0L2KT42Z/BTHFGL30U/iYg9pidxAjc1BpsXadJJwkjt";
  var channelToken = "T0L2KT42Z/BTHFGL30U/Bu5wXrmFVTvCkuGjFJmJSTuS" 
  if(production) {
    // old url "T0L2KT42Z/BTHFGL30U/iYg9pidxAjc1BpsXadJJwkjt";
    channelToken = "T0L2KT42Z/BTHFGL30U/Bu5wXrmFVTvCkuGjFJmJSTuS" 
  }

  var webhook_url = 'https://hooks.slack.com/services/' + channelToken;
  xmlhttp.open('POST', webhook_url, false);
  xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
  xmlhttp.send(messageJson);
}

/**
 * @param eventName
 * @param event_category
 * @param event_label
 * @returns
 */
function setCustomEvent(event_category,eventName,event_label) {
  console.log(eventName, event_category, event_label);
  gtag('event', eventName, {
      'event_category': event_category,
      'event_label': event_label,
      'value': 1
  });
}


/**
 * Sets the format for input fields with numbers to have commas and separators.
 */
function setFormatInput(inputSelector = "input.targetValueNumber", otherOptions = {}, fallbackTargetUnit = undefined) {

  $(inputSelector).each(function (i, ele) {

    const name = $(ele).attr('name');

    const $parentAvailable = $(ele).closest('.targetValue-block');
    let targetUnitSelected;

    if ($parentAvailable.length > 0) {
      $brotherContent = $parentAvailable.siblings('.targetUnit-block');
      let $select = ($brotherContent.find('select').length > 0) ? $brotherContent.find('select') : null;
      targetUnitSelected = ($select !== null) ? $select.val() : '-1';
    } else {
      $brotherContent = $('.targetUnit-block');
      targetUnitSelected = $brotherContent.attr('data-targetunit');
    }

    // Sections with no Target Unit at all can state their own through
    // fallbackTargetUnit; without one the icon stays '?'.
    if (targetUnitSelected === undefined) {
      targetUnitSelected = fallbackTargetUnit;
    }

    const modifiedIcon = (targetUnit) => {
      const typeTargetUnit = {
        "129": '%',
        "42": '#',
        "35": '%',
        "1": "%",
        "-1": "#"
      }
      return typeTargetUnit[targetUnit] || "?";
    }

    const modfiedOptions = (targetUnit) => {
      const typeTargetUnit = {
        "129": {
          maxDigits: 8,
          allowDecimals: true,
          isPercentage: true
        },
        "42": {
          maxDigits: 8,
          allowDecimals: false,
          isPercentage: false
        },
        "35": {
          maxDigits: 8,
          allowDecimals: true,
          isPercentage: true
        },
        "1": {
          maxDigits: 8,
          allowDecimals: true,
          isPercentage: true
        },
        "-1": {
          maxDigits: 8,
          allowDecimals: false,
          isPercentage: false
        }
      }
      return {...typeTargetUnit[targetUnit] || {}, ...otherOptions};
    }

    //add icon to after parent .input to visualize the unit
    const $parent = $(ele).closest('.input');
    $parent.attr('data-targetunit', modifiedIcon(targetUnitSelected));

    //add validations to input field through initNumberField
    // initNumberField writes thousands separators back into the field, and an
    // <input type="number"> rejects a value with commas outright - it blanks
    // it - so number fields keep the step / floor guard forms.ftl gives them.
    if ($(ele).attr('type') !== 'number') {
      initNumberField(name, modfiedOptions(targetUnitSelected));
    }


  });

}

function setMaskInputAllianceId(){

  const translation = {
    'translation': {
      Z: { pattern: /[A-Z]/, optional: false },
      0: { pattern: /[0-9]/, optional: false }
    }
  }

  $("input.targetValueAllianceId").each(function(i,ele){

    $(ele).mask('ZZZ-0000', translation);

    if($(ele).attr("value") === "") {
      $(ele).empty();
      $(ele).unmask();
      $(ele).val("");
    }

    $(ele).on("focus", function () {
      if($(ele).attr("value") === "") {
        $(ele).mask('ZZZ-0000', translation);
      }
    });

  });
}

function initNumberField(fieldId, options = {}) {

  const inputElement = document.querySelector(`input[name="${fieldId}"]`);
  if (!inputElement) {
      console.error(`El elemento con name = ${fieldId} no existe`);
      return;
  }
  
  // Opciones por defecto
  const defaultOptions = {
      maxDigits: 8,
      removeTrailingZeros: true,
      keepOneDecimalZero: true,
      allowDecimals: true,
      isPercentage: true,
      isRecallMethod: false
  };
  
  // Combinar opciones por defecto con las proporcionadas
  const config = { ...defaultOptions, ...options };
  
  // Guarda el valor original con todos los decimales permitidos
  let originalValue = '';
  
  // Clear previous event listeners using cloneNode
  const oldElement = inputElement;
  const newElement = oldElement.cloneNode(true);

  if(config.isRecallMethod) {
      oldElement.parentNode.replaceChild(newElement, oldElement);
  }
  
  // Reference the new element for our listeners
  const element = config.isRecallMethod ? newElement : inputElement;
  
  // Función para normalizar ceros iniciales y finales
  function normalizeZeros(value) {
      if (!value) return '';
      
      // Dividir en parte entera y decimal
      const parts = value.split('.');
      let integerPart = parts[0];
      let decimalPart = config.allowDecimals && parts.length > 1 ? parts[1] : '';
      
      // Verificar si tiene ceros iniciales en la parte entera
      if (integerPart.length > 1 && integerPart.startsWith('0')) {
          // Eliminar ceros iniciales
          integerPart = integerPart.replace(/^0+/, '');
          
          // Si quedó vacío después de quitar ceros, dejar un solo cero
          if (integerPart === '') {
              integerPart = '0';
          }
      }
      
      // Si hay parte decimal, está permitido y configurado para eliminar ceros
      if (decimalPart && config.allowDecimals && config.removeTrailingZeros) {
          // Verificar si solo contiene ceros
          const onlyZeros = /^0+$/.test(decimalPart);
          
          if (onlyZeros && config.keepOneDecimalZero) {
              // Si solo hay ceros, dejar un solo cero
              decimalPart = '0';
          } else if (onlyZeros && !config.keepOneDecimalZero) {
              // Si no queremos mantener ni un cero, eliminar todos
              decimalPart = '';
          } else {
              // Si tiene otros dígitos, eliminar ceros finales
              decimalPart = decimalPart.replace(/0+$/, '');
          }
      }
      
      // Reconstruir el valor
      if (decimalPart && config.allowDecimals) {
          return integerPart + '.' + decimalPart;
      }
      
      return integerPart;
  }
  
  // Función para formatear el número con los decimales correctos (para blur)
  function formatAsDecimal(value) {
      if (value === '') return '';
      
      // Si no se permiten decimales, eliminar cualquier parte decimal
      if (!config.allowDecimals) {
          value = value.split('.')[0];
      }
      
      // Agregar un 0 si el valor comienza con punto decimal
      if (config.allowDecimals && value.startsWith('.')) {
          value = '0' + value;
      }
      
      // Normalizar ceros iniciales y finales
      value = normalizeZeros(value);
      
      // Dividir en parte entera y decimal manteniendo la cadena original
      const parts = value.split('.');
      const integerPart = parts[0];
      const decimalPart = config.allowDecimals && parts.length > 1 ? parts[1] : '';
      
      // Formatear la parte entera con comas
      const formattedInteger = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
      
      // Si no se permiten decimales, devolver solo la parte entera
      if (!config.allowDecimals) {
          return formattedInteger;
      }
      
      // Verificar cuántos decimales podemos mantener dentro del límite de dígitos
      const availableDecimals = Math.max(0, config.maxDigits - integerPart.length);
      
      // Si no hay parte decimal o no hay espacio para decimales
      if (!decimalPart || availableDecimals === 0) {
          return formattedInteger;
      }
      
      // Preservar tantos decimales como sean posibles dentro del límite
      const visibleDecimalPart = decimalPart.substring(0, availableDecimals);
      
      // Guardar el valor original para usarlo en focus
      originalValue = integerPart + (decimalPart ? '.' + decimalPart : '');
      
      return formattedInteger + (visibleDecimalPart ? '.' + visibleDecimalPart : '');
  }
  
  // Función para formatear con comas en tiempo real
  function formatWithCommas(value) {
      if (!value) return '';
      
      // Si no se permiten decimales, eliminar cualquier parte decimal
      if (!config.allowDecimals) {
          value = value.split('.')[0];
      }
      
      // Agregar un 0 si el valor comienza con punto decimal
      if (config.allowDecimals && value.startsWith('.')) {
          value = '0' + value;
      }
      
      // Dividir en parte entera y decimal
      const parts = value.split('.');
      const integerPart = parts[0];
      let decimalPart = config.allowDecimals && parts.length > 1 ? parts[1] : '';
      
      // Formatear la parte entera con comas
      const integerWithCommas = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
      
      // Si hay parte decimal y se permiten decimales, mantenerla
      if (parts.length > 1 && config.allowDecimals) {
          return integerWithCommas + '.' + decimalPart;
      }
      
      return integerWithCommas;
  }
  
  // Función para obtener la posición del cursor después de agregar comas
  function getCaretPositionAfterFormat(value, valueWithCommas, caretPos) {
      // Contar cuántas comas hay antes de la posición del cursor
      let countCommasBefore = 0;
      let valueIndex = 0;
      
      for (let i = 0; i < valueWithCommas.length && valueIndex < caretPos; i++) {
          if (valueWithCommas[i] === ',') {
              countCommasBefore++;
          } else {
              valueIndex++;
          }
      }
      
      return caretPos + countCommasBefore;
  }

  if (element.value) {
    // Formatear el valor inicial según la configuración
    element.value = formatAsDecimal(element.value);
  }

  // Validar si se digito una coma y convertirla en .
  element.addEventListener('keydown', function(e) {
    if (e.key === ',' || e.keyCode === 188) {
      e.preventDefault();

      const currentValue = this.value.replace(/,/g, '');
      this.value = formatWithCommas(currentValue) + '.';

      const newCursorPos = getCaretPositionAfterFormat(currentValue, this.value, this.selectionStart) + 1;
      this.setSelectionRange(newCursorPos, newCursorPos);
    }
  });
  
  // Validación estricta en tiempo real mientras se escribe
  element.addEventListener('input', function(e) {
      const cursorPos = this.selectionStart;
      const originalInputValue = this.value;
      
      // Eliminar comas existentes para procesar correctamente
      let valueWithoutCommas = originalInputValue.replace(/,/g, '');
      
      // Agregar un 0 si el valor comienza con punto decimal y se permiten decimales
      let startsWithDot = false;
      if (config.allowDecimals && valueWithoutCommas.startsWith('.')) {
          valueWithoutCommas = '0' + valueWithoutCommas;
          startsWithDot = true;
      }
      
      // Normalizar ceros iniciales (mantenemos los ceros finales durante la edición)
      const normalizedValue = valueWithoutCommas.replace(/^0+(\d)/, '$1');
      let adjustCursor = valueWithoutCommas.length - normalizedValue.length;
      valueWithoutCommas = normalizedValue === '' ? '0' : normalizedValue;
      
      // Primera fase: eliminar caracteres no válidos (solo números y un punto si se permiten decimales)
      let cleanValue = '';
      let hasDecimal = false;
      
      for (let i = 0; i < valueWithoutCommas.length; i++) {
          const char = valueWithoutCommas[i];
          
          if (config.allowDecimals && char === '.' && !hasDecimal) {
              cleanValue += char;
              hasDecimal = true;
          } else if (/[0-9]/.test(char)) {
              cleanValue += char;
          }
      }
      
      // Segunda fase: aplicar restricción de máximo de dígitos en total
      const parts = hasDecimal ? cleanValue.split('.') : [cleanValue];
      const integerPart = parts[0];
      const decimalPart = parts.length > 1 ? parts[1] : '';
      
      // Contar dígitos totales
      const totalDigits = integerPart.length + (config.allowDecimals ? decimalPart.length : 0);
      
      if (totalDigits > config.maxDigits) {
          // Si hay más dígitos que el máximo, tenemos que recortar
          if (hasDecimal && config.allowDecimals) {
              // Priorizar mantener la parte entera y recortar decimales
              const maxDecimalDigits = Math.max(0, config.maxDigits - integerPart.length);
              const trimmedDecimal = decimalPart.substring(0, maxDecimalDigits);
              cleanValue = integerPart + (trimmedDecimal ? '.' + trimmedDecimal : '');
          } else {
              // Sin punto decimal, simplemente recortar al máximo
              cleanValue = cleanValue.substring(0, config.maxDigits);
          }
      }
      
      // Actualizar valor original
      originalValue = cleanValue;
      
      // Calcular posición relativa del cursor (sin contar comas)
      let cleanCursorPos;
      if (cleanValue !== valueWithoutCommas || adjustCursor > 0) {
          // Si el valor ha cambiado, calcular nueva posición del cursor
          let countBeforeCursor = 0;
          for (let i = 0; i < cursorPos && i < originalInputValue.length; i++) {
              if (originalInputValue[i] !== ',') {
                  countBeforeCursor++;
              }
          }
          
          // Ajustar por los ceros iniciales eliminados
          countBeforeCursor = Math.max(0, countBeforeCursor - adjustCursor);
          
          // Ajustar si el valor limpio es más corto
          cleanCursorPos = Math.min(countBeforeCursor, cleanValue.length);
      } else {
          // Si solo se quitaron comas, la posición es más simple
          cleanCursorPos = cursorPos - (originalInputValue.length - valueWithoutCommas.length);
      }
      
      // Aplicar formato con comas para la visualización
      const formattedValue = formatWithCommas(cleanValue);
      
      // Calcular la nueva posición del cursor incluyendo las comas
      let newCursorPos = getCaretPositionAfterFormat(cleanValue, formattedValue, cleanCursorPos);
      
      // Ajustar posición del cursor si agregamos un 0 al inicio
      if (startsWithDot && cursorPos === 0) {
          newCursorPos = 1; // Posicionar después del 0 añadido
      } else if (startsWithDot) {
          newCursorPos += 1; // Ajustar por el 0 añadido
      }
      
      // Actualizar el valor y la posición del cursor
      this.value = formattedValue;
      this.setSelectionRange(newCursorPos, newCursorPos);
  });
  
  // Aplicar formato completo cuando el input pierde el foco
  element.addEventListener('blur', function(e) {
      // Si hay un valor, formatearlo manteniendo los decimales originales
      if (this.value && this.value !== '') {
          // Eliminar cualquier coma existente primero
          const valueWithoutCommas = this.value.replace(/,/g, '');
          
          // Formatear para mostrar los decimales correctos y normalizar ceros
          this.value = formatAsDecimal(valueWithoutCommas);
      }
  });
  
  // Cuando el campo obtiene el foco, mostrar el valor original con todos los dígitos posibles
  element.addEventListener('focus', function(e) {
      if (this.value && this.value !== '') {
          // Eliminar comas para procesar
          let valueWithoutCommas = this.value.replace(/,/g, '');
          
          // Normalizar ceros iniciales y finales para evitar inconsistencias
          valueWithoutCommas = normalizeZeros(valueWithoutCommas);
          
          // Dividir en parte entera y decimal
          const parts = valueWithoutCommas.split('.');
          const integerPart = parts[0];
          const decimalPart = config.allowDecimals && parts.length > 1 ? parts[1] : '';
          
          // Formatear la parte entera con comas
          const integerWithCommas = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
          
          // Reconstruir el valor con la parte decimal (si existe y está permitida)
          let result = integerWithCommas;
          if (decimalPart && config.allowDecimals) {
              result += '.' + decimalPart;
          }
          
          this.value = result;
      }
  });

  
  // Devolver un objeto con métodos públicos para interactuar con el campo
  return {
      getValue: function() {
          return element.value.replace(/,/g, '');
      },
      setValue: function(value) {
          element.value = formatAsDecimal(value);
      },
      clear: function() {
          element.value = '';
      }
  };
}

(function($) {
  if (!$.fn.select2 || !$.fn.select2.amd) {
    console.warn('Select2 or its AMD module is not available.');
    return;
  }

    // Define the custom adapter module
  $.fn.select2.amd.define('select2/dropdown/customAttachBody', [
    'jquery',
    '../utils',
    'select2/dropdown/attachBody',
    'select2/dropdown/closeOnSelect',
  ], function($, Utils, AttachBody, CloseOnSelect) {
    function CustomAttachBody ( decorated, $element, options) {

        // Create options object with get method if not exists
        if (!options) {
          options = {};
        }

        if (typeof options.get !== 'function') {
          options.get = function (key) {
            return options[key] || null;
          };
        }

        // Call base constructor
        decorated.call(this, $element, options);

    }

    Utils.Extend(CustomAttachBody, AttachBody);

    // Re‑attach base handlers and ensure close-on-select still works
    CustomAttachBody.prototype.bind = function(decorated, container, $container) {
      decorated.call(this, container, $container);
      var self = this;
      container.on('results:select', function(evt) {
        self._hideDropdown(); 
        container.trigger('close');
      });
    };

    CustomAttachBody.prototype.render = function(decorated) {
      var $wrapper = $('<span></span>');
      var $dropdown = decorated.call(this);
      if (!$dropdown.jquery) {
        throw new Error('render: decorated must return jQuery');
      }
      $wrapper.append($dropdown);
      this.$dropdownContainer = $wrapper;
      return $wrapper;
    }


    CustomAttachBody.prototype._positionDropdown = function () {

      // Your custom positioning logic here
      // Ensure that 'this.options' is properly defined
      if (!this.options || typeof this.options.get !== 'function') {
        console.warn('Select2 options are not properly configured.');
        return;
      }

      var self = this;
      var $window = $(window);

      // Safely access options with fallback
      var dropdownPositionOption = 'auto';
      if (this.options && typeof this.options.get === 'function') {
        dropdownPositionOption = this.options.get('dropdownPosition') || 'auto';
      }

      var isCurrentlyAbove = this.$dropdown.hasClass('select2-dropdown--above');
      var isCurrentlyBelow = this.$dropdown.hasClass('select2-dropdown--below');

      var offset = this.$container.offset();
      offset.bottom = offset.top + this.$container.outerHeight(false);

      var container = {
        height: this.$container.outerHeight(false),
        top: offset.top,
        bottom: offset.top + this.$container.outerHeight(false)
      };

      this.$dropdown.css('display', 'block');

      var dropdown = {
        height: this.$dropdown.outerHeight(true)
      };

      var $results = this.$dropdown.find('.select2-results__options');

      setTimeout(function () {
        if ($results.length) {
          $results.scrollTop(0);
          dropdown.height = $results.outerHeight(true);
        }

        var viewport = {
          top: $window.scrollTop(),
          bottom: $window.scrollTop() + $window.height()
        };

        var enoughRoomAbove = viewport.top < (offset.top - dropdown.height);
        var enoughRoomBelow = viewport.bottom > (offset.bottom + dropdown.height);

        var css = {
          left: offset.left,
          top: container.bottom
        };

        var $offsetParent = self.$dropdownParent;
        if ($offsetParent.css('position') === 'static') {
          $offsetParent = $offsetParent.offsetParent();
        }

        var parentOffset = $offsetParent.offset();
        css.top -= parentOffset.top;
        css.left -= parentOffset.left;

        var newDirection = null;
        if (dropdownPositionOption === 'above' || dropdownPositionOption === 'below') {
          newDirection = dropdownPositionOption;
        } else {
          if (!isCurrentlyAbove && !isCurrentlyBelow) {
            newDirection = 'below';
          }

          if (!enoughRoomBelow && enoughRoomAbove && !isCurrentlyAbove) {
            newDirection = 'above';
          } else if (!enoughRoomAbove && enoughRoomBelow && isCurrentlyAbove) {
            newDirection = 'below';
          }
        }

        if (newDirection === 'above' ||
          (isCurrentlyAbove && newDirection !== 'below')) {
          css.top = container.top - parentOffset.top - dropdown.height;
        }

        if (newDirection != null) {
          self.$dropdown
            .removeClass('select2-dropdown--below select2-dropdown--above')
            .addClass('select2-dropdown--' + newDirection);
          self.$container
            .removeClass('select2-container--below select2-container--above')
            .addClass('select2-container--' + newDirection);
        }

        self.$dropdownContainer.css(css);
      }, 0);
    }


    return  CustomAttachBody;
  });

})(window.jQuery);