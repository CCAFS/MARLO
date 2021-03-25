[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]
[#macro list projects={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="description"]
  <table class="projectsList" id="projects">
    <thead>
      <tr class="header">
        <th colspan="8">Funding Source information</th>
        <th colspan="3">Actions</th> 
      </tr>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="projectTitles" >[@s.text name="projectsList.fundingTitle" /]</th>
        <th id="projectBudgetType" style="width:9%">[@s.text name="projectsList.projectBudgetType" /]</th>
        <th id="code" >Finance Code</th>
        [#-- <th id="projectStatus">[@s.text name="projectsList.projectStatus" /]</th> --]
        <th id="leader" >[@s.text name="projectsList.institutions" /]</th>
        <th id="endDate" >[@s.text name="projectsList.endDate" /]</th>
        <th id="projectDonor" >[@s.text name="projectsList.projectDonor" /]</th>
        <th id="projectDonor" >[@s.text name="projectsList.originalDonor" /]</th>
        <th id="fieldCheck" ></th>
        <th id="projectDelete">[@s.text name="projectsList.delete" /]</th>
        <th id="projectCopy">[@s.text name="projectsList.copy" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if projects?has_content]
      [#list projects as project]
      
        [#assign hasDraft = (action.getAutoSaveFilePath(project.class.simpleName, "fundingSource", project.id))!false /]
       
        [#assign isCompleted = !(action.hasFundingSourcesMissingFields(project.id)) /]
        
        <tr>
        [#-- ID --]
        <td class="projectId">
          <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='fundingSourceID']${project.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">FS${project.id}</a>
        </td>
          [#-- Funding source Title --]
          <td class="left"> 
            [#-- Draft Tag --]
            [#if hasDraft]<strong class="text-info">[DRAFT]</strong>[/#if]
            
            [#if project.fundingSourceInfo.title?has_content]
              <a href="[@s.url namespace=namespace action=defaultAction] [@s.param name='fundingSourceID']${project.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" title="${project.fundingSourceInfo.title}">
              [#if project.fundingSourceInfo.title?length < 120] ${project.fundingSourceInfo.title}</a> [#else] [@utilities.wordCutter string=project.fundingSourceInfo.title maxPos=120 /]...</a> [/#if]
            [#else]
              <a href="[@s.url namespace=namespace action=defaultAction includeParams='get'] [@s.param name='fundingSourceID']${project.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url] ">
                [@s.text name="projectsList.title.none" /]
              </a>
            [/#if]
            [#-- Funding Source Dates --]
            [#if ((project.fundingSourceInfo.startDate??)!false) && ((project.fundingSourceInfo.startDate??)!false) ]
              <p><small class="text-gray">(${(project.fundingSourceInfo.startDate)!} - [#if (project.fundingSourceInfo.extensionDate??)!false] ${(project.fundingSourceInfo.extensionDate)!} [#else] ${(project.fundingSourceInfo.endDate)!}[/#if])</small></p>
            [/#if]
          </td>
          [#-- Project Budget Type --]
          <td class=""> 
            [#--  ${(project.fundingSourceInfo.budgetTypeName)!'Not defined'} <p><small> US$ <span> ${((action.getFundingSourceBudgetPerPhase(project.id))!0)?number?string(",##0.00")}</span></small></p>--]
            ${(project.fundingSourceInfo.budgetType.name)!'Not defined'} <p><small> US$ <span> ${((action.getFundingSourceBudgetPerPhase(project.id))!0)?number?string(",##0.00")}</span></small></p>
            [#if action.hasSpecificities('crp_fs_w1w2_cofinancing')] ${(project.fundingSourceInfo.w1w2?string('<br /> <span class="programTag">Co-Financing</span> ',''))!}[/#if]
          </td>
          [#-- Finance Code --]          
          <td style="position:relative">
            [#if project.fundingSourceInfo.financeCode?has_content]
              [#assign isSynced = (project.fundingSourceInfo.synced)!false ]
              [#-- Icon --]
              [#if isSynced]<span title="Synced on ${(project.fundingSourceInfo.syncedDate)!''}" class="glyphicon glyphicon-retweet" style="color: #2aa4c9;"></span>[/#if]
              [#-- Code --]
              <span [#if isSynced]style="color: #2aa4c9;"[/#if]>${project.fundingSourceInfo.financeCode}</span>
              
              [#if (project.fundingSourceInfo.leadCenter.acronym?has_content)!false ]
                <br /><small>(${project.fundingSourceInfo.leadCenter.acronym})</small>
              [/#if]
              
            [#else]
              <p class="text-muted" style="opacity:0.5">Not defined</p>
            [/#if]
          </td>
          [#-- Project Status 
          <td>
            ${(project.statusName)!'none'}
          </td>
          --]
          [#-- Center Lead --]
          
          <td class="institutionLead"> 
            [#if project.institutions?has_content]
              <div class="institutions-list mCustomScrollbar" data-mcs-theme="dark">
                [#list project.institutions as institutionLead]
                  [#if institutionLead_index!=0]<hr />
                  [/#if]
                  ${(institutionLead.institution.acronym)!institutionLead.institution.name}
                [/#list]
              </div>
            [#else]
              <p class="emptyText"> [@s.text name="No lead partner added yet." /]</p> 
            [/#if]
          </td>
          [#-- End Date --]
          <td class="">
            [#if (project.fundingSourceInfo.status)?? || project.fundingSourceInfo.status=4]
              [#local fsEndDate][#if (project.fundingSourceInfo.extensionDate??)!false]${(project.fundingSourceInfo.extensionDate)!}[#else]${(project.fundingSourceInfo.endDate)!}[/#if][/#local]
            [#else]
              [#local fsEndDate]${(project.fundingSourceInfo.endDate)!}[/#local]
            [/#if]
            
            [#if fsEndDate?has_content]
              [#local fsYear = fsEndDate?date?string('yyyy')?number ]
              [#local validDate = (fsYear >= actualPhase.year)!false ]
              <span class="hidden">${fsYear}</span>
              <nobr><p class="${(!validDate)?string('fieldError', '')}">${fsEndDate}</p></nobr>
            [#else]
              <p style="opacity:0.5">Not defined</p>
            [/#if]
            
          </td>
          [#-- Direct Donor --]
          <td class="" title="${(project.fundingSourceInfo.directDonor.composedName)!}">
            ${(project.fundingSourceInfo.directDonor.acronymName)!'<p style="opacity:0.5">Not defined</p>'}
          </td>
          
          [#-- Original Donor --]
          <td class="" title="${(project.fundingSourceInfo.originalDonor.composedName)!}"> 
            ${(project.fundingSourceInfo.originalDonor.acronymName)!'<p style="opacity:0.5">Not defined</p>'}
          </td>
          
          [#-- Field Check --]
          <td class=""> 
            [#if isCompleted && !hasDraft]
              <span class="hide">true</span>  <span class="icon-20 icon-check" title="[@s.text name="message.fieldsCheck.complete" /]"></span>
            [#else]
              [#if hasDraft]
                <span class="hide">false</span> <span class="icon-20 icon-uncheck" title="[@s.text name="message.fieldsCheck.draft" ][@s.param][@s.text name="global.fundingSource" /][/@s.param][/@s.text]"></span> 
              [#else]
                <span class="hide">false</span> <span class="icon-20 icon-uncheck" title="[@s.text name="message.fieldsCheck.incomplete"][@s.param][@s.text name="global.fundingSource" /][/@s.param][/@s.text]"></span> 
              [/#if]
            [/#if]
          </td>
        
          [#-- Delete Project--]
          <td class="text-center">
            [#if (action.canBeDeleted(project.id, project.class.name) && action.canAddFunding() && !crpClosed)   && action.getActualPhase().editable]
              <a id="removeDeliverable-${project.id}" class="removeProject" href="[@s.url namespace=namespace action="${(crpSession)!}/deleteFundingSources"][@s.param name='fundingSourceID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" title="">
                <img src="${baseUrlCdn}/global/images/trash.png"/> 
              </a>
            [#else]
              <img src="${baseUrlCdn}/global/images/trash_disable.png" title="[@s.text name="projectsList.cannotDelete" /]" />
            [/#if]
          </td>
          
          [#-- Create Copy--]
          <td class="text-center">
            [#if (action.canDuplicateFunding() && !crpClosed) && action.getActualPhase().editable]
              <a id="copyDeliverable-${project.id}" class="copyProject" href="[@s.url namespace=namespace action="${(crpSession)!}/copyFundingSource"][@s.param name='fundingSourceID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" title="">
                <img src="${baseUrlCdn}/global/images/duplicate_enabled.png"/> 
              </a>
            [#else]
              <img src="${baseUrlCdn}/global/images/duplicate_disabled.png" title="[@s.text name="projectsList.cannotDuplicate" /]" />
            [/#if]
          </td>
        </tr>  
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]


[#macro archivedList projects={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="description"]
  <table class="projectsList" id="projects">
    <thead>
      <tr class="header">
        <th colspan="8">Funding Source information</th>
        
      </tr>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="projectTitles" >[@s.text name="projectsList.fundingTitle" /]</th>
        <th id="projectBudgetType" style="width:9%">[@s.text name="projectsList.projectBudgetType" /]</th>
        <th id="code" >Finance Code</th>
        [#-- <th id="projectStatus">[@s.text name="projectsList.projectStatus" /]</th> --]
        <th id="leader" >[@s.text name="projectsList.institutions" /]</th>
        <th id="endDate" >[@s.text name="projectsList.endDate" /]</th>
        <th id="projectDonor" >[@s.text name="projectsList.projectDonor" /]</th>
        <th id="projectDonor" >[@s.text name="projectsList.originalDonor" /]</th>
        
      </tr>
    </thead>
    <tbody>
    [#if projects?has_content]
      [#list projects as project]
        [#assign hasDraft = (action.getAutoSaveFilePath(project.class.simpleName, "fundingSource", project.id))!false /]
        <tr>
        [#-- ID --]
        <td class="projectId">
          <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='fundingSourceID']${project.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">FS${project.id}</a>
        </td>
          [#-- Funding source Title --]
          <td class="left"> 
            [#-- Draft Tag --]
            [#if hasDraft]<strong class="text-info">[DRAFT]</strong>[/#if]
            
            [#if project.fundingSourceInfo.title?has_content]
              <a href="[@s.url namespace=namespace action=defaultAction] [@s.param name='fundingSourceID']${project.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" title="${project.fundingSourceInfo.title}">
              [#if project.fundingSourceInfo.title?length < 120] ${project.fundingSourceInfo.title}</a> [#else] [@utilities.wordCutter string=project.fundingSourceInfo.title maxPos=120 /]...</a> [/#if]
            [#else]
              <a href="[@s.url namespace=namespace action=defaultAction includeParams='get'] [@s.param name='fundingSourceID']${project.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url] ">
                [@s.text name="projectsList.title.none" /]
              </a>
            [/#if]
            [#-- Funding Source Dates --]
            [#if ((project.fundingSourceInfo.startDate??)!false) && ((project.fundingSourceInfo.startDate??)!false) ]
              <p><small class="text-gray">(${(project.fundingSourceInfo.startDate)!} - [#if (project.fundingSourceInfo.extensionDate??)!false] ${(project.fundingSourceInfo.extensionDate)!} [#else] ${(project.fundingSourceInfo.endDate)!}[/#if])</small></p>
            [/#if]
          </td>
          [#-- Project Budget Type --]
          <td class=""> 
            ${(project.fundingSourceInfo.budgetType.name)!'Not defined'} 
            [#if action.hasSpecificities('crp_fs_w1w2_cofinancing')] ${(project.fundingSourceInfo.w1w2?string('<br /> <span class="programTag">Co-Financing</span> ',''))!}[/#if]
          </td>
          [#-- Finance Code --]
          <td>
            [#if project.fundingSourceInfo.financeCode?has_content]
              ${project.fundingSourceInfo.financeCode}
              [#if (project.fundingSourceInfo.leadCenter.acronym?has_content)!false ]
                <br /><small>(${project.fundingSourceInfo.leadCenter.acronym})</small>
              [/#if]
            [#else] 
              <p class="text-muted">Not defined</p>
            [/#if]
          </td>
          [#-- Project Status
          <td>
            ${(project.statusName)!'none'}
          </td>
           --]
          [#-- Center Lead --]
          <td class="institutionLead"> 
            [#if project.institutions?has_content]
              <div class="institutions-list mCustomScrollbar" data-mcs-theme="dark">
              [#list project.institutions as institutionLead]
                [#if institutionLead_index!=0]
                  <hr />
                [/#if]
                  <span class="name col-md-11">${(institutionLead.institution.acronym)!institutionLead.institution.name}</span>
                  <div class="clearfix"></div>
              [/#list]
              </div>
              [#else]
              <p class="emptyText"> [@s.text name="No lead partner added yet." /]</p> 
            [/#if]
          </td>
          
           <td class=""> 
            ${(project.fundingSourceInfo.endDate)!'Not defined'}
          </td>
          
          [#-- Direct Donor --]
          <td class=""> 
            ${(project.fundingSourceInfo.directDonor.acronymName)!'Not defined'}
          </td>
          
          [#-- Original Donor --]
          <td class=""> 
            ${(project.fundingSourceInfo.originalDonor.acronymName)!'Not defined'}
          </td>
        </tr>  
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]


[#macro institutionsFilter institutions={}]
  <div class="institutions-filter">
   <div class="filter-title portfolio">Filter by Institutions</div>
   <div class="items-list">
   [@customForm.input name="searchInstitutions" showTitle=false help="" placeholder="Search institution" className="searchInstitutions" editable=true /]
   <div class="filter-list-buttons">
    <button type="button" id="selectAllInstitutions" class="btn btn-link">Select all </button> -  <button type="button" id="clearAllInstitutions" class="btn btn-link">Clear all </button>
   </div>
   [@s.form namespace="/fundingSources" action='${(crpSession)!}/fundingSourcesList' method="GET" enctype="multipart/form-data" cssClass=""]
     <ul class="filter-items">
     <input type="hidden" name="phaseID" value="${(actualPhase.id)!}" />
     [#if institutions?has_content]
      [#list institutions as institution]
        [#local institutionName = (institution.institution.acronym)!institution.institution.name /]
        [#local institutionId = (institution.institution.id)!0 /]
        <li>[@customForm.checkmark id="${institutionName}" label="${institutionName}" name="" cssClass="institutionsFilter" value="${institutionId}" checked=institution.isChecked editable=true centered=true /]</li>
      [/#list]
     [/#if]
     </ul>
     <input type="hidden" id="institutionsID" name="institutionsID" value="" />
     <button type="submit" class="filter-btn">Filter</button>
    [/@s.form]
   </div>
  </div>

[/#macro]

[#macro selectedInstitutions institutions = {}]
  [#if institutions?has_content]
   <div class="listOfFilters">
    Filtered by: 
    [#list institutions as institution]
     [#local selectedInstitutionName = (institution.acronym)!institution.name /]
     ${selectedInstitutionName}[#if institution?has_next],[/#if]
    [/#list]
   </div>
  [/#if]
[/#macro]