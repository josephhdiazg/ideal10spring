package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.ChargeTypeRequest;
import com.ideal_10.ideal10spring.dtos.ChargeTypeResponse;
import com.ideal_10.ideal10spring.entities.ChargeType;
import com.ideal_10.ideal10spring.exceptions.DuplicateResourceException;
import com.ideal_10.ideal10spring.exceptions.ResourceNotFoundException;
import com.ideal_10.ideal10spring.mapper.ChargeTypeMapper;
import com.ideal_10.ideal10spring.repositories.ChargeTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChargeTypeService {

    private final ChargeTypeRepository chargeTypeRepository;
    private final ChargeTypeMapper chargeTypeMapper;

    @Transactional(readOnly = true)
    public List<ChargeTypeResponse> findAll() {
        return chargeTypeRepository.findAll().stream()
                .map(chargeTypeMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChargeTypeResponse findById(Long id) {
        return chargeTypeMapper.toResponse(getEntity(id));
    }

    @Transactional
    public ChargeTypeResponse create(ChargeTypeRequest request) {
        validateUniqueCode(request.code(), null);
        ChargeType chargeType = new ChargeType();
        applyRequest(chargeType, request);
        return chargeTypeMapper.toResponse(chargeTypeRepository.save(chargeType));
    }

    @Transactional
    public ChargeTypeResponse update(Long id, ChargeTypeRequest request) {
        ChargeType chargeType = getEntity(id);
        validateUniqueCode(request.code(), id);
        applyRequest(chargeType, request);
        return chargeTypeMapper.toResponse(chargeType);
    }

    @Transactional
    public void delete(Long id) {
        chargeTypeRepository.delete(getEntity(id));
    }

    public ChargeType getEntity(Long id) {
        return chargeTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Charge type not found with id " + id));
    }

    private void applyRequest(ChargeType chargeType, ChargeTypeRequest request) {
        chargeType.setCode(request.code());
        chargeType.setName(request.name());
        chargeType.setDescription(request.description());
        chargeType.setActive(Boolean.TRUE.equals(request.active()) || request.active() == null);
    }

    private void validateUniqueCode(String code, Long currentId) {
        chargeTypeRepository.findByCode(code)
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Charge type already exists with code " + code);
                });
    }
}
