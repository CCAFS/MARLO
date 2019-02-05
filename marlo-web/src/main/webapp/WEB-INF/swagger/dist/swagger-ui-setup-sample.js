window.onload = function() {

  // Build a system
  const ui = SwaggerUIBundle({
    url : "https://localhost/marlo-web/api/v2/api-docs",
    dom_id : '#swagger-ui',
    deepLinking : true,
    presets : [ SwaggerUIBundle.presets.apis, SwaggerUIStandalonePreset ],
    plugins : [ SwaggerUIBundle.plugins.DownloadUrl ],
    layout : "StandaloneLayout",
    docExpansion: 'list', // ["list"*, "full", "none"]
    operationsSorter : (a, b) =>{
      var result = a.get("path").localeCompare(b.get("path"));

      if (result === 0) {
        result = a.get("path").localeCompare(b.get("path"));
      }
      return result;
    },
   })

  window.ui = ui
}