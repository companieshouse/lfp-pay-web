# Companies House Late Filing Penalties Web Service
The Companies House Web Service for handling payments of LFPs. This application is written using the [Spring Boot](http://projects.spring.io/spring-boot/) Java framework.

- Retrieves Payable LFPs from the LFP-PAY-API service
- Displays payable LFPs and redirects to the Payments Service to take payment.

### Requirements
In order to run this Web App locally you will need to install:

- [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven](https://maven.apache.org/download.cgi)
- [Git](https://git-scm.com/downloads)
- [Payments API](https://github.com/companieshouse/payments.api.ch.gov.uk)
- [LFP API](https://github.com/companieshouse/lfp-pay-api)

### Getting Started

1. [Configure your service](#configuration) if you want to override any of the defaults.
1. Run `make`
1. Run `./start.sh`


### Configuration

Key                | Description
-------------------|------------------------------------
`LFP_PAY_WEB_PORT` |The port of the LFP-PAY-WEB service
`HUMAN_LOG`        |For human readable logs


### Web Pages

Page                                     | Address
-----------------------------------------|-----------------------------
Temporary page replacing GovUk start     | `/late-filing-penalty`
Tell us your penalty details             | `/late-filing-penalty/enter-details`
