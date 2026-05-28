package com.ideal_10.ideal10spring.config;

import com.ideal_10.ideal10spring.entities.ChargeType;
import com.ideal_10.ideal10spring.entities.FiscalYear;
import com.ideal_10.ideal10spring.entities.TaxBenefit;
import com.ideal_10.ideal10spring.entities.TaxRate;
import com.ideal_10.ideal10spring.enums.PropertyClassification;
import com.ideal_10.ideal10spring.repositories.ChargeTypeRepository;
import com.ideal_10.ideal10spring.repositories.FiscalYearRepository;
import com.ideal_10.ideal10spring.repositories.TaxBenefitRepository;
import com.ideal_10.ideal10spring.repositories.TaxRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class TaxDataInitializer {

    @Bean
    @Transactional
    public CommandLineRunner seedTaxData(
            FiscalYearRepository fiscalYearRepository,
            TaxRateRepository taxRateRepository,
            ChargeTypeRepository chargeTypeRepository,
            TaxBenefitRepository taxBenefitRepository
    ) {
        return args -> {
            seedFiscalYearAndRates(fiscalYearRepository, taxRateRepository);
            seedChargeTypes(chargeTypeRepository);
            seedTaxBenefits(taxBenefitRepository);
        };
    }

    private void seedFiscalYearAndRates(
            FiscalYearRepository fiscalYearRepository,
            TaxRateRepository taxRateRepository
    ) {
        if (fiscalYearRepository.findByYear(2026).isPresent()) {
            return;
        }

        FiscalYear fy2026 = new FiscalYear();
        fy2026.setYear(2026);
        fy2026.setDescription("Vigencia fiscal 2026");
        fy2026.setStartDate(LocalDate.of(2026, 1, 1));
        fy2026.setEndDate(LocalDate.of(2026, 12, 31));
        fy2026.setActive(true);
        fiscalYearRepository.save(fy2026);

        TaxRate urbanRate = new TaxRate();
        urbanRate.setFiscalYear(fy2026);
        urbanRate.setClassification(PropertyClassification.URBAN);
        urbanRate.setRatePerThousand(new BigDecimal("9.00"));
        urbanRate.setActive(true);
        taxRateRepository.save(urbanRate);

        TaxRate ruralRate = new TaxRate();
        ruralRate.setFiscalYear(fy2026);
        ruralRate.setClassification(PropertyClassification.RURAL);
        ruralRate.setRatePerThousand(new BigDecimal("5.00"));
        ruralRate.setActive(true);
        taxRateRepository.save(ruralRate);
    }

    private void seedChargeTypes(ChargeTypeRepository chargeTypeRepository) {
        if (chargeTypeRepository.findByCode("IPU").isPresent()) {
            return;
        }

        chargeTypeRepository.save(chargeType("IPU", "Impuesto Predial Unificado",
                "Cargo principal del impuesto predial"));
        chargeTypeRepository.save(chargeType("INT", "Intereses de mora",
                "Intereses por pago tardío"));
        chargeTypeRepository.save(chargeType("DESC", "Descuento por pronto pago",
                "Descuento aplicado al pagar antes del vencimiento"));
    }

    private void seedTaxBenefits(TaxBenefitRepository taxBenefitRepository) {
        if (taxBenefitRepository.findByCode("DESC_ADULTO_MAYOR").isPresent()) {
            return;
        }

        taxBenefitRepository.save(taxBenefit(
                "DESC_ADULTO_MAYOR",
                "Descuento adulto mayor",
                "Descuento del 15% para propietarios mayores de 60 años",
                new BigDecimal("15.00"),
                null
        ));
        taxBenefitRepository.save(taxBenefit(
                "DESC_RURAL_PROD",
                "Predio rural productivo",
                "Descuento del 10% para predios rurales con actividad agropecuaria activa",
                new BigDecimal("10.00"),
                PropertyClassification.RURAL
        ));
        taxBenefitRepository.save(taxBenefit(
                "DESC_VIS",
                "Vivienda de interés social",
                "Descuento del 20% para predios clasificados como VIS",
                new BigDecimal("20.00"),
                PropertyClassification.URBAN
        ));
    }

    private ChargeType chargeType(String code, String name, String description) {
        ChargeType ct = new ChargeType();
        ct.setCode(code);
        ct.setName(name);
        ct.setDescription(description);
        ct.setActive(true);
        return ct;
    }

    private TaxBenefit taxBenefit(
            String code, String name, String description,
            BigDecimal discountPercentage, PropertyClassification classification
    ) {
        TaxBenefit tb = new TaxBenefit();
        tb.setCode(code);
        tb.setName(name);
        tb.setDescription(description);
        tb.setDiscountPercentage(discountPercentage);
        tb.setApplicableClassification(classification);
        tb.setActive(true);
        return tb;
    }
}
