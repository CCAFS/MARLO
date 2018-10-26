[#ftl]
<section id="generalMessages" class="container">
  [#-- Messages are going to show using notify plugin (see global.js) --]
  <ul class="messages" style="display: none;">
     [@s.iterator value="actionMessages"]<li id="message" class="success">[@s.property escape="false" /]</li>[/@s.iterator]
     [@s.iterator value="errorMessages"]<li id="message" class="error">[@s.property escape="false" /]</li>[/@s.iterator]
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
      [#if errorMessages?has_content]
      <li><strong>Debug Mode: Error Messages:</strong>
        <ul>
          [#list errorMessages as message]<li>${message}</li>[/#list]
        </ul>
      </li>
      [/#if]
    </ul>
  [/#if]
</section>