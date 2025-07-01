package com.quantumsoft.hrms.serviceimpl;

import com.quantumsoft.hrms.entity.Benefit;
import com.quantumsoft.hrms.repository.BenefitRepository;
import com.quantumsoft.hrms.servicei.BenefitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BenefitServiceImpl implements BenefitService {
    private final BenefitRepository repository;

    @Override
    public Benefit createBenefit(Benefit benefit) {
        benefit.setCreatedAt(LocalDateTime.now());
        return repository.save(benefit);
    }

    @Override
    public List<Benefit> getAllBenefits() {
        return repository.findAll();
    }
}