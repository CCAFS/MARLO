$(document).ready(init);
function init() {
  // Add Select2 Plugin
  $('select').select2({
    width: '100%'
  });
  
  // Add Tag Editor Plugin
  $('.keywords').tagEditor({ 
    delimiter: ",",
    forceLowercase: false,
    placeholder: "Enter keywords here ..."
  });
  
  // Attach events
  attachEvents();
}

// *************************************************** Events ********************************************************//

function attachEvents() {
  // Select reports type
  $('.summariesSection a, .summariesSection span').on('click', selectSummariesSection);
  $('.summariesSection a, .summariesSection img').on('click', selectSummariesSection);
  // Select a report
  $(".collapseButton").on("click", selectReport);
  $(".imgArrow").on("click", selectReport);
  
  
  // Add predefined gender keywords
  $('.addGenderKeys').on('click', addGenderKeys);
  
  // Remove all keywords
  $('.removeAllTags').on('click', removeAllTags);
  
  // Update project List
  // $('[name="cycle"], [name="year"]').on('change', changePhaseParameters);
    $('[name="phaseID"]').on('change', changePhaseParameters);

  // Show or hide select a cluster
  $('#AICCRA_progressReportProcessSummary #1-showAllYears-false').on('click', hideShowClusterSelect);
  $('#AICCRA_progressReportProcessSummary #1-showAllYears-true').on('click', hideShowClusterSelect);
  
  // Copy to clipboard functionality
  $('.btn-copy').on('click', copyToClipboard);
}

// ************************************************ Functions *******************************************************//
function hideShowClusterSelect() {
  if ($(this).attr('value') == 'true') {
    $('#AICCRA_progressReportProcessSummary').children().eq(1).hide('slow');
  } else {
    $('#AICCRA_progressReportProcessSummary').children().eq(1).show('slow');
  }
}

function changePhaseParameters(){
  var $parent = $(this).parents('.summariesFiles');
  if($parent.hasClass('allowProjectID')){
    getProjectsByCycleYear($parent, $parent.find('[name="phaseID"]').val());
  }
}

function addGenderKeys(){
  // Gender Tags
  var genderArray =
    [
      "Gender", "female", "male", "men", "elderly", "caste", "women", "equitable", "inequality", "equity",
      "social differentiation", "social inclusion", "youth", "social class", "children", "child"
      ];
  $.each(genderArray, function(i,tag){
    $('.keywords').tagEditor('addTag', tag);
  });
}

function removeAllTags(){
  var tags = $('.keywords').tagEditor('getTags')[0].tags;
  $.each(tags, function(i,tag){
    $('.keywords').tagEditor('removeTag', tag); 
  });
}

function selectReport() {
  if($(this).parent().hasClass('selected')){
    $('.summariesFiles').removeClass("selected");
    $('.extraOptions').slideUp();
    $(this).parent().find('.imgArrow').css("rotate", "0deg" )
    $('.imgArrow').css("margin-top", "auto" )
    return
  }
  // Update the project list if necessary
  var $parent = $(this).parent();
  if($parent.hasClass('allowProjectID')){
    getProjectsByCycleYear($parent, $parent.find('[name="phaseID"]').val());
  }
  // Hide all reports
  $('.summariesFiles').removeClass("selected");
  $('.extraOptions').slideUp();
  $('.imgArrow').css("rotate", "0deg" );
  $('.imgArrow').css("margin-top", "auto" )

  // Show selected report
  $(this).parent().find('.extraOptions').slideDown();
  $(this).parent().find('.extraOptions').find('select, input').attr('disabled', false).trigger("liszt:updated");
  $(this).parent().addClass("selected");
  $(this).parent().find('.imgArrow').css("rotate", "180deg" )
  $(this).parent().find('.imgArrow').css("margin-top", "-4px" )

}

function selectSummariesSection(e) {
  e.preventDefault();
  var $section = $(e.target).parents('.summariesSection');
  var $content = $('#' + $section.attr('id') + '-contentOptions');
  $section.siblings().removeClass('current');
  $section.addClass('current');
  $content.siblings().hide();
  $content.fadeIn(600);
  $(".summariesFiles").removeClass("selected");
  $(".extraOptions").slideUp();
}

/**
 * Update Projects List
 * 
 * @param {DOM} parent - summariesFiles div
 * @param {String} cycle - Planning/Reporting
 * @param {Number} year
 * @returns
 */
function getProjectsByCycleYear(parent, phaseID) {
  var $parent = $(parent);
  $parent.find(".allProjectsSelect").empty();
  $parent.find('.loading').fadeIn();
  $.ajax({
      url: baseURL + "/projectListByPhase.do?",
      type: 'GET',
      data: {
        // cycle: cycle,
        // year: year,
        phaseID: phaseID
      },
      success: function(m) {
        $.each(m.projects, function(i,e) {
          $parent.find(".allProjectsSelect").addOption(e.id, "C" + e.id + " - " + e.description);
        })
      },
      complete: function() {
        $parent.find('.loading').fadeOut();
      }
  });
}

// ************************************************ AI Prompting *******************************************************//

