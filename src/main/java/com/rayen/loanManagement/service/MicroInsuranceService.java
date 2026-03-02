package com.rayen.loanManagement.service;

import com.rayen.loanManagement.entity.Loan;
import com.rayen.loanManagement.entity.MicroInsurance;
import com.rayen.loanManagement.model.MicroInsuranceResponse;
import com.rayen.loanManagement.repository.CreditScoreRepository;
import com.rayen.loanManagement.repository.LoanRepository;
import com.rayen.loanManagement.repository.MicroInsuranceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MicroInsuranceService implements IMicroInsuranceService {

    private static final String APPROVED = "APPROVED";
    private static final String ACTIVE = "ACTIVE";
    private static final String PENDING = "PENDING";
    private static final String CANCELLED = "CANCELLED";

    private final MicroInsuranceRepository microInsuranceRepository;
    private final LoanRepository loanRepo;
    private final CreditScoreRepository creditScoreRepo;
    private final ICreditScoreService creditScoreService;

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

    @Override
    @Transactional
    public MicroInsuranceResponse subscribe(Long loanId, String type, Long userId) {
        Loan loan = loanRepo.findById(loanId)
            .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        List<MicroInsurance> activeOfType = microInsuranceRepository
            .findByLoan_LoanIdAndStatus(loanId, ACTIVE).stream()
            .filter(i -> type != null && type.equalsIgnoreCase(i.getType()))
            .toList();
        if (!activeOfType.isEmpty()) {
            throw new IllegalArgumentException("Insurance type already active for this loan");
        }

        double amount = loan.getAmount();
        double coverage;
        double premium;
        switch (type != null ? type.toUpperCase() : "") {
            case "LIFE" -> {
                coverage = amount * 1.5;
                premium = amount * 0.02;
            }
            case "DISABILITY" -> {
                coverage = amount * 1.0;
                premium = amount * 0.015;
            }
            case "UNEMPLOYMENT" -> {
                coverage = amount * 0.8;
                premium = amount * 0.01;
            }
            default -> throw new IllegalArgumentException("Unknown insurance type: " + type);
        }

        String status = APPROVED.equals(loan.getStatus()) ? ACTIVE : PENDING;
        LocalDate startDate = loan.getStartDate() != null ? loan.getStartDate() : LocalDate.now();
        int months = loan.getDurationMonths() != null ? loan.getDurationMonths() : 12;
        LocalDate endDate = startDate.plusMonths(months);

        MicroInsurance ins = new MicroInsurance();
        ins.setLoan(loan);
        ins.setUserId(userId);
        ins.setType(type);
        ins.setCoverageAmount(coverage);
        ins.setPremium(premium);
        ins.setStatus(status);
        ins.setStartDate(startDate);
        ins.setEndDate(endDate);
        ins = microInsuranceRepository.save(ins);

        if (ACTIVE.equals(status)) {
            creditScoreService.evaluateCreditScore(userId);
        }

        return toResponse(ins);
    }

    @Override
    @Transactional
    public MicroInsuranceResponse cancelInsurance(Long insuranceId) {
        MicroInsurance ins = microInsuranceRepository.findById(insuranceId)
            .orElseThrow(() -> new IllegalArgumentException("Insurance not found"));
        ins.setStatus(CANCELLED);
        microInsuranceRepository.save(ins);
        if (ins.getUserId() != null) {
            creditScoreService.evaluateCreditScore(ins.getUserId());
        }
        return toResponse(ins);
    }

    @Override
    public List<MicroInsuranceResponse> getByLoan(Long loanId) {
        return microInsuranceRepository.findByLoan_LoanId(loanId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    public List<MicroInsuranceResponse> getByUser(Long userId) {
        return microInsuranceRepository.findByUserId(userId).stream()
            .map(this::toResponse)
            .toList();
    }

    private MicroInsuranceResponse toResponse(MicroInsurance ins) {
        double loanAmount = ins.getLoan() != null ? ins.getLoan().getAmount() : 0.0;
        String protectionLevel = MicroInsuranceResponse.computeProtectionLevel(
            ins.getCoverageAmount(), loanAmount);
        Long loanId = ins.getLoan() != null ? ins.getLoan().getLoanId() : null;
        return new MicroInsuranceResponse(
            ins.getInsuranceId(),
            loanId,
            ins.getUserId(),
            ins.getType(),
            ins.getCoverageAmount(),
            ins.getPremium(),
            ins.getStatus(),
            ins.getStartDate(),
            ins.getEndDate(),
            protectionLevel
        );
    }
}
