package com.example.droolsSpring;

import com.pnmac.pam.data.model.v3.loan.Loan;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public class HelloController {

    @RequestMapping("/hello")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @PostMapping(value = "/process", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Loan> runProcess(@RequestBody Loan loan) {
        String groupID = "com.myspace";
        String artifactId = "PNMACTestProject";
        String version = "1.0.5-SNAPSHOT";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("loanMain", loan);

        Loan myLoan = new Loan();
        myLoan.setUnderwritingEscrowIndicator(loan.getUnderwritingEscrowIndicator());

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(groupID, artifactId, version);
        KieContainer kContainer = ks.newKieContainer(releaseId);
        KieSession kSession = kContainer.newKieSession();
        KieRuntime kRuntime = (KieRuntime) kSession;
        ProcessInstance processInstance = kRuntime.startProcess("PNMACTestProject.TestProcess", params);
        System.out.println("Completed");
        System.out.println(loan.getUnderwritingEscrowIndicator());
        System.out.println(loan.getLoanIssues().getIssues().contains("12345678-90ab-cdef-ghij-klmnopqrstuv"));

        return new ResponseEntity<Loan>(loan, HttpStatus.OK);
    }
}
