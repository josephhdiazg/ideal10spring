package com.ideal_10.ideal10spring.mapper;

import com.ideal_10.ideal10spring.dtos.TaxBenefitResponse;
import com.ideal_10.ideal10spring.entities.TaxBenefit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaxBenefitMapper {
    TaxBenefitResponse toResponse(TaxBenefit entity);
}
