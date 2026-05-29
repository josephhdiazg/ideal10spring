package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.MunicipalityRequest;
import com.ideal_10.ideal10spring.dtos.MunicipalityResponse;
import com.ideal_10.ideal10spring.entities.Municipality;
import com.ideal_10.ideal10spring.exceptions.DuplicateResourceException;
import com.ideal_10.ideal10spring.exceptions.ResourceNotFoundException;
import com.ideal_10.ideal10spring.repositories.MunicipalityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MunicipalityServiceTest {

    @Mock
    private MunicipalityRepository municipalityRepository;

    @InjectMocks
    private MunicipalityService municipalityService;

    @Test
    void createDefaultsActiveToTrueWhenRequestActiveIsNull() {
        MunicipalityRequest request = new MunicipalityRequest("Medellin", "Antioquia", null);
        when(municipalityRepository.findByNameIgnoreCaseAndDepartmentIgnoreCase("Medellin", "Antioquia"))
                .thenReturn(Optional.empty());
        when(municipalityRepository.save(any(Municipality.class))).thenAnswer(invocation -> {
            Municipality municipality = invocation.getArgument(0);
            municipality.setId(1L);
            return municipality;
        });

        MunicipalityResponse response = municipalityService.create(request);

        assertEquals(1L, response.id());
        assertEquals("Medellin", response.name());
        assertEquals("Antioquia", response.department());
        assertTrue(response.active());
    }

    @Test
    void createThrowsWhenMunicipalityAlreadyExists() {
        Municipality existing = municipality(10L, "Medellin", "Antioquia", true);
        MunicipalityRequest request = new MunicipalityRequest("Medellin", "Antioquia", true);
        when(municipalityRepository.findByNameIgnoreCaseAndDepartmentIgnoreCase("Medellin", "Antioquia"))
                .thenReturn(Optional.of(existing));

        assertThrows(DuplicateResourceException.class, () -> municipalityService.create(request));
        verify(municipalityRepository, never()).save(any(Municipality.class));
    }

    @Test
    void updateAllowsSameMunicipalityToKeepNameAndDepartment() {
        Municipality existing = municipality(10L, "Medellin", "Antioquia", true);
        MunicipalityRequest request = new MunicipalityRequest("Medellin", "Antioquia", false);
        when(municipalityRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(municipalityRepository.findByNameIgnoreCaseAndDepartmentIgnoreCase("Medellin", "Antioquia"))
                .thenReturn(Optional.of(existing));

        MunicipalityResponse response = municipalityService.update(10L, request);

        assertEquals(10L, response.id());
        assertEquals("Medellin", response.name());
        assertEquals("Antioquia", response.department());
        assertEquals(false, response.active());
    }

    @Test
    void findAllMapsMunicipalitiesToResponses() {
        when(municipalityRepository.findAll())
                .thenReturn(List.of(municipality(1L, "Medellin", "Antioquia", true)));

        List<MunicipalityResponse> response = municipalityService.findAll();

        assertEquals(1, response.size());
        assertEquals(1L, response.getFirst().id());
        assertEquals("Medellin", response.getFirst().name());
    }

    @Test
    void getEntityThrowsWhenMunicipalityDoesNotExist() {
        when(municipalityRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> municipalityService.getEntity(99L));
    }

    private Municipality municipality(Long id, String name, String department, Boolean active) {
        Municipality municipality = new Municipality();
        municipality.setId(id);
        municipality.setName(name);
        municipality.setDepartment(department);
        municipality.setActive(active);
        return municipality;
    }
}
