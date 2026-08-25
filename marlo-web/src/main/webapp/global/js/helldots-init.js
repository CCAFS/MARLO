/**
 * HellDots adapter for MARLO. Mounted only for authenticated users outside production; see footer.ftl.
 */
(function () {
  "use strict";

  var config = document.getElementById("helldots-config");
  if (!config || !window.HellDots) {
    return;
  }

  var api = config.dataset.baseUrl.replace(/\/+$/, "") + "/api/helldots";

  function request(method, url, body) {
    return fetch(url, {
      method: method,
      credentials: "same-origin",
      headers: body ? { "Content-Type": "application/json" } : {},
      body: body ? JSON.stringify(body) : undefined
    }).then(function (response) {
      if (!response.ok) {
        throw new Error(method + " " + url + " -> " + response.status);
      }
      return response.status === 204 ? null : response.json();
    });
  }

  var overlay = window.HellDots.createCommentOverlay({
    user: { name: config.dataset.userName, id: config.dataset.userId },
    locale: "en",

    transformScreenshot: function (dataUrl, info) {
      return fetch(dataUrl)
        .then(function (response) { return response.blob(); })
        .then(function (blob) {
          var form = new FormData();
          form.append("file", blob);
          form.append("kind", info.kind);
          if (info.commentId) {
            form.append("commentId", info.commentId);
          }
          return fetch(api + "/screenshots", {
            method: "POST",
            credentials: "same-origin",
            body: form
          });
        })
        .then(function (response) {
          if (!response.ok) {
            throw new Error("upload failed: " + response.status);
          }
          return response.json();
        })
        .then(function (result) { return result.url; });
    },

    onReady: function (instance) {
      request("GET", api + "/comments?page=" + encodeURIComponent(window.location.pathname))
        .then(function (comments) {
          instance.loadComments(comments || []);
        })
        .catch(function (error) {
          console.warn("[helldots] could not load comments", error);
        });
    },

    onCommentRequested: function (id) {
      return request("GET", api + "/comments/" + encodeURIComponent(id)).then(function (comment) {
        if (comment) {
          overlay.loadComments([comment]);
        }
      });
    },

    onChange: function (event) {
      // Our own writes are echoed back as origin "host"; forwarding them would loop forever.
      if (event.origin === "host") {
        return;
      }
      request("POST", api + "/events", event).catch(function (error) {
        console.warn("[helldots] could not persist event", event.type, error);
      });
    },

    onError: function (error, context) {
      console.warn("[helldots]", context, error);
    }
  });

  // Exposed so AJAX sections can re-anchor after rebuilding their DOM.
  window.marloHelldots = overlay;
})();
