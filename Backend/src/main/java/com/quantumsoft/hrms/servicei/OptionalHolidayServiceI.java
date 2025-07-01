package com.quantumsoft.hrms.servicei;

import com.quantumsoft.hrms.entity.OptionalHoliday;

import java.util.List;

public interface OptionalHolidayServiceI {
   public String addOptionalHoliday(OptionalHoliday optionalHoliday);

   List<OptionalHoliday> getOptionalHolidays();
}
