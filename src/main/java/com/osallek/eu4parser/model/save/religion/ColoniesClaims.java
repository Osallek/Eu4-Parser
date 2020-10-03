package com.osallek.eu4parser.model.save.religion;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.common.Eu4Utils;
import com.osallek.eu4parser.model.save.country.Country;

import java.util.ArrayList;
import java.util.List;

public class ColoniesClaims {

    private final ClausewitzList list;

    public ColoniesClaims(ClausewitzList list) {
        this.list = list;
    }

    public List<String> getColonyClaims() {
        return this.list == null ? new ArrayList<>() : this.list.getValues();
    }

    public String getColonyClaim(int index) {
        if (this.list != null) {
            return this.list.get(index);
        }

        return null;
    }

    public void setColonyClaim(int index, Country country) {
        if (this.list != null) {
            this.list.set(index, ClausewitzUtils.isBlank(country.getTag()) ? Eu4Utils.DEFAULT_TAG : country.getTag());
        }
    }

    public void removeColonyClaim(int index) {
        if (this.list != null) {
            this.list.set(index, Eu4Utils.DEFAULT_TAG);
        }
    }
}
