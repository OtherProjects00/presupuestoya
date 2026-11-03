package com.presupuestoya.service;

import com.presupuestoya.domain.AppUser;
import com.presupuestoya.domain.BusinessProfile;
import com.presupuestoya.repository.BusinessProfileRepository;
import com.presupuestoya.web.form.SettingsForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

@Service
public class BusinessProfileService {

    private final BusinessProfileRepository profiles;

    public BusinessProfileService(BusinessProfileRepository profiles) {
        this.profiles = profiles;
    }

    @Transactional(readOnly = true)
    public BusinessProfile get(AppUser user) {
        return profiles.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("Perfil de negocio no encontrado."));
    }

    @Transactional
    public BusinessProfile update(AppUser user, SettingsForm form) {
        BusinessProfile profile = get(user);
        profile.setBusinessName(form.getBusinessName().trim());
        profile.setOwnerName(clean(form.getOwnerName()));
        profile.setPhone(clean(form.getPhone()));
        profile.setEmail(clean(form.getEmail()));
        profile.setAddress(clean(form.getAddress()));
        profile.setTaxId(clean(form.getTaxId()));
        profile.setCurrency(form.getCurrency().trim().toUpperCase(Locale.ROOT));
        profile.setDefaultTaxRate(form.getDefaultTaxRate().setScale(2, RoundingMode.HALF_UP));
        profile.setDefaultValidityDays(form.getDefaultValidityDays());
        return profile;
    }

    public SettingsForm toForm(BusinessProfile profile) {
        SettingsForm form = new SettingsForm();
        form.setBusinessName(profile.getBusinessName());
        form.setOwnerName(profile.getOwnerName());
        form.setPhone(profile.getPhone());
        form.setEmail(profile.getEmail());
        form.setAddress(profile.getAddress());
        form.setTaxId(profile.getTaxId());
        form.setCurrency(profile.getCurrency());
        form.setDefaultTaxRate(profile.getDefaultTaxRate());
        form.setDefaultValidityDays(profile.getDefaultValidityDays());
        return form;
    }

    private String clean(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
