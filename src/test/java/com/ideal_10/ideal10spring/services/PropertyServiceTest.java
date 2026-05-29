package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.MunicipalityResponse;
import com.ideal_10.ideal10spring.dtos.PropertyRequest;
import com.ideal_10.ideal10spring.dtos.PropertyResponse;
import com.ideal_10.ideal10spring.entities.Municipality;
import com.ideal_10.ideal10spring.entities.Property;
import com.ideal_10.ideal10spring.enums.PropertyStatus;
import com.ideal_10.ideal10spring.enums.PropertyUse;
import com.ideal_10.ideal10spring.exceptions.DuplicateResourceException;
import com.ideal_10.ideal10spring.exceptions.ResourceNotFoundException;
import com.ideal_10.ideal10spring.repositories.PropertyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private MunicipalityService municipalityService;

    @InjectMocks
    private PropertyService propertyService;

    @Test
    void createDefaultsStatusToActiveAndMapsMunicipality() {
        Municipality municipality = municipality(3L);
        PropertyRequest request = request("CAD-001", null, 3L);
        when(propertyRepository.findByCadastralCodeIgnoreCase("CAD-001")).thenReturn(Optional.empty());
        when(municipalityService.getEntity(3L)).thenReturn(municipality);
        when(municipalityService.toResponse(municipality))
                .thenReturn(new MunicipalityResponse(3L, "Medellin", "Antioquia", true));
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> {
            Property property = invocation.getArgument(0);
            property.setId(8L);
            return property;
        });

        PropertyResponse response = propertyService.create(request);

        assertEquals(8L, response.id());
        assertEquals("CAD-001", response.cadastralCode());
        assertEquals(PropertyStatus.ACTIVE, response.status());
        assertEquals(3L, response.municipality().id());
    }

    @Test
    void createThrowsWhenCadastralCodeAlreadyExists() {
        Property existing = property(99L, "CAD-001", municipality(3L));
        PropertyRequest request = request("CAD-001", PropertyStatus.ACTIVE, 3L);
        when(propertyRepository.findByCadastralCodeIgnoreCase("CAD-001")).thenReturn(Optional.of(existing));

        assertThrows(DuplicateResourceException.class, () -> propertyService.create(request));
        verify(municipalityService, never()).getEntity(any());
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    void updateAllowsSamePropertyToKeepCadastralCode() {
        Municipality municipality = municipality(4L);
        Property existing = property(8L, "CAD-001", municipality);
        PropertyRequest request = request("CAD-001", PropertyStatus.INACTIVE, 4L);
        when(propertyRepository.findById(8L)).thenReturn(Optional.of(existing));
        when(propertyRepository.findByCadastralCodeIgnoreCase("CAD-001")).thenReturn(Optional.of(existing));
        when(municipalityService.getEntity(4L)).thenReturn(municipality);
        when(municipalityService.toResponse(municipality))
                .thenReturn(new MunicipalityResponse(4L, "Envigado", "Antioquia", true));

        PropertyResponse response = propertyService.update(8L, request);

        assertEquals(8L, response.id());
        assertEquals("CAD-001", response.cadastralCode());
        assertEquals(PropertyStatus.INACTIVE, response.status());
        assertEquals(4L, response.municipality().id());
    }

    @Test
    void findAllMapsPropertiesToResponses() {
        Municipality municipality = municipality(3L);
        Property property = property(8L, "CAD-001", municipality);
        when(propertyRepository.findAll()).thenReturn(List.of(property));
        when(municipalityService.toResponse(municipality))
                .thenReturn(new MunicipalityResponse(3L, "Medellin", "Antioquia", true));

        List<PropertyResponse> response = propertyService.findAll();

        assertEquals(1, response.size());
        assertEquals("CAD-001", response.getFirst().cadastralCode());
        assertEquals(3L, response.getFirst().municipality().id());
    }

    @Test
    void getEntityThrowsWhenPropertyDoesNotExist() {
        when(propertyRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> propertyService.getEntity(42L));
    }

    private PropertyRequest request(String cadastralCode, PropertyStatus status, Long municipalityId) {
        return new PropertyRequest(
                cadastralCode,
                "Calle 10 #20-30",
                PropertyUse.RESIDENTIAL,
                status,
                new BigDecimal("150000000.00"),
                municipalityId
        );
    }

    private Municipality municipality(Long id) {
        Municipality municipality = new Municipality();
        municipality.setId(id);
        municipality.setName(id.equals(3L) ? "Medellin" : "Envigado");
        municipality.setDepartment("Antioquia");
        municipality.setActive(true);
        return municipality;
    }

    private Property property(Long id, String cadastralCode, Municipality municipality) {
        Property property = new Property();
        property.setId(id);
        property.setCadastralCode(cadastralCode);
        property.setAddress("Calle 10 #20-30");
        property.setPropertyUse(PropertyUse.RESIDENTIAL);
        property.setStatus(PropertyStatus.ACTIVE);
        property.setCadastralValue(new BigDecimal("150000000.00"));
        property.setMunicipality(municipality);
        return property;
    }
}
