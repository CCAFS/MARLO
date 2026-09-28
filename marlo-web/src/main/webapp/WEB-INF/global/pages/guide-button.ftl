[#ftl]

[#assign currentActionName = (actionName!'')]
[#assign actionPath = currentActionName?contains("/")?then(
  currentActionName?substring(currentActionName?index_of("/") + 1), currentActionName)]

[#if canEdit || actionPath == 'crpDashboard']
<div id="guide-button" class="guide-button" >
  <img src="${baseUrlCdn}/global/images/guideButton.png" />
</div>
[/#if]

  <div class="popup-guide animate__animated animate__bounce" id="guide">
    <div class="button-exit close-modal-evidences">
      <div class="x-close-modal" id="x-close-modal-guide" ></div>
    </div>

    <p class="title-modal-evidences">Guide to this section</p>
    <div class="line-modal" ></div>
    <div class="text-modal-evidences">
    
    [#if reportingActive?exists && reportingActive]
      [#if actionPath == 'crpDashboard']
        <h3 >[@s.text name="guide.button.home.popup.title" /]</h3>    
        <div class="text-inter">
          [@s.text name="guide.button.home.popup.descriptionAR" /]
      [#elseif actionPath == 'description']
        <h3 >[@s.text name="guide.button.description.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.description.popup.descriptionAR" /]
      [#elseif actionPath == 'partners']
        <h3 >[@s.text name="guide.button.partner.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.partner.popup.descriptionAR" /]
      [#elseif actionPath == 'locations']
        <h3 >[@s.text name="guide.button.location.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.location.popup.descriptionAR" /]
      [#elseif actionPath == 'contributionsCrpList']
        <h3 >[@s.text name="guide.button.contribution.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.contributionList.popup.descriptionAR" /]
      [#elseif actionPath == 'contributionCrp']
        <h3 >[@s.text name="guide.button.contribution.popup.title" /]</h3>   
        <div class="text-inter">
          [#if projectOutcome.crpProgramOutcome.crpProgram.acronym ==' PDO']
            [@s.text name="guide.button.contributionPDO.popup.descriptionAR" /]
          [#else]
            [@s.text name="guide.button.contribution.popup.descriptionAR" /]
          [/#if]  
      [#elseif actionPath == 'studies']
        <h3 >[@s.text name="guide.button.oicrs.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.oicrsList.popup.descriptionAR" /]
      [#elseif actionPath == 'study']
        <h3 >[@s.text name="guide.button.oicrs.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.oicrs.popup.descriptionAR" /]
      [#elseif actionPath == 'deliverableList']
        <h3 >[@s.text name="guide.button.deliverable.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.deliverableList.popup.descriptionAR" /]
      [#elseif actionPath == 'deliverable']
        <h3 >[@s.text name="guide.button.deliverable.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.deliverable.popup.descriptionAR" /]
      [#elseif actionPath == 'innovationsList' || actionPath == 'innovation']
        <h3 >[@s.text name="guide.button.innovation.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.innovation.popup.descriptionAR" /]
      [#elseif actionPath == "activities" ]
        <h3 >[@s.text name="guide.button.activity.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.activity.popup.descriptionAR" /]
      [#elseif actionPath == "budgetByPartners" ]
        <h3 >[@s.text name="guide.button.budget.popup.title" /]</h3>   
        
          [@s.text name="guide.button.budget.popup.descriptionAR" /]
      [#else]
        <div class="text-inter">
      [/#if] 
    [/#if]

    [#if POWB?exists && POWB ]
      [#if actionPath == 'crpDashboard']
        <h3 >[@s.text name="guide.button.home.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.home.popup.descriptionAWPB" /]
      [#elseif actionPath == 'description']
        <h3 >[@s.text name="guide.button.description.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.description.popup.descriptionAWPB" /]
      [#elseif actionPath == 'partners']
        <h3 >[@s.text name="guide.button.partner.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.partner.popup.descriptionAWPB" /]
      [#elseif actionPath == 'locations']
        <h3 >[@s.text name="guide.button.location.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.location.popup.descriptionAWPB" /]
      [#elseif actionPath == 'contributionsCrpList']
        <h3 >[@s.text name="guide.button.contribution.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.contributionList.popup.descriptionAWPB" /]
      [#elseif actionPath == 'contributionCrp']
        <h3 >[@s.text name="guide.button.contribution.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.contribution.popup.descriptionAWPB" /]
      [#elseif actionPath == 'studies']
        <h3 >[@s.text name="guide.button.oicrs.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.oicrsList.popup.descriptionAWPB" /]
      [#elseif actionPath == 'study']
        <h3 >[@s.text name="guide.button.oicrs.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.oicrs.popup.descriptionAWPB" /]
      [#elseif actionPath == 'deliverableList']
        <h3 >[@s.text name="guide.button.deliverable.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.deliverableList.popup.descriptionAWPB" /]
      [#elseif actionPath == 'deliverable']
        <h3 >[@s.text name="guide.button.deliverable.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.deliverable.popup.descriptionAWPB" /]
      [#elseif actionPath == 'innovationsList' || actionPath == 'innovation']
        <h3 >[@s.text name="guide.button.innovation.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.innovation.popup.descriptionAWPB" /]
      [#elseif actionPath == "activities" ]
        <h3 >[@s.text name="guide.button.activity.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="guide.button.activity.popup.descriptionAWPB" /]
      [#elseif actionPath == "budgetByPartners" ]
        <h3 >[@s.text name="guide.button.budget.popup.title" /]</h3>   
        
          [@s.text name="guide.button.budget.popup.descriptionAWPB" /]
      [#else]
        <div class="text-inter">
      [/#if] 
    [/#if]

    [#if UpKeepActive?exists &&  UpKeepActive]
      [#if actionPath == 'crpDashboard']
        <h3 >[@s.text name="home.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="home.popup.descriptionMY" /]
      [#elseif actionPath == 'description']
        <h3 >[@s.text name="description.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="description.popup.descriptionMY" /]
      [#elseif actionPath == 'partners']
        <h3 >[@s.text name="partner.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="partner.popup.descriptionMY" /]
      [#elseif actionPath == 'locations']
        <h3 >[@s.text name="location.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="location.popup.descriptionMY" /]
      [#elseif actionPath == 'contributionsCrpList']
        <h3 >[@s.text name="contribution.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="contributionList.popup.descriptionMY" /]
      [#elseif actionPath == 'contributionCrp']
        <h3 >[@s.text name="contribution.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="contribution.popup.descriptionMY" /]
      [#elseif actionPath == 'studies']
        <h3 >[@s.text name="oicrs.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="oicrsList.popup.descriptionMY" /]
      [#elseif actionPath == 'study']
        <h3 >[@s.text name="oicrs.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="oicrs.popup.descriptionMY" /]
      [#elseif actionPath == 'deliverableList']
        <h3 >[@s.text name="deliverable.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="deliverableList.popup.descriptionMY" /]
      [#elseif actionPath == 'deliverable']
        <h3 >[@s.text name="deliverable.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="deliverable.popup.descriptionMY" /]
      [#elseif actionPath == 'innovationsList']
        <h3 >[@s.text name="innovation.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="innovationList.popup.descriptionMY" /]
      [#elseif actionPath == 'innovation']
        <h3 >[@s.text name="innovation.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="innovation.popup.descriptionMY" /]
      [#elseif actionPath == "activities" ]
        <h3 >[@s.text name="activity.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="activity.popup.descriptionMY" /]
      [#elseif actionPath == "budgetByPartners" ]
        <h3 >[@s.text name="budget.popup.title" /]</h3>   
        
          [@s.text name="budget.popup.descriptionMY" /]
      [#else]
        <div class="text-inter">
      [/#if] 
    [/#if]
    </div> 
    </div>
    <div class="line-modal-bottom" ></div>
    <div class="container-buttons-evidences">
      [#if reportingActive?exists && reportingActive]
        <a  target="_blank" href="${baseUrlCdn}/global/documents/MARLO_AICCRA_Annual_Report_Detailed_instructions.pdf">
      [/#if]
      [#if POWB?exists && POWB ]
        <a  target="_blank" href="${baseUrlCdn}/global/documents/AICCRA_Project_Planning_Detailed_Instructions.pdf">
      [/#if]
      [#if UpKeepActive?exists &&  UpKeepActive]
        <a  target="_blank" href="${baseUrlCdn}/global/documents/MARLO_AICCRA_Mid_year_Report_Detailed_instructions.pdf">
      [/#if]
        
        <div class="button-pdf-modal" >
          <p>See full MARLO guidance</p>
          <img src="${baseUrlCdn}/global/images/pdf.png" alt="Download document" />
        </div>
      </a>

    </div>
    
  </div>


  <script>
    $('#guide-button').click(function() {
      if ($('.popup-guide').is(':visible')) {
        $('.popup-guide').slideUp();
      } else {
        
        $('.popup-guide').slideDown();
        
      }
    });
    
    $('#x-close-modal-guide').click(function() {     
        $('.popup-guide').slideUp();
    });
    
    
  </script>