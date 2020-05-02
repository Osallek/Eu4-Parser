package com.osallek.eu4parser.model.religion;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.common.Eu4Utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class ColoniesClaims {

    private final ClausewitzList list;

    public ColoniesClaims(ClausewitzList list) {
        this.list = list;
    }

    public Map<Colony, String> getColonyClaims() {
        Map<Colony, String> claims = new LinkedHashMap<>();

        if (this.list != null) {
            int max = Math.min(this.list.getValues().size(), Colony.values().length);
            for (int i = 0; i < max; i++) {
                String value = this.list.get(i).equals(Eu4Utils.DEFAULT_TAG) ? null : this.list.get(i);
                claims.put(Colony.values()[i], value);
            }
        }

        return claims;
    }

    public String getColonyClaim(Colony colony) {
        if (this.list != null) {
            return this.list.get(colony.ordinal()).equals(Eu4Utils.DEFAULT_TAG) ? null : this.list.get(colony.ordinal());
        }

        return null;
    }

    public void setColonyClaim(Colony colony, String tag) {
        if (this.list != null) {
            this.list.set(colony.ordinal(), ClausewitzUtils.isBlank(tag) ? Eu4Utils.DEFAULT_TAG : tag);
        }
    }

    public void removeColonyClaim(Colony colony) {
        if (this.list != null) {
            this.list.set(colony.ordinal(), Eu4Utils.DEFAULT_TAG);
        }
    }
}
