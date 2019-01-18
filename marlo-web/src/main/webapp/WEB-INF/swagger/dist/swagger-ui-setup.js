window.onload = function() {

	// Build a system
	const ui = SwaggerUIBundle({
		url : "https://ciatco00579l.cgiarad.org:8443/marlo-web/api/v2/api-docs",
		dom_id : '#swagger-ui',
		deepLinking : true,
		presets : [ SwaggerUIBundle.presets.apis, SwaggerUIStandalonePreset ],
		plugins : [ SwaggerUIBundle.plugins.DownloadUrl ],
		layout : "StandaloneLayout",
	    docExpansion: 'list', // ["list"*, "full", "none"]
		tagsSorter : (a, b) =>{
			if (a.substring(0, 1)== "_"){
				result = 1
			}
			else{ 
				if (b.substring(0, 1)== "_"){
				result = -1
				}
				else {
				result = a.localeCompare(b);
				}
			}
			return result;
		},
		operationsSorter : (a, b) =>{
			// var operationOrder = [""];
			// var result = operationOrder.indexOf(a.get("path") ) -
			// operationOrder.indexOf( b.get("path") );
			// Or if you want to sort the methods alphabetically (delete,
			// get, head,options,
			var result = a.get("path").localeCompare(b.get("path"));

			if (result === 0) {
				result = a.get("path").localeCompare(b.get("path"));
			}

			return result;
		},
	/*
	 * (a, b) => { // var methodsOrder = ["get", "post", "put", "delete",
	 * "patch", // "options", "trace"]; var operationOrder =
	 * ["/innovation-types", "/cross-cutting-marker-scores",
	 * "/2-maturity-of-changes", "/cross-cutting-marker-scores/{id}",
	 * "/1-research-partnerships"]; var result = operationOrder.indexOf(
	 * a.get("path") ) - operationOrder.indexOf( b.get("path") ); // Or if you
	 * want to sort the methods alphabetically (delete, // get, head, options,
	 * ...): // var result = a.get("method").localeCompare(b.get("method"));
	 * 
	 * if (result === 0) { result = a.get("path").localeCompare(b.get("path")); }
	 * 
	 * return result; }
	 */
	})

	window.ui = ui
}