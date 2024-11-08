# Companies House Late Filing Penalties Web Service
The Companies House Web Service for handling payments of penalties. This application is written using the [Spring Boot](http://projects.spring.io/spring-boot/) Java framework.

- Retrieves Payable penalties from the PPS-PAY-API service
- Displays payable penalties and redirects to the Payments Service to take payment.

### Requirements
In order to run this Web App locally you will need to install:

- [Java 21](https://www.oracle.com/java/technologies/downloads/?er=221886#java21)
- [Maven](https://maven.apache.org/download.cgi)
- [Git](https://git-scm.com/downloads)
- [Payments API](https://github.com/companieshouse/payments.api.ch.gov.uk)
- [PPS API](https://github.com/companieshouse/pps-pay-api)

### Getting Started

1. [Configure your service](#configuration) if you want to override any of the defaults.
1. Run `make`
1. Run `./start.sh`


### Configuration

Key                | Description
-------------------|------------------------------------
`PPS_PAY_WEB_PORT` |The port of the PPS-PAY-WEB service
`HUMAN_LOG`        |For human readable logs
`CH_BANK_ACC_NUM`  |Companies House Bank account number (for penalty start page)
`CH_BANK_SORT_CODE`|Companies House Bank sort code (for penalty start page)
`CH_BANK_ACC_NAME` |Companies House Bank Bank account name (for penalty start page)


### Web Pages

Page                                     | Address
-----------------------------------------|-----------------------------
Start page for Penalty Service           | `/late-filing-penalty`
Tell us your penalty details             | `/late-filing-penalty/enter-details`
