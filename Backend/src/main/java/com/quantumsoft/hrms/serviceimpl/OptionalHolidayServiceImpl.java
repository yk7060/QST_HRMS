package com.quantumsoft.hrms.serviceimpl;

import com.quantumsoft.hrms.entity.OptionalHoliday;
import com.quantumsoft.hrms.repository.OptionalHolidayRepository;
import com.quantumsoft.hrms.servicei.OptionalHolidayServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptionalHolidayServiceImpl implements OptionalHolidayServiceI {

    @Autowired
    private OptionalHolidayRepository optionalHolidayRepository;

    @Override
    public String addOptionalHoliday(OptionalHoliday optionalHoliday) {
        try {
            optionalHolidayRepository.save(optionalHoliday);
            return "Optional Holiday added successfully..!";
        }catch (Exception e){
            return "Failed to add optional holiday " + e.getMessage();
        }
    }

    @Override
    public List<OptionalHoliday> getOptionalHolidays() {
        return optionalHolidayRepository.findAll();
    }
}
