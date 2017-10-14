[#ftl]
[#assign title = "Legal Information" /]
[#assign globalLibs = ["jquery", "noty"] /]
[#assign customJS = ["${baseUrl}/global/js/legalInformation.js"] /]
[#assign customCSS = ["${baseUrl}/global/css/legalInformation.css"] /]
[#assign currentSection = "home" /]
[#assign currentCycleSection = "legalInformation" /]
[#assign currentStage = "legalInformation" /]

[#include "/WEB-INF/${(headerPath)!'crp'}/pages/header.ftl" /]
[#include "/WEB-INF/${(headerPath)!'crp'}/pages/main-menu.ftl" /]

<section class="container contentForm">
  <div class="fullContent">
    <div class="col-md-offset-1 col-md-10">
     

      <h1 class="contentTitle">Legal Information</h1>
      [#--  
      <div class="btn-group pull-right" role="group" aria-label="...">
        <a class="btn btn-default" href="#privacyNotice">Privacy Notice</a>
        <a class="btn btn-default" href="#termsConditions">Terms and Conditions</a>
        <a class="btn btn-default" href="#copyRight" >Copyright</a>
      </div>
       --]
    
      <h2 id="privacyNotice">Privacy Notice</h2>
      <p>This privacy policy has been compiled to better serve those who are concerned with how their 'Personally Identifiable Information' (PII) is being used online. PII, as described in Colombian privacy protection law (Law 1581 of 2012), is any information linked or that can be linked to a specific or specifiable natural person or to a particular group of people. Please read our privacy policy carefully to get a clear understanding of how we collect, use, protect or otherwise handle your Personally Identifiable Information in accordance with our website.</p>
      <p>The International Center for Tropical Agriculture (CIAT) - the legal entity administering MARLO- is an international organization with headquarters in Colombia. As such, it accepts and complies with the personal data protection norms contained in Law 1581 of 2012 and Decree 1377 of 2013 by guaranteeing the protection of personal information provided by you and making every reasonable effort to handle collected information appropriately. Further, all information collected will be handled with care in accordance with CIAT’s standards for integrity, transparency and equity. </p>
  
      <div class="simpleBox">
        <div>
          <strong>What personal information do we collect from the people that visit our blog, website or app?</strong>
          <p>When registering or logging in on our site, as appropriate, you may be asked to enter your email address or other details, such as your name and institutional affiliation, ORCID number to help you with your experience. If you provide such information, you should do it voluntarily.</p>
          <p>Through your interactions with our site, we may collect, have access to, store and use your computer’s IP address, the URL/domain name of any referring website, the time and date of your visit to the site.</p>
        </div>
        <div>
          <strong>When do we collect information?</strong>
          <ul>
            <li>We collect information from you when you register on our site, fill out a form, Use Live Chat or enter information on our site.</li>
            <li>Provide us with feedback on our products or services </li>
          </ul>
        </div>
        <div>
          <strong>How do we use your information?</strong>
          <p>We may use the information we collect from you when you register, login in make a purchase, respond to a form, surf the website, or use certain other site features in the following ways:</p>
          <ul>
            <li>To personalize your experience and to allow us to deliver the type of content in which you are most interested.</li>
            <li>To help diagnose and solve issues with the site in order to improve it and better serve you.</li>
            <li>To follow up with you after correspondence (live chat or email inquiries), send information, respond to inquiries, and/or other requests or questions</li>
          </ul>
        </div>
        <div>
          <strong>How do we protect your information?</strong>
          <p>Your personal information is contained behind secured networks and is only accessible by a limited number of persons who have special access rights to such systems, and are required to keep the information confidential. In addition, all sensitive information you supply is encrypted via Secure Socket Layer (SSL) technology.</p>
          <p>We implement a variety of security measures when a user enters, submits, or accesses their information to maintain the safety of your personal information.</p>
          <p>All transactions are processed through a gateway provider and are not stored or processed on our servers.</p>
        </div>
        <div>
          <strong>Do we use 'cookies'?</strong>
          <p>Yes. Cookies are small files that a site or its service provider transfers to your computer's hard drive through your Web browser (if you allow) that enables the site's or service provider's systems to recognize your browser and capture and remember certain information. For instance, we use cookies to help us remember the place where you want to login. They are also used to help us understand your preferences based on previous or current site activity, which enables us to provide you with improved services. We also use cookies to help us compile aggregate data about site traffic and site interaction so that we can offer better site experiences and tools in the future.</p>
        </div>
        <div>
          <strong>We use cookies to:</strong>
          <ul>
            <li>Understand and save user's preferences for future visits.</li>
          </ul>
          <p>You can choose to have your computer warn you each time a cookie is being sent, or you can choose to turn off all cookies. You do this through your browser settings. Since browser is a little different, look at your browser's Help Menu to learn the correct way to modify your cookies.</p>
        </div>
        <div>
          <strong>If users disable cookies in their browser:</strong>
          <p>If you turn cookies off, some of the features that make your site experience more efficient may not function properly. </p>
        </div>
        <div>
          <strong>Third-party disclosure</strong>
          <p>We do not sell, trade, or otherwise transfer to outside parties your Personally Identifiable Information unless we provide users with advance notice and you give your consent to it. This does not include website hosting partners and other parties who assist us in operating our website, conducting our business, or serving our users, so long as those parties agree to keep this information confidential. </p>
          <p>We may also release information when its release is appropriate to comply with the law, enforce our site policies, or protect ours or others' rights, property or safety. In this line, CIAT reserves the right to disclose personal information in the case of a government authorities’ request, national or international, where it is necessary or appropriate to investigate illegal or fraudulent facts. To establish or defend the rights of CIAT against fraud, legal action or in compliance with law; or if CIAT deems it necessary to investigate or act against fraud or illegal reports on this website.</p>
          <p>However, non-personally identifiable visitor information may be provided to other parties for marketing, advertising, or other uses.</p>
        </div>
        <div>
          <strong>Third-party links</strong>
          <p>Occasionally, at our discretion, we may include or offer third-party services on our website. These third-party sites have separate and independent privacy policies. We therefore have no responsibility or liability for the content and activities of these linked sites. To avoid any doubt, we do not assume responsibility for the information you may provide to that third party or on/at that website, or for how that third party may use or disclose any information you may provide to them. Nonetheless, we seek to protect the integrity of our site and welcome any feedback about these sites.</p>
        </div>
        <div>
          <strong>Google</strong>
          <p>Google, as a third-party vendor, uses cookies to track statistics on our site. More information can be found in: https://www.google.com/intl/en/policies/privacy/</p>
        </div>
        <div>
          <strong>We have implemented the following:</strong>
          <ul>
            <li>Google Display Network Impression Reporting</li>
            <li>Demographics and Interests Reporting</li>
          </ul>
          <p>We, along with third-party vendors such as Google use first-party cookies (such as the Google Analytics cookies) and third-party cookies (such as the DoubleClick cookie) or other third-party identifiers together.</p>
          <p>We use them to compile data regarding user interactions.</p>
        </div>
        <div>
          <strong>Cybercrime Law</strong>
          <p>The Colombian Cybercrime Law (Law 1273 of 2009) sets out the rules for commercial email, establishes requirements for commercial messages, gives recipients the right to have emails stopped from being sent to them, and spells out tough penalties for violations. Acting in accordance with the mentioned Law, we agree to the following:</p>
          <ul>
            <li>As holder of the personal data you can, at any time: access your personal data to know the details of treatment of them, update them, rectify them in the event of being inaccurate, incorrect or outdated, and cancel or delete when it is considered not required for any of the purposes indicated in this privacy policy, or are being used for purposes not consented.</li>
            <li>If you need access to the personal data you provided or exercise any of the rights provided by law, send an application to MARLOSupport@cgiar.org , or to the physical address stated below. The MARLO team will attend your claim within fifteen (15) days from the day after the day of receipt. If during this period, we request from you more information to execute your wish and no response is received after one (1) month, the claim will be deemed withdrawn. The request for deletion of the information and the revocation of the authorization will not proceed when the holder has a legal or contractual duty to remain in the database.</li>
            <li>Except in countries or jurisdictions where it is not permitted by law, we may from time to time send you email communications regarding our products or services in response to your use of the site. By providing your email address, you consent to receive such communications. If at any time you would like to unsubscribe from receiving future emails, you can email us at MARLOSupport@cgiar.org and we will promptly remove you from ALL correspondence.</li>
          </ul>
        </div>
        <div>
          <strong>Conacting Us</strong>
          <p>If there are any questions regarding this privacy policy, you may contact us using the information below. </p>
          <span>International Center for Tropical Agriculture (CIAT)</span>
          <span>Km 17 Recta Cali-Palmira</span>
          <span>Palmira, Valle del Cauca 763537</span>
          <span>Colombia</span>
          <span>MARLOSupport@cgiar.org</span>
        </div>
        
        <i>Last Edited on 2017-10-12</i>
      </div>
      
      <h2 id="termsConditions">Terms and Conditions</h2>
      <div class="simpleBox">
        [#--  
        <div>
          <strong>1. Terms</strong>
          <p>By accessing the website at https://marlo.cgiar.org, you are agreeing to be bound by these terms of service, all applicable laws and regulations, and agree that you are responsible for compliance with any applicable local laws. If you do not agree with any of these terms, you are prohibited from using or accessing this site. The materials contained in this website are protected by applicable copyright and trademark law.</p>
        </div>
        <div>
          <strong>2. Use License</strong>
          <ul>
            <li>
              <p>a. Permission is granted to temporarily download one copy of the materials (information or software) on MARLO’s website for personal, non-commercial transitory viewing only. This is the grant of a license, not a transfer of title, and under this license you may not:</p>
              <ul>
                <li>Modify or copy the materials</li>
                <li>Use the materials for any commercial purpose, or for any public display (commercial or non-commercial)</li>
                <li>Attempt to decompile or reverse engineer any software contained on MARLO's website</li>
                <li>Remove any copyright or other proprietary notations from the materials</li>
                <li>Transfer the materials to another person or "mirror" the materials on any other server</li>
              </ul>
            </li>
            <li>
              <p>b. This license shall automatically terminate if you violate any of these restrictions and may be terminated by International Center for Tropical Agriculture (CIAT) at any time. Upon terminating your viewing of these materials or upon the termination of this license, you must destroy any downloaded materials in your possession whether in electronic or printed format.</p>
            </li>
          </ul>
        </div>
        <div>
          <strong>3. Disclaimer</strong>
          <ul>
            <li>The materials on MARLO's website are provided on an 'as is' basis. The International Center for Tropical Agriculture (CIAT) makes no warranties, expressed or implied, and hereby disclaims and negates all other warranties including, without limitation, implied warranties or conditions of merchantability, fitness for a particular purpose, or non-infringement of intellectual property or other violation of rights.</li>
            <li>b.  Further, the International Center for Tropical Agriculture (CIAT) does not warrant or make any representations concerning the accuracy, likely results, or reliability of the use of the materials on its website or otherwise relating to such materials or on any sites linked to this site.</li>
          </ul>
        </div>
        <div>
          <strong>4. Limitations</strong>
          <p>In no event shall the International Center for Tropical Agriculture (CIAT) or its suppliers be liable for any damages (including, without limitation, damages for loss of data or profit, or due to business interruption) arising out of the use or inability to use the materials on MARLO's website, even if the International Center for Tropical Agriculture (CIAT) authorized representative has been notified orally or in writing of the possibility of such damage. Because some jurisdictions do not allow limitations on implied warranties, or limitations of liability for consequential or incidental damages, these limitations may not apply to you.</p>
        </div>
        <div>
          <strong>5. Accuracy of materials</strong>
          <p>The materials appearing on MARLO’s website could include technical, typographical, or photographic errors. The International Center for Tropical Agriculture (CIAT) does not warrant that any of the materials on its website are accurate, complete or current. The MARLO team may make changes to the materials contained on its website at any time without notice. However the International Center for Tropical Agriculture (CIAT) does not make any commitment to update the materials.</p>
        </div>
        <div>
          <strong>6. Links</strong>
          <p>The International Center for Tropical Agriculture (CAIT) has not reviewed all of the sites linked to MARLO’s website and is not responsible for the contents of any such linked site. The inclusion of any link does not imply endorsement by the International Center for Tropical Agriculture (CIAT) of the site. Use of any such linked website is at the user's own risk.</p>
        </div>
        <div>
          <strong>7. Modifications</strong>
          <p>The International Center for Tropical Agriculture (CIAT) may revise these terms of service for its website at any time without notice. By using this website you are agreeing to be bound by the then current version of these terms of service.</p>
        </div>
        <div>
          <strong>8. Governing Law</strong>
          <p>These terms and conditions are governed by and construed in accordance with the laws of Palmira / Colombia and you irrevocably submit to the exclusive jurisdiction of the courts in that location.</p>
        </div>
        --]
        <p>Comming...</p>
      </div>
      
      <h2 id="copyRight">Copyright</h2>
      <div class="simpleBox">
        <a class="pull-right" href="https://www.gnu.org/licenses/gpl-3.0.en.html"><img class="" src="https://www.gnu.org/graphics/gplv3-127x51.png" alt="GPL" /></a>
        <p>GNUv3 license</p>
        <div class="clearfix"></div>
      </div>
      
      
      
    </div>
  </div>
</section>

[#include "/WEB-INF/${(headerPath)!'crp'}/pages/footer.ftl"]