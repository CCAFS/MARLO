[#ftl]
[#-- ACCESSIBLE --]
<div class="borderBox form-group">
<input type="hidden"  name="deliverable.dissemination.id" value="${(deliverable.dissemination.id)!"-1"}" />
  <div class="row ">
    <label class="col-md-9" for="">[@s.text name="Is this deliverable Open Access?" /]</label>
    <div class="col-md-3">
      [@customForm.yesNoInput name="deliverable.dissemination.isOpenAccess"  editable=editable inverse=false cssClass="accessible text-center" /]  
    </div>
  </div>
  <div class="clearfix"></div>

  <div class="col-md-12 openAccessOptions radio-block" style="display: ${((deliverable.dissemination.isOpenAccess)!false)?string("none","block")};">
    <hr />
    [#if editable] 
   <label for="">Select the Open Access restriction:<span class="red">*</span></label>
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

[#-- FINDABLE --]
<div class="borderBox form-group">
  <div class=" row">
    <span class="col-md-9">
      <label  for="">[@s.text name="Is this deliverable already disseminated? " /]</label>
      <p><small>Is the deliverable already uploaded to a public repository?</small></p>
    </span>
    <div class="col-md-3">
      [@customForm.yesNoInput name="deliverable.dissemination.alreadyDisseminated"  editable=editable inverse=false cssClass="findable text-center" /] 
    </div>  
  </div>
  
  <div class="findableOptions" style="display:[#if (deliverable.dissemination.alreadyDisseminated)?? && (deliverable.dissemination.alreadyDisseminated)]block[#else]none [/#if]">
    <hr />
    <div class="col-md-12 note">[@s.text name = "The following list of dissemination channels are in accordance to the CGIAR Open Access Policy (i.e. adopt an Interoperability Protocol and Dublin Core Metadata Schema)." /]</div>
    <div class="row">
      <div class="col-md-4">
      [#if editable]
        [@customForm.select name="deliverable.dissemination.disseminationChannel" value="'${(deliverable.dissemination.disseminationChannel)!}'"  stringKey=true label=""  i18nkey="Select a dissemination channel" listName="channels" className="disseminationChannel"   multiple=false required=true   editable=editable/]
      [#else]
      <label for="disChannel" style="display:block;">Dissemination channel:</label>
      <p>Prefilled if available</p>
      [/#if]
      </div>
      [#-- CGSpace examples & instructions --]
      <div class="exampleUrl-block channel-cgspace col-md-8" style="display:[#if deliverable.dissemination.disseminationChannel?? && deliverable.dissemination.disseminationChannel=="cgspace"]block[#else]none[/#if];">
        <label for="">Example of URL:</label>
        <p><small>https://cgspace.cgiar.org/handle/10568/52163</small></p>
      </div>
      [#-- Dataverse examples & instructions --]
      <div class="exampleUrl-block channel-dataverse col-md-8" style="display:[#if deliverable.dissemination.disseminationChannel?? &&  deliverable.dissemination.disseminationChannel=="dataverse"]block[#else]none[/#if];">
        <label for="">Example of URL:</label>
        <p><small>https://dataverse.harvard.edu/dataset.xhtml?persistentId=doi:10.7910/DVN/0ZEXKC</small></p>
      </div>
    </div>
    
    <div id="disseminationUrl" style="display:[#if deliverable.dissemination.disseminationChannel?? && (deliverable.dissemination.disseminationChannel=="cgspace" || deliverable.dissemination.disseminationChannel=="dataverse")]block[#else]none[/#if];">
      [@customForm.input name="deliverable.dissemination.disseminationUrl" type="text" i18nkey="Dissemination URL"  placeholder="" className="deliverableDisseminationUrl" required=true editable=editable /]
      [#if editable]
      <div id="fillMetadata" class="checkButton" style="display:[#if deliverable.dissemination.disseminationChannel?? && (deliverable.dissemination.disseminationChannel=="cgspace" || deliverable.dissemination.disseminationChannel=="dataverse")]block[#else]none[/#if];">Search & Fill Metadata</div>
      [/#if]
      <div class="clearfix"></div>
    </div>
    <div id="metadata-output" class="col-md-12"></div>
    <div style="display:none;">
      [@customForm.input name="" value="" type="text" i18nkey="Deliverable URL"  placeholder="" className="" required=true editable=editable /]
    </div>
    <div class="clearfix"></div>
  </div>
</div>

[#-- METADATA --]
<h3 class="headTitle">[@s.text name="Deliverable Metadata" /]</h3>  

<div class="borderBox">
  <div class="form-group col-md-12">
    [@metadataField title="title" encodedName="dc.title" type="input" require=true/]
  </div>
  <div class="form-group col-md-12">
    [@metadataField title="description" encodedName="dc.description.abstract" type="textArea" require=false/]
  </div>
  <div class="col-md-6">
    [@metadataField title="date" encodedName="dc.date" type="input" require=false/]
  </div>
  <div class="col-md-6">
    [@metadataField title="language" encodedName="dc.language" type="input" require=false/]
  </div>
  <div class="col-md-6">
    [@metadataField title="country" encodedName="cg:coverage.country" type="input" require=false/]
  </div>
  <div class="col-md-6">
    [@metadataField title="keywords" encodedName="marlo.keywords" type="input" require=false/]
  </div>
    
  <div class="col-md-12">
    [@metadataField title="citation" encodedName="dc.identifier.citation" type="textArea" require=false/]
  </div>
  <div class="col-md-6">
    [@metadataField title="Handle" encodedName="marlo.handle" type="input" require=false/]
  </div>
  <div class="col-md-6">
    [@metadataField title="DOI" encodedName="marlo.doi" type="input" require=false/]
  </div>
  <div class="clearfix"></div>
  <hr />
  <div class="clearfix"></div>
  [#-- Creator/Authors --]
  <div class="col-md-12 form-group">
    [#if editable]
      <div class="col-md-12 note">[@s.text name = "To change an author's data, you must do double click on that field." /]</div>
    [/#if]
    <label for="">Creator/Authors: <span class="red">*</span></label>
    <div class="authorsList simpleBox col-md-12" >
      [#if deliverable.users?has_content]
        [#list deliverable.users as author]
          [@authorMacro element=author index=author_index name="deliverable.users"  /]
        [/#list]
      [#else]
        <p class="emptyText text-center "> [@s.text name="No Creator/Authors added yet." /]</p>
      [/#if]
    </div>
    [#if editable]
    <div class="col-md-12">
      <div class="col-md-3">
        <input class="form-control input-sm lName" placeholder="Last Name" type="text" /> 
      </div>
      <div class="col-md-3">
        <input class="form-control input-sm fName" placeholder="First Name" type="text" /> 
      </div>
      <div class="col-md-3">
        <input class="form-control input-sm oId" placeholder="Orcid Id" type="text" /> 
       </div>
       <div class="col-md-3">
         <div id="" class="addAuthor text-right">
          <div class="button-blue "><span class="glyphicon glyphicon-plus-sign"></span> [@s.text name="Add author" /]</div>
        </div>
       </div>
    </div>
    [/#if] 
  </div>
<div class="clearfix"></div>
<div class="publicationMetadataBlock" style="display:${checkDeliverableTypes()!};">
<h4 class="sectionSubTitle">[@s.text name="Publication Metadata"/]</h4>
 <div class="col-md-12">
  <div class="row form-group">
  <input type="hidden" name="deliverable.publication.id" value="${(deliverable.publication.id)!}"/>
    <div class="col-md-4 form-group">
      [@customForm.input name="deliverable.publication.volume" i18nkey="Volume" className="" type="text" disabled=!editable  required=true editable=editable /]
    </div>
    <div class="col-md-4 form-group">
      [@customForm.input name="deliverable.publication.issue" i18nkey="Issue" className="" type="text" disabled=!editable  required=false editable=editable /]
    </div>
    <div class="col-md-4 form-group">
      [@customForm.input name="deliverable.publication.pages" i18nkey="Pages" className="" type="text" disabled=!editable  required=false editable=editable /]
    </div>
    <div class="col-md-12">
      [@customForm.input name="deliverable.publication.journal" i18nkey="Journal/Publisher name" className="" type="text" disabled=!editable  required=true editable=editable /]
    </div>
  </div>
  <label for="">Indicators for journal articles:<span class="red">*</span></label>
  [#if editable]
    <div class="form-group">
     <input type="checkbox" name="deliverable.publication.isiPublication" value="true" [#if deliverable.publication?? && deliverable.publication.isiPublication?? && deliverable.publication.isiPublication]checked[/#if]/>Tick this box if this journal article is an ISI publication <small>(check at http://ip-science.thomsonreuters.com/mjl/ for the list)</small>  
    </div>
    <div class="form-group">  
     <input type="checkbox" name="deliverable.publication.nasr" value="true" [#if deliverable.publication?? && deliverable.publication.nasr?? && deliverable.publication.nasr]checked[/#if]/>Does this article have a co-author from a developing country National Agricultural Research System (NARS) ?
    </div>
    <div class="form-group">
     <input type="checkbox" name="deliverable.publication.coAuthor" value="true" [#if deliverable.publication?? && deliverable.publication.coAuthor?? && deliverable.publication.coAuthor]checked[/#if] />Does this article have a co-author based in an Earth System Science-related academic department?
    </div>
    [#else]
      <p [#if deliverable.publication?? && deliverable.publication.isiPublication?? && deliverable.publication.isiPublication]class="checked">[#else]class="noChecked ">[/#if]Tick this box if this journal article is an ISI publication (check at http://ip-science.thomsonreuters.com/mjl/ for the list)</p>
      <p [#if deliverable.publication?? && deliverable.publication.nasr?? && deliverable.publication.nasr]class="checked"[#else]class="noChecked"[/#if]>Does this article have a co-author from a developing country National Agricultural Research System (NARS) ?</p>
      <p [#if deliverable.publication?? && deliverable.publication.coAuthor?? && deliverable.publication.coAuthor]class="checked"[#else]class="noChecked"[/#if]>Does this article have a co-author based in an Earth System Science-related academic department?</p>
    [/#if]
  <div class="clearfix"></div>
  <br />
  <div class="row ">
    <label class="col-md-9" for="">[@s.text name="Does the publication acknowledge?" /]</label>
    <div class="col-md-3">
      [@customForm.yesNoInput name="acknowledge"  editable=editable inverse=false value="true" cssClass="acknowledge text-center" /] 
    </div> 
  </div>
  <hr />
  
  <div class="row">
    <label class="col-md-12" for="">Is this publication contributing to any other flagships?</label>
  </div>
  <div class="clearfix"></div>
  <div class="row">
    <div class="col-md-12">
      <div class="flagshipList simpleBox col-md-12">
        [#if deliverable.crps?has_content]
          [#list deliverable.crps as flagShips]
            [@flagshipMacro element=flagShips index=flagShips_index name="deliverable.crps"  isTemplate=false /]
          [/#list]
        [#else]
          <p class="emptyText text-center "> [@s.text name="No Flagships added yet." /]</p> 
        [/#if]
      </div>
      <div class="clearfix"></div>
    </div>
    [#if editable]
    <div class="col-md-5">
      [@customForm.select name="" label=""  i18nkey="Select to add a CRP" listName="crps"   multiple=false required=false  className="crpSelect form-control input-sm " editable=editable/]
    </div>
    <div class="col-md-7">
      [@customForm.select name="" label=""  i18nkey="Select to add a CCAFS Flaghsip" listName="programs"   multiple=false required=false  className="flaghsipSelect form-control input-sm " editable=editable/]
    </div>
    [/#if] 
  </div>
 </div>
</div>
</div>
[@deliverableLicense title="" encodedName="" type="input" list="" require=false/]
    <div class="clearfix"></div>
    </div>
  </div>
</div>