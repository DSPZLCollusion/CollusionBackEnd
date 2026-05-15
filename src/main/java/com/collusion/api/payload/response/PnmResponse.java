package com.collusion.api.payload.response;

import com.collusion.api.model.Pnm;
import com.collusion.api.model.enums.ClassYear;
import com.collusion.api.model.enums.Dorm;
import com.collusion.api.model.enums.HousingType;
import com.collusion.api.model.enums.PnmStatus;
import lombok.Getter;

@Getter
public class PnmResponse {

    private final Long        id;
    private final String      firstName;
    private final String      lastName;
    private final ClassYear   year;
    private final PnmStatus   status;
    private final HousingType housingType;

    // On-campus (null if off-campus)
    private final Dorm dorm;
    private final String roomNumber;

    // Off-campus (null if on-campus)
    private final String streetAddress;
    private final String city;
    private final String state;
    private final String zipCode;

    private PnmResponse(Long id, String firstName, String lastName,
                        ClassYear year, PnmStatus status, HousingType housingType,
                        Dorm dorm, String roomNumber,
                        String streetAddress, String city, String state, String zipCode) {
        this.id            = id;
        this.firstName     = firstName;
        this.lastName      = lastName;
        this.year          = year;
        this.status        = status;
        this.housingType   = housingType;
        this.dorm          = dorm;
        this.roomNumber    = roomNumber;
        this.streetAddress = streetAddress;
        this.city          = city;
        this.state         = state;
        this.zipCode       = zipCode;
    }

    public static PnmResponse from(Pnm pnm) {
        Dorm dorm          = null;
        String roomNumber    = null;
        String streetAddress = null;
        String city          = null;
        String state         = null;
        String zipCode       = null;

        if (pnm.getHousingType() == HousingType.ON_CAMPUS && pnm.getOnCampusHousing() != null) {
            dorm       = pnm.getOnCampusHousing().getDorm();
            roomNumber = pnm.getOnCampusHousing().getRoomNumber();
        } else if (pnm.getHousingType() == HousingType.OFF_CAMPUS && pnm.getOffCampusHousing() != null) {
            streetAddress = pnm.getOffCampusHousing().getStreetAddress();
            city          = pnm.getOffCampusHousing().getCity();
            state         = pnm.getOffCampusHousing().getState();
            zipCode       = pnm.getOffCampusHousing().getZipCode();
        }

        return new PnmResponse(
                pnm.getId(), pnm.getFirstName(), pnm.getLastName(),
                pnm.getYear(), pnm.getStatus(), pnm.getHousingType(),
                dorm, roomNumber,
                streetAddress, city, state, zipCode
        );
    }
}