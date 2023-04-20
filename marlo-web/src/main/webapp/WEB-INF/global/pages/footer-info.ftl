[#ftl]
<div class="row">
  [#-- Contact Information --]
  <div class="col-md-4 infoLinks text-left">
    <strong>Contact</strong><br />
    <ul>
      <li><a href="mailto:MARLOSupport@cgiar.org">MARLOSupport@cgiar.org</a></li>
    </ul>
  </div>
  [#-- Legal Information--]
  <div class="col-md-4 infoLinks text-left">
    <strong>Legal</strong><br />
    <ul>
      <li><a target="_blank" href="[@s.url namespace="/" action='legalInformation'][/@s.url]#privacyNotice">Privacy Notice </a></li>
      <li><a target="_blank" href="[@s.url namespace="/" action='legalInformation'][/@s.url]#termsConditions">Terms and Conditions</a></li>
    </ul>
  </div>
  [#-- Glossary --]
  <div class="col-md-4 infoLinks text-left">
    <strong>Others</strong><br />
    <ul>
      <li><a target="_blank" href="[@s.url namespace="/" action='glossary'][/@s.url]">Glossary of relevant items </a></li>
      [#if action.isAiccra()]
       <!-- <li><a data-toggle="modal" data-target="#modal1">Reporting Schedule </a></li>-->

          <!--Modal: Name-->
        <div class="modal fade" id="modal1" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
          <div class="modal-dialog modal-lg" role="document">

            <!--Content-->
            <div class="modal-content">

              <!--Body-->
              <div class="modal-body mb-0 p-0">

                <div class="embed-responsive embed-responsive-16by9 z-depth-1-half">
                  <img class="img-fluid z-depth-1" src="${baseUrlCdn}/global/images/aiccra-planning-2021.png" alt="video"
                      width="100%">
                </div>

              </div>

              <!--Footer-->
              <div class="modal-footer justify-content-center">
                <button type="button" class="btn btn-outline-primary btn-rounded btn-md ml-4" data-dismiss="modal">Close</button>
              </div>
            </div>
            <!--/.Content-->
          </div>
        </div>
        <!--Modal: Name-->

        <!--<li><a data-toggle="modal" data-target="#modal2">AICCRA Roadmap</a></li>-->

          <!--Modal: Name-->
        <div class="modal fade" id="modal2" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
          <div class="modal-dialog modal-lg" role="document">

            <!--Content-->
            <div class="modal-content">

              <!--Body-->
              <div class="modal-body mb-0 p-0">

                <div class="embed-responsive embed-responsive-16by9 z-depth-1-full">
                  <img class="embed-responsive-item" src="${baseUrlCdn}/global/images/aiccra-roadmap-2021.png" alt="video"
                      width="100%">
                </div>

              </div>

              <!--Footer-->
              <div class="modal-footer justify-content-center">
                <button type="button" class="btn btn-outline-primary btn-rounded btn-md ml-4" data-dismiss="modal">Close</button>
              </div>
            </div>
            <!--/.Content-->
          </div>
        </div>
        <!--Modal: Name-->

      [/#if]
    </ul>
  </div>
</div>

[#-- Copyright --]
<div class="copyRight">
  <hr />
  <span> Copyright &#169; 2013-2019 International Center for Tropical Agriculture (CIAT)</span><br />
  <span style="opacity:0.5;"> Some rights reserved</span>
</div>

[#-- MARLO Blog --]
<div class="newsButton">
  <a href="https://www.notion.so/cgiar-prms/AICCRA-Knowledge-and-Data-Sharing-team-achievements-3a83ae1ef2804fa8b9220bdba2f5d1f7?pvs=4" target="__blank">
     <b> [@s.text name="footer.checkBlog" /] </b> <span class="fa fa-external-link-square"></span>
  </a>
</div>