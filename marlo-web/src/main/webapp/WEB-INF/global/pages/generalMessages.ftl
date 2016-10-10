[#ftl]
<section id="generalMessages" class="container">
  [#-- Messages are going to show using notify plugin (see global.js) --]
  <ul class="" style="display: none;">
    [#if actionMessages?has_content]
      [#list actionMessages as message]
        <li class="message">${message}</li>
      [/#list]
    [/#if]
  </ul>
</section>