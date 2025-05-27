[#ftl]

[#macro innovationDescription element name index=-1 template=false ]
  <div class="borderBox generalInformationInnovations">    
    [#-- General Inputs --]
    <div class="form-group row">

      [#-- hr in elements --]
      <hr class="line-hr" />
    
      [#-- Innovation ID --]
      <div class="col-md-4">
        [@customForm.input name="${(innovationID)!}" i18nkey="projectInnovations.id" helpIcon=false required=false editable=false readOnly=true /]
      </div>

      [#-- Year --]        
      <div class="col-md-4">                   
        [@customForm.select name="innovation.projectInnovationInfo.year" className="setSelect2" i18nkey="policy.year" listName="getInnovationsYears(${innovationID})" header=false required=true editable=editable /]                        
      </div>      

      [#-- Buttons - Shared Clusters & Copy --]
      <div class="col-md-4 generalInnovationsOptions">
        [#-- Shared Clusters --]
        <button type="button" class="btn btn-default btn-sm" style="margin-right: 5px;" data-toggle="modal" data-target="#sharedClusterModal">
          <p><span class="glyphicon glyphicon-log-out"></span>[@s.text name="projectInnovations.shared" /]</p>
          <p id="modalCounterShared">0</p>
        </button>

        [#-- Copy --]
        <button type="button" class="btn btn-default btn-sm copyButton" style="margin-right: 5px;">
          <p><span class="glyphicon glyphicon-duplicate"></span>[@s.text name="projectInnovations.copylink" /]</p> 
        </button>
        [#local summaryPDF = "${baseUrl}/summaries/${crpSession}/projectInnovationSummary.do?innovationID=${(element.id)!}&cycle=Reporting&year=${(actualPhase.year)!}"]
        [@customForm.input name="innovation.projectExpectedStudyInfo.link" i18nkey="study.general.link" className="form-control input-sm urlInput" value="${summaryPDF}" editable=editable display=false readOnly=true/]
        <div class="message text-center" style="display:none; margin-top:6px;">[@s.text name="study.general.link.copy" /]</div>

      </div>

      [#-- Shared Cluster Modal --]
      <div class="form-group col-md-12 sharedClusterMessage">
        <div class="modal fade" id="sharedClusterModal" tabindex="-1" role="dialog" aria-labelledby="sharedClusterModalLabel" aria-hidden="true">
          <div class="modal-dialog">
            <div class="modal-content">
              <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="sharedClusterModalLabel">[@s.text name="projectInnovations.sharedProjects.title" /]</h4>
              </div>
              <div class="modal-body">
                      [#-- Projects shared --]
                <h5 class="headTitle">[@s.text name="projectInnovations.sharedProjects.title" /]</h5>
                [@customForm.elementsListComponent name="innovation.sharedInnovations" elementType="project" elementList=(innovation.sharedInnovations)![] label="projectInnovations.sharedProjects"  listName="myProjects" keyFieldName="id" displayFieldName="composedName" required=false /]
              </div>
              <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">[@s.text name="projectInnovations.sharedProjects.close" /]</button>
              </div>
            </div>
          </div>
        </div>

        <div class="clearfix"></div>
      </div>
              

    </div>         
  </div>
[/#macro]

[#macro innovationGeneral element name index=-1 template=false ]
        [#local isProgressActive = action.isProgressActive() ]
        
        <div id="general" class="borderBox clearfix">   

            [#--
            <div class="form-group row">
              
              <div class="col-md-4">
                [@customForm.select name="innovation.projectInnovationInfo.year" className="setSelect2" i18nkey="policy.year" listName="getInnovationsYears(${innovationID})" header=false required=true editable=editable /]
              </div>
            
              <div class="col-md-12">
                [#local guideSheetURL = "https://drive.google.com/file/d/1JvceA0bdvqS5Een056ctL7zJr3hidToe/view" /]
                <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" /> #C1 Innovations  -  Guideline </a> </small>
              </div>
            </div>
            <hr /> --]

            [#-- Note --]
            <div class="note">
              <p>[@s.text name="projectInnovations.generalInformation.help" /]</p>
            </div> 
          
            [#-- Title --]
            <div class="form-group">
              [@customForm.input name="innovation.projectInnovationInfo.title" type="text" i18nkey="projectInnovations.title"  placeholder="" className="limitWords-30" help="projectInnovations.title.helpText" helpIcon=false required=true editable=editable isMainTitle=true isWidthFull=true /]
            </div>
            
            [#-- Short Title --]
            <div class="form-group">
              [@customForm.input name="innovation.projectInnovationInfo.shortTitle" type="text" i18nkey="projectInnovations.shortTitle"  placeholder="" className="limitWords-15" help="projectInnovations.shortTitle.helpText" helpIcon=false required=true editable=editable isMainTitle=true isWidthFull=true /]
            </div>
          
            [#-- Narrative --] 
            <div class="form-group">
              [@customForm.textArea name="innovation.projectInnovationInfo.narrative"  i18nkey="projectInnovations.narrative"  placeholder="" className="limitWords-80" help="projectInnovations.narrative.helpText" isNote=true helpIcon=false required=true editable=editable isMainTitle=true isWidthFull=true /]         
            </div>
            
            [#-- Innovation Bundle --]
            <div class="form-group col-md-12">
              [@customForm.labelText name="innovation.innovationBundle" text="projectInnovations.innovationBundle" required=true isMainTitle=true /]
              [@customForm.labelText name="innovation.innovationBundle.helpText" text="projectInnovations.innovationBundle.helpText"  /]
              [#local isInnovationBundle = ((innovation.projectInnovationInfo?has_content)&&(innovation.projectInnovationInfo.innovationBundle?has_content && innovation.projectInnovationInfo.innovationBundle?c == "true")) /]
              <div class="col-md-12">
                <div class="col-md-3 radioFlat-flex">
                  [@customForm.radioFlat id="isInnovationBundle-determined" name="innovation.projectInnovationInfo.innovationBundle" i18nkey="projectInnovations.innovationBundle.innovationOption" value="false" checked=((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.innovationBundle??)&&(!isInnovationBundle)) cssClass="radioType-isInnovationBundle" cssClassLabel="radio-label" editable=editable /]
                </div>
                <div class="col-md-3 radioFlat-flex">
                  [@customForm.radioFlat id="isInnovationBundle-undetermined" name="innovation.projectInnovationInfo.innovationBundle" i18nkey="projectInnovations.innovationBundle.bundleOption" value="true" checked=((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.innovationBundle??)&&(isInnovationBundle)) cssClass="radioType-isInnovationBundle" cssClassLabel="radio-label" editable=editable /]
                </div>
              </div>
            </div>
            
            [#-- Phase of research and Stage of innovation - DEPRECIATED --] 
            [#-- <div class="form-group row">
              <div class="col-md-6 ">
                [@customForm.select name="innovation.projectInnovationInfo.repIndStageInnovation.id" label=""  i18nkey="projectInnovations.stage" listName="stageInnovationList" keyFieldName="id"  displayFieldName="name"   required=!isProgressActive  className="stageInnovationSelect" editable=editable/]
                [#local isStageFour = (innovation.projectInnovationInfo.repIndStageInnovation.id == 4)!false]
              </div>
              <div class="col-md-6 ">
              </div>
            </div>  --]
            
            [#-- Innovation nature --]
            <div class="form-group col-md-12">  
              [@customForm.select name="innovation.projectInnovationInfo.repIndInnovationNature.id" label="" i18nkey="projectInnovations.innovationNature" listName="innovationNatureList" keyFieldName="id" displayFieldName="composedName" required=true help="projectInnovations.innovationNature.helpText" isNote=true helpIcon=false className="innovationNatureSelect" editable=editable isMainTitle=true /]
            </div>

            <div class="form-group">
              [#-- Other Innovation Nature --]
              [#local isNatureFour = (innovation.projectInnovationInfo.repIndInnovationNature.id == 4)!false]
              <div class="col-md-12">
                <div class="form-group natureFourBlock" style="display:${isNatureFour?string('block','none')}">              
                  [@customForm.input name="innovation.projectInnovationInfo.otherInnovationNature"  type="text" i18nkey="projectInnovations.otherInnovationNature" helpIcon=false required=!isProgressActive editable=editable  /]
                </div>
              </div>
            </div>

            [#-- Innovation Type --]
            <div class="form-group">
              [@customForm.select name="innovation.projectInnovationInfo.repIndInnovationType.id" label="" i18nkey="projectInnovations.innovationType" listName="innovationTypeList" keyFieldName="id" displayFieldName="name" required=true help="projectInnovations.innovationType.helpText" isNote=true helpIcon=false className="innovationTypeSelect" editable=editable isMainTitle=true isWidthFull=true /]
            </div>

            <div class="form-group">
              [#-- Other Innovation Type --]
              [#local isTypeSix = (innovation.projectInnovationInfo.repIndInnovationType.id == 6)!false]
              <div class="col-md-12">
                <div class="form-group typeSixBlock" style="display:${isTypeSix?string('block','none')}">              
                  [@customForm.input name="innovation.projectInnovationInfo.otherInnovationType"  type="text" i18nkey="projectInnovations.otherInnovation" helpIcon=false required=!isProgressActive editable=editable  /]
                </div>
              </div>
            </div> 
            
            
            [#-- Degree of Innovation --] 
            [#--  <div class="form-group row">
              <div class="col-md-6 ">
                [@customForm.select name="innovation.projectInnovationInfo.repIndDegreeInnovation.id" label=""  i18nkey="projectInnovations.degreeInnovation" listName="degreeInnovationList" keyFieldName="id"  displayFieldName="name" required=true  className="" editable=editable/]
              </div>
            </div>--]
            
            [#-- Specify next user organizational type (Only if stage 4) - DEPRECIATED --]
            [#-- <div class="form-group stageFourBlock-true" >
              [@customForm.elementsListComponent name="innovation.organizations" elementType="repIndOrganizationType" elementList=innovation.organizations label="projectInnovations.nextUserOrganizationalType"  listName="organizationTypeList" keyFieldName="id" displayFieldName="name"/]
            </div> --]

            [#-- 6.  Geographic scope - Countries
            <div class="form-group geographicScopeBlock">
              [#local geographicScopeList = (innovation.geographicScopes)![] ]
              [#local isRegional =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeRegional) /]
              [#local isMultiNational = findElementID(geographicScopeList,  action.reportingIndGeographicScopeMultiNational) /]
              [#local isNational =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeNational) /]
              [#local isSubNational =   findElementID(geographicScopeList,  action.reportingIndGeographicScopeSubNational) /]
              
              <div class="form-group">
                <div class="row">
                  <div class="col-md-6">
                    [@customForm.elementsListComponent name="innovation.geographicScopes" elementType="repIndGeographicScope" elementList=innovation.geographicScopes maxLimit=1 label="projectInnovations.geographicScope" listName="geographicScopeList" keyFieldName="id" displayFieldName="name" required=!isProgressActive /]
                  </div>
                </div>
                <div class="form-group regionalBlock" style="display:${(isRegional)?string('block','none')}">
                  [@customForm.elementsListComponent name="innovation.regions" elementType="locElement" elementList=innovation.regions label="projectInnovations.region"  listName="regions" keyFieldName="id" displayFieldName="composedName" required=false /]
                </div>
                <div class="form-group nationalBlock" style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}">
                  [@customForm.select name="innovation.countriesIds" label="" i18nkey="projectInnovations.countries" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="innovation.countriesIds" multiple=true required=!isProgressActive className="countriesSelect" disabled=!editable/]
                </div>
              </div>
            </div>
            --]
            [#-- Geographic scope - Countries  --]
            <div class="form-group geographicScopeBlock">
              [#local geographicScopeElement = (element.geographicScopes)![] ]
              [#local isGlobal =        findElementID(geographicScopeElement,  action.reportingIndGeographicScopeGlobal) /]
              [#local isRegional =      findElementID(geographicScopeElement,  action.reportingIndGeographicScopeRegional) /]
              [#local isMultiNational = findElementID(geographicScopeElement,  action.reportingIndGeographicScopeMultiNational) /]
              [#local isNational =      findElementID(geographicScopeElement,  action.reportingIndGeographicScopeNational) /]
              [#local isSubNational =   findElementID(geographicScopeElement,  action.reportingIndGeographicScopeSubNational) /]
              
              [#local hasSpecifiedOutputCountries = ((element.projectInnovationInfo??) && (element.projectInnovationInfo.hasSpecifiedOutputCountries??))?then(element.projectInnovationInfo.hasSpecifiedOutputCountries,"") /]

              [@customForm.labelText name="projectInnovations.geographicScope" text="projectInnovations.geographicScope" required=(editable && reportingActive) isMainTitle=true /]
              [@customForm.labelText name="projectInnovations.geographicImpact" text="projectInnovations.geographicImpact"  /]
              <div class="form-group ('','simpleBox') geographicScopeInput">
                <div class="form-group">
                  <div class="form-group col-md-12">
                    [#local existGeographicScope = element.geographicScopes?? && element.geographicScopes[0]??  /]
                    [#if existGeographicScope]
                      [#local geographicScopeID = (element.geographicScopes[0].id!"")?string /]

                      [@customForm.input name="innovation.geographicScopes[0].id" editable=false display=false value=geographicScopeID /]
                    [/#if]

                    [#-- Geographic Scope --]
                    [#list geographicScopeList as geoScope]
                      [#local isChecked = false /]
                      [#if ((existGeographicScope) && (element.geographicScopes[0].repIndGeographicScope??) && (geoScope.id == element.geographicScopes[0].repIndGeographicScope.id))]
                        [#local isChecked = true /]
                      [/#if]
                      [#local isYetDetermined = geoScope.id == 6 /]
                      <div class="col-md-${isYetDetermined?string('4','2')} radioFlat-flex" style="margin-bottom: 0px;">
                        [@customForm.radioFlat id="geoScope-${geoScope.id}" name="innovation.geographicScopes[0].repIndGeographicScope.id" label="${geoScope.name}" value="${geoScope.id}" checked=isChecked cssClass="radioType-geographicScopes" cssClassLabel="radio-label" editable=editable disabled=!editable /]
                      </div>
                    [/#list]
                    [#-- [@customForm.elementsListComponent name="innovation.geographicScopes" elementType="repIndGeographicScope" elementList=innovation.geographicScopes maxLimit=1 label="projectInnovations.geographicScope" listName="geographicScopeList" keyFieldName="id" displayFieldName="name" required=!isProgressActive orderById=true /] --]
                  </div>

                  <div class="geographicScopeMessage" style="display:${(isNational)?string('block','none')}">
                    [@customForm.labelText name="projectInnovations.geographicScopeTopic" text="projectInnovations.geographicScopeTopic" /]
                  </div>

                  <div class="form-group nationalBlock col-md-12" style="display:${(isNational)?string('block','none')}">
                    [#-- Multinational, National and Subnational scope --]
                    [@customForm.select name="innovation.countriesIds" label="" i18nkey="projectInnovations.country" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="innovation.countriesIds" multiple=true required=!isProgressActive className="countriesSelect" disabled=!editable cssClassContainer="col-md-12 countriesControlStyle" isFlex=true /]
                  </div>
                  <div class="form-group regionalBlock col-md-12" style="display:${(isRegional)?string('block','none')}">
                    [#local geographicCountrySpecific = (element.projectInnovationInfo?? && element.projectInnovationInfo.hasSpecifiedOutputCountries??)?then(element.projectInnovationInfo.hasSpecifiedOutputCountries, false) /]
                    [#-- Regional scope --]
                    [@customForm.elementsListComponent name="innovation.regions" elementType="locElement" elementList=innovation.regions label="projectInnovations.region"  listName="regions" keyFieldName="id" displayFieldName="composedName" required=true cssClassContainer="col-md-6" isFlex=true /]

                    <div class="col-md-12">
                      [@customForm.labelText name="geographicCountrySpecific" text="projectInnovations.geographicCountrySpecific" required=true /]
                      <div class="col-md-1 radioFlat-flex">
                        [@customForm.radioFlat id="hasSpecifiedOutputCountries-yes" name="innovation.projectInnovationInfo.hasSpecifiedOutputCountries" i18nkey="projectInnovations.hasSpecifiedOutputCountries.yes" value="true" checked=((element.projectInnovationInfo??) && (element.projectInnovationInfo.hasSpecifiedOutputCountries??) && (hasSpecifiedOutputCountries))  cssClass="radioType-hasSpecifiedOutputCountries" cssClassLabel="radio-label-yes" editable=editable /]
                      </div>
                      <div class="col-md-1 radioFlat-flex">
                        [@customForm.radioFlat id="hasSpecifiedOutputCountries-no" name="innovation.projectInnovationInfo.hasSpecifiedOutputCountries" i18nkey="projectInnovations.hasSpecifiedOutputCountries.no" value="false" checked=((element.projectInnovationInfo??) && (element.projectInnovationInfo.hasSpecifiedOutputCountries??) && (!hasSpecifiedOutputCountries)) cssClass="radioType-hasSpecifiedOutputCountries" cssClassLabel="radio-label-no" editable=editable /]
                      </div>
                    </div>
                    <div class="col-md-12 block-hasSpecifiedOutputCountries" style="display:${(geographicCountrySpecific)?string('block','none')}">
                      [#-- Multinational, National and Subnational scope --]
                      [@customForm.labelText name="projectInnovations.geographicScopeTopic" text="projectInnovations.geographicScopeTopic" /]
                      [#-- Multinational, National and Subnational scope --]
                      [@customForm.select name="innovation.countriesIds" label="" i18nkey="projectInnovations.countries" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="innovation.countriesIds" multiple=true required=!isProgressActive className="countriesSelect" disabled=!editable cssClassContainer="col-md-12 countriesControlStyle" isFlex=true /]
                    </div>
                      
                  </div>
                  [#--  
                  <div class="form-group col-md-12">
                    [@customForm.textArea name="${customName}.projectExpectedStudyInfo.scopeComments" className="limitWords-30" i18nkey="study.generalInformation.geographicScopeComments" help="study.generalInformation.geographicScopeComments.help" helpIcon=false  editable=editable required=false/]
                  </div>
                  --]
                </div>
              </div>
            </div>

            [#-- Contributing Centers/ PPA partners  --]
            <div class="form-group contributionsCenters">
              <label class="label--2">[@s.text name="projectInnovations.contributingCenters.title" /]: [@customForm.req required=true /]</label>
              <div class="note">
                <span class="glyphicon glyphicon-question-sign"></span> [@s.text name="study.generalInformation.ppapartner.note"][@s.param] 
                <a href="[@s.url namespace="/projects" action='${crpSession}/partners'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">&nbsp; clicking here</a>[/@][/@]
              </div>
              
              [#-- Contributing Centers --]
              <div class="col-md-12">
                [@customForm.elementsListComponent name="innovation.centers" i18nkey="innovation.centers" elementType="institution" elementList=innovation.centers label="projectInnovations.contributingCenters"  listName="centers" keyFieldName="id" displayFieldName="composedName" required=!isProgressActive /]
              </div>

              [#-- Define organizationRoles list for checkbox options --]
              [#local organizationRoles = [
                {"name": "Scaling"},
                {"name": "Demand"},
                {"name": "Innovation"},
                {"name": "Other"}
              ] /]

              [#-- External Contributing Centers --]
              <div class="col-md-12 top-five-contributing">
                [@customForm.elementsListComponent name="innovation.contributingOrganizations" i18nkey="innovation.contributingOrganizations" help="projectInnovations.contributingOrganizations.help" helpIcon=false isNote=false maxLimit=5 elementType="institution" elementList=innovation.contributingOrganizations label="projectInnovations.contributingOrganizations"  listName="contributingPartnerList" keyFieldName="id" displayFieldName="nameWithCountry" hasInnerCheckbox=true argsInnerCheckbox={"listCheckbox": organizationRoles, "subtitleElement": "Organization", "subtitleCheckbox": "Role"}  /]
              </div>

            </div>
            
            [#-- Description of Stage reached - DEPRECIATED --] 
            [#-- <div class="form-group">
              [@customForm.textArea name="innovation.projectInnovationInfo.descriptionStage" i18nkey="projectInnovations.stageDescription" help="projectInnovations.stageDescription.help" helpIcon=false placeholder="" className="limitWords-50" required=!isProgressActive editable=editable /]
            </div> --]
            


            [#-- Lead Organization --]
            [#-- Is clear lead  --]
            [#--[#local isClearLead = (innovation.projectInnovationInfo.clearLead)!false /]--}
            [#--  <div class="oldManageContributions">
              <div class="form-group isClearLead">
                [@customForm.checkmark id="isClearLeadToAddRequired" name="clearLead" i18nkey="projectInnovations.clearLead" help="" paramText="" value="true" helpIcon=true disabled=false editable=editable checked=(innovation.projectInnovationInfo.clearLead)!false cssClass="isClearLead" cssClassLabel=""  /]
              </div>
              <div class="form-group lead-organization" style="display:${isClearLead?string('none','block')}">
                [@customForm.select name="innovation.projectInnovationInfo.leadOrganization.id" label=""  i18nkey="projectInnovations.leadOrganization" listName="institutions" keyFieldName="id"  displayFieldName="composedName" className="" editable=editable required=!isProgressActive /]
              </div>
            </div>  --]
            

            
            [#-- Novel or Adaptive research --]
            [#-- <div class="form-group">
              [@customForm.textArea name="innovation.projectInnovationInfo.adaptativeResearchNarrative" i18nkey="projectInnovations.novelOrAdaptative" placeholder="" className="" required=false editable=editable /]
            </div>--]
          
            [#-- Specify an Outcome Case Study (Only if stage 4) - DEPRECIATED --]
            [#-- <div class="form-group stageFourBlock-true">
              [@customForm.elementsListComponent name="innovation.studies" elementType="projectExpectedStudy" elementList=innovation.studies label="projectInnovations.outcomeCaseStudy" helpIcon=false listName="expectedStudyList" keyFieldName="id" displayFieldName="composedNameAlternative" required=(isEvidenceRequired!false && !isProgressActive)/]
            </div> --]
                  
            [#-- Evidence Link - DEPRECIATED --] 
            [#-- <div class="form-group stageFourBlock-false" >
              [@customForm.input name="innovation.projectInnovationInfo.evidenceLink"  type="text" i18nkey="projectInnovations.evidenceLink"  placeholder="marloRequestCreation.webSiteLink.placeholder" className="" required=!isProgressActive editable=editable /]
            </div> --]
          
            [#-- Or Deliverable ID (optional) - DEPRECIATED --]

            [#-- <div class="form-group">
              [@customForm.elementsListComponent name="innovation.deliverables" elementType="deliverable" elementList=innovation.deliverables label="projectInnovations.deliverableId"  listName="deliverableList" required=false keyFieldName="id" displayFieldName="tagTitle"/]
            </div> --]
            
            [#-- Link to Performance Indicators --]
            <div class="form-group col-md-12">
              [@customForm.elementsListComponent name="innovation.crpOutcomes" elementType="crpOutcome" elementList=(innovation.crpOutcomes)![] label="innovation.outcomes" helpIcon=false listName="crpOutcomes" keyFieldName="id" displayFieldName="composedName" required=!isProgressActive isMainTitle=true cssClassContainer="margin-buttom-0" /]
              <div class="note left">
                <span class="glyphicon glyphicon-question-sign"></span>
                [@s.text name="project.deliverable.generalInformation.keyOutputNotice2"][@s.param] <a href="[@s.url namespace=namespace action="${crpSession}/contributionsCrpList"][@s.param name='projectID']${projectID?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">&nbsp;clicking here</a>[/@] [/@]  
              </div>
              <br/>
            </div> 
          
            [#-- Contributing cNE/Platforms --]
            [#if !action.isAiccra()]
              <div class="form-group">
                [@customForm.elementsListComponent name="innovation.crps" elementType="globalUnit" elementList=innovation.crps label="projectInnovations.contributing"  listName="crpList" keyFieldName="id" displayFieldName="composedName" required=false /]
              </div>
            [/#if]
              

            [#if !action.isAiccra()]
            [#-- Sub IDOs (maxLimit=3 -Requested for AR2019) --]      
            <div class="form-group simpleBox">
              [@customForm.elementsListComponent name="innovation.subIdos" elementType="srfSubIdo" elementList=(innovation.subIdos)![] label="innovation.subIDOs" helpIcon=false listName="subIdos" maxLimit=3 keyFieldName="id" displayFieldName="description" required=!isProgressActive hasPrimary=true/]
            [#--  <div class="buttonSubIdo-content"><br> <div class="selectSubIDO" ><span class=""></span>View sub-IDOs</div></div> --]
              [#-- [@customForm.primaryListComponent name="innovation.subIdos" checkName="subIdoPrimaryId" elementType="srfSubIdo" elementList=(innovation.subIdos)!"" label="innovation.subIDOs" labelPrimary="policy.primarySubIdo" listName="subIdos" maxLimit=3 keyFieldName="id" displayFieldName="description" required=false /]
              --]
            </div> 
            [/#if]      

            [#--  Contact person    --]
            <div class="form-group stageProcessOne col-md-12">
              <label class="label--2">[@s.text name="projectInnovations.communications.contacts" /]:</label>
              <div id="addPartnerText" class="note--2">
                  [@s.text name="projectInnovations.communications.contacts.help" /]
                  <a class="popup" href="[@s.url action='${crpSession}/partnerSave' namespace="/projects"][@s.param name='projectID']${(projectID)!}[/@s.param][@s.param name='context'](${(actionName)!}: ID-${(innovation.id)!})[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
                    [@s.text name="projectInnovations.communications.contacts.help2" /]
                  </a>
              </div>
              <label for="">[@customForm.text name="projectInnovations.communications.indicateResponsablePartner" readText=!editable/]:</label>
              <div class="projectInnovationsPartners">
                [@deliverableMacros.deliverablePartnerMacro element=(element.partnerships[0])!{} name="innovation.partnerships" index=0 defaultType=1 /]
              </div>
            </div>
          
        </div>
        
[/#macro]

[#macro innovationAlliance element name index=-1 template=false]
  
  [#local customName = "${name}"/]

  <div id="alliance" class="borderBox clearfix">
    [#-- Alliance Research Theme --]
    <div class="form-group radioToCheckbox ">
      [@customForm.labelText name="innovation.allianceLevers" text="projectInnovations.alliance.researchTheme" required=true isMainTitle=true /]
      <label>[@s.text name="projectInnovations.alliance.researchTheme.subtitle" /]</label>
      [#if allianceLeverList?has_content]
          [#list allianceLeverList as lever]
            [#if lever.description?has_content]
              [#local customLabel = "${lever.name} : ${lever.description}" /]
            [#else]
              [#local customLabel = "${lever.name}" /]
            [/#if]

            [#local isOther = (lever.name == "Other")!false /]

            [#local isChecked = false /]

            <div class="containerRadioToCheckbox ${isOther?then('containerRadioToCheckbox--other','')}">
              [#list element.allianceLevers as elementLever]
                [#if elementLever.allianceLever.id == lever.id]
                  <div class="form-group hiddenIdReference">
                      [@customForm.input name="${customName}.allianceLevers[${elementLever_index}].id" editable=false display=false value="${elementLever.id!-1}" /]
                  </div>
                  [#local isChecked = true /]
                  [#break /]
                [/#if]

              [/#list]
 
              [@customForm.checkBoxFlat id="lever-${lever.id}" name="${customName}.allianceLevers[${lever_index}].allianceLever.id" label="${customLabel}" value="${lever.id}" checked=((isChecked)||(lever.id == 3)) editable=editable disabled=(lever.id == 3) /]
              [#if isOther]
                <div class="form-group inputOther">
                  [@customForm.input name="${customName}.projectInnovationInfo.otherAllianceLever" placeholder="Other" editable=editable showTitle=false /]
                </div>
              [/#if]
            </div>
          [/#list]
        [#else]
        <p>No information available</p>
      [/#if]
    </div>
    [#-- Intellectual property rights --]
    <div class="form-group">
      <label class="label--2" style="width:100%">[@s.text name="projectInnovations.alliance.intellectualProperty" /]:</label>
      <label class="note--2">
        <p>[@s.text name="projectInnovations.alliance.intellectualProperty.subtitle" /]</p>
      </label>
      [#-- Intellectual property rights - Owner --]
      <div class="form-group">
        [@customForm.select name="${customName}.projectInnovationInfo.intellectualPropertyInstitution.id" i18nkey="projectInnovations.alliance.intellectualProperty.description" listName="intellectualInstitutionsList" keyFieldName="id" className="innovationPropertyRightsSelect" displayFieldName="customName" required=false editable=editable /]
        [#local otherIntellectualProperty = (element.projectInnovationInfo.intellectualPropertyInstitution.id == 4)!false  /]
        <div class="col-md-12 margin-top-10">
          <div class="form-group otherIntellectualProperty" style="display: ${otherIntellectualProperty?then('block','none')}">
            [@customForm.input name="${customName}.projectInnovationInfo.otherIntellectualProperty" type="text" i18nkey="projectInnovations.alliance.intellectualProperty.other" helpIcon=false required=false editable=editable /]
          </div>
        </div>
        
      </div>
      [#-- Intellectual property rights - Legal Restrictions --]
      <div class="form-group">
        <div class="col-md-12">
          <label>
            [@s.text name="projectInnovations.alliance.intellectualProperty.legalRestrictions" /]
          </label> 
        </div>
        [#local innovationIntellectualLegalRestrictions = "innovationIntellectualLegalRestrictions" /]
        [#local showLegalRestrictions = (innovation.projectInnovationInfo.hasLegalRestrictions)! /]    

        <div class="col-md-1">
          [@customForm.radioFlat id="${innovationIntellectualLegalRestrictions}-yes" name="${customName}.projectInnovationInfo.hasLegalRestrictions" label="Yes" value="true" checked=((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.hasLegalRestrictions??) &&(showLegalRestrictions)) cssClass="radioType-${innovationIntellectualLegalRestrictions}" cssClassLabel="radio-label-yes" editable=editable /]
        </div>
        <div class="col-md-1">
          [@customForm.radioFlat id="${innovationIntellectualLegalRestrictions}-no" name="${customName}.projectInnovationInfo.hasLegalRestrictions" label="No" value="false" checked=((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.hasLegalRestrictions??) &&(!showLegalRestrictions)) cssClass="radioType-${innovationIntellectualLegalRestrictions}" cssClassLabel="radio-label-no" editable=editable /]
        </div>
      </div>
      [#-- Intellectual property rights - Commercialization --]
      <div class="form-group">
        <div class="col-md-12">
          <label>
            [@s.text name="projectInnovations.alliance.intellectualProperty.commercialization" /]
          </label> 
        </div>
        [#local innovationIntellectualCommercialization = "innovationIntellectualCommercialization" /]
        [#local showCommercialization = (innovation.projectInnovationInfo.hasAssetPotential)! /]   

        <div class="col-md-1">
          [@customForm.radioFlat id="${innovationIntellectualCommercialization}-yes" name="${customName}.projectInnovationInfo.hasAssetPotential" label="Yes" value="true" checked=((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.hasAssetPotential??) && (showCommercialization)) cssClass="radioType-${innovationIntellectualCommercialization}" cssClassLabel="radio-label-yes" editable=editable /]
        </div>
        <div class="col-md-1">
          [@customForm.radioFlat id="${innovationIntellectualCommercialization}-no" name="${customName}.projectInnovationInfo.hasAssetPotential" label="No" value="false" checked=((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.hasAssetPotential??) && (!showCommercialization))  cssClass="radioType-${innovationIntellectualCommercialization}" cssClassLabel="radio-label-no" editable=editable /]
        </div>
      </div>
      [#-- Intellectual property rights - Further Development --]
      <div class="form-group">
        <div class="col-md-12">
          <label>
            [@s.text name="projectInnovations.alliance.intellectualProperty.furtherDevelopment" /]
          </label> 
        </div>
        [#local innovationIntellectualFurtherDevelopment = "innovationIntellectualFurtherDevelopment" /]
        [#local showFurtherDevelopment = (innovation.projectInnovationInfo.hasFurtherDevelopment)! /]

        <div class="col-md-1">
          [@customForm.radioFlat id="${innovationIntellectualFurtherDevelopment}-yes" name="${customName}.projectInnovationInfo.hasFurtherDevelopment" label="Yes" value="true" checked=((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.hasFurtherDevelopment??)&&(showFurtherDevelopment)) cssClass="radioType-${innovationIntellectualFurtherDevelopment}" cssClassLabel="radio-label-yes" editable=editable /]
        </div>
        <div class="col-md-1">
          [@customForm.radioFlat id="${innovationIntellectualFurtherDevelopment}-no" name="${customName}.projectInnovationInfo.hasFurtherDevelopment" label="No" value="false" checked=((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.hasFurtherDevelopment??)&&(!showFurtherDevelopment)) cssClass="radioType-${innovationIntellectualFurtherDevelopment}" cssClassLabel="radio-label-no" editable=editable /]
        </div>
      </div>
      
    </div>
  </div>
[/#macro]

[#macro innovationOneCGIAR element name index=-1 template=false]
  [#local customName = "${name}"/]

  [#local hasContributionToCGIAR = (element.projectInnovationInfo.hasCgiarContribution)!false ]

  <div id="oneCGIAR" class="borderBox">

    [#-- reflect a contribution --]
    [#-- 
    <div class="form-group">
      <label class="label--2">[@s.text name="projectInnovations.oneCGIARAligment.contributionToCGIAR" /]:[@customForm.req required=(editable) /]</label>
      <div class="form-group col-md-12">
      
        <div class="col-md-1">
          [@customForm.radioFlat id="optionOneCGIAR-Yes" name="${customName}.projectInnovationInfo.hasCgiarContribution" i18nkey="projectInnovations.oneCGIARAligment.contributionToCGIARYes" value="true" checked=(((element.projectInnovationInfo.hasCgiarContribution??) && (hasContributionToCGIAR))!false) cssClass="radioType-contributionToCGIAR" cssClassLabel="font-normal" editable=editable /]
        </div>
        <div class="col-md-1">
          [@customForm.radioFlat id="optionOneCGIAR-No" name="${customName}.projectInnovationInfo.hasCgiarContribution" i18nkey="projectInnovations.oneCGIARAligment.contributionToCGIARNo" value="false" checked=(((element.projectInnovationInfo.hasCgiarContribution??) && (!hasContributionToCGIAR))!false) cssClass="radioType-contributionToCGIAR" cssClassLabel="font-normal" editable=editable /]
        </div>
      </div>
    </div>


    Innovation importance
    <div class="form-group linkToImpactAreas" style="display:${(((hasContributionToCGIAR?c) == 'true')&& (element.projectInnovationInfo.hasCgiarContribution??))?string('block','none')};" >

      Impact Areas
      <div class="form-group  col-md-12">
        <label class="label--2 col-md-12">[@s.text name="projectInnovations.oneCGIARAligment.impactAreas" /]:[@customForm.req required=true /]</label>
        <label>[@s.text name="projectInnovations.oneCGIARAligment.impactAreas.subtitle" /]</label>
        [@customForm.elementsListComponent name="${customName}.impactAreas" elementType="impactArea" elementList=(element.impactAreas)![] helpIcon=false listName="impactAreaList" keyFieldName="id" displayFieldName="name" required=false showTitle=false /]
      </div>
    </div>

    Reason not provided
    <div class="form-group contributionToCGIARComment col-md-12" style="display:${((element.projectInnovationInfo??)&&((hasContributionToCGIAR?c) == 'false') && (element.projectInnovationInfo.hasCgiarContribution??))?string('block','none')};">
      [@customForm.textArea name="${customName}.projectInnovationInfo.reasonNotCgiarContribution" i18nkey="projectInnovations.oneCGIARAligment.contributionToCGIAR.reasonToNoProvided"  helpIcon=false className="limitWords-200" required=(editable) editable=editable /]
    </div>
    --]

    [#-- SDG Targets --]
    <div class="form-group">
      <label for="innovation.sdgs" class="label--2" style="width:100%">[@s.text name="projectInnovations.oneCGIARAligment.sdgTargets" /]:[@customForm.req required=true /]</label>
      <label>[@s.text name="projectInnovations.oneCGIARAligment.sdgTargets.subtitle" /]:</label>
      [@customForm.elementsListComponent name="${customName}.sdgs" elementType="sdg" elementList=(innovation.sdgs)![] helpIcon=false listName="sdgList" keyFieldName="id" displayFieldName="shortName" required=true showTitle=false /]
    </div>
    
    <div class="form-group">
      <label class="label--2">[@s.text name="projectInnovations.oneCGIARAligment.impactAreasScore" /]:</label>
      <div class="note" style="align-items: unset;">
        <span class="glyphicon glyphicon-question-sign"></span> [@s.text name="innovation.oneCGIAR.tooltip" /]
      </div>
    </div>

    [#local genderScoreValue = (element.projectInnovationInfo.genderScore)!{} /]
    [#local climateChangeScoreValue = (element.projectInnovationInfo.climateChangeScore)!{} /]
    [#local foodSecurityScoreValue = (element.projectInnovationInfo.foodSecurityScore)!{} /]
    [#local environmentalScoreValue = (element.projectInnovationInfo.environmentalScore)!{} /]
    [#local povertyScoreValue = (element.projectInnovationInfo.povertyScore)!{} /]
    
    <div class="form-group">
      [#-- Gender --]
      [@impactScoreRadioGroup 
        fieldName="genderScore" 
        fieldLabel="projectInnovations.oneCGIARAligment.genderScore" 
        fieldValue=genderScoreValue
        editable=editable /]
      
      [@impactScoreRadioGroup 
        fieldName="climateChangeScore" 
        fieldLabel="projectInnovations.oneCGIARAligment.climateChangeScore" 
        fieldValue=climateChangeScoreValue
        editable=editable /]
      
      [@impactScoreRadioGroup 
        fieldName="foodSecurityScore" 
        fieldLabel="projectInnovations.oneCGIARAligment.nutritionScore" 
        fieldValue=foodSecurityScoreValue
        editable=editable /]
      
      [@impactScoreRadioGroup 
        fieldName="environmentalScore" 
        fieldLabel="projectInnovations.oneCGIARAligment.environmentScore" 
        fieldValue=environmentalScoreValue
        editable=editable /]
      
      [@impactScoreRadioGroup 
        fieldName="povertyScore" 
        fieldLabel="projectInnovations.oneCGIARAligment.povertyScore" 
        fieldValue=povertyScoreValue
        editable=editable /]
    </div>
   

  </div>
[/#macro]

[#macro innovationReadiness element name index=-1 template=false]
  <div id="readiness" class="borderBox clearfix">
    <div class="form-group">

      [#-- Innovation importance --]
      <div class="form-group col-md-12">
        [@customForm.textArea name="innovation.projectInnovationInfo.innovationImportance" i18nkey="projectInnovations.oneCGIARAligment.innovationImportance" help="projectInnovations.oneCGIARAligment.innovationImportance.help" helpIcon=false className="limitWords-200" required=false editable=editable isNote=true /]
      </div>

      [#-- Innovation Readiness reason --]
      <div class="form-group">
        [@customForm.textArea name="${name}.projectInnovationInfo.readinessReason" i18nkey="projectInnovations.readiness.reason" help="projectInnovations.readiness.reason.help" helpIcon=false className="limitWords-80" required=true isNote=true showTitle=true isMainTitle=true editable=editable isWidthFull=true /]
      </div>
    </div>
  </div>
[/#macro]

[#macro innovationSharing element name index=-1 template=false]
  [#local customName = "${name}"/]

  <div id="funding" class="borderBox clearfix">
    <div class="form-group">

      [#-- Innovation Scaling Readiness --]
      <div class="form-group">
        [@scalingMacro name="innovation.projectInnovationInfo.readinessScale" element=(innovation.projectInnovationInfo.readinessScale)!-1 editable=true label="projectInnovations.readiness.scale" helpLabel="projectInnovations.readiness.scale.help" listName=scalingReadinessList class="innovationScaling" /]
      </div>

      [#-- Foresee barriers --]
      <div class="form-group col-md-12">
        <label class="col-md-12">[@s.text name="projectInnovations.sharing.aboutTheTool.foreseeBarriers" /][@customForm.req required=editanle /]</label>
        [#local foreseeBarriersText = "foreseeBarriers" /]
        [#local foreseeBarriers = (element.projectInnovationInfo.foreseeBarriers)! /]  

        <div class="col-md-1">
          [@customForm.radioFlat id="${foreseeBarriersText}-yes" name="${customName}.projectInnovationInfo.foreseeBarriers" label="Yes" value="true" checked=((element.projectInnovationInfo??)&&(element.projectInnovationInfo.foreseeBarriers??) &&(foreseeBarriers)) cssClass="radioType-${foreseeBarriersText}" cssClassLabel="radio-label-yes" editable=editable /]
        </div>
        <div class="col-md-1">
          [@customForm.radioFlat id="${foreseeBarriersText}-no" name="${customName}.projectInnovationInfo.foreseeBarriers" label="No" value="false" checked=((element.projectInnovationInfo??)&&(element.projectInnovationInfo.foreseeBarriers??) &&(!foreseeBarriers)) cssClass="radioType-${foreseeBarriersText}" cssClassLabel="radio-label-no" editable=editable /]
        </div>
        [#-- knowledgeToolUsesNarrative --]
        <div class="col-md-12 padding-left-2 block-foreseeBarriers" style="display:${((element.projectInnovationInfo??)&&(element.projectInnovationInfo.foreseeBarriers??)&&(!foreseeBarriers))?then('block','none')};">
          [@customForm.textArea name="${customName}.projectInnovationInfo.knowledgeToolUsesNarrative" i18nkey="projectInnovations.sharing.aboutTheTool.uses" helpIcon=false className="limitWords-500" required=editable editable=editable /]
        </div>
      </div>  

      [#-- knowledgeResultsNarrative --]
      <div class="col-md-12">
        [@customForm.textArea name="${customName}.projectInnovationInfo.knowledgeResultsNarrative" i18nkey="projectInnovations.sharing.aboutTheTool.results" helpIcon=false className="limitWords-500" required=false editable=editable /]
      </div>

      [#-- Knowledge Sharing and Scaling Potential --]
      <div class="form-group">
        <div class="col-md-12">
          <label class="col-md-12 label--2">[@s.text name="projectInnovations.sharing.knowledge" /]:</label>
        </div>
        <div class="col-md-12 padding-left-2">

          [#-- Technical excellence --]
          <label class="label--2 col-md-12 blueLightColor">
            [@s.text name="projectInnovations.sharing.technicalExcellence" /]:
          </label>

          [#local cheaperAlternativesValue = (element.projectInnovationInfo.cheaperAlternatives)!{} /]
          [#local simplerUseValue = (element.projectInnovationInfo.simplerUse)!{} /]
          [#local performBetterValue = (element.projectInnovationInfo.performBetter)!{} /]

          [@customForm.likertScale name="${customName}.projectInnovationInfo.cheaperAlternatives" id="${customName}.projectInnovationInfo.cheaperAlternatives" value=(cheaperAlternativesValue) label="projectInnovations.sharing.technicalExcellence.cheaperAlternatives" required=true editable=editable /]
          [@customForm.likertScale name="${customName}.projectInnovationInfo.simplerUse" id="${customName}.projectInnovationInfo.simplerUse" value=(simplerUseValue) label="projectInnovations.sharing.technicalExcellence.simplerUse" required=true editable=editable /]
          [@customForm.likertScale name="${customName}.projectInnovationInfo.performBetter" id="${customName}.projectInnovationInfo.performBetter" value=(performBetterValue) label="projectInnovations.sharing.technicalExcellence.performBetter" required=true editable=editable /]   

          [#-- Demand and Investment --]
          <label class="label--2 col-md-12 blueLightColor">
            [@s.text name="projectInnovations.sharing.demand&Investment" /]:
          </label>
          
          [#local innovationDesirableValue = (element.projectInnovationInfo.innovationDesirable)!{} /]
          [#local innovationCommerciallyValue = (element.projectInnovationInfo.innovationCommercially)!{} /]

          [@customForm.likertScale name="${customName}.projectInnovationInfo.innovationDesirable" id="${customName}.projectInnovationInfo.innovationDesirable" value=(innovationDesirableValue) label="projectInnovations.sharing.demand&Investment.innovationDesirable" required=true editable=editable /]
          [@customForm.likertScale name="${customName}.projectInnovationInfo.innovationCommercially" id="${customName}.projectInnovationInfo.innovationCommercially" value=(innovationCommerciallyValue) label="projectInnovations.sharing.demand&Investment.innovationCommercially" required=true editable=editable /]
          
          [#-- Sustained use --]
          <label class="label--2 col-md-12 blueLightColor">
            [@s.text name="projectInnovations.sharing.sustainedUse" /]:
          </label>

          [#local innovationSupportedValue = (element.projectInnovationInfo.innovationSupported)!{} /]
          [#local evidenceUptakeValue = (element.projectInnovationInfo.evidenceUptake)!{} /]

          [@customForm.likertScale name="${customName}.projectInnovationInfo.innovationSupported" id="${customName}.projectInnovationInfo.innovationSupported" value=(innovationSupportedValue) label="projectInnovations.sharing.sustainedUse.innovationSupported" required=true editable=editable /]
          [@customForm.likertScale name="${customName}.projectInnovationInfo.evidenceUptake" id="${customName}.projectInnovationInfo.evidenceUptake" value=(evidenceUptakeValue) label="projectInnovations.sharing.sustainedUse.evidenceUptake" required=true editable=editable /]
          
        </div>
      </div>

      [#-- hasKnowledgePotential --]
      <div class="col-md-12">
        [@customForm.labelText name="innovation.hasKnowledgePotential" text="projectInnovations.sharing.aboutTheTool.potential" required=true /]
        [#local hasKnowledgePotentialText = "hasKnowledgePotential" /]
        [#local hasKnowledgePotential = (element.projectInnovationInfo.hasKnowledgePotential.id)!-1 /]    

        <div class="col-md-1">
          [@customForm.radioFlat id="${hasKnowledgePotentialText}-yes" name="${customName}.projectInnovationInfo.hasKnowledgePotential.id" label="Yes" value="1" checked=(hasKnowledgePotential == 1) cssClass="radioType-${hasKnowledgePotentialText}" cssClassLabel="radio-label-yes" editable=editable /]
        </div>
        <div class="col-md-3">
          [@customForm.radioFlat id="${hasKnowledgePotentialText}-yesW" name="${customName}.projectInnovationInfo.hasKnowledgePotential.id" label="Yes with adaptations" value="2" checked=(hasKnowledgePotential == 2) cssClass="radioType-${hasKnowledgePotentialText}" cssClassLabel="radio-label-yes" editable=editable /]
        </div>
        <div class="col-md-1">
          [@customForm.radioFlat id="${hasKnowledgePotentialText}-no" name="${customName}.projectInnovationInfo.hasKnowledgePotential.id" label="No" value="3" checked=(hasKnowledgePotential == 3) cssClass="radioType-${hasKnowledgePotentialText}" cssClassLabel="radio-label-no" editable=editable /]
        </div>

        <div class="col-md-12 block-w-${hasKnowledgePotentialText} padding-left-2" style="display:${((element.projectInnovationInfo??)&&(element.projectInnovationInfo.hasKnowledgePotential??)&&(hasKnowledgePotential == 2))?then('block','none')};">
          [@customForm.textArea name="${customName}.projectInnovationInfo.reasonKnowledgePotential" i18nkey="projectInnovations.sharing.aboutTheTool.reasonProvided"  helpIcon=false className="limitWords-500" required=(editable) editable=editable /]
        </div>
      </div>
      [#-- knowledge support the outreach --]
      <div class="col-md-12">
        [@customForm.textArea name="${customName}.projectInnovationInfo.knowledgeMethodsAndToolsNarrative" i18nkey="projectInnovations.sharing.aboutTheTool.supportTheOutreach" helpIcon=false className="limitWords-500" required=false editable=editable /]
      </div>

      [#-- About the innovation --]
      [#--        <div class="form-group">
        <label class="label--2 col-md-12 blueLightColor">
          [@s.text name="projectInnovations.sharing.aboutTheTool" /]:
        </label>
        <div class="col-md-12 padding-left-2">
          -- Objetive --
          <div class="col-md-12">
            [@customForm.elementsListComponent name="${customName}.toolCategories" elementType="toolCategory" elementList=(element.toolCategories)![] label="projectInnovations.sharing.aboutTheTool.objetive" listName="toolCategoryList" keyFieldName="id" displayFieldName="name" required=false /]
          </div>
          -- knowledgeToolUsesNarrative 

        </div>
      </div>  --]
      
      [#-- Anticipated users --]
      <div class="form-group col-md-12 block-innovationAnticipatedUsers">
        [@customForm.labelText name="innovation.anticipatedUsers" text="projectInnovations.anticipatedUsers" twoPoints=false required=true isMainTitle=true /]
        [#local areUsersDetermined = (innovation.projectInnovationInfo.areUsersDetermined)! /]
        <div class="col-md-12">
          <div class="col-md-4">
            [@customForm.radioFlat id="anticipatedUsers-determined" name="innovation.projectInnovationInfo.areUsersDetermined" i18nkey="projectInnovations.anticipatedUsers.determined" value="true" checked=((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.areUsersDetermined??)&&(areUsersDetermined)) cssClass="radioType-anticipatedUsers" cssClassLabel="radio-label-yes" editable=editable /]
          </div>
          <div class="col-md-4">
            [@customForm.radioFlat id="anticipatedUsers-undetermined" name="innovation.projectInnovationInfo.areUsersDetermined" i18nkey="projectInnovations.anticipatedUsers.undetermined" value="false" checked=((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.areUsersDetermined??)&&(!areUsersDetermined)) cssClass="radioType-anticipatedUsers" cssClassLabel="radio-label-no" editable=editable /]
          </div>
        </div>
        <div class="col-md-12 block-anticipatedUsers" style="display: ${((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.areUsersDetermined??)&&(areUsersDetermined))?then('block','none')}">
          [#-- Actors --]
          <div class="col-md-12 actorsBlock">
            <label for="innovation.actors">[@s.text name="projectInnovations.anticipatedUsers.actors" /]:[@customForm.req required=true /]</label>
            <label class="note--2">
              <span class="inner-note">[@s.text name="projectInnovations.anticipatedUsers.actors.help" /]</span>
            </label>
            [#-- list of items --]
            <div class="actorsList">
              [#list (element.actors)![] as actor]
                [@actorsMacro name="innovation.actors" element=actor index=actor_index template=false /]
              [/#list]
            </div>
            [#if editable]
              <div class="addActors bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add actor </div>
              <div class="clearfix"></div>
            [/#if]
          </div>
          [#-- Organizations --]
          <div class="col-md-12 organizationsBlock">
            <label for="innovation.allianceOrganizations">[@s.text name="projectInnovations.anticipatedUsers.organizations" /]:[@customForm.req required=true /]</label>
            <label class="note--2">
              <p>[@s.text name="projectInnovations.anticipatedUsers.organizations.help" /]</p>
            </label>
            [#-- list of items --]
            <div class="organizationsList">
              [#list (element.allianceOrganizations)![] as organization]
                [@organizationsMacro name="innovation.allianceOrganizations" element=organization index=organization_index template=false /]
              [/#list]
            </div>
            [#if editable]
              <div class="addOrganizations bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add organization </div>
              <div class="clearfix"></div>
            [/#if]
          </div>
          [#-- Element item Template --]
          <div style="display:none">
            [@actorsMacro name="innovation.actors" element={} index=-1 template=true /]
            [@organizationsMacro name="innovation.allianceOrganizations" element={} index=-1 template=true /]
          </div>

        </div>
          
      </div>

      [#-- Beneficiries --]
      <div class="form-group col-md-12">
        [@customForm.textArea name="innovation.projectInnovationInfo.beneficiariesNarrative"  i18nkey="projectInnovations.beneficiaries"  placeholder="" className="limitWords-80" help="projectInnovations.beneficiaries.helpText" isNote=true helpIcon=false required=true editable=editable isMainTitle=true isWidthFull=true /]
      </div>
      
      [#-- URLs: Tool website, publications, stories and more  --]
      [#--        <div class="form-group">
        <label class="label--2 col-md-12 blueLightColor">[@s.text name="projectInnovations.sharing.urls" /]:</label>
        #-- hasToolUrl ---
        <div class="col-md-12 padding-left-2">
          [@customForm.labelText name="innovation.hasToolUrl" text="projectInnovations.sharing.urls.tool" /]
          [#local hasToolUrlText = "hasToolUrl" /]
          [#local hasToolUrl = (element.projectInnovationInfo.hasToolUrl)! /]    

          <div class="col-md-1">
            [@customForm.radioFlat id="${hasToolUrlText}-yes" name="${customName}.projectInnovationInfo.hasToolUrl" label="Yes" value="true" checked=((element.projectInnovationInfo??)&&(element.projectInnovationInfo.hasToolUrl??) &&(hasToolUrl)) cssClass="radioType-${hasToolUrlText}" cssClassLabel="radio-label-yes" editable=editable /]
          </div>
          <div class="col-md-1">
            [@customForm.radioFlat id="${hasToolUrlText}-no" name="${customName}.projectInnovationInfo.hasToolUrl" label="No" value="false" checked=((element.projectInnovationInfo??)&&(element.projectInnovationInfo.hasToolUrl??) &&(!hasToolUrl)) cssClass="radioType-${hasToolUrlText}" cssClassLabel="radio-label-no" editable=editable /]
          </div>

          #-- If yes - Evidence/Reference --
          <div class="col-md-12 block-yes-${hasToolUrlText}" style="display:${((element.projectInnovationInfo??)&&(element.projectInnovationInfo.hasToolUrl??) && (hasToolUrl))?then('block','none')};">
            <label class="note--2" style="width:100%"><p class="col-md-12">[@s.text name="projectInnovations.sharing.urls.tool.help" /]</p></label>
            #-- List URLs --
            <div class="form-group">
              <div class="col-md-12">
                <div class="referenceBlock">
                  <div class="referenceListUrl">
                    [#if element.referenceUrls?has_content]
                      [#list element.referenceUrls as referenceUrl]
                        [@customForm.evidence name="innovation.referenceUrls" element=referenceUrl index=referenceUrl_index template=false class="Url" /]
                      [/#list]
                    [/#if]
                  </div>
                  [#if editable]
                  <div class="addButtonReferenceUrl bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>Add Reference </div>
                  <div class="clearfix"></div>
                  [/#if]
                </div>
                #-- Element item Template --
                <div style="display:none">
                  [@customForm.evidence name="innovation.referenceUrls" element={} index=-1 template=true class="Url" /]
                </div>
              </div>
            </div>
          </div>
            
          #-- If not - reasonNotToolUrl --
          <div class="col-md-12 block-no-${hasToolUrlText}" style="display:${((element.projectInnovationInfo??)&&(element.projectInnovationInfo.hasToolUrl??) && (!hasToolUrl))?then('block','none')};">
            [@customForm.textArea name="${customName}.projectInnovationInfo.reasonNotToolUrl" i18nkey="projectInnovations.sharing.urls.reasonNoProvided"  helpIcon=false className="limitWords-500" required=(editable) editable=editable /]
          </div>
        </div>
      </div>  --]

      [#-- Collaboration with other innovations --]
      [#--        <div class="form-group">
        <label class="label--2 col-md-12 blueLightColor">[@s.text name="projectInnovations.sharing.collaboration" /]:</label>
        <div class="col-md-12 padding-left-2">
          #-- knowledgeCollaboration --
          <div class="col-md-12">
            <label class="note--2"><p>[@s.text name="projectInnovations.sharing.collaboration.help" /]</p></label>
            [@customForm.textArea name="${customName}.projectInnovationInfo.knowledgeCollaboration" i18nkey="projectInnovations.sharing.collaboration.knowledgeCollaboration" helpIcon=false className="limitWords-200" required=false editable=editable /]  
          </div>
          #-- urls Complementary Solutions --
          <div class="col-md-12">
            [@customForm.labelText name="innovation.hasComplementarySolutions" text="projectInnovations.sharing.collaboration.complementarySolutions" /]
            <div class="col-md-12">
              <div class="referenceBlock">
                <div class="referenceListComplementary">
                  [#if element.referenceComplementarySolutions?has_content]
                    [#list element.referenceComplementarySolutions as referenceComplementary]
                      [@customForm.evidence name="innovation.referenceComplementarySolutions" element=referenceComplementary index=referenceComplementary_index template=false class="Complementary" isDeliverable=false /]
                    [/#list]
                  [/#if]
                </div>
                [#if editable]
                <div class="addButtonReferenceComplementary bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>Add associated innovation</div>
                <div class="clearfix"></div>
                [/#if]
              </div>
              #-- Element item Template --
              <div style="display:none">
                [@customForm.evidence name="innovation.referenceComplementarySolutions" element={} index=-1 template=true  class="Complementary" isDeliverable=false /]
              </div>
            </div>
          </div>
        </div>
      </div>  --]

      [#-- Evidence and Reference --]
      <div class="form-group">
        [@customForm.labelText name="innovation.references" text="projectInnovations.readiness.evidence" helpText="projectInnovations.readiness.evidence.help" required=false isNote=true isMainTitle=true twoPoints=false classNameLabel="blueLightColor" isNoteCss="note" /]
        <br>
        <label class="note note--3 statusEvidenceInImpactArea"><span class="glyphicon glyphicon-exclamation-sign"></span> <span class="contentInformation" >Problem available</span></label>
        <div class="col-md-12">
          <div class="referenceBlock">
            <div class="referenceListReadiness">
              [#if element.references?has_content]
                [#list element.references as reference]
                  [@customForm.evidence name="innovation.references" element=reference index=reference_index template=false class="Readiness" /]
                [/#list]
              [/#if]
            </div>
            [#if editable]
            <div class="addButtonReferenceReadiness bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>Add evidence </div>
            <div class="clearfix"></div>
            [/#if]
          </div>
          [#-- Element item Template --]
          <div style="display:none">
            [@customForm.evidence name="innovation.references" element={} index=-1 template=true class="Readiness" /]
          </div>
        </div>
      </div>
      
  </div>
[/#macro]

[#function findElementID list id]
  [#list (list)![] as item]
    [#if (item.repIndGeographicScope.id == id)!false][#return true][/#if]
  [/#list]
  [#return false]
[/#function]

[#macro actorsMacro name element index=-1 template=false class=""]
  [#local customName = "${template?string('_TEMPLATE_', '')}${name}[${index}]"]

  [#local sexAgeNotApply = ((element.sexAgeNotApply??) && (element.sexAgeNotApply == true))! /]

  <div id="actorsInnovation-${(template?string('template', ''))}" class="actorsInnovation form-group grayBlueBox ${class}">
    [#-- Hidden not saved - id --]
    [@customForm.input name="${customName}.id" value=((element.id)?string)!"" editable=false display=false /]
    [#-- Dropdown Actors - Type --]
    <div class="col-md-12 select--flex padding-bottom-1">
      <div class="col-md-6">
        [@customForm.select name="${customName}.actor.id" i18nkey="projectInnovations.anticipatedUsers.actors.typeActor" listName="actorList" keyFieldName="id" displayFieldName="name" required=false editable=true /]
      </div>
      <div class="col-md-6 checkbox-sexAgeNotApply align-content-end">
        [@customForm.checkBoxFlat id="${customName}.sexAgeNotApply" name="${customName}.sexAgeNotApply" label="projectInnovations.anticipatedUsers.actors.sexAgeNotApply" value="true" checked=sexAgeNotApply editable=true cssClass="sexAgeNotApply" /]
      </div>
    </div>

    [#-- Checkbox Actors - Genders --]
    <div class="block-sexAgeNotApply col-md-12" style="display: ${(!sexAgeNotApply)?then('block','none')}">
      <div class="col-md-6">
        <label class="col-md-12">[@s.text name="projectInnovations.anticipatedUsers.actors.women" /]:</label>
        [#local isWomanWithYouth = ((element.womenYouth??) && (element.womenYouth == true)) /] 
        [#local isWomanNotYouth = ((element.womenNotYouth??) && (element.womenNotYouth == true)) /]
        <div class="col-md-5 innerOptions select--flex">
          <div class="col-md-6">
            [@customForm.checkBoxFlat id="${customName}.womenYouth" name="${customName}.womenYouth" label="projectInnovations.anticipatedUsers.actors.optionYouth" value="true" checked=isWomanWithYouth editable=true cssClass="check-gender" /]
          </div>
          <div class="col-md-6 align-content-end">
            [@customForm.input name="${customName}.womenYouthNumber"  className="targetValueNumber numericInput" type="number" required=false editable=true showTitle=false /]
          </div>
        </div>
        <div class="col-md-7 innerOptions select--flex">
          <div class="col-md-6">
            [@customForm.checkBoxFlat id="${customName}.womenNotYouth" name="${customName}.womenNotYouth" label="projectInnovations.anticipatedUsers.actors.optionNoYouth" value="true" checked=isWomanNotYouth editable=true cssClass="check-gender" /]
          </div>
          <div class="col-md-6 align-content-end">
            [@customForm.input name="${customName}.womenNonYouthNumber" className="targetValueNumber numericInput" type="number" required=false editable=true showTitle=false /] 
          </div>
        </div>
      </div>
      <div class="col-md-6">
        <label class="col-md-12">[@s.text name="projectInnovations.anticipatedUsers.actors.men" /]:</label>
        [#local isMenWithYouth = ((element.menYouth??) && (element.menYouth == true)) /] 
        [#local isMenNotYouth = ((element.menNotYouth??) && (element.menNotYouth == true)) /]
        <div class="col-md-5 innerOptions select--flex">
          <div class="col-md-6">
            [@customForm.checkBoxFlat id="${customName}.menYouth" name="${customName}.menYouth" label="projectInnovations.anticipatedUsers.actors.optionYouth" value="true" checked=isMenWithYouth editable=true cssClass="check-gender" /]
          </div>
          <div class="col-md-6 align-content-end">
            [@customForm.input name="${customName}.menYouthNumber"  className="targetValueNumber numericInput" type="number" required=false editable=true showTitle=false /]
          </div> 
        </div>
        <div class="col-md-7 innerOptions select--flex">
          <div class="col-md-6">
            [@customForm.checkBoxFlat id="${customName}.menNotYouth" name="${customName}.menNotYouth" label="projectInnovations.anticipatedUsers.actors.optionNoYouth" value="true" checked=isMenNotYouth editable=true cssClass="check-gender" /]
          </div>
          <div class="col-md-6 align-content-end">
            [@customForm.input name="${customName}.menNonYouthNumber" className="targetValueNumber numericInput" type="number" required=false editable=true showTitle=false /]
          </div>
        </div>
      </div>
      [#--        <div class="col-md-4">
        <label>[@s.text name="projectInnovations.anticipatedUsers.actors.noBinary" /]:</label>
        [#local isNonbinaryWithYouth = ((element.nonbinaryYouth??) && (element.nonbinaryYouth == true)) /] 
        [#local isNonbinaryNotYouth = ((element.nonbinaryNotYouth??) && (element.nonbinaryNotYouth == true)) /]
        [@customForm.checkBoxFlat id="${customName}.nonbinaryYouth" name="${customName}.nonbinaryYouth" label="projectInnovations.anticipatedUsers.actors.optionYouth" value="true" checked=isNonbinaryWithYouth editable=true /]
        [@customForm.checkBoxFlat id="${customName}.nonbinaryNotYouth" name="${customName}.nonbinaryNotYouth" label="projectInnovations.anticipatedUsers.actors.optionNoYouth" value="true" checked=isNonbinaryNotYouth editable=true /]
      </div>  --]
    </div>

    [#-- Remove --]
    [#if editable]<div class="removeElement sm removeIcon removeActor ${class}" title="Remove"></div>[/#if]
    <div class="clearfix"></div>
  </div>
[/#macro]

[#macro organizationsMacro name element index=-1 template=false class=""]
  [#local customName = "${template?string('_TEMPLATE_', '')}${name}[${index}]"]
  
  <div id="organizationsInnovation-${(template?string('template', ''))}" class="organizationsInnovation form-group grayBlueBox ${class}">
    [#-- Hidden not saved - id --]
    [@customForm.input name="${customName}.id" className="indexTag" value=((element.id)?string)!"" editable=false display=false /]
    [#-- "Dropdown Organizations - Type 
    <div class="col-md-12">
      [@customForm.select name="${customName}.institutionType.id" showTitle=false  i18nkey="projectInnovations.organizations" listName="institutionTypeList" keyFieldName="id" displayFieldName="name" required=false editable=true /]
    </div>
    --]
    [#-- Input Organization name --]
    <div class="col-md-12">
      <div class="col-md-6">
        [#local organizationId = (element.institution.id)!"" /]
        [@customForm.labelText name="${customName}.institution.id" text="projectInnovations.anticipatedUsers.organizations.name" required=true /]
        <mal-select name="${customName}.institution.id" id="${customName}.institution.id" data-value="${organizationId}" class="allianceOrganizations-institutions" ></mal-select> 
        [#-- [@customForm.select name="${customName}.institution.id" label="projectInnovations.anticipatedUsers.organizations.name"  i18nkey="projectInnovations.anticipatedUsers.organizations" listName="institutions" keyFieldName="id" displayFieldName="composedNameType" required=false editable=true /] --]
      </div>
      <div class="col-md-6">
        <div class="col-md-12">
          [@customForm.input name="${customName}.number" className="targetValueNumber" type="number" required=false editable=true i18nkey="projectInnovations.anticipatedUsers.organizations.number" /]
        </div>
      </div>
    </div>
    [#-- Checkbox - is a co-development --]
    [#--      <div class="col-md-12">
      [#local isCoDevelopment = ((element.scalingPartner??) && (element.scalingPartner == true)) /]
      [@customForm.checkBoxFlat id="${customName}.scalingPartner" name="${customName}.scalingPartner" label="projectInnovations.anticipatedUsers.organizations.coDevelopment" value="true" checked=isCoDevelopment editable=true /]
    </div>  --]

    [#-- Remove --]
    [#if editable]<div class="removeElement sm removeIcon removeOrganization ${class}" title="Remove"></div>[/#if]
    <div class="clearfix"></div>
  </div>
[/#macro]

[#macro scalingMacro name element editable label="" helpLabel="" listName=[] class=""]
  [#local customName = "${name}"]
  <div id="scalingInnovation" class="scaling form-group ${class}">
    [@customForm.labelText name="${customName}" text="${label}" helpText="${helpLabel}" required=true isMainTitle=true isNote=true /]

    <div class="scaling__container">
      <div class="scaling__line col-md-11"></div>
      [#if listName?has_content]
        [#list listName as item]
          <div class="col-md-1 scaling__item">
            [@customForm.radioFlat id="${customName}_${item_index}" name="${customName}" label="${item.id-1}" value="${item.id}" checked=((element??) && (element == (item.id)))!false editable=editable cssClass="scalingInnovation__item__value" canComment=false /]
          </div>
        [/#list]
      [/#if]
    </div>
    <div class="scaling__message grayBox">
      [#if element != -1]
        [#local elemInformation = listName?filter(it -> (it.id == element)) /]
        [#if elemInformation?size > 0]
          <h5>[@s.text name=elemInformation[0].name /]</h5>
          <p>[@s.text name=elemInformation[0].description /]</p>
        [/#if]
      [#else]
        <h5>[@s.text name="projectInnovations.readiness.scale.message1" /]</h5>
        <p>[@s.text name="projectInnovations.readiness.scale.message2" /]</p>
      [/#if]
    </div>
    <div class="scaling__hiddenInfo" style="display: none">
      [#if listName?has_content]
        [#list listName as item]
          <div class="scaling__hiddenInfo__item" class="scaling__hiddenInfo__item_${item_index}" id="${item.id}">
            <h5>${item.name}</h5>
            [#if item.description?has_content]
              <p>${item.description!""}</p>
            [#else]
              <p>No descripition available</p>
            [/#if]
          </div>
        [/#list]
      [/#if]
        
    </div>
    <div class="clearfix"></div>
  </div>
[/#macro]

[#macro impactScoreRadioGroup fieldName fieldLabel fieldValue={} editable=editable]
  <div class="form-group col-md-12">
    [#local fieldValue = fieldValue!{}]
    <div class="col-sm-12 col-md-6 col-lg-6 col-xlg-5 col-xxlg-4" style="padding-left: 0px;">
      <label class="label--2">
        [@s.text name="${fieldLabel}" /]:[@customForm.req required=(editable) /]
      </label>
      <div style="display: flex; flex-wrap: nowrap; gap: 3px; align-items: left;">
        [#if impactAreaScores?? && (impactAreaScores?size > 0)]
          [#list impactAreaScores as option]
            <div>
              [@customForm.radioFlat
                id="${fieldName}-${option.id}"
                name="innovation.projectInnovationInfo.${fieldName}.id"
                value="${option.id}"
                label="${option.description}"
                checked=(fieldValue.id?? && fieldValue.id == option.id)
                cssClass="radioType-contributionToCGIAR"
                cssClassContainer="padding-left-0"
                cssClassLabel="font-normal"
                editable=editable
              /]
            </div>
          [/#list]
        [#else]
          <p class="text-muted">No options available.</p>
        [/#if]
      </div>
    </div>
    <div class="col-sm-12 col-md-6 col-lg-6 col-xlg-7 col-xxlg-8" style="padding-right: 0px;">
      <div class="note" name="innovation.projectInnovationInfo.${fieldName}.id" style="display: ${(fieldValue.id?? && fieldValue.id == 3)?string('block','none')};" >
        [@s.text name="innovation.oneCGIAR.tooltip2"][@s.param]<strong style="display: contents;">[@s.text name="${fieldLabel}" /]</strong>[/@][/@]
      </div>
    </div>
  </div>
[/#macro]