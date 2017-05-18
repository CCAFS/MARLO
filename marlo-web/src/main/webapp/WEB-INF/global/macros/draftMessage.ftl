[#ftl] 
[#if editable && draft]
  <div class="note">
    [@s.text name="message.confirmChanges"][@s.param]<span class="label label-success">save</span>[/@s.param][/@s.text]
  </div>
[/#if]