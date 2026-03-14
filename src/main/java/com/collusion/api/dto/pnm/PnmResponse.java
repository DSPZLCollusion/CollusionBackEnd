package com.collusion.api.dto.pnm;

import com.collusion.api.domain.pnm.*;

public record PnmResponse(
        Long                     id,
        String                   firstName,
        String                   lastName,
        ClassYear                year,
        PnmStatus                status,
        HousingType              housingType,
        OnCampusHousingResponse  onCampusHousing,
        OffCampusHousingResponse offCampusHousing
) {
    public static PnmResponse from(Pnm pnm) {
        OnCampusHousingResponse onCampus = null;
        OffCampusHousingResponse offCampus = null;

        if (pnm.getOnCampusHousing() != null) {
            onCampus = new OnCampusHousingResponse(
                    pnm.getOnCampusHousing().getDorm(),
                    pnm.getOnCampusHousing().getRoomNumber()
            );
        }
        if (pnm.getOffCampusHousing() != null) {
            offCampus = new OffCampusHousingResponse(
                    pnm.getOffCampusHousing().getStreetAddress(),
                    pnm.getOffCampusHousing().getCity(),
                    pnm.getOffCampusHousing().getState(),
                    pnm.getOffCampusHousing().getZipCode()
            );
        }

        return new PnmResponse(
                pnm.getId(),
                pnm.getFirstName(),
                pnm.getLastName(),
                pnm.getYear(),
                pnm.getStatus(),
                pnm.getHousingType(),
                onCampus,
                offCampus
        );
    }
}