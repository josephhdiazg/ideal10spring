package com.ideal_10.ideal10spring.mapper;

import com.ideal_10.ideal10spring.dtos.TaxRateResponse;
import com.ideal_10.ideal10spring.entities.TaxRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaxRateMapper {

    @Mapping(source = "fiscalYear.id", target = "fiscalYearId")
    @Mapping(source = "fiscalYear.year", target = "year")
    TaxRateResponse toResponse(TaxRate entity);
}
