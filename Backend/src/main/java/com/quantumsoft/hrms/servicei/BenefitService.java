package com.quantumsoft.hrms.servicei;

import com.quantumsoft.hrms.entity.Benefit;

import java.util.List;

public interface BenefitService {
    Benefit createBenefit(Benefit benefit);
    List<Benefit> getAllBenefits();
}
