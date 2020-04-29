package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

public class Technology {

    private final ClausewitzItem item;

    public Technology(ClausewitzItem item) {
        this.item = item;
    }

    public Integer getAdm() {
        return this.item.getVarAsInt("adm_tech");
    }

    public void setAdm(Integer adm) {
        ClausewitzVariable var = this.item.getVar("adm_tech");

        if (var != null) {
            var.setValue(adm);
        } else {
            this.item.addVariable("adm_tech", adm);
        }
    }

    public Integer getDip() {
        return this.item.getVarAsInt("dip_tech");
    }

    public void setDip(Integer dip) {
        ClausewitzVariable var = this.item.getVar("dip_tech");

        if (var != null) {
            var.setValue(dip);
        } else {
            this.item.addVariable("dip_tech", dip);
        }
    }

    public Integer getMil() {
        return this.item.getVarAsInt("mil_tech");
    }

    public void setMil(Integer mil) {
        ClausewitzVariable var = this.item.getVar("mil_tech");

        if (var != null) {
            var.setValue(mil);
        } else {
            this.item.addVariable("mil_tech", mil);
        }
    }
}
