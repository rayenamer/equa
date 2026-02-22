package com.rayen.loanManagement.service;

import com.rayen.loanManagement.entity.MicroInsurance;
import com.rayen.loanManagement.repository.MicroInsuranceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MicroInsuranceService implements IMicroInsuranceService {

    private final MicroInsuranceRepository microInsuranceRepository;

    public MicroInsuranceService(MicroInsuranceRepository microInsuranceRepository) {
        this.microInsuranceRepository = microInsuranceRepository;
    }

    @Override
    public List<MicroInsurance> getAllMicroInsurance() {
        return microInsuranceRepository.findAll();
    }

    @Override
    public MicroInsurance getMicroInsuranceById(Long idInsurance) {
        return microInsuranceRepository.findById(idInsurance).orElse(null);
    }

    @Override
    public MicroInsurance addMicroInsurance(MicroInsurance microInsurance) {
        return microInsuranceRepository.save(microInsurance);
    }

    @Override
    public void removeMicroInsurance(Long idInsurance) {
        microInsuranceRepository.deleteById(idInsurance);
    }

    @Override
    public MicroInsurance modifyMicroInsurance(MicroInsurance microInsurance) {
        return microInsuranceRepository.save(microInsurance);
    }
}
