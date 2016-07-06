[#ftl]
<section id="generalMessages" class="container">
  [#-- Messages are going to show using notify plugin (see global.js) --]
  <ul class="messages" style="display: none;">
  [@s.iterator value="actionMessages"]    
    <li class="success">[@s.property escape="false" /]</li>    
  [/@s.iterator]
  [@s.iterator value="errorMessages"]    
    <li class="error">[@s.property escape="false" /]</li>    
  [/@s.iterator]
  </ul>
</section>