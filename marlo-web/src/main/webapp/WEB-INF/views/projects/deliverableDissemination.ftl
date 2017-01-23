[#ftl]
[#-- ACCESSIBLE --]
<div class="borderBox form-group">
  <div class=" row ">
    <label class="col-md-9" for="">[@s.text name="Is this deliverable Open Access?" /]</label>
    <div class="col-md-3">
      [@customForm.yesNoInput name="deliverable.dissemination.isOpenAccess"  editable=true inverse=false cssClass="accessible text-center" /]  
    </div>  
  </div>
<div class="clearfix"></div>

  <div class="col-md-9 openAccessOptions" style="display: block;">
    <hr />
   <label for="">Select the Open Access restriction:</label>
    <div class="radio">
      <label><input type="radio" name="deliverable.dissemination.type" value="intellectualProperty" [#if (deliverable.dissemination.type == "intellectualProperty")!false]checked="checked"[/#if]>Intellectual Property Rights (confidential information)</label>
    </div>
    <div class="radio">
      <label><input type="radio" name="deliverable.dissemination.type" value="limitedExclusivity" [#if (deliverable.dissemination.type == "limitedExclusivity")!false]checked="checked"[/#if]>Limited Exclusivity Agreements</label>
    </div>
    <div class="radio">
      <label><input type="radio" name="deliverable.dissemination.type" value="restrictedAccess" [#if (deliverable.dissemination.type == "restrictedAccess")!false]checked="checked"[/#if]>Restricted Use Agreement - Restricted access (if so, what are these periods?)</label>
    </div>
    <div class="radio">
      <label><input type="radio" name="deliverable.dissemination.type" value="embargoedPeriods"[#if (deliverable.dissemination.type == "embargoedPeriods")!false]checked="checked"[/#if] >Effective Date Restriction - embargoed periods (if so, what are these periods?)</label>
    </div>
    <div class="row restrictionDate-block" style="display:none;">
      <div class="col-md-5">
        [@customForm.input name="deliverable.dissemination.restrictedEmbargoedText" value="" type="text" i18nkey="text"  placeholder="" className="restrictionDate col-md-6" required=true editable=editable /]
      </div>
    </div>
  </div>
</div>

[#-- FINDABLE --]
<div class="borderBox form-group">
  <div class=" row">
    <span class="col-md-9">
      <label  for="">[@s.text name="Is this deliverable already disseminated? " /]</label>
      <br />
      <span style="font-size:0.9em;">Is the deliverable already uploaded onto a public repository?</span>
    </span>
    <div class="col-md-3">
      [@customForm.yesNoInput name="findable"  editable=true inverse=false value="true" cssClass="findable text-center" /] 
    </div>  
  </div>
  
  <div class="findableOptions" style="display:block;">
    <hr />
    <div class="col-md-12 note">[@s.text name = "The following list of dissemination channels are in accordance to the CGIAR Open Access Policy (i.e. adopt an Interoperability Protocol and Dublin Core Metadata Schema)." /]</div>
    <div class="row">
      <div class="col-md-4">
        <label for="disChannel" style="display:block;">Select a dissemination channel:<span class="red">*</span></label>
        <select name="" id="disChannel" class="disseminationChannel">
          <option value="-1">Select an option</option>
          <option value="2">CGSpace</option>
          <option value="3">Dataverse (Harvard)</option>
          <option value="1">Other</option>
        </select>
      </div>
      [#-- CGSpace examples & instructions --]
      <div class="exampleUrl-block channel-2 col-md-8" style="display:none;">
        <label for="">Example of URL:</label>
        <p><small>https://cgspace.cgiar.org/handle/10568/52163</small></p>
      </div>
      [#-- Dataverse examples & instructions --]
      <div class="exampleUrl-block channel-3 col-md-8" style="display:none;">
        <label for="">Example of URL:</label>
        <p><small>https://dataverse.harvard.edu/dataset.xhtml?persistentId=doi:10.7910/DVN/0ZEXKC</small></p>
      </div>
    </div>
    
    <div id="disseminationUrl" style="display:none;">
      [@customForm.input name="" value="" type="text" i18nkey="Dissemination URL"  placeholder="" className="deliverableDisseminationUrl" required=true editable=editable /]
      <div id="fillMetadata" class="checkButton" style="display:none;">Search & Fill Metadata</div>
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
  <div class="col-md-12">
    <label for="">Creator/Authors:</label>
    <div class="authorsList simpleBox">
      <p class="emptyText text-center "> [@s.text name="No Creator/Authors added yet." /]</p> 
    </div>
    <div class="addPerson text-right">
      <div class="button-green addAuthor"><span class="glyphicon glyphicon-plus-sign"></span>[@s.text name="Add other creator/author" /]</div>
    </div> 
  </div>
  <div class="col-md-6">
    [@metadataField title="date" encodedName="dc.date" type="input" require=false/]
  </div>
  <div class="col-md-6">
    [@metadataField title="language" encodedName="dc.language" type="input" require=true/]
  </div>
  <div class="col-md-6">
    [@metadataField title="country" encodedName="cg:coverage.country" type="input" require=false/]
  </div>
  <div class="col-md-6">
    [@customForm.input name="" i18nkey="keywords" className="" type="text" disabled=!editable  required=true editable=editable /]
  </div>
    
  <div class="col-md-12">
    [@metadataField title="citation" encodedName="dc.identifier.citation" type="textArea" require=false/]
  </div>
  <div class="col-md-6">
    [@customForm.input name="" i18nkey="Handle" className="handleMetadata" type="text" disabled=!editable  required=false editable=editable /]
  </div>
  <div class="col-md-6">
    [@customForm.input name="" i18nkey="DOI" className="doiMetadata" type="text" disabled=!editable  required=false editable=editable /]
  </div>
</div>

<h3 class="headTitle publicationMetadataBlock" style="display:none;">[@s.text name="Publication Metadata" /]</h3>

<div class="borderBox row publicationMetadataBlock" style="display:none;">
  <div class="row">
    <div class="col-md-4">
      [@customForm.input name="" i18nkey="Volume" className="" type="text" disabled=!editable  required=true editable=editable /]
    </div>
    <div class="col-md-4">
      [@customForm.input name="" i18nkey="Issue" className="" type="text" disabled=!editable  required=true editable=editable /]
    </div>
    <div class="col-md-4">
      [@customForm.input name="" i18nkey="Pages" className="" type="text" disabled=!editable  required=true editable=editable /]
    </div>
    <div class="col-md-12">
      [@customForm.input name="" i18nkey="Journal/Publisher name" className="" type="text" disabled=!editable  required=true editable=editable /]
    </div>
  </div>
  <label for="">Indicators for journal articles:</label>
  <div class="col-md-12 form-group">
    <input type="checkbox" />Tick this box if this journal article is an ISI publication (check at http://ip-science.thomsonreuters.com/mjl/ for the list)  
  </div>
  <div class="col-md-12 form-group">  
    <input type="checkbox" />Does this article have a co-author from a developing country National Agricultural Research System (NARS) ?
  </div>
  <div class="col-md-12 form-group">
    <input type="checkbox" />Does this article have a co-author based in an Earth System Science-related academic department?
  </div>
  <div class="clearfix"></div>
  <br />
  <div class="row ">
    <label class="col-md-9" for="">[@s.text name="Does the publication acknowledge?" /]</label>
    <div class="col-md-3">
      [@customForm.yesNoInput name="acknowledge"  editable=true inverse=false value="true" cssClass="acknowledge text-center" /] 
    </div> 
  </div>
  
  
  <div class="row">
    <label class="col-md-12" for="">Is this publication contributing to any other flagships?</label>
  </div>
  <div class="clearfix"></div>
  <div class="row simpleBox">
    <div class="col-md-6">
      [@customForm.select name="" label=""  i18nkey="Select relevant CRPs" listName="" keyFieldName=""  displayFieldName=""  multiple=false required=true  className=" form-control input-sm " editable=editable/]
    </div>
    <div class="col-md-6">
      <input type="checkbox" /> Select relevant Flagship
    </div>
  </div>
</div>

<div class="borderBox">
  <div class=" row">
    <label class="col-md-9" for="">[@s.text name="Have you adopted a license?" /]</label>
    <div class="col-md-3">
      [@customForm.yesNoInput name="license"  editable=true inverse=false value="true" cssClass="license text-center" /] 
    </div>  
  </div>
  <hr />
  [#-- Deliverable type computer software --]
  <div class=" licenseOptions computerLicense" style="display:none;">
    <div class="col-md-12">
      <input type="radio" name="deliverable.license" id="" value="1"/> MIT License
    </div>
    <div class="col-md-12">
      <input type="radio" name="deliverable.license" id="" value="2"/> GNU General Public License
    </div>
    <div class="clearfix"></div>
  </div>
  
  [#-- Deliverable type data --]
  <div class=" licenseOptions dataLicense" style="display:none;">
    <div class="col-md-12">
      <input type="radio" name="deliverable.license" id="" value="3"/> CC licenses version 4.0

    </div>
    <div class="col-md-12">
      <input type="radio" name="deliverable.license" id="" value="4"/> CC Public Domain Dedication (CC0 1.0)

    </div>
    <div class="col-md-12" style="display:none;">
      <input type="radio" name="deliverable.license" id="" value="5"/> Open Data Commons (ODC)
    </div>
    <div class="clearfix"></div>
  </div>
  
  [#-- Deliverable type other research types --]
  <div class=" licenseOptions" style="display:block;">
    <div class="col-md-12">
      <input type="radio" name="deliverable.license" id="" value="6"/> CC-BY (allow modifications and commercial use)
    </div>
    <div class="col-md-12">
      <input type="radio" name="deliverable.license" id="" value="7"/> CC-BY-SA (allow modifications as long as other share alike and commercial use)
    </div>
    <div class="col-md-12">
      <input type="radio" name="deliverable.license" id="" value="8"/> CC-BY-ND (allow commercial use but no modifications)
    </div>
    <div class="col-md-12">
      <input type="radio" name="deliverable.license" id="" value="9"/> CC-BY-NC (allow modifications but no commercial use)
    </div>
    <div class="col-md-12">
      <input type="radio" name="deliverable.license" id="" value="10"/> CC-BY-NC-SA (allow modifications as long as other share alike, but no commercial use)
    </div>
    <div class="col-md-12">
      <input type="radio" name="deliverable.license" id="" value="11"/> CC-BY-NC-ND (don't allow modifications neither commercial use)
    </div>
    <div class="clearfix"></div>
  </div>
  <br />
  [#-- Other --]
  <div class="row">
  
    <div class="col-md-6 form-group">
      <div class="col-md-4">
        <input type="radio" name="deliverable.license" id="" value="12"/> Other
      </div>
      <div class="col-md-8">
        [@customForm.input name="" showTitle=false className="" type="text" placeholder="Text here" disabled=!editable  required=true editable=editable /]
      </div>
    </div>
    <div class=" col-md-6 licence-modifications" style="display:none;">
      <label class="col-md-6" for="">Does this license allow modifications?</label>
      <div class="col-md-6">
        [@customForm.yesNoInput name="licenceModifications"  editable=true inverse=false value="true" cssClass="licenceModifications text-center" /] 
      </div>  
    </div>
    <div class="clearfix"></div>
  </div>
</div>