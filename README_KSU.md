K-State Attendance
==========

Environment Setup
------------
This application is using the LEAP environment so you can set up a VM to develop on using the [ome_lti](https://github.com/kstateome/ome_lti) cookbook. After vagrant finishes, you can drop an EAR file into /home/vagrant/deploy/ and start up wildfly.

External resources
------------
These are external resources that this application needs to be able to talk to over the network.

### Data sources
This application shares the same database schema as the rest of our LTI applications. The connection is defined in the `ome_jboss_datasources` data bag in `ome_chef_data`. The relevant files are `lti-*.json`

### Web Services
- Canvas API: Canvas runs in Amazon and is accessed via web services on port 443 (HTTPS)

Accessing the application
------------
Once the application is deployed, you can access the LTI configuration page on your dev VM via:  
[https://localhost:10443/attendance/](https://localhost:10443/attendance/) (port subject to change if you are running multiple VMs)

In order to properly test the application functionality you will need to install the LTI application in Canvas. Typically we work in the test instnace. For reference, all the Canvas instances are:
- Beta: https://k-state.beta.instructure.com
- Test: https://k-state.test.instructure.com
- Production: https://k-state.instructure.com

It is necessary to install the application as an LTI integration into Canvas. The following URL's are needed during the installation process
- Dev: https://localhost:10443/attendance
- LTI Test 1: https://lti.test.canvas.k-state.edu/attendance
- LTI Test 2: https://lti.test2.canvas.k-state.edu/attendance
- LTI Test 3: https://lti.test3.canvas.k-state.edu/attendance
- Production: https://lti.canvas.k-state.edu/attendance
