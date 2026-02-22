package com.rayen.loanManagement.service;

import com.rayen.loanManagement.entity.MicroInsurance;

import java.util.List;

public interface IMicroInsuranceService {

    List<MicroInsurance> getAllMicroInsurance();
    MicroInsurance getMicroInsuranceById(Long idInsurance);
    MicroInsurance addMicroInsurance(MicroInsurance microInsurance);
    void removeMicroInsurance(Long idInsurance);
    MicroInsurance modifyMicroInsurance(MicroInsurance microInsurance);
}
