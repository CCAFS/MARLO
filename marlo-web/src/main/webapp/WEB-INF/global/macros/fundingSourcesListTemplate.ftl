[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]
[#macro list projects={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="description"]
  <table class="projectsList" id="projects">
    <thead>
      <tr class="header">
        <th colspan="7">Funding Source information</th>
        <th colspan="2">Actions</th> 
      </tr>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="projectTitles" >[@s.text name="projectsList.fundingTitle" /]</th>
        <th id="projectBudgetType" style="width:9%">[@s.text name="projectsList.projectBudgetType" /]</th>
        <th id="code" >Finance Code</th>
        <th id="projectStatus">[@s.text name="projectsList.projectStatus" /]</th>
        <th id="leader" >[@s.text name="projectsList.institutions" /]</th>
        <th id="projectDonor" >[@s.text name="projectsList.projectDonor" /]</th>
        <th id="fieldCheck" >[@s.text name="message.fieldsCheck.required" /]</th>
        <th id="projectDelete">[@s.text name="projectsList.delete" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if projects?has_content]
      [#list projects as project]
        [#assign hasDraft = (action.getAutoSaveFilePath(project.class.simpleName, "fundingSource", project.id))!false /]
        [#assign isCompleted = (action.getFundingSourceStatus(project.id))!false /]
        
        <tr>
        [#-- ID --]
        <td class="projectId">
          <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='fundingSourceID']${project.id}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]">FS${project.id}</a>
        </td>
          [#-- Funding source Title --]
          <td class="left"> 
            [#-- Draft Tag --]
            [#if hasDraft]<strong class="text-info">[DRAFT]</strong>[/#if]
            
            [#if project.fundingSourceInfo.title?has_content]
              <a href="[@s.url namespace=namespace action=defaultAction] [@s.param name='fundingSourceID']${project.id}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]" title="${project.fundingSourceInfo.title}">
              [#if project.fundingSourceInfo.title?length < 120] ${project.fundingSourceInfo.title}</a> [#else] [@utilities.wordCutter string=project.fundingSourceInfo.title maxPos=120 /]...</a> [/#if]
            [#else]
              <a href="[@s.url namespace=namespace action=defaultAction includeParams='get'] [@s.param name='fundingSourceID']${project.id}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url] ">
                [@s.text name="projectsList.title.none" /]
              </a>
            [/#if]
          </td>
          [#-- Project Budget Type --]
          <td class=""> 
            ${(project.fundingSourceInfo.budgetType.name)!'Not defined'} 
            [#if action.hasSpecificities('crp_fs_w1w2_cofinancing')] ${(project.fundingSourceInfo.w1w2?string('<br /> <span class="programTag">Co-Financing</span> ',''))!}[/#if]
          </td>
          [#-- Finance Code --]
          <td>
            [#if project.fundingSourceInfo.financeCode?has_content]${project.fundingSourceInfo.financeCode}[#else] <p class="text-muted">Not defined</p>  [/#if]
          </td>
          [#-- Project Status --]
          <td>
            ${(project.statusName)!'none'}
          </td>
          [#-- Center Lead --]
          <td class=""> 
            [#if project.institutions?has_content]
              [#list project.institutions as institutionLead]
                [#if institutionLead_index!=0]
                  <hr />
                [/#if]
                  <span class="name col-md-11">${(institutionLead.institution.acronym)!institutionLead.institution.name}</span>
                  <div class="clearfix"></div>
              [/#list]
              [#else]
              <p class="emptyText"> [@s.text name="No lead partner added yet." /]</p> 
            [/#if]
          </td>
          
          [#-- Donor --]
          <td class=""> 
            ${(project.fundingSourceInfo.institution.composedNameLoc)!'Not defined'}
          </td>
          
          [#-- Field Check --]
          <td class=""> 
            [#if isCompleted]
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
            [#if (action.canBeDeleted(project.id, project.class.name) && action.canAddFunding() && !crpClosed) ||action.canAccessSuperAdmin() ]
              <a id="removeDeliverable-${project.id}" class="removeProject" href="[@s.url namespace=namespace action="${(crpSession)!}/deleteFundingSources"][@s.param name='fundingSourceID']${project.id?c}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]" title="">
                <img src="${baseUrlMedia}/images/global/trash.png"/> 
              </a>
            [#else]
              <img src="${baseUrlMedia}/images/global/trash_disable.png" title="[@s.text name="projectsList.cannotDelete" /]" />
            [/#if]
          </td>
        </tr>  
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]