function getAIText(e, service = 'AIReportSummary.do') {
  e.preventDefault();

  const eventBtn = $(e.target);

  const form = $(eventBtn).closest('form');

  const container = $('.iaPromptContainer');
  const textarea = container.find('textarea');

  const indicatorNameValue = form.find('select[name="indicatorName"]').val();
  const yearValue = form.find('select[name="year"]').val();

  if (indicatorNameValue == -1 || indicatorNameValue == "-1" || indicatorNameValue == null || indicatorNameValue == "") {
    form.find('select[name="indicatorName"]').next().addClass('fieldError');

    return;
  }

  form.find('select[name="indicatorName"]').next().removeClass('fieldError');

  $.ajax({
    url: baseURL + "/" + service,
    method: 'GET',
    dataType: 'json',
    contentType: 'application/json',
    data: {
      indicatorName : indicatorNameValue,
      year : yearValue
    },
    beforeSend: function() {
      // Disable the button to prevent multiple clicks
      eventBtn.prop('disabled', true);
      // Show loading spinner or message
      eventBtn.html('<span class="glyphicon glyphicon-refresh glyphicon-refresh-animate"></span> Generating AI text...');

      // Show the container
      container.show();
      // Enable the textarea and focus on it
      textarea.trumbowyg('disable');

      // Clear the textarea
      textarea.trumbowyg('html', '');

      // Add load animation to the textarea
      textarea.closest('form').find('.trumbowyg-editor').addClass('loading');
    },
    success: function(response) {

      // Enable the button after the request is complete
      eventBtn.prop('disabled', false);
      eventBtn.html('<span class="glyphicon glyphicon-download-alt"></span> Generate with AI');

      // Add load animation to the textarea
      textarea.closest('form').find('.trumbowyg-editor').removeClass('loading');

      if (response && response.jsonResponse) {
        const jsonResponse = response.jsonResponse;
        let text = JSON.parse(jsonResponse).content;

        if (!text) {
          console.error("No text found in the response.");
          text = "No text found in the response.";
        }

        // Convert Markdown to HTML
        const htmlText = convertMarkdownToHTML(text);

        startTyping(htmlText);
        
      } else {
        console.error("No text found in the response.");
        return "No text found in the response.";
      }
    },
    error: function(xhr, status, error) {
      console.error("Error fetching AI text:", error);
      return "Error fetching AI text.";
    }
  })

}

function convertMarkdownToHTML(markdownText) {
  // Convert Markdown to HTML using a simple regex-based approach
  // This is a basic implementation; consider using a library like marked.js for more complex Markdown
  let htmlText = markdownText
    .replace(/### (.*?)(\n|$)/g, '<h4>$1</h4>') // H4
    .replace(/## (.*?)(\n|$)/g, '<h3>$1</h3>') // H3
    .replace(/# (.*?)(\n|$)/g, '<h2>$1</h2>') // H2
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>') // Bold
    .replace(/\*(.*?)\*/g, '<em>$1</em>') // Italic
    .replace(/~~(.*?)~~/g, '<del>$1</del>') // Strikethrough
    .replace(/\[(.*?)\]\((.*?)\)/g, '<a href="$2">$1</a>') // Links
    .replace(/```(.*?)```/g, '<pre><code>$1</code></pre>') // Code blocks
    .replace(/`(.*?)`/g, '<code>$1</code>') // Inline code
    .replace(/^\s*-\s+(.*?)(\n|$)/gm, '<ul><li>$1</li></ul>') // Unordered lists
    .replace(/^\s*\d+\.\s+(.*?)(\n|$)/gm, '<ol><li>$1</li></ol>') // Ordered lists
    .replace(/\n/g, '<br>'); // New lines to <br>



  return htmlText;
}

function typeText(element, text, speed = 30) {
  let index = 0;
  
  // Clear the textarea first
  element.trumbowyg('html', '');
  
  function typeChar() {
    if (index < text.length) {
      const currentText = element.val();
      element.trumbowyg('html',currentText + text.charAt(index));
      index++;
      setTimeout(typeChar, speed);
    }
  }

  typeChar();
}

function startTyping(textToTypeParam) {
  const container = $('.iaPromptContainer');
  const textarea = container.find('textarea');

  const textToType = textToTypeParam; // Fetch the AI-generated text
  if (!textToType) {
    console.error("No text to type. Exiting function.");
    return; // Exit if no text is available
  }
  
  // Enable the textarea and focus on it
  textarea.trumbowyg('enable');
  textarea.focus();

  // Start typing with AI-like effect
  typeText(textarea, textToType, 1); // Faster speed for more AI-like feel
}

function copyToClipboard(e) {
  e.preventDefault();
  const textareaContainer = $(this).siblings('.col-md-12').find('.trumbowyg-editor');
  console.log("Copying formatted text to clipboard");
  
  if (textareaContainer.length > 0) {
    // Create a temporary div with the formatted content
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = textareaContainer.html();
    
    // Create a selection range
    const selection = window.getSelection();
    const range = document.createRange();
    
    // Clear any existing selection
    selection.removeAllRanges();
    
    // Select the temporary div
    document.body.appendChild(tempDiv);
    range.selectNodeContents(tempDiv);
    selection.addRange(range);
    
    // Execute the copy command
    document.execCommand('copy');
    
    // Clean up
    selection.removeAllRanges();
    document.body.removeChild(tempDiv);
    
    // Visual feedback
    const originalText = $(this).html();
    $(this).html('<span class="glyphicon glyphicon-ok"></span> Copied!');
    $(this).addClass('btn-success').removeClass('btn-copy');
    
    setTimeout(() => {
      $(this).html(originalText);
      $(this).removeClass('btn-success').addClass('btn-copy');
    }, 2000);
  }
}