window.onload = function() {

      // Build a system
      const ui = SwaggerUIBundle({
          url: "https://localhost:8443/marlo-web/api/v2/api-docs",
          dom_id: '#swagger-ui',
          deepLinking: true,
          presets: [
              SwaggerUIBundle.presets.apis, SwaggerUIStandalonePreset
          ],
          plugins: [
            SwaggerUIBundle.plugins.DownloadUrl
          ],
          layout: "StandaloneLayout"
      })

      window.ui = ui
    }