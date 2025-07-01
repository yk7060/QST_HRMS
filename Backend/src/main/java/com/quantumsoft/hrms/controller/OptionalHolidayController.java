package com.quantumsoft.hrms.controller;

import com.quantumsoft.hrms.entity.OptionalHoliday;
import com.quantumsoft.hrms.servicei.OptionalHolidayServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/optionalHoliday")
public class OptionalHolidayController {

    @Autowired
    private OptionalHolidayServiceI optionalHolidayService;

    @PreAuthorize("hasRole('HR')")
    @PostMapping(value = "/add")
    public ResponseEntity<String> addOptionalHoliday(@RequestBody OptionalHoliday optionalHoliday){
       return new ResponseEntity<String>(optionalHolidayService.addOptionalHoliday(optionalHoliday), HttpStatus.CREATED);
    }

    @GetMapping(value = "/get")
    public ResponseEntity<List<OptionalHoliday>> getOptionalHolidays(){
        return new ResponseEntity<List<OptionalHoliday>>(optionalHolidayService.getOptionalHolidays(), HttpStatus.OK);
    }


}
