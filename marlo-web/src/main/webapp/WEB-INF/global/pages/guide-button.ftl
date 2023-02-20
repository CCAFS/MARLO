[#ftl]

[#if canEdit || actionName == 'AICCRA/crpDashboard']
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
      [#if actionName == 'AICCRA/crpDashboard']
        <h3 style="margin-top: 10px;">[@s.text name="home.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="home.popup.descriptionAR" /]
      [#elseif actionName == 'AICCRA/description']
        <h3 style="margin-top: 10px;">[@s.text name="description.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="description.popup.descriptionAR" /]
      [#elseif actionName == 'AICCRA/partners']
        <h3 style="margin-top: 10px;">[@s.text name="partner.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="partner.popup.descriptionAR" /]
      [#elseif actionName == 'AICCRA/locations']
        <h3 style="margin-top: 10px;">[@s.text name="location.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="location.popup.descriptionAR" /]
      [#elseif actionName == 'AICCRA/contributionsCrpList' || actionName =='AICCRA/contributionCrp']
        <h3 style="margin-top: 10px;">[@s.text name="contribution.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="contribution.popup.descriptionAR" /]
      [#elseif actionName == 'AICCRA/studies' || actionName =='AICCRA/study']
        <h3 style="margin-top: 10px;">[@s.text name="oicrs.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="oicrs.popup.descriptionAR" /]
      [#elseif actionName == 'AICCRA/deliverable' || actionName =='AICCRA/deliverableList']
        <h3 style="margin-top: 10px;">[@s.text name="deliverable.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="deliverable.popup.descriptionAR" /]
      [#elseif actionName == 'AICCRA/innovationsList' || actionName =='AICCRA/innovation']
        <h3 style="margin-top: 10px;">[@s.text name="innovation.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="innovation.popup.descriptionAR" /]
      [#elseif actionName == "AICCRA/activities" ]
        <h3 style="margin-top: 10px;">[@s.text name="activity.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="activity.popup.descriptionAR" /]
      [#elseif actionName == "AICCRA/budgetByPartners" ]
        <h3 style="margin-top: 10px;">[@s.text name="budget.popup.title" /]</h3>   
        
          [@s.text name="budget.popup.descriptionAR" /]
      [#else]
        <div class="text-inter">
      [/#if] 
    [/#if]

    [#if POWB?exists && POWB ]
      [#if actionName == 'AICCRA/crpDashboard']
        <h3 style="margin-top: 10px;">[@s.text name="home.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="home.popup.descriptionAWPB" /]
      [#elseif actionName == 'AICCRA/description']
        <h3 style="margin-top: 10px;">[@s.text name="description.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="description.popup.descriptionAWPB" /]
      [#elseif actionName == 'AICCRA/partners']
        <h3 style="margin-top: 10px;">[@s.text name="partner.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="partner.popup.descriptionAWPB" /]
      [#elseif actionName == 'AICCRA/locations']
        <h3 style="margin-top: 10px;">[@s.text name="location.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="location.popup.descriptionAWPB" /]
      [#elseif actionName == 'AICCRA/contributionsCrpList' || actionName =='AICCRA/contributionCrp']
        <h3 style="margin-top: 10px;">[@s.text name="contribution.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="contribution.popup.descriptionAWPB" /]
      [#elseif actionName == 'AICCRA/studies' || actionName =='AICCRA/study']
        <h3 style="margin-top: 10px;">[@s.text name="oicrs.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="oicrs.popup.descriptionAWPB" /]
      [#elseif actionName == 'AICCRA/deliverable' || actionName =='AICCRA/deliverableList']
        <h3 style="margin-top: 10px;">[@s.text name="deliverable.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="deliverable.popup.descriptionAWPB" /]
      [#elseif actionName == 'AICCRA/innovationsList' || actionName =='AICCRA/innovation']
        <h3 style="margin-top: 10px;">[@s.text name="innovation.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="innovation.popup.descriptionAWPB" /]
      [#elseif actionName == "AICCRA/activities" ]
        <h3 style="margin-top: 10px;">[@s.text name="activity.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="activity.popup.descriptionAWPB" /]
      [#elseif actionName == "AICCRA/budgetByPartners" ]
        <h3 style="margin-top: 10px;">[@s.text name="budget.popup.title" /]</h3>   
        
          [@s.text name="budget.popup.descriptionAWPB" /]
      [#else]
        <div class="text-inter">
      [/#if] 
    [/#if]

    [#if UpKeepActive?exists &&  UpKeepActive]
      [#if actionName == 'AICCRA/crpDashboard']
        <h3 style="margin-top: 10px;">[@s.text name="home.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="home.popup.descriptionMY" /]
      [#elseif actionName == 'AICCRA/description']
        <h3 style="margin-top: 10px;">[@s.text name="description.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="description.popup.descriptionMY" /]
      [#elseif actionName == 'AICCRA/partners']
        <h3 style="margin-top: 10px;">[@s.text name="partner.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="partner.popup.descriptionMY" /]
      [#elseif actionName == 'AICCRA/locations']
        <h3 style="margin-top: 10px;">[@s.text name="location.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="location.popup.descriptionMY" /]
      [#elseif actionName == 'AICCRA/contributionsCrpList' || actionName =='AICCRA/contributionCrp']
        <h3 style="margin-top: 10px;">[@s.text name="contribution.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="contribution.popup.descriptionMY" /]
      [#elseif actionName == 'AICCRA/studies' || actionName =='AICCRA/study']
        <h3 style="margin-top: 10px;">[@s.text name="oicrs.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="oicrs.popup.descriptionMY" /]
      [#elseif actionName == 'AICCRA/deliverable' || actionName =='AICCRA/deliverableList']
        <h3 style="margin-top: 10px;">[@s.text name="deliverable.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="deliverable.popup.descriptionMY" /]
      [#elseif actionName == 'AICCRA/innovationsList' || actionName =='AICCRA/innovation']
        <h3 style="margin-top: 10px;">[@s.text name="innovation.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="innovation.popup.descriptionMY" /]
      [#elseif actionName == "AICCRA/activities" ]
        <h3 style="margin-top: 10px;">[@s.text name="activity.popup.title" /]</h3>   
        <div class="text-inter">
          [@s.text name="activity.popup.descriptionMY" /]
      [#elseif actionName == "AICCRA/budgetByPartners" ]
        <h3 style="margin-top: 10px;">[@s.text name="budget.popup.title" /]</h3>   
        
          [@s.text name="budget.popup.descriptionMY" /]
      [#else]
        <div class="text-inter">
      [/#if] 
    [/#if]
    </div> 
    </div>

    <div class="container-buttons-evidences">
      [#if reportingActive?exists && reportingActive]
        <a  target="_blank" href="${baseUrlCdn}/global/documents/MARLO_AICCRA_Annual_Report_Detailed_instructions.pdf" download>
      [/#if]
      [#if POWB?exists && POWB ]
        <a  target="_blank" href="${baseUrlCdn}/global/documents/AICCRA_Project_Planning_Detailed_Instructions.pdf" download>
      [/#if]
      [#if UpKeepActive?exists &&  UpKeepActive]
        <a  target="_blank" href="${baseUrlCdn}/global/documents/MARLO_AICCRA_Half_year_Report_Detailed_instructions.pdf" download>
      [/#if]
        <div class="button-pdf-modal" >
          <p>Read full guidance</p>
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