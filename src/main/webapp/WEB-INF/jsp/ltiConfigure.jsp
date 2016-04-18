<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head><title>Student Aviation Reporting LTI integration</title></head>
<body>
<h2>Student Aviation Reporting LTI integration</h2>
<p>In order to use the Aviation Reporting LTI plugin for Canvas, please follow these steps:</p>
<ol>
    <li>Navigate to the account settings</li>
    <li>Click on the "Apps" tab</li>
    <li>Click "View App Configurations"</li>
    <li>Click "Add New App"</li>
    <li>Enter "Aviation Reporting" in the Name field</li>
    <li>Obtain Consumer Key and Shared Secret from an administrator</li>
    <li>In the "Configuration Type" dropdown select: Paste XML</li>
    <li>Copy and paste XML configuration listed below</li>
    <li>Submit the form</li>
    <li>Navigate to your institution account page. There should now be an "Aviation Reporting" link in the account navigation bar on the left.</li>
</ol>

    <pre>
----------- Start copying the next line -----------
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;cartridge_basiclti_link xmlns="http://www.imsglobal.org/xsd/imslticc_v1p0"
    xmlns:blti = "http://www.imsglobal.org/xsd/imsbasiclti_v1p0"
    xmlns:lticm ="http://www.imsglobal.org/xsd/imslticm_v1p0"
    xmlns:lticp ="http://www.imsglobal.org/xsd/imslticp_v1p0"
    xmlns:xsi = "http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation = "http://www.imsglobal.org/xsd/imslticc_v1p0 http://www.imsglobal.org/xsd/lti/ltiv1p0/imslticc_v1p0.xsd
    http://www.imsglobal.org/xsd/imsbasiclti_v1p0 http://www.imsglobal.org/xsd/lti/ltiv1p0/imsbasiclti_v1p0.xsd
    http://www.imsglobal.org/xsd/imslticm_v1p0 http://www.imsglobal.org/xsd/lti/ltiv1p0/imslticm_v1p0.xsd
    http://www.imsglobal.org/xsd/imslticp_v1p0 http://www.imsglobal.org/xsd/lti/ltiv1p0/imslticp_v1p0.xsd"&gt;
    &lt;blti:title&gt;Aviation Reporting&lt;/blti:title&gt;
    &lt;blti:description&gt;Aviation Reporting&lt;/blti:description&gt;
    &lt;blti:launch_url&gt;${url}&lt;/blti:launch_url&gt;
    &lt;blti:extensions platform="canvas.instructure.com"&gt;
      &lt;lticm:property name="privacy_level"&gt;public&lt;/lticm:property&gt;
      &lt;lticm:options name="course_navigation"&gt;
        &lt;lticm:property name="enabled"&gt;true&lt;/lticm:property&gt;
        &lt;lticm:property name="visibility"&gt;admins&lt;/lticm:property&gt;
      &lt;/lticm:options&gt;
    &lt;/blti:extensions&gt;
&lt;/cartridge_basiclti_link&gt;
----------- Stop. Do not copy this line -----------
    </pre>

</body>
</html>