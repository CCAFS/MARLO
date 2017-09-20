[#ftl]
<div class="fullBlock"> 
  [#-- Deliverable file List --]
  <h5>[@s.text name="projectDeliverable.dataSharing.deliverableFiles" /]</h5>
  <div id="filesUploaded">
    <ul>
     [#if deliverable.files?has_content]
      [#list deliverable.files as file]
       <li class="fileUploaded"> 
         <input class="fileID" name="deliverable.files[${file_index}].id" type="hidden" value="${(file.id)!}">
         <input class="fileHosted" name="deliverable.files[${file_index}].hosted" type="hidden" value="${(file.hosted)!}">
         <input class="fileName" name="deliverable.files[${file_index}].name" type="hidden" value="${(file.name)!}">
         <div class="fileName">${(file.name)!((file.link)!"Not defined")}</div>
         <div class="fileFormat">${(file.hosted)!'Untitled'}</div>
         <div class="fileSize">[#if file.size > 0]${(file.size/1024)?string("0.00")} KB[#else] <span title="Unknown size">- -</span> [/#if]</div>
         [#if editable]<img class="removeInput" src="${baseUrl}/global/images/icon-remove.png" alt="Remove"/>[/#if]
         <div class="clearfix"></div>
       </li>
      [/#list]
     </ul> 
     <p class="text" style="display:none">Use the following options to upload here the deliverable files or links</p>
     [#else]
     </ul>
     <p class="text">Use the following options to upload here the deliverable files or links</p>
     [/#if] 
  </div>
  
  [#if editable]
    [#-- Deliverable options to upload --]
    <h5>[@s.text name="projectDeliverable.dataSharing.chooseOptions" /]</h5> 
    <div id="dataSharingOptions">
      [#-- Option #1--]
      <div>
        <label for="option-1">
          <input id="option-1" type="radio" name="sharingOption" value="Externally" >
          [@s.text name="projectDeliverable.dataSharing.hostedInstitutional" /]
          <span class="quote">[@s.text name="projectDeliverable.dataSharing.hostedInstitutional.help" /]</span>
        </label> 
        <div id="fileURL" class="fullBlock uploadBlock" style="display:none">
          [@customForm.input name="linkExternally" type="text" i18nkey="projectDeliverable.filename" value="http://"/]
          <div id="addFileURL-external" class="addButton addFileURL">[@s.text name="projectDeliverable.dataSharing.addURL" /]</div>
        </div>
      </div>
      [#-- Option #2--]
      <div>
        <label for="option-2">
          <input id="option-2" type="radio" name="sharingOption" value="To download" >
          [@s.text name="projectDeliverable.dataSharing.fileGreater" /]
          <span class="quote">[@s.text name="projectDeliverable.dataSharing.fileGreater.help" /]</span>
        </label>
      </div>
      [#-- This is used for run a JQuery (dropzone) plugin to drag and drop deliverables files--]
      <div id="dragAndDrop" class="dropzone uploadBlock" style="display:none">
        <div class="fallback"> 
          <div id="addFileInput" class="addButton">[@s.text name="projectDeliverable.dataSharing.addFile" /]</div>
        </div>
      </div>
    </div>
  [/#if]
</div>

