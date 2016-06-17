Aviation Reporting
==========
This is an LTI application that assists instructors in keeping attendance records for aviation related courses. The FAA has some very strict attendance requirements for some classes. Contact time between instructors and students must be tracked down to the minute with some make-up time allowed, also tracked in minutes. This application provides a convenient place in Canvas to track contact time.

Technologies Used
------------
- Spring MVC 4
- Maven 3.1.1
- Java 8
- Arquillian 1.11.11


Environment Setup
------------
This application is using the LEAP environment so you can set up a VM to develop on using the [ome_lti](https://github.com/kstateome/ome_lti) cookbook. After vagrant finishes, you can drop an EAR file into /home/vagrant/deploy/ and start up wildfly.

External resources
------------
These are external resources that this application needs to be able to talk to over the network.

### Data sources
This application shares the same database schema as the rest of our LTI applications. The connection is defined in the `ome_jboss_datasources` data bag in [ome_chef_data](https://github.com/kstateome/ome_chef_data/tree/master/data_bags/ome_jboss_datasources). The relevant files are `lti-*.json`

### Web Services
- Canvas API: Canvas runs in Amazon and is accessed via web services on port 443 (HTTPS)

Accessing the application
------------
Once the application is deployed, you can access the LTI configuration page on your dev VM via:  
[https://localhost:10443/aviationReporting/](https://localhost:10443/aviationReporting/) (port subject to change if you are running multiple VMs)

In order to properly test the application functionality you will need to install the LTI application in Canvas. Typically we work in the test instnace. For reference, all the Canvas instances are:
- Beta: https://k-state.beta.instructure.com
- Test: https://k-state.test.instructure.com
- Production: https://k-state.instructure.com

It is necessary to install the application as an LTI integration into Canvas. The following URL's are needed during the installation process
- Dev: https://localhost:10443/aviationReporting
- LTI Test 1: https://lti.test.canvas.k-state.edu/aviationReporting
- LTI Test 2: https://lti.test2.canvas.k-state.edu/aviationReporting
- LTI Test 3: https://lti.test3.canvas.k-state.edu/aviationReporting
- Production: https://lti.canvas.k-state.edu/aviationReporting
