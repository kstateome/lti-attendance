
Note: The example commands install Wildfly in your home directory and startup Wildfly in standalone mode. In preparation of 
this installation, setup an Oracle database and a dedicated schema and user for use with this application.

1. Download Wildfly 8.2.0
    * You can see a list of available versions for download at this URL -- http://wildfly.org/downloads/
    ```
    cd ~/
    curl -O http://download.jboss.org/wildfly/8.2.0.Final/wildfly-8.2.0.Final.zip
    ```
  
2. Unpack Wildfly into an installation directory
    * ```unzip wildfly-8.2.0.Final.zip```
  
3. Download the 12.1.0.1.0 Oracle Driver
    * Navigate to http://www.oracle.com/technetwork/database/features/jdbc/jdbc-drivers-12c-download-1958347.html
    * Create an Oracle account if necessary
    * Accept the License Agreement
    * Download the ojdbc7.jar
  
4. Install the downloaded Oracle Driver within Wildfly
    * ```cp ~/Downloads/ojdbc7.jar ~/wildfly-8.2.0.Final/standalone/deployments/ojdbc7-12.1.0.1.0.jar```
  
5. Add the LtiDS datasource to the standalone configuration
    * ```vi ~/wildfly-8.2.0.Final/standalone/configuration/standalone.xml```
    * Add the following datasource entries within the datasources element. Be sure to change the username, password, and database hostname.

    ```
    <datasource jndi-name="java:/LtiDS" pool-name="lti" enabled="true" use-java-context="true" use-ccm="true">
    <connection-url>jdbc:oracle:thin:@//database-host-name.edu:1521/OMED.world</connection-url>
                    <driver-class>oracle.jdbc.driver.OracleDriver</driver-class>
                    <driver>ojdbc7-12.1.0.1.0.jar</driver>
                   <pool>
                       <min-pool-size>3</min-pool-size>
                       <max-pool-size>20</max-pool-size>
                   </pool>
                    <validation>
                        <background-validation>true</background-validation>
                        <check-valid-connection-sql>select * from dual</check-valid-connection-sql>
                        <background-validation-millis>60000</background-validation-millis>
                    </validation>
                    <timeout>
                        <idle-timeout-minutes>2</idle-timeout-minutes>
                    </timeout>
                    <security>
                        <user-name>db-owner</user-name>
                        <password>db-pass</password>
                    </security>
                </datasource>
    ```

6. Create the Wildfly Canvas module
    ```
      cd ~/wildfly-8.2.0.Final/modules/
      mkdir -p edu/ksu/canvas/main
    ```
   
7. Add module definition file
    ```
      cd ~/wildfly-8.2.0.Final/modules/edu/ksu/canvas/main/
      vi module.xml
    ```
    * Add the following content to the file 
   
    ```
    <?xml version="1.0" encoding="UTF-8"?>
    <module xmlns="urn:jboss:module:1.1" name="edu.ksu.canvas">
      <resources>
        <resource-root path="."/>
      </resources>
    </module>
    ```

8. Add application.properties file
    ```
      cd ~/wildfly-8.2.0.Final/modules/edu/ksu/canvas/main/
      vi application.properties
    ```
    * Add the following content. Change the canvas_domain match the URL of the Canvas instance.
  
    ```
    # Set spring profile to use real database. "dev" sets up an in-memory database for automated tests
    spring.profiles.active=prod
    #clustered=true
    canvas_domain=k-state.test.instructure.com
    ```

