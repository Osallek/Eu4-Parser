package com.osallek.eu4parser.model.save.empire;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.Date;

public class Decree {

    private final ClausewitzItem item;

    public Decree(ClausewitzItem item) {
        this.item = item;
    }

    public DecreeType getType() {
        ClausewitzVariable var = this.item.getVar("decree_name");

        if (var != null) {
            return DecreeType.valueOf(var.getValue().substring(1, var.getValue().length() - 1).toUpperCase());
        } else {
            return null;
        }
    }

    public Date getDate() {
        ClausewitzVariable dateVar = this.item.getVar("decree_date");

        if (dateVar != null) {
            return dateVar.getAsDate();
        } else {
            return null;
        }
    }

    public void setType(DecreeType type) {
        ClausewitzVariable decreeNameVar = this.item.getVar("decree_name");
        ClausewitzVariable dateVar = this.item.getVar("decree_date");

        if (decreeNameVar != null && dateVar != null) {
            decreeNameVar.setValue("\"" + type.toString().toLowerCase() + "\"");
        }
    }

    public void setDate(Date date) {
        ClausewitzVariable decreeNameVar = this.item.getVar("decree_name");
        ClausewitzVariable dateVar = this.item.getVar("decree_date");

        if (decreeNameVar != null && dateVar != null) {
            dateVar.setValue(date);
        }
    }

    public void setDecree(DecreeType type, Date date) {
        ClausewitzVariable decreeNameVar = this.item.getVar("decree_name");

        if (decreeNameVar != null) {
            decreeNameVar.setValue("\"" + type.toString().toLowerCase() + "\"");
        } else {
            this.item.addVariable("decree_name", "\"" + type.toString().toLowerCase() + "\"");
        }

        ClausewitzVariable dateVar = this.item.getVar("decree_date");

        if (dateVar != null) {
            dateVar.setValue(date);
        } else {
            this.item.addVariable("decree_date", date);
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, Date date) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "decree", parent.getOrder() + 1);
        toItem.addVariable("decree_name", name);
        toItem.addVariable("decree_date", date);

        parent.addChild(toItem);
        return toItem;
    }
}
