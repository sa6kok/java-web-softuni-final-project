package com.localbandb.localbandb.web.api.controllers;

import com.localbandb.localbandb.services.services.CountryService;
import javassist.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class ReservationApiController {
  private final CountryService countryService;

  public ReservationApiController(CountryService countryService) {
    this.countryService = countryService;
  }

  @GetMapping("/reservation/api/create/{country}")
  public List<String> fillCitiesForCountryInSelect(@PathVariable String country) throws NotFoundException {
    return countryService.getOrderedCitiesForCountry(country);
  }
}
