package com.osallek.eu4parser.model.save.empire;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.game.Decree;
import com.osallek.eu4parser.model.save.Save;

import java.time.LocalDate;
import java.util.Date;

public class SaveDecree {

    private final ClausewitzItem saveItem;

    private final Save save;

    private Decree decree;

    public SaveDecree(ClausewitzItem saveItem, Save save) {
        this.saveItem = saveItem;
        this.save = save;

        refreshAttributes();
    }

    public String getType() {
        return this.saveItem.getVarAsString("decree_name");
    }

    public LocalDate getDate() {
        return this.saveItem.getVarAsDate("decree_date");
    }

    public void setType(String type) {
        this.saveItem.setVariable("decree_name", ClausewitzUtils.addQuotes(type));
        refreshAttributes();
    }

    public void setDate(LocalDate date) {
        this.saveItem.setVariable("decree_date", date);
    }

    public void setDecree(Decree decree) {
        if (decree == null) {
            setDecree(null, null);
        } else {
            setDecree(decree.getName(), this.save.getDate());
        }
    }

    public void setDecree(String type, LocalDate date) {
        if (ClausewitzUtils.isBlank(type)) {
            this.saveItem.removeVariable("decree_name");
            this.saveItem.removeVariable("decree_date");
            refreshAttributes();
            return;
        }

        if (getDecree() == null || !getDecree().getName().equals(type)) {
            setType(type);
            setDate(date);
        }
    }

    public Decree getDecree() {
        return decree;
    }

    public String getLocalizedName() {
        return this.decree.getLocalizedName();
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, LocalDate date) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "decree", parent.getOrder() + 1);
        toItem.addVariable("decree_name", name);
        toItem.addVariable("decree_date", date);

        parent.addChild(toItem);
        return toItem;
    }

    private void refreshAttributes() {
        this.decree = this.save.getGame().getDecree(ClausewitzUtils.removeQuotes(this.getType()));
    }
}
