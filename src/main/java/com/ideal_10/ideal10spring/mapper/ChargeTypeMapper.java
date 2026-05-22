package com.ideal_10.ideal10spring.mapper;

import com.ideal_10.ideal10spring.dtos.ChargeTypeResponse;
import com.ideal_10.ideal10spring.entities.ChargeType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChargeTypeMapper {
    ChargeTypeResponse toResponse(ChargeType entity);
}
