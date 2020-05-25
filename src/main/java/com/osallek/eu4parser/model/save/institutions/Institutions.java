package com.osallek.eu4parser.model.save.institutions;

import com.osallek.clausewitzparser.model.ClausewitzList;

public class Institutions {

    private final ClausewitzList origins;

    private final ClausewitzList available;

    public Institutions(ClausewitzList origins, ClausewitzList available) {
        this.origins = origins;
        this.available = available;
    }

    public boolean isAvailable(int institution) {
        return this.available.getAsInt(institution) == 1;
    }

    public int getOrigin(int institution) {
        return this.origins.getAsInt(institution);
    }

    public void changeOrigin(int institution, int provinceId) {
        if (isAvailable(institution)) {
            this.origins.set(institution, provinceId);
        }
    }

    public void availableIn(int institution, int provinceId) {
        this.available.set(institution, 1);
        this.origins.set(institution, provinceId);
    }

    public void disable(int institution) {
        this.available.set(institution, 0);
        this.origins.set(institution, 0);
    }
}
