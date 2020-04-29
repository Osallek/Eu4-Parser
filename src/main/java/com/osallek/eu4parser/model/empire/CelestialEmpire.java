package com.osallek.eu4parser.model.empire;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Date;

public class CelestialEmpire extends Empire {

    private Decree decree;

    public CelestialEmpire(ClausewitzItem item) {
        super(item);
        refreshAttributes();
    }

    @Override
    public void dismantle() {
        super.dismantle();
        this.item.getChild("decree").removeAll();
        refreshAttributes();
    }

    public Decree getDecree() {
        return decree;
    }

    public void setDecreeType(DecreeType decreeType) {
        this.decree.setType(decreeType);
    }

    public void setDecreeDate(Date decreeDate) {
        this.decree.setDate(decreeDate);
    }

    public void setDecree(DecreeType decreeType, Date decreeDate) {
        this.decree.setDecree(decreeType, decreeDate);
    }

    @Override
    protected void refreshAttributes() {
        super.refreshAttributes();
        ClausewitzItem decreeItem = this.item.getChild("decree");

        if (decreeItem != null) {
            this.decree = new Decree(decreeItem);
        }
    }
}
