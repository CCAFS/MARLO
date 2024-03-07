$(document).ready(function() {
    var isFullscreen = false;
    var iframe = null;
    var iframeInitialWidth = '100%';
    var iframeInitialHeight = '800px';
    var fullscreenButton = null;

    $.ajax({
        url: baseURL + '/tipGenerateUrlAction.do',
        type: 'GET',
        dataType: 'json',
        success: function(response) {
            
            var embeddedPageUrl = response.dinamicTipURL;
            console.log("embeddedPageUrl " + embeddedPageUrl);
            iframe = document.createElement('iframe');
            iframe.src = embeddedPageUrl;
            iframe.style.width = iframeInitialWidth;
            iframe.style.height = iframeInitialHeight;
            
            /****************/
            iframe.setAttribute('allow', 'autoplay');
            iframe.setAttribute('sandbox', 'allow-scripts allow-forms allow-same-origin allow-top-navigation');

            // Add iframe to the element with ID 'embeddedPage'
            document.getElementById('embeddedPage').appendChild(iframe);

            // Create and add fullscreen button
            fullscreenButton = document.createElement('button');
            fullscreenButton.classList.add('fullscreen-button');
            fullscreenButton.innerHTML = '<i class="fas fa-expand"></i> Fullscreen';

            fullscreenButton.addEventListener('click', toggleFullscreen);

            // Add button to the 'embeddedPage' container
            document.getElementById('embeddedPage').appendChild(fullscreenButton);
        },
        error: function(xhr, status, error) {
            console.error('Error getting embedded URL:', error);
        }
    });

    function toggleFullscreen() {
        var elem = document.getElementById('embeddedPage');

        if (!isFullscreen) {
            if (elem.requestFullscreen) {
                elem.requestFullscreen();
            } else if (elem.mozRequestFullScreen) { /* Firefox */
                elem.mozRequestFullScreen();
            } else if (elem.webkitRequestFullscreen) { /* Chrome, Safari and Opera */
                elem.webkitRequestFullscreen();
            } else if (elem.msRequestFullscreen) { /* IE/Edge */
                elem.msRequestFullscreen();
            }
        } else {
            if (document.exitFullscreen) {
                document.exitFullscreen();
            } else if (document.mozCancelFullScreen) { /* Firefox */
                document.mozCancelFullScreen();
            } else if (document.webkitExitFullscreen) { /* Chrome, Safari and Opera */
                document.webkitExitFullscreen();
            } else if (document.msExitFullscreen) { /* IE/Edge */
                document.msExitFullscreen();
            }
        }
    }

    // Detect fullscreen change event
    document.addEventListener('fullscreenchange', function() {
        isFullscreen = !!document.fullscreenElement;
        if (isFullscreen) {
            iframeInitialWidth = iframe.style.width; // Save initial width
            iframeInitialHeight = iframe.style.height; // Save initial height
            iframe.style.width = '100vw'; // Take full window width
            iframe.style.height = '100vh'; // Take full window height
            fullscreenButton.innerHTML = '<i class="fas fa-compress"></i> Exit Fullscreen';
        } else {
            iframe.style.width = iframeInitialWidth; // Revert to initial width
            iframe.style.height = iframeInitialHeight; // Revert to initial height
            fullscreenButton.innerHTML = '<i class="fas fa-expand"></i> Fullscreen';
        }
    });
});