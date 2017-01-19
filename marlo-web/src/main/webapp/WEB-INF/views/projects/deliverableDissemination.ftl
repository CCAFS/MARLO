[#ftl]
[#-- ACCESSIBLE --]
<div class="borderBox form-group">
  <div class=" row ">
    <label class="col-md-9" for="">[@s.text name="Is this deliverable Open Access?" /]</label>
    <div class="col-md-3">
      [@customForm.yesNoInput name="accessible"  editable=true inverse=false cssClass="accessible text-center" /]  
    </div>  
  </div>
<div class="clearfix"></div>

  <div class="col-md-9 openAccessOptions" style="display: block;">
    <hr />
   <label for="">Select the Open Access restriction:</label>
    <div class="radio">
      <label><input type="radio" name="optradio">Intellectual Property Rights (confidential information)</label>
    </div>
    <div class="radio">
      <label><input type="radio" name="optradio">Limited Exclusivity Agreements</label>
    </div>
    <div class="radio">
      <label><input type="radio" name="optradio">Restricted Use Agreement - Restricted access (if so, what are these periods?)</label>
    </div>
    <div class="radio">
      <label><input type="radio" name="optradio">Effective Date Restriction - embargoed periods (if so, what are these periods?)</label>
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
      [@customForm.yesNoInput name="findable"  editable=true inverse=false cssClass="findable text-center" /] 
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
    [@customForm.input name="deliverableMetadataDate" i18nkey="Publication date" className="startDate" type="text" disabled=!editable  required=true editable=editable /]
  </div>
  <div class="col-md-6">
    [@customForm.input name="" i18nkey="Language" className="language" type="text" disabled=!editable  required=true editable=editable /]
  </div>
  <div class="col-md-6">
    [@customForm.select name="" label=""  i18nkey="Country" listName="" keyFieldName=""  displayFieldName=""  multiple=false required=true  className=" form-control input-sm " editable=editable/]
  </div>
  <div class="col-md-6">
    [@customForm.input name="" i18nkey="keywords" className="" type="text" disabled=!editable  required=true editable=editable /]
  </div>
    
  <div class="col-md-12">
    [@customForm.textArea value="" name="" i18nkey="citation" required=true className="citation" editable=editable /]
  </div>
  <div class="col-md-6">
    [@customForm.input name="" i18nkey="Handle" className="handle" type="text" disabled=!editable  required=false editable=editable /]
  </div>
  <div class="col-md-6">
    [@customForm.input name="" i18nkey="DOI" className="doi" type="text" disabled=!editable  required=false editable=editable /]
  </div>
</div>

<h3 class="headTitle">[@s.text name="Publication Metadata" /]</h3>

<div class="borderBox row">
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
      [@customForm.yesNoInput name="acknowledge"  editable=true inverse=false cssClass="acknowledge text-center" /] 
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
      [@customForm.yesNoInput name="license"  editable=true inverse=false cssClass="license text-center" /] 
    </div>  
  </div>
  
  [#-- Deliverable type computer software --]
  <div class=" licenseOptions" style="display:block;">
    <div class="col-md-12">
      <input type="radio" name="cycle" id="" value="MIT"/> MIT License
    </div>
    <div class="col-md-12">
      <input type="radio" name="cycle" id="" value="GNU"/> GNU General Public License
    </div>
    <div class="clearfix"></div>
  </div>
  
  [#-- Deliverable type data --]
  <div class=" licenseOptions" style="display:block;">
    <div class="col-md-12">
      <input type="radio" name="cycle" id="" value="No"/> CC licenses version 4.0

    </div>
    <div class="col-md-12">
      <input type="radio" name="cycle" id="" value="No"/> CC Public Domain Dedication (CC0 1.0)

    </div>
    <div class="col-md-12" style="display:none;">
      <input type="radio" name="cycle" id="" value="No"/> Open Data Commons (ODC)
    </div>
    <div class="clearfix"></div>
  </div>
  
  [#-- Deliverable type other research types --]
  <div class=" licenseOptions" style="display:block;">
    <div class="col-md-12">
      <input type="radio" name="cycle" id="" value="No"/> CC-BY (allow modifications and commercial use)
    </div>
    <div class="col-md-12">
      <input type="radio" name="cycle" id="" value="No"/> CC-BY-SA (allow modifications as long as other share alike and commercial use)
    </div>
    <div class="col-md-12">
      <input type="radio" name="cycle" id="" value="No"/> CC-BY-ND (allow commercial use but no modifications)
    </div>
    <div class="col-md-12">
      <input type="radio" name="cycle" id="" value="No"/> CC-BY-NC (allow modifications but no commercial use)
    </div>
    <div class="col-md-12">
      <input type="radio" name="cycle" id="" value="No"/> CC-BY-NC-SA (allow modifications as long as other share alike, but no commercial use)
    </div>
    <div class="col-md-12">
      <input type="radio" name="cycle" id="" value="No"/> CC-BY-NC-ND (don't allow modifications neither commercial use)
    </div>
    <div class="clearfix"></div>
  </div>
  <br />
  [#-- Other --]
  <div class="row">
  
    <div class="col-md-6 form-group">
      <div class="col-md-4">
        <input type="radio" name="cycle" id="" value="No"/> Other
      </div>
      <div class="col-md-8">
        [@customForm.input name="" showTitle=false className="" type="text" placeholder="Text here" disabled=!editable  required=true editable=editable /]
      </div>
    </div>
    <div class=" col-md-6">
      <label class="col-md-6" for="">[@s.text name="Does this license allow modifications?" /]</label>
      <div class="col-md-6">
        [@customForm.yesNoInput name="other"  editable=true inverse=false cssClass="other text-center" /] 
      </div>  
    </div>
    <div class="clearfix"></div>
  </div>
</div>