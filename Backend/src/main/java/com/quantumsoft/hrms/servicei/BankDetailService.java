package com.quantumsoft.hrms.servicei;

import com.quantumsoft.hrms.entity.BankDetail;

import java.util.List;

public interface BankDetailService {

    BankDetail createBankDetail(BankDetail bankDetail);

    BankDetail getBankDetailById(Long id);

    List<BankDetail> getAllBankDetails();

    BankDetail updateBankDetail(Long id, BankDetail bankDetail);

    void deleteBankDetail(Long id);

}
