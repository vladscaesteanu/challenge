package com.extia.challenge.rest;

import com.extia.challenge.model.ExchangeResultDTO;
import com.extia.challenge.rate.RateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExchangeServiceController {
    
    @Autowired
    private RateService rateService;

    @GetMapping("/{currency1}/transform")
    public ResponseEntity<ExchangeResultDTO> getExchange(@PathVariable(value="currency1") String currency1, 
        @RequestParam(value="currency2", defaultValue = "RON") String currency2) {
        ExchangeResultDTO result = rateService.getExchangeResult(currency1, currency2); 
        return ResponseEntity.ok(result);
    }
}