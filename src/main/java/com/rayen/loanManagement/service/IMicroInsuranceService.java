package com.rayen.loanManagement.service;

import com.rayen.loanManagement.entity.MicroInsurance;
import com.rayen.loanManagement.model.MicroInsuranceResponse;

import java.util.List;

public interface IMicroInsuranceService {

    List<MicroInsurance> getAllMicroInsurance();
    MicroInsurance getMicroInsuranceById(Long idInsurance);
    MicroInsurance addMicroInsurance(MicroInsurance microInsurance);
    void removeMicroInsurance(Long idInsurance);
    MicroInsurance modifyMicroInsurance(MicroInsurance microInsurance);

    MicroInsuranceResponse subscribe(Long loanId, String type, Long userId);
    MicroInsuranceResponse cancelInsurance(Long insuranceId);
    List<MicroInsuranceResponse> getByLoan(Long loanId);
    List<MicroInsuranceResponse> getByUser(Long userId);
}