9. Generate Self-Signed SSL Certificate
    ```
      cd ~/
      keytool -genkey -alias attendance -keyalg RSA -keystore attendance.keystore -validity 10950
    ```
    * Answer the prompts as appropriate
    * Note: Of all of the questions asked this is the most important and should match the host name that will be entered into the web browser to connect to the admin console.
  
    ```
    Enter keystore password:  
    Re-enter new password: 
    What is your first and last name?
      [Unknown]:  localhost
    What is the name of your organizational unit?
      [Unknown]:  K-State
    What is the name of your organization?
      [Unknown]:  K-^Ckazg5:~ kaz$ keytool -genkey -alias attendance -keyalg RSA -keystore attendance.keystore -validity 10950
    Enter keystore password:  
    Re-enter new password: 
    What is your first and last name?
      [Unknown]:  Developer
    What is the name of your organizational unit?
      [Unknown]:  OME
    What is the name of your organization?
      [Unknown]:  K-State
    What is the name of your City or Locality?
      [Unknown]:  Manhattan
    What is the name of your State or Province?
      [Unknown]:  Kansas
    What is the two-letter country code for this unit?
      [Unknown]:  US
    Is CN=Developer, OU=OME, O=K-State, L=Manhattan, ST=Kansas, C=US correct?
      [no]:  yes

    Enter key password for <attendance>
    	(RETURN if same as keystore password):  
    Re-enter new password: 
    ```

10. Copy Self-Signed certificate to Wildfly configuration directory
    * ```cp attendance.keystore ~/wildfly-8.2.0.Final/standalone/configuration/```
  
11. Configure Wildfly for HTTPS/SSL
    * ```vi ~/wildfly-8.2.0.Final/standalone/configuration/standalone.xml```
  
    * In the undertow subsystem, add to the default-server element so that it has an https-listener element as follows

    ```
            <server name="default-server">
                <http-listener name="default" socket-binding="http"/>
                <https-listener name="default-https" socket-binding="https" security-realm="ApplicationRealm" />
                <host name="default-host" alias="localhost">
                    <location name="/" handler="welcome-content"/>
                    <filter-ref name="server-header"/>
                    <filter-ref name="x-powered-by-header"/>
                </host>
            </server>
    ```

    * In the security-realm element of "ApplicationRealm", add the following sever-identies element. Change the keystore-password and key-password to whatever use used when generating the self signed certificate. In this example, both are set to "attendance".
    ```
              <security-realm name="ApplicationRealm">
                <server-identities>
                    <ssl>
                        <keystore path="attendance.keystore" relative-to="jboss.server.config.dir" keystore-password="attendance" alias="attendance" key-password="attendance" />
                    </ssl>
                </server-identities>
                <authentication>
                    <local default-user="$local" allowed-users="*" skip-group-loading="true"/>
                    <properties path="application-users.properties" relative-to="jboss.server.config.dir"/>
                </authentication>
                <authorization>
                    <properties path="application-roles.properties" relative-to="jboss.server.config.dir"/>
                </authorization>
            </security-realm>
    ```
 
12. Copy LTI Attendance Artifact into the Wildfly Deploy directory
    ```
      curl -O https://artifactory.ome.ksu.edu/artifactory/ome-appdev-release/edu/ksu/ome/lti/lti-attendance/1.1.8/lti-attendance-1.1.8.war
      cp ~/lti-attendance-1.1.8.war ~/wildfly-8.2.0.Final/standalone/deployments/
    ```
13. Auto-generate the tables by temporarily starting up the application
    * Make sure that Java 8 is installed locally and being used by default
    ```
      cd ~/wildfly-8.2.0.Final/bin
      ./standalone.sh
    ```
    * Wait for the server to startup
    * Then stop the server by using Control-C
  
14. Setup the required Key and Secret pair values that are needed as for the Zero-Legged OAuth signing process
    * Insert a row into the LTI_KEY table
    * ```insert into lti_key (key_id, created_at, key_key, secret, consumer_profile, application_name) values (1, sysdate, 'the-key', 'the-secret', null, 'Attendance');```
  
15. Startup Standalone version of Wildfly
    * Make sure that Java 8 is installed locally and being used by default
    ```
      cd ~/wildfly-8.2.0.Final/bin
      ./standalone.sh
    ```
  
16. Install LTI Attendance Application in Canvas
    * Navigate to https://localhost:8443/attendance
    * Follow the instructions on the page that is returned
