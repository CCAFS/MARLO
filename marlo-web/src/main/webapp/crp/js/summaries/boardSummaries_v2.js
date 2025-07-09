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
  $content.fadeIn();
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

const textToType = "(2024-2025) IPI 1.1: Climate-relevant knowledge products, decision-making tools and advisory services created or enhanced, including GSI dimensions (Number)\\n\\nTheme 1: Activities led by ILRI has made significant progress in developing knowledge products and decision support tools that help disseminate information on gender and socially inclusive CIS/CSA practices. By mid-year 2025, the cluster has achieved 5 out of 5 planned knowledge products, representing 100% of their annual target. These include the completed 'Learning Note: Global Goal on Adaptation progress on metrics selection' \[[https://hdl.handle.net/10568/175062\]](https://hdl.handle.net/10568/175062%5D), which provides insights on the UAE-Belem work program of the Global Goal on Adaptation, and a blog story highlighting the partnership with AUDA-NEPAD on soil health monitoring \[[https://aiccra.cgiar.org/news/equipping-au-member-states-monitoring-policy-and-technical-guidance-soil-health\]](https://aiccra.cgiar.org/news/equipping-au-member-states-monitoring-policy-and-technical-guidance-soil-health%5D). The cluster has also produced an insight brief on 'The Imperative for Strengthening Soil Information Systems in Africa' \[[https://hdl.handle.net/10568/174930\]](https://hdl.handle.net/10568/174930%5D), a report on 'Advancing Africa's Soil Health Monitoring to Support the Nairobi Declaration and CAADP Kampala Agenda' \[[https://hdl.handle.net/10568/175002\]](https://hdl.handle.net/10568/175002%5D), and a policy brief on 'Regional Policy Coherence for The Great Green Wall Initiative' \[[https://hdl.handle.net/10568/175287\]](https://hdl.handle.net/10568/175287%5D). Additionally, the cluster collaborated with partners to develop a submission to the Sharm el Sheikh workshop for SB62 \[[https://www4.unfccc.int/sites/SubmissionsStaging/Documents/202504291032---Submission%20by%20the%20%20United%20Rep.%20of%20Tanzania%20on%20behalf%20of%20the%20AGN%20WorkshopTopic1%20Submission%20FV%20SB62.pdf\]](https://www4.unfccc.int/sites/SubmissionsStaging/Documents/202504291032---Submission%20by%20the%20%20United%20Rep.%20of%20Tanzania%20on%20behalf%20of%20the%20AGN%20WorkshopTopic1%20Submission%20FV%20SB62.pdf%5D) and published a journal article on welfare effects of adopting CSA practices in Kenya \[[https://doi.org/10.1016/j.wds.2025.100204\]](https://doi.org/10.1016/j.wds.2025.100204%5D), which examines gender dimensions of CSA adoption.\\n\\nTheme 2: Activities led by the Alliance has made substantial progress in developing knowledge products and tools that support investment planning, innovation scaling, and responsible scaling of CSA/CIS solutions. By mid-year 2025, the cluster has achieved 9 out of 11 planned knowledge products, representing 82% of their annual target. Key deliverables include the 'AICCRA Scaling Framework' brief \[[https://hdl.handle.net/10568/173835\]](https://hdl.handle.net/10568/173835%5D), which introduces the concept of responsible scaling and provides examples of its application in AICCRA. The cluster has also developed a 'Compendium of impact pathways and indicators for adaptation tracking of SME investments' and is working on 'Next-gen Masterclass on adaptation tracking for public sector policy.' Other notable outputs include a report on 'Climate and finance risk assessments of 10 agribusinesses using AI-based tools' and a manual on 'Applying the science-to-business interface tool with 10 agribusinesses.' The cluster has also produced climate solution profiles for scaling in Mali and Zambia, and has supported the World Bank soil health innovation challenge through engagement, evaluation, and advisory processes.\\n\\nTheme 3: Gender and Social Inclusion Leader (Led by ILRI) has focused on integrating gender and social inclusion dimensions into climate-relevant knowledge products. By mid-year 2025, the cluster has achieved 5 out of 6 planned knowledge products, representing 83% of their annual target. Key deliverables include the completed 'AICCRA Gender and Social Inclusion: Looking Forward in Gender Smart Agriculture' info-note \[[https://hdl.handle.net/10568/175370\]](https://hdl.handle.net/10568/175370%5D), which summarizes the development of AICCRA GSI approaches and learning over the past five years. The cluster has also produced an 'Analysis of Gender Inclusion in African Long-Term Strategies' and a 'Gender Action Plan for the AICCRA III Concept Note.' Additionally, the cluster has completed an 'Analysis of Gender and Social Inclusion in NDC Planning and Implementation in sub-Saharan Africa' \[[https://hdl.handle.net/10568/175367\]](https://hdl.handle.net/10568/175367%5D) and a brief on 'Granular Gender and Climate Change Vulnerability Hotspot Mapping for Decision Support in Kenya' \[[https://www.agnesafrica.org/download/granular-gender-and-climate-change-vulnerability-hotspot-mapping-for-decision-support-in-kenya-report/\]](https://www.agnesafrica.org/download/granular-gender-and-climate-change-vulnerability-hotspot-mapping-for-decision-support-in-kenya-report/%5D). The cluster has also strengthened its social media presence with a LinkedIn post highlighting the gender and climate hotspot mapping exercises in Botswana, Uganda, and Kenya \[[https://www.linkedin.com/posts/agnes-africa-53bb172a9\climatechange-activity-7265642623602679808-mxjr/\].\\n\\n\\Theme](https://www.linkedin.com/posts/agnes-africa-53bb172a9climatechange-activity-7265642623602679808-mxjr/%5D.%5Cn%5CnTheme) 4: Activities led by Alliance\\ has made significant progress in developing climate-relevant knowledge products and decision-making tools. By mid-year 2025, the cluster has achieved 9 out of 14 planned knowledge products, representing 64% of their annual target. Key deliverables include the completed 'Bias-Aware AI in Agricultural Extension' info note \[[https://hdl.handle.net/10568/174306\]](https://hdl.handle.net/10568/174306%5D), which explores the transformative potential of AI in agricultural extension services with a focus on inclusivity and bias mitigation. The cluster has also produced a report on 'Introduction to Climate Information Services: AGNES Leadership Course' \[[https://hdl.handle.net/10568/175288\]](https://hdl.handle.net/10568/175288%5D) and a blog post on 'Strengthening Capacities and Linkages between Africa-Wide and Regional Institutions for Improved Climate Services' \[[https://alliancebioversityciat.org/stories/alliance-forges-strategic-partnership-pan-african-meteorological-body\]](https://alliancebioversityciat.org/stories/alliance-forges-strategic-partnership-pan-african-meteorological-body%5D). Additionally, the cluster has completed a report on 'Support for Coordinated Adaptation of the Climate Risk Management in Agricultural Extension (CRMAE) Curriculum for Livestock Production Systems' \[[https://hdl.handle.net/10568/175292\]](https://hdl.handle.net/10568/175292%5D), a working paper on 'Evaluating the Effectiveness of AI for Gender-Sensitive and Gender-Responsive Advisories' \[[https://hdl.handle.net/10568/175290\]](https://hdl.handle.net/10568/175290%5D), and a blog post on 'Tailoring innovative agricultural solutions to address Zambia's climate challenges' \[[https://aiccra.cgiar.org/news/tailoring-innovative-agricultural-solutions-address-zambias-climate-challenges\]](https://aiccra.cgiar.org/news/tailoring-innovative-agricultural-solutions-address-zambias-climate-challenges%5D). The cluster has also produced a video story on 'Women take the lead on building climate resilience together in Senegal' \[[https://www.youtube.com/watch?v=HNtCsIkyi1A\]](https://www.youtube.com/watch?v=HNtCsIkyi1A%5D) and a blog post on 'Against the Grain: How Targeting Women with Climate-Smart Innovations Amplifies Resilience' \[[https://foodtank.com/news/2025/04/how-targeting-women-with-climate-smart-innovations-amplifies-resilience/\].\\n\\n\\East](https://foodtank.com/news/2025/04/how-targeting-women-with-climate-smart-innovations-amplifies-resilience/%5D.%5Cn%5CnEast) and Southern Africa regional engagement (EA)\\ has made significant progress in developing climate-relevant knowledge products and decision-making tools. By mid-year 2025, the cluster has achieved 6 out of 10 planned knowledge products, representing 60% of their annual target. Key deliverables include the completed 'State of Climate in Africa Report 2024' \[[https://hdl.handle.net/10568/174715\]](https://hdl.handle.net/10568/174715%5D), which provides an assessment of past and current climate trends across the African continent. The cluster has also produced a journal article on 'Current and projected changes in climate extremes and agro-climatic zones over East Africa' \[[https://doi.org/10.1007/s00704-025-05405-2\]](https://doi.org/10.1007/s00704-025-05405-2%5D), a blog post on 'ASARECA and ILRI Lead Regional Consultations to Co-Design Climate-Smart Legume Production Guidelines in Eastern Africa' \[[https://www.asareca.org/asareca-and-ilri-lead-regional-consultations-to-co-design-climate-smart-legume-production-guidelines-in-eastern-africa/)\]](https://www.asareca.org/asareca-and-ilri-lead-regional-consultations-to-co-design-climate-smart-legume-production-guidelines-in-eastern-africa/\)%5D), and a report on 'Co-Designing Climate-Smart Legume Production Guides in the Southern Africa Region' \[[https://hdl.handle.net/10568/174975\]](https://hdl.handle.net/10568/174975%5D). The cluster has also enhanced the East Africa Hazards Watch system \[[https://eahazardswatch.icpac.net/\]](https://eahazardswatch.icpac.net/%5D) to incorporate new climate products and integrate alerting systems for improved access to risk information.\\n\\nWest Africa regional engagement (WA) has made progress in developing climate-relevant knowledge products and decision-making tools. By mid-year 2025, the cluster has achieved 1 out of 10 planned knowledge products, representing 10% of their annual target. The key deliverable completed is a journal article on 'A novel integrated computational approach for agroecological similarity' \[[https://doi.org/10.1016/j.envsoft.2025.106494\]](https://doi.org/10.1016/j.envsoft.2025.106494%5D), which presents an innovative computational approach to systematically evaluate similarities among agroecological sites. The cluster is also working on several other knowledge products, including a technical guide to seasonal and sub-seasonal climate forecasting in West Africa and the Sahel, a brief on gender and social inclusion in climate information based on AGRHYMET's experience, and a report on strengthening capacities on tools and methods for implementing climate-smart villages. Additionally, the cluster is documenting the use of CSA technologies and innovations disseminated through regional partners in spillover countries and supporting the uptake and use of CSA/CIS curricula in African higher education systems.\\n\\nEthiopia: Activities led by ILRI has made progress in developing climate-relevant knowledge products and decision-making tools. By mid-year 2025, the cluster has achieved 5 out of 10 planned knowledge products, representing 50% of their annual target. Key deliverables include five journal articles published on topics such as evaluating effects of Climate Smart Agricultural practices on productivity, adaptation, and mitigation indicators in Ethiopia; key determinants shaping farmers' satisfaction with site-specific fertilizer recommendations; climate information services enhancing farmers' resilience to climate change; impact of climate change on the productivity and adaptation of Ethiopia's Bonga and Menz sheep breeds; and status and determinants of multi-dimensional poverty in Wolaita Sodo Town. The cluster has also produced an info-note on 'The empowerment of women and youth in Ethiopia through small ruminants and access to agro-advisory services' \[[https://hdl.handle.net/10568/175368\]](https://hdl.handle.net/10568/175368%5D). Additionally, the cluster is working on improving the CSA knowledge hub, developing cropping calendars for wheat and maize value chains, and translating SmartPack guidelines and manuals into local languages.\\n\\nGhana: Activities led by IITA has made progress in developing climate-relevant knowledge products and decision-making tools. By mid-year 2025, the cluster has achieved 1 out of 5 planned knowledge products, representing 20% of their annual target. The cluster is deploying accelerator consortia-led scaling, with each consortia deploying tools to enhance access to and use of CIS-CSA bundles. These include the PUKPARA Platform to disseminate CS-IPM solutions to farmers via SMS, the Trotro Tractor digital platform for sustainable farm mechanization services, the Farm Radio International URLIZER tool to disseminate CIS and agro-advisories, and the IWMI-IRRILINE decision support tool to enhance farmer-led irrigation. The cluster is also working on improving the Cropping Calendar and agro-advisory tool and enhancing the EWRR-PD Scouting tool to include Fall Army Worm forecast as an early warning mechanism.\\n\\nKenya: Activities led by ILRI has made progress in developing climate-relevant knowledge products and decision-making tools. By mid-year 2025, the cluster has achieved 0 out of 15 planned knowledge products, representing 0% of their annual target. However, the cluster is working on several knowledge products, including the adaptation of the Climate Risk Management in Agricultural Extension (CRMAE) Curriculum for Livestock Production Systems, the development of guidelines on equitable and inclusive CIS and CSA in drylands, and the adaptation and deployment of the Intelligent Systems Advisory Tool (iSAT) for selected crops in the drylands of Kenya. The cluster is also working on adding High-Demand ENACTS Maprooms to the Kenya Meteorological Department's Data Library, including the Climate & Crop Suitability Maproom, which allows users to easily identify suitable areas for growth of crops with specified climatic parameters.\\n\\nMali: Activities led by AfricaRice has made significant progress in developing climate-relevant knowledge products and decision-making tools. By mid-year 2025, the cluster has achieved 5 out of 5 planned knowledge products, representing 100% of their annual target. Key deliverables include the 'Improved AgData Hub based on users' needs' \[[https://hdl.handle.net/10568/175206\]](https://hdl.handle.net/10568/175206%5D), which has been enhanced with features such as ARC2 rainfall data, decadal rainfall aggregation, and improved satellite imagery visualization. The cluster has also produced the 'Mali Climate Suitability for Crop Maproom' \[[https://hdl.handle.net/10568/175273\]](https://hdl.handle.net/10568/175273%5D), which helps assess regional suitability for crops based on rainfall and temperature, and the 'RIICE, A satellite-based digital platform for monitoring rice production and climate change impact in Mali' \[[https://hdl.handle.net/10568/175235\]](https://hdl.handle.net/10568/175235%5D), which provides national authorities with timely, reliable data for agricultural planning and food security monitoring. Additionally, the cluster has completed a working paper on 'Gender-based barriers hindering the uptake of CSA and CIS technologies in rice production systems in Mali' \[[https://hdl.handle.net/10568/175239\]](https://hdl.handle.net/10568/175239%5D) and a report on 'Assessing the potential area for rice-fish system in Mali' \[[https://hdl.handle.net/10568/169040\].\\n\\n\\Senegal](https://hdl.handle.net/10568/169040%5D.%5Cn%5CnSenegal): Activities led by ILRI\\ has made progress in developing climate-relevant knowledge products and decision-making tools. By mid-year 2025, the cluster has achieved 9 out of 17 planned knowledge products, representing 53% of their annual target. Key deliverables include the completed journal article 'Simulating pearl millet growth and yield with DSSAT-CERES-millet model' \[[https://doi.org/10.1079/ab.2025.0011\]](https://doi.org/10.1079/ab.2025.0011%5D), which evaluates climate-smart practices for uptake across diverse environments in Senegal. The cluster has also produced a book chapter on 'Strategies for building resilient pastoral and agropastoral systems in Africa' \[[https://irc2025.rangelandcongress.org/wp-content/uploads/2022/12/XII-IRC-Proceedings-Draft-com-compressed.pdf\]](https://irc2025.rangelandcongress.org/wp-content/uploads/2022/12/XII-IRC-Proceedings-Draft-com-compressed.pdf%5D), a blog post on 'AgData Hub: How climate data is transforming agricultural decisions in Senegal' \[[https://aiccra.cgiar.org/news/agdata-hub-comment-les-donnees-climatiques-transforment-les-decisions-agricoles-au-senegal\]](https://aiccra.cgiar.org/news/agdata-hub-comment-les-donnees-climatiques-transforment-les-decisions-agricoles-au-senegal%5D), and a blog post on 'Comment l'élevage laitier devient un levier d'innovation et de résilience porté par les femmes au Sénégal' \[[https://aiccra.cgiar.org/news/comment-lelevage-laitier-devient-un-levier-dinnovation-et-de-resilience-porte-par-les-femmes\]](https://aiccra.cgiar.org/news/comment-lelevage-laitier-devient-un-levier-dinnovation-et-de-resilience-porte-par-les-femmes%5D). Additionally, the cluster has completed a report on 'Jokalante CIS e-Learning Module Development Consultation' \[[https://hdl.handle.net/10568/175356\]](https://hdl.handle.net/10568/175356%5D) and has published the 'Pastoral Bulletin with new features' \[[https://www.linkedin.com/feed/update/urn:li:activity:7341047616069091329/\]](https://www.linkedin.com/feed/update/urn:li:activity:7341047616069091329/%5D), which now includes data on heat stress, fodder availability, state of boreholes, and bush fire.\\n\\nZambia: Activities led by IWMI has made progress in developing climate-relevant knowledge products and decision-making tools. By mid-year 2025, the cluster has achieved 6 out of 10 planned knowledge products, representing 60% of their annual target. Key deliverables include the completed 'Soil Fertility Map of Zambia and accompanying strategic framework to inform policy and planning' \[[https://hdl.handle.net/10568/174998\]](https://hdl.handle.net/10568/174998%5D), which provides a comprehensive overview of soil fertility across the country. The cluster has also operationalized the AgDataHub and deployed it with Smart Zambia \[[https://hdl.handle.net/10568/175007\]](https://hdl.handle.net/10568/175007%5D), and has developed the 'Blue Resilience' aquaculture advisory tool \[[https://hdl.handle.net/10568/175008\]](https://hdl.handle.net/10568/175008%5D), which aims to enhance knowledge sharing, promote best practices, and improve the productivity and sustainability of aquaculture in the region. The cluster is also working on several other knowledge products, including a journal publication on the development of climate advisory tools for crop-livestock enterprises in Zambia, a report on CSA-CIS demand-driven research, and a report on business incubation for CSA-CIS innovation commercialization.\\n\\n Information for Summary Table\\n\\nBy mid-year 2025, AICCRA has made significant progress in developing climate-relevant knowledge products, decision-making tools, and advisory services. The project has focused on creating or enhancing tools that include gender and social inclusion dimensions, with many deliverables already completed and disseminated. By mid-year 2025, AICCRA had already achieved 49 out of 114 planned knowledge products, representing 42.98% progress for indicator IPI 1.1.";

function typeText(element, text, speed = 30) {
  let index = 0;
  
  // Clear the textarea first
  element.val('');
  
  function typeChar() {
    if (index < text.length) {
      const currentText = element.val();
      element.val(currentText + text.charAt(index));
      index++;
      setTimeout(typeChar, speed);
    }

    //if the text is fully typed, enable the textarea
    if (index === text.length) {
      element.prop('disabled', true);
    }
  }

  typeChar();
}

function startTyping(e) {
  e.preventDefault();
  const container = $('.iaPromptContainer');
  const textarea = container.find('textarea');

  // Show the container
  container.show();
  
  // Enable the textarea and focus on it
  textarea.prop('disabled', false);
  textarea.focus();

  // Start typing with AI-like effect
  typeText(textarea, textToType, 5); // Faster speed for more AI-like feel
}

function copyToClipboard(e) {
  e.preventDefault();
  const textarea = $(this).siblings('.col-md-12').find('.aiPrompt');
  
  if (textarea.length > 0) {
    textarea.select();
    document.execCommand('copy');
    
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