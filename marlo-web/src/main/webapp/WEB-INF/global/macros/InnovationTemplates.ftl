[#ftl]
[#import "/WEB-INF/global/macros/deliverableMacros.ftl" as deliverableMacros /]

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
        [#local summaryPDF = "${baseUrl}/projects/${crpSession}/studySummary.do?studyID=${(element.id)!}&cycle=Reporting&year=${(actualPhase.year)!}"]
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

            <div class="form-group row">
            [#--  
              <div class="col-md-4">
                [@customForm.select name="innovation.projectInnovationInfo.year" className="setSelect2" i18nkey="policy.year" listName="getInnovationsYears(${innovationID})" header=false required=true editable=editable /]
              </div>
              --]
              <div class="col-md-12">
                [#local guideSheetURL = "https://drive.google.com/file/d/1JvceA0bdvqS5Een056ctL7zJr3hidToe/view" /]
                <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrlCdn}/global/images/icon-file.png" alt="" /> #C1 Innovations  -  Guideline </a> </small>
              </div>
            </div>
            <hr />
          
            [#-- Title --]
            <div class="form-group">
              [@customForm.input name="innovation.projectInnovationInfo.title" type="text" i18nkey="projectInnovations.title"  placeholder="" className="limitWords-30" help="projectInnovations.title.helpText" helpIcon=false required=true editable=editable isMainTitle=true /]
            </div>
            
            [#-- Short Title --]
            <div class="form-group">
              [@customForm.input name="innovation.projectInnovationInfo.shortTitle" type="text" i18nkey="projectInnovations.shortTitle"  placeholder="" className="limitWords-30" help="projectInnovations.shortTitle.helpText" helpIcon=false required=true editable=editable isMainTitle=true /]
            </div>
          
            [#-- Narrative --] 
            <div class="form-group">
              [@customForm.textArea name="innovation.projectInnovationInfo.narrative"  i18nkey="projectInnovations.narrative"  placeholder="" className="limitWords-80" help="projectInnovations.narrative.helpText" isNote=true helpIcon=false required=true editable=editable isMainTitle=true /]         
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
            <div class="form-group ">  
              [@customForm.select name="innovation.projectInnovationInfo.repIndInnovationNature.id" label="" i18nkey="projectInnovations.innovationNature" listName="innovationNatureList" keyFieldName="id" displayFieldName="name" required=true help="projectInnovations.innovationNature.helpText" isNote=true helpIcon=false className="innovationTypeSelect" editable=editable isMainTitle=true /]
            </div>

            [#-- Innovation Type --]
            <div class="form-group">
              [@customForm.select name="innovation.projectInnovationInfo.repIndInnovationType.id" label="" i18nkey="projectInnovations.innovationType" listName="innovationTypeList" keyFieldName="id" displayFieldName="name" required=true help="projectInnovations.innovationType.helpText" isNote=true helpIcon=false className="innovationTypeSelect" editable=editable isMainTitle=true /]
            </div>

            [#-- Contribution of CRP --] 
            <div class="form-group row">
            
              [#-- Other Innovation Type --]
              [#local isTypeSix = (innovation.projectInnovationInfo.repIndInnovationType.id == 6)!false]
              <div class="col-md-6 ">
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
              [#local geographicScopeList = (element.geographicScopes)![] ]
              [#local isGlobal =        findElementID(geographicScopeList,  action.reportingIndGeographicScopeGlobal) /]
              [#local isRegional =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeRegional) /]
              [#local isMultiNational = findElementID(geographicScopeList,  action.reportingIndGeographicScopeMultiNational) /]
              [#local isNational =      findElementID(geographicScopeList,  action.reportingIndGeographicScopeNational) /]
              [#local isSubNational =   findElementID(geographicScopeList,  action.reportingIndGeographicScopeSubNational) /]
              
              <label for="" class="label--2">[@s.text name="study.generalInformation.geographicScopeTopic" /]:[@customForm.req required=(editable && reportingActive) /]
                <div>
                    [@customForm.helpLabel name="study.generalInformation.geographicScopeTopic.note" showIcon=false isNote=true editable=editable/]
                </div>
              </label>
              <div class="form-group ('','simpleBox') geographicScopeInput">
                <div class="form-group row">
                  <div class="col-md-12 margin-top-10">
                    [#local isDisplayTitleScope = ((isMultiNational || isNational || isSubNational || isRegional) || (isGlobal && (geographicScopeList.length >1)))!false /]
                    <label for="innovation.geographicScopes" class="col-md-4">[@s.text name="study.generalInformation.geographicScope" /]: [@customForm.req required=(editable && reportingActive) /] </label>
                    <label for="" name="study.generalInformation.geographicImpact" class="col-md-8" style="display:${isDisplayTitleScope?string('block','none')}">[@s.text name="projectInnovations.geographicImpact" /]: [@customForm.req required=(editable && reportingActive) /]</label>
                  </div>
                </div>
                <div class="form-group row">
                  <div class="form-group col-md-4">
                    [#-- Geographic Scope --]
                    [@customForm.elementsListComponent name="innovation.geographicScopes" elementType="repIndGeographicScope" elementList=innovation.geographicScopes maxLimit=1 label="projectInnovations.geographicScope" listName="geographicScopeList" keyFieldName="id" displayFieldName="name" required=!isProgressActive /]
                  </div>
                  <div class="form-group nationalBlock col-md-4" style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}">
                    [#-- Multinational, National and Subnational scope --]
                    [@customForm.select name="innovation.countriesIds" label="" i18nkey="projectInnovations.countries" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="innovation.countriesIds" multiple=true required=!isProgressActive className="countriesSelect" disabled=!editable/]
                  </div>
                  <div class="form-group regionalBlock col-md-4" style="display:${(isRegional)?string('block','none')}">
                    [#-- Regional scope --]
                    [@customForm.elementsListComponent name="innovation.regions" elementType="locElement" elementList=innovation.regions label="projectInnovations.region"  listName="regions" keyFieldName="id" displayFieldName="composedName" required=false /]
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
              <label class="label--2">[@s.text name="projectInnovations.contributingCenters" /]: [@customForm.req required=true /]</label>
              <div class="note">
                <span class="glyphicon glyphicon-question-sign"></span> [@s.text name="study.generalInformation.ppapartner.note"][@s.param] 
                <a href="[@s.url namespace="/projects" action='${crpSession}/partners'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="__BLANK">clicking here</a>[/@][/@]
              </div>
              
              [#-- Contributing Centers --]
              <div class="col-md-6">
                [@customForm.elementsListComponent name="innovation.centers" i18nkey="innovation.centers" elementType="institution" elementList=innovation.centers label="projectInnovations.contributingCenters"  listName="centers" keyFieldName="id" displayFieldName="composedName" required=!isProgressActive /]
              </div>

              [#-- External Contributing Centers --]
              <div class="col-md-6 top-five-contributing">
                [@customForm.elementsListComponent name="innovation.contributingOrganizations" i18nkey="innovation.contributingOrganizations" maxLimit=5 elementType="institution" elementList=innovation.contributingOrganizations label="projectInnovations.contributingOrganizations"  listName="institutions" keyFieldName="id" displayFieldName="composedName" /]

                [#-- Request partner adition --]
                [#if editable]
                  <p id="addPartnerText" class="helpMessage">
                    If you cannot find the organization you are looking for, please 
                    <a class="popup" href="[@s.url action='${crpSession}/partnerSave' namespace="/projects"][@s.param name='projectID']${(projectID)!}[/@s.param][@s.param name='context'](${(actionName)!}: ID-${(innovation.id)!})[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
                      click here to [@s.text name="projectPartners.addPartnerMessage.second" /]
                    </a>
                  </p>
                  <br />
                [/#if]
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
            
            [#-- Milestones Contribution or Performance Indicators --]
            <div class="form-group">
                <div class="col-md-12">
                  <label class="label--2" style="width:100%;">[@s.text name="innovation.outcomes" /]:[@customForm.req required=(editable && !isProgressActive) /]
                  </label>
                  <label>
                    [@s.text name="innovation.outcomes.help" /]
                  </label> 
                </div>    
                [#local innovationMilestoneLink = "innovationMilestoneLink"]
                [#local showMilestoneIndicator = (innovation.projectInnovationInfo.hasMilestones?string)!"" /]
                <div class="col-md-1">
                  [@customForm.radioFlat id="${innovationMilestoneLink}-yes" name="innovation.projectInnovationInfo.hasMilestones" label="Yes" value="true" checked=(showMilestoneIndicator == "true") cssClass="radioType-${innovationMilestoneLink}" cssClassLabel="radio-label-yes" editable=editable /]
                </div>
                <div class="col-md-1">
                  [@customForm.radioFlat id="${innovationMilestoneLink}-no" name="innovation.projectInnovationInfo.hasMilestones" label="No" value="false" checked=(showMilestoneIndicator == "false") cssClass="radioType-${innovationMilestoneLink}" cssClassLabel="radio-label-no" editable=editable /]
                </div>
            </div> 

          
            <div class="form-group col-md-12 block-${innovationMilestoneLink}" style="display:${(showMilestoneIndicator == 'true')?string('block','none')}">
              [@customForm.elementsListComponent name="innovation.crpOutcomes" elementType="crpOutcome" elementList=(innovation.crpOutcomes)![] label="innovation.outcomes" helpIcon=false listName="crpOutcomes" keyFieldName="id" displayFieldName="composedName" required=!isProgressActive /]
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

            [#-- Anticipated users --]
            <div class="form-group col-md-12 block-innovationAnticipatedUsers">
              <label class="label--2">[@s.text name="projectInnovations.anticipatedUsers" /][@customForm.req required=true /]</label>
              <div class="col-md-12">
                <div class="col-md-4">
                  [@customForm.radioFlat id="anticipatedUsers-determined" name="innovation.projectInnovationInfo.hasMilestones" i18nkey="projectInnovations.anticipatedUsers.determined" value="true" checked=false cssClass="radioType-anticipatedUsers" cssClassLabel="radio-label-yes" editable=editable /]
                </div>
                <div class="col-md-4">
                  [@customForm.radioFlat id="anticipatedUsers-undetermined" name="innovation.projectInnovationInfo.hasMilestones" i18nkey="projectInnovations.anticipatedUsers.undetermined" value="false" checked=false cssClass="radioType-anticipatedUsers" cssClassLabel="radio-label-no" editable=editable /]
                </div>
              </div>
              <div class="col-md-12">
                [#-- Actors --]
                <div class="col-md-6 actorsBlock">
                  <label for="">[@s.text name="projectInnovations.anticipatedUsers.actors" /]:[@customForm.req required=false /]</label>
                  [#-- list of items --]
                  <div class="actorsList">
                  </div>
                  [#if editable]
                    <div class="addActors bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add actor </div>
                    <div class="clearfix"></div>
                  [/#if]
                </div>
                [#-- Organizations --]
                <div class="col-md-6 organizationsBlock">
                  <label for="">[@s.text name="projectInnovations.anticipatedUsers.organizations" /]:[@customForm.req required=false /]</label>
                  [#-- list of items --]
                  <div class="organizationsList">
                  </div>
                  [#if editable]
                    <div class="addOrganizations bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add organization </div>
                    <div class="clearfix"></div>
                  [/#if]
                </div>
                [#-- Element item Template --]
                <div style="display:none">
                  [@actorsMacro name="innovation.actors" element={} index=-1 template=true /]
                  [@organizationsMacro name="innovation.organizations" element={} index=-1 template=true /]
                </div>

              </div>
                
            </div>

            [#-- Partner users TEMPLATE --]
            <div id="partnerUsers" style="display:none">
              [#list partners as partner]
                <div class="institution-${partner.institution.id}">
                  [#assign usersList = (action.getUserList(partner.institution.id))![]]
                  <div class="users-2">
                    [#list usersList as user]
                      [@deliverableMacros.deliverableUserMacro element={} user=user index=user_index name="_TEMPLATE_innovation.partnerships[0].partnershipPersons" isUserChecked=false isResponsable=false /]
                    [/#list]
                  </div>
                </div>
              [/#list]
            </div>
      

            [#--  Contact person    --]
            <div class="form-group stageProcessOne col-md-12">
              <label class="label--2">[@s.text name="study.communications.contacts" /]:</label>
              <div id="addPartnerText" class="note--2">
                <p>
                  [@s.text name="study.communications.contacts.help" /]
                  <a class="popup" href="[@s.url namespace="/projects" action='${crpSession}/partnerSave'][@s.param name='expectedID']${(expectedID)!}[/@s.param][/@s.url]">
                    [@s.text name="study.communications.contacts.help2" /]
                  </a>
                </p>
              </div>
              <div class="projectInnovationsPartners">
                [@deliverableMacros.deliverablePartnerMacro element=(element.partnerships[0])!{} name="innovation.partnerships" index=0 defaultType=2 /]
              </div>
            </div>
          
        </div>
        
[/#macro]

[#macro innovationAlliance element name index=-1 template=false]
  <div id="alliance" class="borderBox clearfix">
    [#-- SDG Targets --]
    <div class="form-group">
      <label class="label--2" style="width:100%">[@s.text name="projectInnovations.alliance.sdgTargets" /]:</label>
      <label>[@s.text name="projectInnovations.alliance.sdgTargets.subtitle" /]</label>
      [@customForm.elementsListComponent name="innovation.sdgTargets" elementType="sdgTarget" elementList=(innovation.sdgTargets)![] helpIcon=false listName="sdgList" keyFieldName="id" displayFieldName="shortName" required=false showTitle=false /]
    </div>
    [#-- Alliance Research Theme --]
    <div class="form-group">
      <label class="label--2" style="width:100%">[@s.text name="projectInnovations.alliance.researchTheme" /]:</label>
      <label>[@s.text name="projectInnovations.alliance.researchTheme.subtitle" /]</label>
      [#if allianceLeverList?has_content]
        [#list allianceLeverList as lever]
          [#if lever.description?has_content]
            [#local customLabel = "${lever.name} : ${lever.description}" /]
          [#else]
            [#local customLabel = "${lever.name}" /]
          [/#if]
          [@customForm.checkBoxFlat id="lever-${lever.id}" name="innovation.allianceLevers" label="${customLabel}" value="${lever.id}" checked=(innovation.allianceLevers?seq_contains(lever.id)) editable=editable /]
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
      [@customForm.elementsListComponent name="innovation.intellectualProperties" elementType="intellectualProperty" elementList=(innovation.intellectualProperties)![] helpIcon=false listName="intellectualPropertyList" keyFieldName="id" displayFieldName="name" required=false showTitle=true label="projectInnovations.alliance.intellectualProperty.description" /]
      [#-- Intellectual property rights - Legal Restrictions --]
      <div class="form-group">
        <div class="col-md-12">
          <label>
            [@s.text name="projectInnovations.alliance.intellectualProperty.legalRestrictions" /]
          </label> 
        </div>
        [#local innovationIntellectualLegalRestrictions = "innovationIntellectualLegalRestrictions" /]
        [#local showLegalRestrictions = (innovation.projectInnovationInfo.hasLegalRestrictions?string)!"" /]    

        <div class="col-md-1">
          [@customForm.radioFlat id="${innovationIntellectualLegalRestrictions}-yes" name="innovation.projectInnovationInfo.hasLegalRestrictions" label="Yes" value="true" checked=(showLegalRestrictions == "true") cssClass="radioType-${innovationIntellectualLegalRestrictions}" cssClassLabel="radio-label-yes" editable=editable /]
        </div>
        <div class="col-md-1">
          [@customForm.radioFlat id="${innovationIntellectualLegalRestrictions}-no" name="innovation.projectInnovationInfo.hasLegalRestrictions" label="No" value="false" checked=(showLegalRestrictions == "false") cssClass="radioType-${innovationIntellectualLegalRestrictions}" cssClassLabel="radio-label-no" editable=editable /]
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
        [#local showCommercialization = (innovation.projectInnovationInfo.hasCommercialization?string)!"" /]    

        <div class="col-md-1">
          [@customForm.radioFlat id="${innovationIntellectualCommercialization}-yes" name="innovation.projectInnovationInfo.hasCommercialization" label="Yes" value="true" checked=(showCommercialization == "true") cssClass="radioType-${innovationIntellectualCommercialization}" cssClassLabel="radio-label-yes" editable=editable /]
        </div>
        <div class="col-md-1">
          [@customForm.radioFlat id="${innovationIntellectualCommercialization}-no" name="innovation.projectInnovationInfo.hasCommercialization" label="No" value="false" checked=(showCommercialization == "false") cssClass="radioType-${innovationIntellectualCommercialization}" cssClassLabel="radio-label-no" editable=editable /]
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
        [#local showFurtherDevelopment = (innovation.projectInnovationInfo.hasFurtherDevelopment?string)!"" /]    

        <div class="col-md-1">
          [@customForm.radioFlat id="${innovationIntellectualFurtherDevelopment}-yes" name="innovation.projectInnovationInfo.hasFurtherDevelopment" label="Yes" value="true" checked=(showFurtherDevelopment == "true") cssClass="radioType-${innovationIntellectualFurtherDevelopment}" cssClassLabel="radio-label-yes" editable=editable /]
        </div>
        <div class="col-md-1">
          [@customForm.radioFlat id="${innovationIntellectualFurtherDevelopment}-no" name="innovation.projectInnovationInfo.hasFurtherDevelopment" label="No" value="false" checked=(showFurtherDevelopment == "false") cssClass="radioType-${innovationIntellectualFurtherDevelopment}" cssClassLabel="radio-label-no" editable=editable /]
        </div>
      </div>
      
    </div>
  </div>
[/#macro]

[#macro innovationOneCGIAR element name index=-1 template=false]
  <div id="oneCGIAR" class="borderBox clearfix">
    <div class="form-group row">
      <hr class="line-hr" />
      <div class="col-md-12">
        <h3>[@s.text name="projectInnovations.oneCGIAR" /]</h3>
      </div>
    </div>
  </div>
[/#macro]

[#macro innovationReadiness element name index=-1 template=false]
  <div id="readiness" class="borderBox clearfix">
    <div class="form-group row">
      <hr class="line-hr" />
      <div class="col-md-12">
        <h3>[@s.text name="projectInnovations.readiness" /]</h3>
      </div>
    </div>
  </div>
[/#macro]

[#macro innovationFunding element name index=-1 template=false]
  <div id="funding" class="borderBox clearfix">
    <div class="form-group row">
      <hr class="line-hr" />
      <div class="col-md-12">
        <h3>[@s.text name="projectInnovations.funding" /]</h3>
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
  <div id="actorsInnovation-${(template?string('template', ''))}" class="actorsInnovation form-group grayBlueBox ${class}">
    [#-- Dropdown Actors - Type --]
    <div class="col-md-12">
      [@customForm.elementsListComponent name="${customName}.actors" showTitle=false elementType="actor" elementList=(element.actors)![] label="projectInnovations.actors" listName="actors" keyFieldName="id" displayFieldName="composedName" required=false /]
    </div>
    [#-- Checkbox Actors - Genders --]
    <div class="col-md-12">
      <div class="col-md-4">
        <label>[@s.text name="projectInnovations.anticipatedUsers.actors.women" /]:</label>
        [@customForm.checkBoxFlat id="innovation_actors_women_${index}" name="${customName}.actors.women" label="projectInnovations.anticipatedUsers.actors.optionYouth" value="true" checked=false editable=true /]
        [@customForm.checkBoxFlat id="innovation_actors_women_${index}" name="${customName}.actors.women" label="projectInnovations.anticipatedUsers.actors.optionNoYouth" value="false" checked=false editable=true /]
      </div>
      <div class="col-md-4">
        <label>[@s.text name="projectInnovations.anticipatedUsers.actors.men" /]:</label>
        [@customForm.checkBoxFlat id="innovation_actors_men_${index}" name="${customName}.actors.men" label="projectInnovations.anticipatedUsers.actors.optionYouth" value="true" checked=false editable=true /]
        [@customForm.checkBoxFlat id="innovation_actors_men_${index}" name="${customName}.actors.men" label="projectInnovations.anticipatedUsers.actors.optionNoYouth" value="false" checked=false editable=true /]
      </div>
      <div class="col-md-4">
        <label>[@s.text name="projectInnovations.anticipatedUsers.actors.noBinary" /]:</label>
        [@customForm.checkBoxFlat id="innovation_actors_noBinary_${index}" name="${customName}.actors.noBinary" label="projectInnovations.anticipatedUsers.actors.optionYouth" value="true" checked=false editable=true /]
        [@customForm.checkBoxFlat id="innovation_actors_noBinary_${index}" name="${customName}.actors.noBinary" label="projectInnovations.anticipatedUsers.actors.optionNoYouth" value="false" checked=false editable=true /]
      </div>
    </div>

    [#-- Remove --]
    [#if editable]<div class="removeElement sm removeIcon removeActor ${class}" title="Remove"></div>[/#if]
    <div class="clearfix"></div>
  </div>
[/#macro]

[#macro organizationsMacro name element index=-1 template=false class=""]
  [#local customName = "${template?string('_TEMPLATE_', '')}${name}[${index}]"]
  <div id="organizationsInnovation-${(template?string('template', ''))}" class="organizationsInnovation form-group grayBlueBox ${class}">
    [#-- "Dropdown Organizations - Type --]
    <div class="col-md-12">
      [@customForm.elementsListComponent name="${customName}.organizations" showTitle=false elementType="organization" elementList=(element.organizations)![] label="projectInnovations.organizations" listName="organizations" keyFieldName="id" displayFieldName="composedName" required=false /]
    </div>
    [#-- Input Organization name --]
    <div class="col-md-12">
      <label>[@s.text name="projectInnovations.anticipatedUsers.organizations.name" /]:</label>
      [@customForm.input name="${customName}.organizations.name" type="text" i18nkey="projectInnovations.anticipatedUsers.organizations" helpIcon=false required=false editable=true showTitle=false /]
    </div>
    [#-- Checkbox - is a co-development --]
    <div class="col-md-12">
      [@customForm.checkBoxFlat id="innovation_organizations_coDevelopment_${index}" name="${customName}.organizations.coDevelopment" label="projectInnovations.anticipatedUsers.organizations.coDevelopment" value="true" checked=false editable=true /]
    </div>

    [#-- Remove --]
    [#if editable]<div class="removeElement sm removeIcon removeOrganization ${class}" title="Remove"></div>[/#if]
    <div class="clearfix"></div>
  </div>
[/#macro]