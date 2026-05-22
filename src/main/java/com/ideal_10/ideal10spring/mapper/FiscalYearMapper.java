package com.ideal_10.ideal10spring.mapper;

import com.ideal_10.ideal10spring.dtos.FiscalYearResponse;
import com.ideal_10.ideal10spring.entities.FiscalYear;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FiscalYearMapper {
    FiscalYearResponse toResponse(FiscalYear entity);
}
