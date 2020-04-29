package com.osallek.eu4parser.model.empire;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.ArrayList;
import java.util.List;

public class Hre extends Empire {

    public Hre(ClausewitzItem item) {
        super(item);
    }

    @Override
    public void dismantle() {
        super.dismantle();
        this.item.removeList("electors");
    }

    @Override
    public void setReformLevel(int reformLevel) {
        super.setReformLevel(reformLevel);

        this.setImperialBanAllowed(reformLevel >= 1);
        this.setInternalHreCb(reformLevel < 5);
        this.setHreInheritable(reformLevel >= 6);
    }

    public Integer getContinent() {
        if (dismantled()) {
            return null;
        }

        ClausewitzVariable var = this.item.getVar("continent");

        if (var != null) {
            return var.getAsInt();
        } else {
            return null;
        }
    }

    public void setContinent(int id) {
        if (dismantled()) {
            return;
        }

        ClausewitzVariable var = this.item.getVar("continent");

        if (var != null) {
            var.setValue(id);
        } else {
            this.item.addVariable("continent", id);
        }
    }

    public Boolean getImperialBanAllowed() {
        if (dismantled()) {
            return null;
        }

        ClausewitzVariable var = this.item.getVar("imperial_ban_allowed");

        if (var != null) {
            return var.getAsBool();
        } else {
            return null;
        }
    }

    public void setImperialBanAllowed(boolean imperialBanAllowed) {
        if (dismantled()) {
            return;
        }

        ClausewitzVariable var = this.item.getVar("imperial_ban_allowed");

        if (var != null) {
            var.setValue(imperialBanAllowed);
        } else {
            this.item.addVariable("imperial_ban_allowed", imperialBanAllowed);
        }
    }

    public Boolean getInternalHreCb() {
        if (dismantled()) {
            return null;
        }

        ClausewitzVariable var = this.item.getVar("internal_hre_cb");

        if (var != null) {
            return var.getAsBool();
        } else {
            return null;
        }
    }

    public void setInternalHreCb(boolean internalHreCb) {
        if (dismantled()) {
            return;
        }

        ClausewitzVariable var = this.item.getVar("internal_hre_cb");

        if (var != null) {
            var.setValue(internalHreCb);
        } else {
            this.item.addVariable("internal_hre_cb", internalHreCb);
        }
    }

    public Boolean getHreInheritable() {
        if (dismantled()) {
            return null;
        }

        ClausewitzVariable var = this.item.getVar("hre_inheritable");

        if (var != null) {
            return var.getAsBool();
        } else {
            return null;
        }
    }

    public void setHreInheritable(boolean hreInheritable) {
        if (dismantled()) {
            return;
        }

        ClausewitzVariable var = this.item.getVar("hre_inheritable");

        if (var != null) {
            var.setValue(hreInheritable);
        } else {
            this.item.addVariable("hre_inheritable", hreInheritable);
        }
    }

    public Boolean getAllowsFemaleEmperor() {
        if (dismantled()) {
            return null;
        }

        ClausewitzVariable var = this.item.getVar("allows_female_emperor");

        if (var != null) {
            return var.getAsBool();
        } else {
            return null;
        }
    }

    public void setAllowsFemaleEmperor(boolean allowsFemaleEmperor) {
        if (dismantled()) {
            return;
        }

        ClausewitzVariable var = this.item.getVar("allows_female_emperor");

        if (var != null) {
            var.setValue(allowsFemaleEmperor);
        } else {
            this.item.addVariable("allows_female_emperor", allowsFemaleEmperor);
        }
    }

    public List<String> getElectors() {
        if (dismantled()) {
            return new ArrayList<>();
        }

        return this.item.getList("electors").getValues();
    }

    public void addElector(String tag) {
        if (dismantled()) {
            return;
        }

        ClausewitzList list = this.item.getList("electors");

        if (list != null) {
            if (!list.contains(tag)) {
                list.add(tag);
            }
        } else {
            this.item.addList("electors", tag);
        }
    }

    public void removeElector(String tag) {
        if (dismantled()) {
            return;
        }

        ClausewitzList list = this.item.getList("electors");

        if (list != null) {
            list.remove(tag);
        }
    }

    public void removeElector(int id) {
        if (dismantled()) {
            return;
        }

        ClausewitzList list = this.item.getList("electors");

        if (list != null) {
            list.remove(id);
        }
    }
}
