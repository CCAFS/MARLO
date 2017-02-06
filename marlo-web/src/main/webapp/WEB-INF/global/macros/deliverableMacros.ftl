[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

[#macro deliverableLicenseMacro]
<div class="simpleBox">
  <div class="form-group row">
    <label class="col-md-9" for="">[@s.text name="project.deliverable.dissemination.adoptedLicenseQuestion" /] [@customForm.req /]</label>
    <div class="col-md-3">[@customForm.yesNoInput name="deliverable.adoptedLicense"  editable=editable inverse=false  cssClass="license text-center" /] </div>  
  </div>
  [#-- Deliverable type computer software --]
  <div class="radio-block licenseOptions-block" style="display:${((deliverable.adoptedLicense)!false)?string('block','none')}">
    <hr />
    [#if editable]
      <div class="licenseOptions computerLicense" style="display:[#if deliverable.deliverableType?? && deliverable.deliverableType.id==52 ]block [#else]none[/#if];">
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="MIT" [#if ((deliverable.licenseType) == "MIT")!false]checked="checked"[/#if]/> MIT License</div>
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="GNU" [#if ((deliverable.licenseType) == "GNU")!false]checked="checked"[/#if]/> GNU General Public License</div>
      </div>
      [#-- Deliverable type data --]
      <div class=" licenseOptions dataLicense" style="display:[#if deliverable.deliverableType?? && (deliverable.deliverableType.id==51 || deliverable.deliverableType.id==74)]block [#else]none[/#if];">
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="CC_LICENSES" [#if ((deliverable.licenseType) == "CC_LICENSES")!false]checked="checked"[/#if]/> CC licenses version 4.0</div>
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="CC_PUBLIC" [#if ((deliverable.licenseType) == "CC_PUBLIC")!false]checked="checked"[/#if]/> CC Public Domain Dedication (CC0 1.0)</div>
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="OPEN_DATA" [#if ((deliverable.licenseType) == "OPEN_DATA")!false]checked="checked"[/#if]/> Open Data Commons (ODC)</div>
      </div>
      [#-- Deliverable type other research types --]
      <div class=" licenseOptions" style="display:block;">
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="CC_BY" [#if ((deliverable.licenseType) == "CC_BY")!false]checked="checked"[/#if]/> CC-BY <small>(allow modifications and commercial use)</small></div>
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="CC_BY_SA" [#if ((deliverable.licenseType) == "CC_BY_SA")!false]checked="checked"[/#if]/> CC-BY-SA <small>(allow modifications as long as other share alike and commercial use)</small></div>
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="CC_BY_ND" [#if ((deliverable.licenseType) == "CC_BY_ND")!false]checked="checked"[/#if]/> CC-BY-ND <small>(allow commercial use but no modifications)</small></div>
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="CC_BY_NC" [#if ((deliverable.licenseType) == "CC_BY_NC")!false]checked="checked"[/#if]/> CC-BY-NC <small>(allow modifications but no commercial use)</small></div>
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="CC_BY_NC_SA" [#if ((deliverable.licenseType) == "CC_BY_NC_SA")!false]checked="checked"[/#if]/> CC-BY-NC-SA <small>(allow modifications as long as other share alike, but no commercial use)</small></div>
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="CC_BY_NC_ND" [#if ((deliverable.licenseType) == "CC_BY_NC_ND")!false]checked="checked"[/#if]/> CC-BY-NC-ND <small>(don't allow modifications neither commercial use)</small></div>
        <div class="radio"><input type="radio" name="deliverable.license" id="" value="OTHER" [#if ((deliverable.licenseType) == "OTHER")!false]checked="checked"[/#if]/> Other</div>
      </div>
      [#-- Other --]
      <div class="licenseOptions">
        <div class="form-group row">
          <div class="col-md-6 licence-modifications" style="display:[#if (deliverable.licenseType)?? && (deliverable.licenseType)=="OTHER"]block[#else]none [/#if];" >
            [@customForm.input name="deliverable.otherLicense" showTitle=false className="" type="text" placeholder="Please specify" disabled=!editable className="otherLicense"  required=true editable=editable /]
          </div>
          <div class="col-md-6 licence-modifications" style="display:[#if (deliverable.licenseType)?? && (deliverable.licenseType)=="OTHER"]block[#else]none [/#if];" >
            <label class="col-md-6" for="">[@s.text name="project.deliverable.dissemination.licenseModifications" /]</label>
            <div class="col-md-6">
              [@customForm.yesNoInput name="deliverable.allowModifications"  editable=editable inverse=false cssClass="licenceModifications text-center" /] 
            </div>  
          </div>
        </div>
      </div>
    [#else]
      [#-- Deliverable type computer software --]
      <div class=" licenseOptions computerLicense" style="display:[#if deliverable.deliverableType?? && deliverable.deliverableType.id==52 ]block [#else]none[/#if];">
        [#if deliverable.licenseType?? && deliverable.licenseType == "MIT"]<p class="checked">MIT License</p>[/#if]
        [#if deliverable.licenseType?? && deliverable.licenseType == "GNU"]<p class="checked">GNU General Public License</p>[/#if]
      </div>
      [#-- Deliverable type data --]
      <div class=" licenseOptions dataLicense" style="display:[#if deliverable.deliverableType?? && (deliverable.deliverableType.id==51 || deliverable.deliverableType.id==74)]block [#else]none[/#if];">
        [#if deliverable.licenseType?? && deliverable.licenseType == "CC_LICENSES"]<p class="checked">CC licenses version 4.0</p>[/#if]
        [#if deliverable.licenseType?? && deliverable.licenseType == "CC_PUBLIC"]<p class="checked">CC Public Domain Dedication (CC0 1.0)</p>[/#if]
        [#if deliverable.licenseType?? && deliverable.licenseType == "OPEN_DATA"]<p class="checked">Open Data Commons (ODC)</p>[/#if]
      </div>
      [#-- Deliverable type other research types --]
      <div class="licenseOptions" style="display:block;">
        [#if deliverable.licenseType?? && deliverable.licenseType == "CC_BY"]<p class="checked">CC-BY <small>(allow modifications and commercial use)</small></p>[/#if]
        [#if deliverable.licenseType?? && deliverable.licenseType == "CC_BY_SA"]<p class="checked">CC-BY-SA <small>(allow modifications as long as other share alike and commercial use)</small></p>[/#if]
        [#if deliverable.licenseType?? && deliverable.licenseType == "CC_BY_ND"]<p class="checked">CC-BY-ND <small>(allow commercial use but no modifications)</small></p>[/#if]
        [#if deliverable.licenseType?? && deliverable.licenseType == "CC_BY_NC"]<p class="checked">CC-BY-NC <small>(allow modifications but no commercial use)</small></small></p>[/#if]
        [#if deliverable.licenseType?? && deliverable.licenseType == "CC_BY_NC_SA"]<p class="checked">CC-BY-NC-SA <small>(allow modifications as long as other share alike, but no commercial use)</small></p>[/#if]
        [#if deliverable.licenseType?? && deliverable.licenseType == "CC_BY_NC_ND"]<p class="checked">CC-BY-NC-ND <small>(don't allow modifications neither commercial use)</small></small></p>[/#if]
      </div>
      [#-- Other --]
      <div class="licenseOptions">
        [#if deliverable.licenseType?? && deliverable.licenseType == "OTHER"]
        
          <p class="checked"> Other </p>
          
          <div class="form-group row">
            <div class="col-md-6 licence-modifications">
              [@customForm.input name="deliverable.otherLicense" showTitle=false className="" type="text" placeholder="Please specify" disabled=!editable className="otherLicense"  required=true editable=editable /]
            </div>
            <div class="col-md-6 licence-modifications">
              <label class="col-md-6" for="">Does this license allows modifications?</label>
              <div class="col-md-6">[@customForm.yesNoInput name="deliverable.allowModifications"  editable=editable inverse=false cssClass="licenceModifications text-center" /] </div>  
            </div>
          </div>
        [/#if]
      </div>
    [/#if]
  </div>
</div>
[/#macro]


[#macro isOpenaccessMacro ]
<div class="simpleBox form-group">
<input type="hidden"  name="deliverable.dissemination.id" value="${(deliverable.dissemination.id)!"-1"}" />
  <div class="row ">
    <label class="col-md-9" for="">Is this deliverable Open Access? [@customForm.req /]</label>
    <div class="col-md-3">
      [@customForm.yesNoInput name="deliverable.dissemination.isOpenAccess"  editable=editable inverse=false cssClass="accessible text-center" /]  
    </div>
  </div> 
  <div class="openAccessOptions radio-block" style="display: ${((deliverable.dissemination.isOpenAccess)!false)?string("none","block")};">
    <hr />
    [#if editable] 
    <label for="">Select the Open Access restriction:[@customForm.req /]</label>
    <div class="radio">
      <label><input type="radio" name="deliverable.dissemination.type" value="intellectualProperty" [#if (deliverable.dissemination.intellectualProperty?? && (deliverable.dissemination.intellectualProperty))]checked="checked"[/#if]>Intellectual Property Rights (confidential information)</label>
    </div>
    <div class="radio">
      <label><input type="radio" name="deliverable.dissemination.type" value="limitedExclusivity" [#if deliverable.dissemination.limitedExclusivity?? && (deliverable.dissemination.limitedExclusivity)]checked="checked"[/#if]>Limited Exclusivity Agreements</label>
    </div>
    <div class="radio">
      <label><input type="radio" name="deliverable.dissemination.type" value="restrictedUseAgreement" [#if deliverable.dissemination.restrictedUseAgreement?? && (deliverable.dissemination.restrictedUseAgreement)]checked="checked"[/#if]>Restricted Use Agreement - Restricted access (if so, what are these periods?)</label>
    </div>
    <div class="radio">
      <label><input type="radio" name="deliverable.dissemination.type" value="effectiveDateRestriction"[#if deliverable.dissemination.effectiveDateRestriction?? && (deliverable.dissemination.effectiveDateRestriction)]checked="checked"[/#if] >Effective Date Restriction - embargoed periods (if so, what are these periods?)</label>
    </div>
    [#else]
    [#if deliverable.dissemination.intellectualProperty?? && deliverable.dissemination.intellectualProperty]<p class="checked">Intellectual Property Rights (confidential information) </p>[/#if]
    [#if deliverable.dissemination.limitedExclusivity?? && deliverable.dissemination.limitedExclusivity]<p class="checked">Limited Exclusivity Agreements </p>[/#if]
    [#if deliverable.dissemination.restrictedUseAgreement?? && deliverable.dissemination.restrictedUseAgreement]<p class="checked">Restricted Use Agreement - Restricted access (if so, what are these periods?) </p>[/#if]
    [#if deliverable.dissemination.effectiveDateRestriction?? && deliverable.dissemination.effectiveDateRestriction]<p class="checked">Effective Date Restriction - embargoed periods (if so, what are these periods?) </p>[/#if]
    [/#if]
    <div class="row restrictionDate-block" style="display:[#if (deliverable.dissemination.restrictedUseAgreement)?? && (deliverable.dissemination.restrictedUseAgreement)||(deliverable.dissemination.effectiveDateRestriction)?? && (deliverable.dissemination.effectiveDateRestriction) ]block[#else]none [/#if];">
      <div class="col-md-5">
        [@customForm.input name="deliverable.dissemination.${(deliverable.dissemination.restrictedUseAgreement?string('restrictedAccessUntil','restrictedEmbargoed'))!'restrictedAccessUntil'}" type="text" i18nkey="${(deliverable.dissemination.restrictedUseAgreement?string('Restricted access until','Restricted embargoed date'))!}"  placeholder="" className="restrictionDate col-md-6" required=true editable=editable /]
      </div>
    </div>
  </div>
</div>
[/#macro]


[#macro alreadyDisseminatedMacro ]
<div class="simpleBox form-group">
  <div class=" row">
    <span class="col-md-9">
      <label  for="">[@s.text name="project.deliverable.dissemination.alreadyDisseminatedQuestion" /] [@customForm.req /]</label>
      <p><small>[@s.text name="project.deliverable.dissemination.alreadyDisseminatedSubQ" /] </small></p>
    </span>
    <div class="col-md-3">
      [@customForm.yesNoInput name="deliverable.dissemination.alreadyDisseminated"  editable=editable inverse=false cssClass="findable text-center" /] 
    </div>  
  </div>
  <div class="findableOptions" style="display:[#if (deliverable.dissemination.alreadyDisseminated)?? && (deliverable.dissemination.alreadyDisseminated)]block[#else]none [/#if]">
    <hr />
    [@findableOptions /]
  </div>
</div>
[/#macro]

[#macro findableOptions ]
[#-- Note --]
<div class="note">[@s.text name="project.deliverable.dissemination.channelInfo" /]</div>
<div class="form-group row">
  <div class="col-md-4">
    [#if editable]
      [@customForm.select name="deliverable.dissemination.disseminationChannel" value="'${(deliverable.dissemination.disseminationChannel)!}'"  stringKey=true label=""  i18nkey="project.deliverable.dissemination.selectChannelLabel" listName="channels" className="disseminationChannel"   multiple=false required=true   editable=editable/]
    [#else]
    <label for="disChannel" style="display:block;">Dissemination channel:</label>
    <p>${(deliverable.dissemination.disseminationChannel)!'Prefilled if available'}</p>
    [/#if]
  </div>
  <div class="col-md-8">
    [#-- CGSpace examples & instructions --]
    <div class="exampleUrl-block channel-cgspace" style="display:[#if deliverable.dissemination.disseminationChannel?? && deliverable.dissemination.disseminationChannel=="cgspace"]block[#else]none[/#if];">
      <label for="">[@s.text name="project.deliverable.dissemination.exampleUrl" /]:</label>
      <p><small>https://cgspace.cgiar.org/handle/10568/79435</small></p>
    </div>
    [#-- Dataverse examples & instructions --]
    <div class="exampleUrl-block channel-dataverse" style="display:[#if deliverable.dissemination.disseminationChannel?? &&  deliverable.dissemination.disseminationChannel=="dataverse"]block[#else]none[/#if];">
      <label for="">[@s.text name="project.deliverable.dissemination.exampleUrl" /]:</label>
      <p><small>https://dataverse.harvard.edu/dataset.xhtml?persistentId=doi:10.7910/DVN/0ZEXKC</small></p>
    </div>
  </div>
</div>
 
<div id="disseminationUrl" style="display:[#if deliverable.dissemination.disseminationChannel?? && (deliverable.dissemination.disseminationChannel=="cgspace" || deliverable.dissemination.disseminationChannel=="dataverse" || deliverable.dissemination.disseminationChannel=="other")]block[#else]none[/#if];">
  <div class="form-group row"> 
    <div class="col-md-10">
      [@customForm.input name="deliverable.dissemination.disseminationUrl" type="text" i18nkey="project.deliverable.dissemination.disseminationUrl"  placeholder="" className="deliverableDisseminationUrl" required=true editable=editable /]
    </div>
    <div class="col-md-2">
      <br />
      [#if editable]<div id="fillMetadata" class="checkButton" style="display:[#if deliverable.dissemination.disseminationChannel?? && (deliverable.dissemination.disseminationChannel=="cgspace" || deliverable.dissemination.disseminationChannel=="dataverse")]block[#else]none[/#if];">[@s.text name="project.deliverable.dissemination.sync" /]</div>[/#if]
    </div>
  </div>
</div>
<div id="metadata-output"></div>
[/#macro]

[#macro deliverableMetadataMacro ]
<div class="form-group ">
  [@deliverableMacros.metadataField title="title" encodedName="dc.title" type="input" require=false/]
</div>
<div class="form-group ">
  [@deliverableMacros.metadataField title="description" encodedName="dc.description.abstract" type="textArea" require=false/]
</div>
<div class="form-group row">
  <div class="col-md-6">
    [@deliverableMacros.metadataField title="date" encodedName="dc.date" type="input" require=false/]
  </div>
  <div class="col-md-6">
    [@deliverableMacros.metadataField title="language" encodedName="dc.language" type="input" require=false/]
  </div>
</div>
<div class="form-group row">
  <div class="col-md-6">
    [@deliverableMacros.metadataField title="country" encodedName="cg:coverage.country" type="input" require=false/]
  </div>
  <div class="col-md-6">
    [@deliverableMacros.metadataField title="keywords" encodedName="marlo.keywords" type="input" require=false/]
  </div>
</div>  
<div class="form-group ">
  [@deliverableMacros.metadataField title="citation" encodedName="dc.identifier.citation" type="textArea" require=false/]
</div>
<div class="form-group row">
  <div class="col-md-6">
    [@deliverableMacros.metadataField title="Handle" encodedName="marlo.handle" type="input" require=false/]
  </div>
  <div class="col-md-6">
    [@deliverableMacros.metadataField title="DOI" encodedName="marlo.doi" type="input" require=false/]
  </div>
</div>
 
<hr />
 
[#-- Creator/Authors --]
<div class="form-group">
  <label for="">[@s.text name="metadata.creator" /]:  </label>
  [#if editable]<div class="note">[@s.text name = "project.deliverable.dissemination.authorsInfo" /]</div>[/#if]
  <div class="authorsList simpleBox row" >
    [#if deliverable.users?has_content]
      [#list deliverable.users as author]
        [@deliverableMacros.authorMacro element=author index=author_index name="deliverable.users"  /]
      [/#list]
    [#else]
      <p class="emptyText text-center "> [@s.text name="project.deliverable.dissemination.notCreators" /]</p>
    [/#if]
  </div>
  [#if editable]
  <div class="form-group row">
    <div class="col-md-3"><input class="form-control input-sm lName" placeholder="Last Name" type="text" /> </div>
    <div class="col-md-3"><input class="form-control input-sm fName" placeholder="First Name" type="text" /> </div>
    <div class="col-md-3"><input class="form-control input-sm oId" placeholder="Orcid Id" type="text" /> </div>
    <div class="col-md-3">
      <div id="" class="addAuthor text-right"><div class="button-blue "><span class="glyphicon glyphicon-plus-sign"></span> [@s.text name="project.deliverable.dissemination.addAuthor" /]</div></div>
    </div>
  </div>
  [/#if] 
</div>

<div class="publicationMetadataBlock" style="display:${checkDeliverableTypes()!};">
  <br />
  <h4 class="sectionSubTitle">[@s.text name="project.deliverable.dissemination.publicationTitle"/]</h4>
   
  <input type="hidden" name="deliverable.publication.id" value="${(deliverable.publication.id)!}"/>
  <div class="form-group row">
    <div class="col-md-4">[@customForm.input name="deliverable.publication.volume" i18nkey="project.deliverable.dissemination.volume" className="" type="text" disabled=!editable  required=true editable=editable /]</div>
    <div class="col-md-4">[@customForm.input name="deliverable.publication.issue" i18nkey="project.deliverable.dissemination.issue" className="" type="text" disabled=!editable  required=false editable=editable /]</div>
    <div class="col-md-4">[@customForm.input name="deliverable.publication.pages" i18nkey="project.deliverable.dissemination.pages" className="" type="text" disabled=!editable  required=false editable=editable /]</div>
  </div>
  <div class="form-group">
    [@customForm.input name="deliverable.publication.journal" i18nkey="project.deliverable.dissemination.journalName" className="" type="text" disabled=!editable  required=true editable=editable /]
  </div>
  <div class="form-group">
    <label for="">[@s.text name="project.deliverable.dissemination.indicatorsJournal" /]:<span class="red">*</span></label>
    <div class="checkbox">
      [#if editable]
        <label for="isiPublication"><input type="checkbox" id="isiPublication"  name="deliverable.publication.isiPublication" value="true" [#if deliverable.publication?? && deliverable.publication.isiPublication?? && deliverable.publication.isiPublication]checked[/#if]/>Tick this box if this journal article is an ISI publication <small>(check at http://ip-science.thomsonreuters.com/mjl/ for the list)</small></label>  
        <label for="nasr"><input type="checkbox" id="nasr" name="deliverable.publication.nasr" value="true" [#if deliverable.publication?? && deliverable.publication.nasr?? && deliverable.publication.nasr]checked[/#if]/>Does this article have a co-author from a developing country National Agricultural Research System (NARS) ?</label>
        <label for="coAuthor"><input type="checkbox" id="coAuthor" name="deliverable.publication.coAuthor" value="true" [#if deliverable.publication?? && deliverable.publication.coAuthor?? && deliverable.publication.coAuthor]checked[/#if] />Does this article have a co-author based in an Earth System Science-related academic department?</label>
      [#else]
        <p [#if deliverable.publication?? && deliverable.publication.isiPublication?? && deliverable.publication.isiPublication]class="checked">[#else]class="noChecked ">[/#if]Tick this box if this journal article is an ISI publication (check at http://ip-science.thomsonreuters.com/mjl/ for the list)</p>
        <p [#if deliverable.publication?? && deliverable.publication.nasr?? && deliverable.publication.nasr]class="checked"[#else]class="noChecked"[/#if]>Does this article have a co-author from a developing country National Agricultural Research System (NARS) ?</p>
        <p [#if deliverable.publication?? && deliverable.publication.coAuthor?? && deliverable.publication.coAuthor]class="checked"[#else]class="noChecked"[/#if]>Does this article have a co-author based in an Earth System Science-related academic department?</p>
      [/#if]
    </div>
  </div> 
  
  <hr />
  <div class="row">
    <label class="col-md-9" for="">[@s.text name="project.deliverable.dissemination.acknowledgeQuestion" /]</label>
    <div class="col-md-3">[@customForm.yesNoInput name="deliverable.publication.publicationAcknowledge"  editable=editable inverse=false  cssClass="acknowledge text-center" /] </div> 
  </div>
  <hr />
  
  <div class="form-group">
    <label for="">[@s.text name="project.deliverable.dissemination.publicationContribution" /]</label>
    <div class="flagshipList simpleBox col-md-12">
      [#if deliverable.crps?has_content]
        [#list deliverable.crps as flagShips]
          [@deliverableMacros.flagshipMacro element=flagShips index=flagShips_index name="deliverable.crps"  isTemplate=false /]
        [/#list]
      [#else]
        <p class="emptyText text-center "> [@s.text name="project.deliverable.dissemination.Notflagships" /]</p> 
      [/#if]
    </div>
    [#if editable]
      <div class="row">
        <div class="col-md-5">
          [@customForm.select name="" label=""  i18nkey="project.deliverable.dissemination.selectCRP" listName="crps"   multiple=false required=false  className="crpSelect form-control input-sm " editable=editable/]
        </div>
        <div class="col-md-7">
          [@customForm.select name="" label=""  i18nkey="project.deliverable.dissemination.selectFlagships" listName="programs"   multiple=false required=false  className="flaghsipSelect form-control input-sm " editable=editable/]
        </div>
      </div>
    [/#if] 
  </div>
</div>

[/#macro]


[#macro complianceCheck ]
<h4 class="headTitle">[@s.text name="project.deliverable.quality.compliance" /]</h4>

<div class="simpleBox" >
  <div class="row">
    <div class="col-md-10">
      <p class="note">[@s.text name = "project.deliverable.quality.info" /]</p>
    </div>
    <div class="col-md-2">
      <div id="box-out">
        <div id="box-inside">
          <div id="red" [#if action.goldDataValue(deliverableID)>14 && action.goldDataValue(deliverableID)<=74]class="highlightRed"[/#if]></div>
          <div id="yellow" [#if action.goldDataValue(deliverableID)>74 && action.goldDataValue(deliverableID)<=104]class="highlightYellow"[/#if]></div>
          <div id="green" [#if action.goldDataValue(deliverableID)>104]class="highlightGreen"[/#if]></div>
        </div>
      </div>
    </div>
  </div>

  <input type="hidden" name="deliverable.qualityCheck.id" value="${(deliverable.qualityCheck.id)!"-1"}">
  [#-- Question1 --]
  <div class="question ">
    <h5>[@s.text name="project.deliverable.quality.question1" /]</h5>
    <hr />
    
    <div class="col-md-4">
    [#list answers as answer]
      <div class="radio">
        [#if editable]
        <label><input type="radio" class="qualityAssurance" name="deliverable.qualityCheck.qualityAssurance.id" value="${(answer.id)!}" [#if deliverable.qualityCheck?? && deliverable.qualityCheck.qualityAssurance?? && deliverable.qualityCheck.qualityAssurance.id==answer.id] checked="checked"[/#if]>${(answer.name)!}</label>
        [#else]
        <p [#if deliverable.qualityCheck?? && deliverable.qualityCheck.qualityAssurance?? && deliverable.qualityCheck.qualityAssurance.id==answer.id] class="checked"[#else]class="noChecked"[/#if]>${(answer.name)!} </p>
        [/#if]
      </div>
    [/#list]
    </div>
    
    [#-- FILE --]
    <div class="col-md-8 fileOptions" style="display:[#if deliverable.qualityCheck?? && deliverable.qualityCheck.qualityAssurance?? && deliverable.qualityCheck.qualityAssurance.id==2] block [#else] none[/#if]">
      <div class="col-md-6 form-group  fileAssuranceContent">
        <label>[@customForm.text name="project.deliverable.proofSubmission" readText=!editable /]:</label>
        [#assign hasFile = deliverable.qualityCheck?? && deliverable.qualityCheck.fileAssurance?? && deliverable.qualityCheck.fileAssurance.id?? /]
        <input class="fileID" type="hidden" name="deliverable.qualityCheck.fileAssurance.id" value="${(deliverable.qualityCheck.fileAssurance.id)!"-1"}" />
        [#-- Input File --]
        [#if editable]
        <div class="fileUpload" style="display:${hasFile?string('none','block')}"> <input class="uploadFileAssurance upload" type="file" name="file" data-url="${baseUrl}/deliverableUploadFile.do"></div>
        [/#if]
        [#-- Uploaded File --]
        <p class="fileUploaded textMessage checked" style="display:${hasFile?string('block','none')}">
          <span class="contentResult" title="${(deliverable.qualityCheck.fileAssurance.fileName)!}">[#if deliverable.qualityCheck?? && deliverable.qualityCheck.fileAssurance?? && deliverable.qualityCheck.fileAssurance??][@utils.wordCutter string=((deliverable.qualityCheck.fileAssurance.fileName)!"") maxPos=20 substr=" "/][/#if]</span> 
          [#if editable]<span class="removeIcon"> </span> [/#if]
        </p>
      </div>
      <span class="col-md-1"> <br /> or</span>
      <div class="col-md-5">
      <br />
      [@customForm.input name="deliverable.qualityCheck.linkAssurance" value="${(deliverable.qualityCheck.linkAssurance)!}" className="urlLink" i18nkey="" showTitle=false placeholder="Please give us the link" required=true  editable=editable /]
      </div>
    </div>
    <div class="clearfix"></div>
  </div>
  [#-- Question2 --]
  <div class="question ">
    <h5>[@s.text name="project.deliverable.quality.question2" /]</h5>
    <hr />
    
    <div class="col-md-4">
    [#list answers as answer]
      <div class="radio">
      [#if editable]
        <label><input type="radio" class="dataDictionary" name="deliverable.qualityCheck.dataDictionary.id" value="${(answer.id)!}" [#if deliverable.qualityCheck?? && deliverable.qualityCheck.dataDictionary?? && deliverable.qualityCheck.dataDictionary.id==answer.id] checked="checked"[/#if]>${(answer.name)!}</label>
      [#else]
        <p [#if deliverable.qualityCheck?? && deliverable.qualityCheck.dataDictionary?? && deliverable.qualityCheck.dataDictionary.id==answer.id] class="checked"[#else]class="noChecked"[/#if]>${(answer.name)!} </p>
      [/#if]
      </div>
    [/#list]
    </div>
    
    [#-- FILE --]
    <div class="col-md-8 fileOptions" style="display:[#if deliverable.qualityCheck?? && deliverable.qualityCheck.dataDictionary?? && deliverable.qualityCheck.dataDictionary.id==2] block[#else]none[/#if]">
      <div class="col-md-6 form-group fileDictionaryContent">
        <label>[@customForm.text name="project.deliverable.proofSubmission" readText=!editable /]:</label>
         [#assign hasFile = deliverable.qualityCheck?? && deliverable.qualityCheck.fileDictionary?? && deliverable.qualityCheck.fileDictionary.id?? /]
        <input class="fileID" type="hidden" name="deliverable.qualityCheck.fileDictionary.id" value="${(deliverable.qualityCheck.fileDictionary.id)!"-1"}" />
        [#-- Input File --]
        [#if editable]
        <div class="fileUpload" style="display:${hasFile?string('none','block')}"> <input class="uploadFileDictionary upload" type="file" name="file" data-url="${baseUrl}/deliverableUploadFile.do"></div>
        [/#if]
        [#-- Uploaded File --]
        <p class="fileUploaded textMessage checked" style="display:${hasFile?string('block','none')}">
          <span class="contentResult" title="${(deliverable.qualityCheck.fileDictionary.fileName)!}">[#if deliverable.qualityCheck?? && deliverable.qualityCheck.fileDictionary?? && deliverable.qualityCheck.fileDictionary??][@utils.wordCutter string=((deliverable.qualityCheck.fileDictionary.fileName)!"") maxPos=20 substr=" "/] [/#if]</span> 
          [#if editable]<span class="removeIcon"> </span> [/#if]
        </p>
      </div>
      <span class="col-md-1"> <br /> or</span>
      <div class="col-md-5">
      <br />
      [@customForm.input name="deliverable.qualityCheck.linkDictionary" value="${(deliverable.qualityCheck.linkDictionary)!}" className="urlLink" i18nkey="" showTitle=false placeholder="Please give us the link" required=true  editable=editable /]
      </div>
    </div>
    <div class="clearfix"></div>
  </div>
  [#-- Question3 --]
  <div class="question ">
    <h5>[@s.text name="project.deliverable.quality.question3" /]</h5>
    <hr />
    
    <div class="col-md-4">
    [#list answers as answer]
      <div class="radio">
      [#if editable]
        <label><input type="radio" class="dataTools" name="deliverable.qualityCheck.dataTools.id" value="${(answer.id)!}" [#if deliverable.qualityCheck?? && deliverable.qualityCheck.dataTools?? && deliverable.qualityCheck.dataTools.id==answer.id] checked="checked"[/#if]>${(answer.name)!}</label>
      [#else]
        <p [#if deliverable.qualityCheck?? && deliverable.qualityCheck.dataTools?? && deliverable.qualityCheck.dataTools.id==answer.id] class="checked"[#else]class="noChecked"[/#if]>${(answer.name)!} </p>
      [/#if]
      </div>
    [/#list]
    </div>
    
    [#-- FILE --]
    <div class="col-md-8 fileOptions" style="display:[#if deliverable.qualityCheck?? && deliverable.qualityCheck.dataTools?? && deliverable.qualityCheck.dataTools.id==2]block[#else]none[/#if]">
      <div class="col-md-6 form-group fileToolsContent">
        <label>[@customForm.text name="project.deliverable.proofSubmission" readText=!editable /]:</label>
        [#assign hasFile = deliverable.qualityCheck?? && deliverable.qualityCheck.fileTools?? && deliverable.qualityCheck.fileTools.id?? /]
        <input class="fileID" type="hidden" name="deliverable.qualityCheck.fileTools.id" value="${(deliverable.qualityCheck.fileTools.id)!"-1"}" />
        [#-- Input File --]
        [#if editable]
        <div class="fileUpload" style="display:${hasFile?string('none','block')}"> <input class="uploadFileTools upload" type="file" name="file" data-url="${baseUrl}/deliverableUploadFile.do"></div>
        [/#if]
        [#-- Uploaded File --]
        <p class="fileUploaded textMessage checked" style="display:${hasFile?string('block','none')}">
          <span class="contentResult" title="${(deliverable.qualityCheck.fileTools.fileName)!}">[#if deliverable.qualityCheck?? && deliverable.qualityCheck.fileTools?? && deliverable.qualityCheck.fileTools??][@utils.wordCutter string=((deliverable.qualityCheck.fileTools.fileName)!"") maxPos=20 substr=" "/][/#if]</span> 
          [#if editable]<span class="removeIcon"> </span> [/#if]
        </p>
      </div>
      <span class="col-md-1"> <br /> or</span>
      <div class="col-md-5">
      <br />
      [@customForm.input name="deliverable.qualityCheck.linkTools" value="${(deliverable.qualityCheck.linkTools)!}" className="urlLink" i18nkey="" showTitle=false placeholder="Please give us the link" required=true  editable=editable /]
      </div>
    </div>
    <div class="clearfix"></div>
  </div>
</div>
[/#macro]

[#macro fairCompliant]
<h4 class="headTitle">[@s.text name="project.deliverable.quality.fairTitle" /]</h4>
  
<div class="simpleBox" > 
  [#-- Findable --] 
  <div class="fairCompliant findable [#if action.isF(deliverable.id)??][#if action.isF(deliverable.id)]achieved[#else]not-achieved[/#if][/#if]">
    <div class="row">
      <div class="col-md-2">
        <div class="sign">F</div>
      </div>
      <div class="col-md-10">
        <strong>[@s.text name="project.deliverable.quality.FLabel" /]</strong>
        <p>[@s.text name="project.deliverable.quality.Fpoint1" /]</p>
        <p>[@s.text name="project.deliverable.quality.Fpoint2" /]</p>
        <p>[@s.text name="project.deliverable.quality.Fpoint3" /]</p>
      </div>
    </div>
  </div>
  
  [#-- Accessible --] 
  <div class="fairCompliant accessible [#if action.isA(deliverable.id)??][#if action.isA(deliverable.id)]achieved[#else]not-achieved[/#if][/#if]">
    <div class="row">
      <div class="col-md-2">
        <div class="sign">A</div>
      </div>
      <div class="col-md-10">
        <strong>[@s.text name="project.deliverable.quality.ALabel" /]</strong>
        <p>[@s.text name="project.deliverable.quality.Apoint1" /]</p>
        <p>[@s.text name="project.deliverable.quality.Apoint2" /]</p>
        <p>[@s.text name="project.deliverable.quality.Apoint3" /]</p>
      </div>
    </div>
  </div>
  
  [#-- Interoperable --] 
  <div class="fairCompliant interoperable [#if action.isI(deliverable.id)??][#if action.isI(deliverable.id)]achieved[#else]not-achieved[/#if][/#if]">
    <div class="row">
      <div class="col-md-2">
        <div class="sign">I</div>
      </div>
      <div class="col-md-10">
        <strong>[@s.text name="project.deliverable.quality.ILabel" /]</strong>
        <p>[@s.text name="project.deliverable.quality.Ipoint1" /]</p>
        <p>[@s.text name="project.deliverable.quality.Ipoint2" /]</p>
        <p>[@s.text name="project.deliverable.quality.Ipoint3" /]</p>
      </div>
    </div>
  </div>
  
  [#-- Reusable --] 
  <div class="fairCompliant reusable [#if action.isR(deliverable.id)??][#if action.isR(deliverable.id)]achieved[#else]not-achieved[/#if][/#if]">
    <div class="row">
      <div class="col-md-2">
        <div class="sign">R</div>
      </div>
      <div class="col-md-10">
        <strong>[@s.text name="project.deliverable.quality.RLabel" /]</strong>
        <p>[@s.text name="project.deliverable.quality.Ipoint1" /]</p>
        <p>[@s.text name="project.deliverable.quality.Ipoint2" /]</p>
      </div>
    </div>
  </div>
</div>
[/#macro]

[#-- Metadata Macro --]
[#macro metadataField title="" encodedName="" type="input" list="" require=false]
  [#local metadataID = (deliverable.getMetadataID(encodedName))!-1 /]
  [#local metadataIndex = (deliverable.getMetadataIndex(encodedName))!-1 /]
  [#local ID = (deliverable.getID(metadataID))!'' /]
  [#local metadataValue = (deliverable.getMetadataValue(metadataID))!'' /]
  [#local customName = 'deliverable' /]
  <input type="hidden" name="${customName}.metadataElements[${metadataIndex}].id" value="${ID}" />
  <input type="hidden" name="${customName}.metadataElements[${metadataIndex}].metadataElement.id" value="${metadataID}" />
  [#if type == "input"]
    [@customForm.input name="${customName}.metadataElements[${metadataIndex}].elementValue" required=require value="${metadataValue}" className="${title}Metadata"  type="text" i18nkey="metadata.${title}" help="metadata.${title}.help" editable=editable/]
  [#elseif type == "textArea"]
    [@customForm.textArea name="${customName}.metadataElements[${metadataIndex}].elementValue" required=require value="${metadataValue}" className="${title}Metadata" i18nkey="metadata.${title}" help="metadata.${title}.help" editable=editable/]
  [#elseif type == "select"]
    [@customForm.select name="${customName}.metadataElements[${metadataIndex}].elementValue" required=require value="${metadataValue}" className="${title}Metadata" i18nkey="metadata.${title}" listName=list  editable=editable /]
  [/#if]
[/#macro]


[#macro authorMacro element index name  isTemplate=false]
  [#assign customName = "${name}[${index}]" /]
  <div id="author-${isTemplate?string('template',(element.id)!)}" class="author  simpleBox col-md-4"  style="display:${isTemplate?string('none','block')}">
    [#if editable] [#--&& (isTemplate) --]
      <div class="removeLink">
        <div class="removeAuthor removeIcon" title="Remove author/creator"></div>
      </div>
    [/#if]
    <div class="row">
      <div class="col-md-12"><span class="lastName">${(element.lastName)!} </span> <span class="firstName">${(element.firstName)!} </span></div>
    </div>
    <span><small class="orcidId">[#if element.elementId?has_content][#else]<b>orcid id:</b>[/#if] ${(element.elementId)!'not filled'}</small></span>
    <input type="hidden" name="${customName}.id" class="id" value="${(element.id)!}" />
    <input type="hidden" name="${customName}.lastName"  class="lastNameInput" value="${(element.lastName)!}" />
    <input type="hidden" name="${customName}.firstName"  class="firstNameInput" value="${(element.firstName)!}" />
    <input type="hidden"name="${customName}.elementId"   class="orcidIdInput" value="${(element.elementId)!}" />
    <div class="clearfix"></div>
  </div>
[/#macro]

[#macro flagshipMacro element index name  isTemplate=false]
  [#assign customName = "${name}[${index}]" /]
  <div id="flagship-${isTemplate?string('template',(projectActivity.id)!)}" class="flagships  borderBox col-md-6"  style="display:${isTemplate?string('none','block')}">
    [#if editable]<div class="removeFlagship removeIcon" title="Remove flagship"></div>[/#if] 
    <input class="idElemento" type="hidden" name="${customName}.id" value="${(element.id)!-1}" />
    <input class="idCrp" type="hidden" name="${customName}.crpPandr.id" value="${(element.crpPandr.id)!}" />
    <input class="idFlagship" type="hidden" name="${customName}.ipProgram.id" value="${(element.ipProgram.id)!}" />
    <span class="name">${(element.crpPandr.name)!}-${(element.ipProgram.acronym)!}</span>
    <div class="clearfix"></div>
  </div>
[/#macro]


[#function checkDeliverableTypes]
  [#if (deliverable.deliverableType.deliverableType.id==49)!false]
    [#return "block"]
  [#else]
    [#return "none"]
  [/#if]
  [#return "none"]
[/#function]
