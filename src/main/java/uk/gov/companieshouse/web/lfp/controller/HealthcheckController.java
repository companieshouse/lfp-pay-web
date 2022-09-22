package uk.gov.companieshouse.web.lfp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Healthcheck controller returns a 200 response if the service is running.
 * <p>The ResponseEntity accepts a wildcard and thus a more verbose healthcheck can be written
 * to query the status of mongo/kafka/disk space etc. The results can be marshalled into
 * an object and passed to the ResponseEntity constructor along with the HttpStatus.
 * <br>This will ultimately act as a more lightweight spring actuator.
 */
@Controller
public class HealthcheckController {

    @RequestMapping(value = "/late-filing-penalty/healthcheck", method = RequestMethod.GET)
    public ResponseEntity<?> performHealthCheck() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
