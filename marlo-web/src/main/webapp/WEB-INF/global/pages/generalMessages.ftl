[#ftl]
[#-- Messages are going to show using notify plugin (see global.js) --]
<section id="generalMessages" class="container">  
  <ul class="messages" style="display: none;">
    <#--  [@s.iterator value="actionMessages"]<li id="message" class="success">[@s.property escape="false" /]</li>[/@s.iterator]  -->
    <#--  [@s.iterator value="actionErrors"]<li id="message" class="error">[@s.property escape="false" /]</li>[/@s.iterator]  -->
  </ul>
</section>  

<section id="generalMessages">  
  <ul class="messages displayNone">
    <div class="container  viewMore-bloc containerAlertMargin">
      <div class=" containerAlert alert-leftovers  globalContainerAlert ">
        <div class="containerLine  globalContainerLine"></div>
        <div class="containerIcon">
          <div class="containerIcon globalContainerIcon">
            <i class="material-icons"></i>      
          </div>
        </div>
        <div class="containerText col-md-12">
          <p class="alertText">
          </p>
        </div>
      </div>
    </div>
  </ul>
</section>

<section class="container">
  [#if !config.production && config.debug]
    <ul class="">
      [#if actionMessages?has_content]
      <li><strong>Debug Mode: Action Messages:</strong>
        <ul>
          [#list actionMessages as message]<li>${message}</li>[/#list]
        </ul>
      </li>
      [/#if]
      [#if actionErrors?has_content]
      <li><strong>Debug Mode: Error Messages:</strong>
        <ul>
          [#list actionErrors as message]<li>${message}</li>[/#list]
        </ul>
      </li>
      [/#if]
    </ul>
  [/#if]
</section>