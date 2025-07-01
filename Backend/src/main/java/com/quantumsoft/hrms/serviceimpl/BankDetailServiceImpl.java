package com.quantumsoft.hrms.serviceimpl;

import com.quantumsoft.hrms.entity.BankDetail;

import com.quantumsoft.hrms.repository.BankDetailRepository;

import com.quantumsoft.hrms.servicei.BankDetailService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;

@Service

@RequiredArgsConstructor

public class BankDetailServiceImpl implements BankDetailService {

    @Autowired

    private BankDetailRepository bankDetailRepository;

    @Override

    public BankDetail createBankDetail(BankDetail bankDetail) {

        return bankDetailRepository.save(bankDetail);

    }

    @Override

    public BankDetail getBankDetailById(Long id) {

        return bankDetailRepository.findById(id)

                .orElseThrow(() -> new RuntimeException("Bank detail not found"));

    }

    @Override

    public List<BankDetail> getAllBankDetails() {

        return bankDetailRepository.findAll();

    }

    @Override

    public BankDetail updateBankDetail(Long id, BankDetail updated) {

        BankDetail existing = bankDetailRepository.findById(id)

                .orElseThrow(() -> new RuntimeException("Bank detail not found"));

        existing.setAccountHolderName(updated.getAccountHolderName());

        existing.setBankName(updated.getBankName());

        existing.setAccountNumber(updated.getAccountNumber());

        existing.setIfscCode(updated.getIfscCode());

        existing.setBranch(updated.getBranch());

        existing.setAccountType(updated.getAccountType());

        return bankDetailRepository.save(existing);

    }

    @Override

    public void deleteBankDetail(Long id) {

        bankDetailRepository.deleteById(id);

    }
}