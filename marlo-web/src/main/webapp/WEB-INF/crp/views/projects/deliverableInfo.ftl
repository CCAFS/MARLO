[#ftl]
<div class="simpleBox">
  [#-- Title input --] 
  <div class="form-group">
    [@customForm.input name="deliverable.deliverableInfo.title" value="${(deliverable.deliverableInfo.title)!}" type="text" i18nkey="project.deliverable.generalInformation.title"  placeholder="" className="limitWords-25" required=true editable=editable /]
  </div>
  
  [#-- Description input on Planning only --] 
  <div class="form-group">
    [@customForm.textArea name="deliverable.deliverableInfo.description" value="${(deliverable.deliverableInfo.description)!}" i18nkey="project.deliverable.generalInformation.description"  placeholder="" className="limitWords-50" required=true editable=editable /]
  </div> 
  [#-- Type and subtype inputs --] 
  <div class="form-group row">
    <div class="col-md-6 ">
      [@customForm.select name="deliverable.deliverableInfo.deliverableType.deliverableCategory.id" label=""  i18nkey="project.deliverable.generalInformation.type" listName="deliverableTypeParent" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm typeSelect" editable=editable/]
    </div>
    <div class="col-md-6 subType-select">
      [@customForm.select name="deliverable.deliverableInfo.deliverableType.id" label=""  i18nkey="project.deliverable.generalInformation.subType" listName="deliverableSubTypes" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm subTypeSelect" editable=editable/]
    </div>
  </div>
  [#-- Deliverable table with categories and sub categories --]
  <div class="form-group deliverableTypeMessage">
    <div id="dialog" title="Deliverable types" style="display: none">
      <table id="deliverableTypes" style="height:700px; width:950px;">
        <th> [@s.text name="project.deliverables.dialogMessage.part1" /] </th>
        <th> [@s.text name="project.deliverables.dialogMessage.part2" /] / [@s.text name="project.deliverables.dialogMessage.part3" /] </th>
        [#if deliverableTypeParent?has_content]
        [#list deliverableTypeParent as mt]
          [#list action.getDeliverablesSubTypes(mt.id) as st]
            <tr>
              [#if st_index == 0]<th rowspan="${action.getDeliverablesSubTypes(mt.id).size()}" class="text-center"> ${mt.name} </th>[/#if]
              <td> 
                ${st.name} 
                [#if ((st.description?has_content)!false) && (st.description != st.name)]<br /> (<i><small>${st.description}</small></i>)[/#if]
                [#if mt.fair || st.fair]<span class="label label-info pull-right">FAIR</span>[/#if]
              </td>
            </tr>
          [/#list]
        [/#list]
        [/#if]  
      </table>
    </div> <!-- End dialog-->

    <div class="note left">
      <div id="popup" class="helpMessage3">
        <p><a id="opener"> <span class="glyphicon glyphicon-info-sign"></span> [@s.text name="project.deliverable.generalInformation.deliverableType" /]</a></p>
      </div>
    </div>
    <div class="clearfix"></div>
  </div>
  
  
  <div class="clearfix"></div>
  [#-- Status and year expected selects --]
  <div class="${reportingActive?string('fieldFocus','simpleBox')}">
    
    <div class="form-group row">
      [#--  [#assign canViewNewExpectedYear = action.candEditExpectedYear(deliverable.id) /]  --]
      [#assign hasExpectedYear = ((deliverable.deliverableInfo.newExpectedYear != -1))!false /]
      [#assign isStatusExtended = (deliverable.deliverableInfo.status == 4)!false]
      [#assign isStatusPartiallyComplete = (deliverable.deliverableInfo.status == 7)!false]

      [#-- Deliverable Status --]
      <div class="col-md-4">
        [@customForm.select name="deliverable.deliverableInfo.status" label=""   i18nkey="project.deliverable.generalInformation.status" listName="status"  multiple=false required=true header=false className="status isNew-${isDeliverableNew?string}" editable=editable || editStatus/]
      </div>
      
      [#-- Deliverable Year --]
      <div id="deliverableYear" class="col-md-4 form-group">
        [#--  [#assign canNotEditYear = (deliverable.deliverableInfo.status == 4)!false || !action.candEditYear(deliverable.id)/]  --]
         [#assign dbExpectedYear = ((deliverable.deliverableInfo.year)!currentCycleYear)  ]
         [#if isDeliverableNew && reportingActive]
          [#assign projectExpectedYear = "project.projectInfo.getYearActualPhase(${currentCycleYear})"]
          [#else]
          [#assign projectExpectedYear = "project.projectInfo.getAllYearsPhase(${dbExpectedYear})"]
         [/#if]
        [#if editable ]
          <div class="overlay" style="display:${((!isDeliverableNew || isStatusExtended || hasExpectedYear))?string('block', 'none')}"></div>
          [@customForm.select name="deliverable.deliverableInfo.year" label=""  i18nkey="project.deliverable.generalInformation.year" listName=projectExpectedYear header=false required=true className="yearExpected"  /]
        [#else]
           <div class="select">
            <label for="">[@s.text name="project.deliverable.generalInformation.year" /]:</label>
            <p>${(deliverable.deliverableInfo.year)!}</p>
            <input type="hidden" name="deliverable.deliverableInfo.year" value="${(deliverable.deliverableInfo.year)!}"/>
          </div>
        [/#if]
      </div>
      
      [#-- New Expected Year - Extended = 4 or exist--]
      <div id="newExpectedYear" class="col-md-4" style="display:${(isStatusExtended)?string('block','none')}">
        [#assign startExpectedYear = ((deliverable.deliverableInfo.year)!currentCycleYear)  ]
        [#if editable || editStatus]
          <div class="overlay expectedDisabled" style="display:${(!isStatusExtended)?string('block', 'none')}"></div>
          [@customForm.select name="deliverable.deliverableInfo.newExpectedYear"  i18nkey="deliverable.newExpectedYear"  listName="project.projectInfo.getYears(${startExpectedYear})" header=true  multiple=false required=true  className="yearNewExpected" editable=editable || editStatus/]
        [#else]
          <div class="select">
            <label for="">[@s.text name="deliverable.newExpectedYear" /]:</label>
            <p>${(deliverable.deliverableInfo.newExpectedYear)!}</p>
            <input type="hidden" class="yearNewExpected" name="deliverable.deliverableInfo.newExpectedYear" value="${(deliverable.deliverableInfo.newExpectedYear)!}" />
          </div>
        [/#if]
      </div> 
      <div class="clearfix"></div>
    </div>
    
    [#-- Status justification textArea --]
      [#assign justificationRequired = ((deliverable.deliverableInfo.status == 4)  || (deliverable.deliverableInfo.status == 5) || (deliverable.deliverableInfo.status == 7))!false ]
      <div class="form-group">
        <div id="statusDescription" class="col-md-12" style="display:${justificationRequired?string('block','none')}">
          [@customForm.textArea name="deliverable.deliverableInfo.statusDescription" className="statusDescription limitWords-150" i18nkey="deliverable.statusJustification.status${(deliverable.deliverableInfo.status)!'NotSelected'}" editable=editable || editStatus/]
          <div id="statusesLabels" style="display:none">
            <div id="status-2">[@s.text name="deliverable.statusJustification.status2" /]:<span class="red">*</span></div>[#-- Ongoing("2", "On-going") --]
            <div id="status-3">[@s.text name="deliverable.statusJustification.status3" /]:<span class="red">*</span></div>[#-- Complete("3", "Complete") --]
            <div id="status-4">[@s.text name="deliverable.statusJustification.status4" /]:<span class="red">*</span></div>[#-- Extended("4", "Extended") --]
            <div id="status-5">[@s.text name="deliverable.statusJustification.status5" /]:<span class="red">*</span></div>[#-- Cancelled("5", "Cancelled") --]
            <div id="status-7">[@s.text name="deliverable.statusJustification.status7" /]:<span class="red">*</span></div>[#-- Partially Complete("7", "Partially Complete") --]
          </div>
        </div>
      </div>
      <div class="clearfix"></div>
    
    <hr />
    [#-- New deliverable at reporting --]
    [#if isDeliverableNew && editable && (reportingActive)]<i class="text-center">The status of this deliverable should be 'Complete' due is new</i> <br />[/#if]
    
    [#-- Deliverable field status --]
    [#if isDeliverableComplete]
      <span class="icon-20 icon-check" title="Complete"></span> Required fields Completed
    [#else]
      <span class="icon-20 icon-uncheck" title=""></span> There are required fields still incompleted
    [/#if]
  </div>
  
  [#-- Key Outputs select --]
  [#if !project.projectInfo.administrative && !phaseOne && !isCenterProject ]
    [#if !(keyOutputs?has_content) && editable]
      <p class="note">The Key outputs list come from the Project Outcomes you choose in ‘[@s.text name="projects.menu.contributionsCrpList" /]’, once the project is contributing, this deliverable can be mapped to a specific Key output.</p>
    [/#if]
    <div class="form-group">
      [@customForm.select name="deliverable.deliverableInfo.crpClusterKeyOutput.id" label=""  i18nkey="project.deliverable.generalInformation.keyOutput" listName="keyOutputs" keyFieldName="id"  displayFieldName="composedName"  multiple=false required=true  className="keyOutput" editable=editable/]
    </div>
  [/#if]
  
  [#-- Contribution to LP6 --]
  [#if action.hasSpecificities('crp_lp6_active') && reportingActive && ((action.getProjectLp6ContributionValue(project.id, actualPhase.id))!false) ]
    <div class="form-group">
      <label>[@s.text name="deliverable.LP6Contribution.contribution"/][@customForm.req required=true /]</label>
      [@customForm.radioFlat id="lp6Contribution-yes" name="deliverable.contribution" label="Yes" value="true"  checked=(action.getHasLp6ContributionDeliverable(deliverable.id,actualPhase.id))!false cssClassLabel="radio-label-yes" editable=editable /]
      [@customForm.radioFlat id="lp6Contribution-no" name="deliverable.contribution" label="No" value="false" checked=!((action.getHasLp6ContributionDeliverable(deliverable.id,actualPhase.id))!true) cssClassLabel="radio-label-no" editable=editable /]
    </div>
  [/#if]
  
  [#-- Funding Source --]
  [#if !phaseOne]
  <div class="panel tertiary">
   <div class="panel-head"><label for=""> [@customForm.text name="project.deliverable.fundingSource" readText=!editable /]:[@customForm.req required=(editable&&action.hasSpecificities('deliverable_funding')) /]</label></div>
    <div id="fundingSourceList" class="panel-body" listname="deliverable.fundingSources"> 
      <ul class="list">
      [#if deliverable.fundingSources?has_content]
        [#list deliverable.fundingSources as element]
          <li class="fundingSources clearfix">
            [#if editable]<div class="removeFundingSource removeIcon" title="Remove funding source"></div>[/#if] 
            <input class="id" type="hidden" name="deliverable.fundingSources[${element_index}].id" value="${(element.id)!}" />
            <input class="fId" type="hidden" name="deliverable.fundingSources[${element_index}].fundingSource.id" value="${(element.fundingSource.id)!}" />
            <span class="name">
              <strong>FS${(element.fundingSource.id)!} - ${(element.fundingSource.fundingSourceInfo.budgetType.name)!} [#if (element.fundingSource.fundingSourceInfo.w1w2)!false] (Co-Financing)[/#if]</strong> <br />
              <span class="description">${(element.fundingSource.fundingSourceInfo.title)!}</span><br />
            </span>
            <div class="clearfix"></div>
          </li>
        [/#list]
        <p style="display:none;" class="emptyText"> [@s.text name="project.deliverable.fundingSource.empty" /]</p>   
      [#else]
        <p class="emptyText"> [@s.text name="project.deliverable.fundingSource.empty" /]</p> 
      [/#if]
      </ul>
      [#if editable ]
        [@customForm.select name="deliverable.fundingSource.id" label=""  showTitle=false  i18nkey="" listName="fundingSources" keyFieldName="id"  displayFieldName="composedName"  header=true required=true  className="fundingSource" editable=editable/]
      
        [#if !fundingSources?has_content]
          <div class="note"> [@s.text name="deliverable.fundingSourceListEmpty" /]  </div>
        [/#if]
      [/#if] 
    </div>
  </div>
  
  [#-- Funding source List --]
  <div style="display:none">
    [#if fundingSources?has_content]
      [#list fundingSources as element]
        <span id="fundingSource-${(element.id)!}">
          <strong>FS${(element.id)!} - ${(element.fundingSourceInfo.budgetType.name)!} [#if (element.w1w2)!false] (Co-Financing) [/#if]</strong> <br />
          <span class="description">${(element.fundingSourceInfo.title)!}</span><br />
        </span>
      [/#list]
    [/#if]
  </div>
  
  [/#if]
</div>

[#-- Geographic Scope  --]
<h3 class="headTitle">Geographic Scope</h3>
<div class="simpleBox">
  [@deliverableMacros.deliverableGeographicScope /]
</div>

[#-- Cross-cutting dimensions --]
<h3 class="headTitle">[@s.text name="deliverable.crossCuttingDimensionsTitle" /] </h3>
<div class="simpleBox">
  [@deliverableMacros.deliverableCrossCuttingMacro /]
</div>



[#-- Partners --] 
<h3 class="headTitle">[@s.text name="Partners contributing to this deliverable" /]</h3>  
<div id="deliverable-partnerships-new" class="form-group simpleBox">
  [#-- Partner who is responsible --]
  <label for="">[@customForm.text name="project.deliverable.indicateResponsablePartner" readText=!editable/]:[@customForm.req required=editable /]</label>
  <div>
    [@deliverableMacros.deliverablePartnerMacro element=(deliverable.responsiblePartnership[0])!{} name="deliverable.responsiblePartnership" index=0 defaultType=1 /]
  </div>
  <hr />
  [#-- Other contact person that will contribute --]
  [#assign displayOtherPerson = (!deliverable.otherPartnerships?has_content && !editable)?string('none','block') /]
  <label for="" style="display:${displayOtherPerson}">[@customForm.text name="projectDeliverable.otherContactContributing" readText=!editable/]</label>
  <div class="otherDeliverablePartners">
    [#list (deliverable.otherPartnerships)![] as otherPartnership]
      [@deliverableMacros.deliverablePartnerMacro element=(otherPartnership)!{} name="deliverable.otherPartnerships" index=otherPartnership_index defaultType=2/]
    [#else]
      <p class="simpleBox emptyText center"> [@s.text name="project.deliverable.partnership.emptyText" /] </p>
    [/#list]
  </div>
  
  [#if editable && canEdit]
    <div class="text-right">
      <div class="button-blue addPartnerItem"><span class="glyphicon glyphicon-plus-sign"></span> [@s.text name="form.buttons.addPartner" /]</div>
    </div>
  [/#if]
  
  [#if editable]
    <div class="partnerListMsj note">
      [@s.text name="project.deliverable.generalInformation.partnerNotList" /]
      <a href="[@s.url namespace="/${currentSection}" action='${(crpSession)!}/partners'] [@s.param name="projectID"]${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"> 
        [@s.text name="project.deliverable.generalInformation.partnersLink" /] 
      </a>
    </div>
  [/#if]

  [#-- Projects shared --]
  <h3 class="headTitle">[@s.text name="deliverable.sharedProjects.title" /]</h3>
  <div class="borderBox">
    [@customForm.elementsListComponent name="deliverable.sharedDeliverables" elementType="project" elementList=(deliverable.sharedDeliverables)![] label="deliverable.sharedProjects"  listName="myProjects" keyFieldName="id" displayFieldName="composedName" required=false /]
  </div>
</div>