package com.easyservice.backend.infrastructure;

import com.easyservice.backend.model.enums.CustomerType;
import com.easyservice.backend.model.enums.IdentityType;
import com.easyservice.backend.service.IdentityVerificationService;
import org.springframework.stereotype.Service;

@Service
public class FakeIdentityVerificationService implements IdentityVerificationService {
    @Override
    public boolean verify(
            CustomerType customerType,
            IdentityType identityType,
            String identityValue) {

        if (customerType == null ||
                identityType == null ||
                identityValue == null ||
                identityValue.isBlank()) {
            return false;
        }

        if (customerType == CustomerType.ETHIOPIAN) {
            return identityType == IdentityType.FAYDA
                    && identityValue.equals("TEST-FAYDA-VALID");
        }

        if (customerType == CustomerType.FOREIGNER) {
            return identityType == IdentityType.PASSPORT
                    && identityValue.equals("TEST-PASSPORT-VALID");
        }

        return false;
    }
}
