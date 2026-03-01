package com.rayen.loanManagement.controller;

import com.rayen.loanManagement.entity.MicroInsurance;
import com.rayen.loanManagement.model.MicroInsuranceResponse;
import com.rayen.loanManagement.service.IMicroInsuranceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/micro-insurance")
public class MicroInsuranceController {

    private final IMicroInsuranceService microInsuranceService;

    public MicroInsuranceController(IMicroInsuranceService microInsuranceService) {
        this.microInsuranceService = microInsuranceService;
    }

    @GetMapping("/retrieve-all-micro-insurances")
    public List<MicroInsurance> retrieveAllMicroInsurances() {
        return microInsuranceService.getAllMicroInsurance();
    }

    @GetMapping("/retrieve-micro-insurance/{insurance-id}")
    public MicroInsurance retrieveMicroInsurance(@PathVariable("insurance-id") Long insuranceId) {
        return microInsuranceService.getMicroInsuranceById(insuranceId);
    }

    @PostMapping("/add-micro-insurance")
    public MicroInsurance addMicroInsurance(@RequestBody MicroInsurance microInsurance) {
        return microInsuranceService.addMicroInsurance(microInsurance);
    }

    @DeleteMapping("/remove-micro-insurance/{insurance-id}")
    public void removeMicroInsurance(@PathVariable("insurance-id") Long insuranceId) {
        microInsuranceService.removeMicroInsurance(insuranceId);
    }

    @PutMapping("/modify-micro-insurance")
    public MicroInsurance modifyMicroInsurance(@RequestBody MicroInsurance microInsurance) {
        return microInsuranceService.modifyMicroInsurance(microInsurance);
    }

    @PostMapping("/subscribe/{loanId}")
    @ResponseStatus(HttpStatus.CREATED)
    public MicroInsuranceResponse subscribe(
            @PathVariable Long loanId,
            @RequestParam String type,
            @RequestParam Long userId) {
        return microInsuranceService.subscribe(loanId, type, userId);
    }

    @GetMapping("/loan/{loanId}")
    public List<MicroInsuranceResponse> getByLoan(@PathVariable Long loanId) {
        return microInsuranceService.getByLoan(loanId);
    }

    @GetMapping("/user/{userId}")
    public List<MicroInsuranceResponse> getByUser(@PathVariable Long userId) {
        return microInsuranceService.getByUser(userId);
    }

    @DeleteMapping("/cancel/{insuranceId}")
    public MicroInsuranceResponse cancelInsurance(@PathVariable Long insuranceId) {
        return microInsuranceService.cancelInsurance(insuranceId);
    }
}
