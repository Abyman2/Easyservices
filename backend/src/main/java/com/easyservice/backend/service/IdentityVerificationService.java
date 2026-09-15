package com.easyservice.backend.service;

import com.easyservice.backend.model.enums.CustomerType;
import com.easyservice.backend.model.enums.IdentityType;

public interface IdentityVerificationService {
    boolean verify(
            CustomerType customerType,
            IdentityType identityType,
            String identityValue
    );
}
