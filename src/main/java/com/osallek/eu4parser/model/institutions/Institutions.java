package com.osallek.eu4parser.model.institutions;

import com.osallek.clausewitzparser.model.ClausewitzList;

public class Institutions {

    private final ClausewitzList origins;

    private final ClausewitzList available;

    public Institutions(ClausewitzList origins, ClausewitzList available) {
        this.origins = origins;
        this.available = available;
    }

    public boolean isAvailable(Institution institution) {
        return this.available.getAsInt(institution.ordinal()) == 1;
    }

    public int getOrigin(Institution institution) {
        return this.origins.getAsInt(institution.ordinal());
    }

    public void changeOrigin(Institution institution, int provinceId) {
        if (isAvailable(institution)) {
            this.origins.set(institution.ordinal(), provinceId);
        }
    }

    public void availableIn(Institution institution, int provinceId) {
        this.available.set(institution.ordinal(), 1);
        this.origins.set(institution.ordinal(), provinceId);
    }

    public void disable(Institution institution) {
        this.available.set(institution.ordinal(), 0);
        this.origins.set(institution.ordinal(), 0);
    }
}
