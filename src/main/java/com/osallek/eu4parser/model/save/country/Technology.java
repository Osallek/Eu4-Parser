package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;

public class Technology {

    private final ClausewitzItem item;

    public Technology(ClausewitzItem item) {
        this.item = item;
    }

    public Integer getAdm() {
        return this.item.getVarAsInt("adm_tech");
    }

    public void setAdm(Integer adm) {
        this.item.setVariable("adm_tech", adm);
    }

    public Integer getDip() {
        return this.item.getVarAsInt("dip_tech");
    }

    public void setDip(Integer dip) {
        this.item.setVariable("dip_tech", dip);
    }

    public Integer getMil() {
        return this.item.getVarAsInt("mil_tech");
    }

    public void setMil(Integer mil) {
        this.item.setVariable("mil_tech", mil);
    }
}
