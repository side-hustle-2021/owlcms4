package app.owlcms.data.customlogin;

import java.util.Collection;
import java.util.EnumSet;

public enum CustomRole {

    ADMIN, ATHLETE, REFEREE, ORGANIZER;

    public static Collection<CustomRole> findAll() {
        return EnumSet.of(CustomRole.ADMIN, CustomRole.ATHLETE, CustomRole.REFEREE, CustomRole.ORGANIZER);
    }

}
