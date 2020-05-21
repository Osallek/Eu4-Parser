package com.osallek.eu4parser.model.save.empire;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class Empire {

    protected final ClausewitzItem item;

    protected List<OldEmperor> oldEmperors;

    public Empire(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public boolean dismantled() {
        ClausewitzVariable dismantledVar = this.item.getVar("hre_dismantled");
        return dismantledVar != null && dismantledVar.getAsBool();
    }

    public void dismantle() {
        this.item.addVariable("hre_dismantled", true);
        this.item.removeVariable("emperor");
        this.item.removeVariable("imperial_influence");
        this.item.removeVariable("reform_level");
    }

    public String getEmperor() {
        if (dismantled()) {
            return null;
        }

        ClausewitzVariable emperorVar = this.item.getVar("emperor");

        if (emperorVar != null) {
            return emperorVar.getValue();
        } else {
            return null;
        }
    }

    public Double getImperialInfluence() {
        if (dismantled()) {
            return null;
        }

        ClausewitzVariable imperialInfluenceVar = this.item.getVar("imperial_influence");

        if (imperialInfluenceVar != null) {
            return imperialInfluenceVar.getAsDouble();
        } else {
            return null;
        }
    }

    public void setImperialInfluence(double imperialInfluence) {
        if (dismantled()) {
            return;
        }

        ClausewitzVariable var = this.item.getVar("imperial_influence");

        if (var != null) {
            var.setValue(imperialInfluence);
        } else {
            this.item.addVariable("imperial_influence", imperialInfluence);
        }
    }

    public Integer getReformLevel() {
        if (dismantled()) {
            return null;
        }

        ClausewitzVariable reformLevelVar = this.item.getVar("reform_level");

        if (reformLevelVar != null) {
            return reformLevelVar.getAsInt();
        } else {
            return null;
        }
    }

    public void setReformLevel(int reformLevel) {
        if (dismantled()) {
            return;
        }

        ClausewitzVariable var = this.item.getVar("reform_level");

        if (var != null) {
            var.setValue(reformLevel);
        } else {
            this.item.addVariable("reform_level", reformLevel);
        }
    }

    public List<OldEmperor> getOldEmperors() {
        return oldEmperors;
    }

    public void setEmperor(String tag, Date currentDate) {
        if (dismantled()) {
            this.item.removeVariable("hre_dismantled");
        }

        this.item.removeVariable("emperor");
        this.item.addVariable("emperor", ClausewitzUtils.hasQuotes(tag) ? tag : "\"" + tag.toUpperCase() + "\"");

        if (getImperialInfluence() == null) {
            this.item.addVariable("imperial_influence", 0d);
        }

        if (getReformLevel() == null) {
            this.item.addVariable("reform_level", 0);
        }

        addOldEmperor(tag, currentDate);
    }

    public void addOldEmperor(String country, Date date) {
        String id = Integer.toString(getOldEmperors().stream()
                                                     .map(OldEmperor::getId)
                                                     .max(Integer::compareTo)
                                                     .orElse(new Random().nextInt(9000)));
        OldEmperor.addToItem(this.item, id, country, date);
        refreshAttributes();
    }

    public void removeOldEmperor(int id) {
        this.item.removeChild("old_emperor", id);
        refreshAttributes();
    }

    protected void refreshAttributes() {
        this.oldEmperors = this.item.getChildren("old_emperor")
                                    .stream()
                                    .map(OldEmperor::new)
                                    .collect(Collectors.toList());
    }
}
