package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.ArrayList;
import java.util.List;

public class Government {

    private final ClausewitzItem item;

    public Government(ClausewitzItem item) {
        this.item = item;
    }

    public String getType() {
        return this.item.getVarAsString("government");
    }

    public void setType(String type) {
        ClausewitzVariable var = this.item.getVar("government");

        if (var != null) {
            var.setValue(type);
        } else {
            this.item.addVariable("government", type);
        }
    }

    public List<String> getReforms() {
        ClausewitzItem reformStack = this.item.getChild("reform_stack");
        List<String> reforms = new ArrayList<>();

        if (reformStack != null) {
            ClausewitzList list = reformStack.getList("reforms");

            if (list != null) {
                return list.getValues();
            }
        }

        return reforms;
    }

    public List<String> getHistory() {
        ClausewitzItem reformStack = this.item.getChild("reform_stack");
        List<String> reforms = new ArrayList<>();

        if (reformStack != null) {
            ClausewitzList list = reformStack.getList("history");

            if (list != null) {
                return list.getValues();
            }
        }

        return reforms;
    }

    public void addReform(String reform) {
        ClausewitzItem reformStack = this.item.getChild("reform_stack");

        if (reformStack == null) {
            reformStack = this.item.addChild("reform_stack");
        }

        reform = ClausewitzUtils.addQuotes(reform);

        ClausewitzList reforms = reformStack.getList("reforms");

        if (reforms != null) {
            reforms.add(reform);
        } else {
            reformStack.addList("reforms", reform);
        }

        ClausewitzList history = reformStack.getList("history");

        if (history != null) {
            history.add(reform);
        } else {
            reformStack.addList("history", reform);
        }
    }

    public void changeReform(String previous, String newOne) {
        ClausewitzItem reformStack = this.item.getChild("reform_stack");

        if (reformStack != null) {
            previous = ClausewitzUtils.addQuotes(previous);
            newOne = ClausewitzUtils.addQuotes(newOne);

            ClausewitzList reforms = reformStack.getList("reforms");

            if (reforms != null) {
                reforms.change(previous, newOne);
            }

            ClausewitzList history = reformStack.getList("history");

            if (history != null) {
                history.change(previous, newOne);
            }
        }
    }

    public void removeReform(String reform) {
        ClausewitzItem reformStack = this.item.getChild("reform_stack");

        if (reformStack != null) {
            ClausewitzList reforms = reformStack.getList("reforms");

            if (reforms != null) {
                reforms.removeLast(reform);
            }

            ClausewitzList history = reformStack.getList("history");

            if (history != null) {
                history.removeLast(reform);
            }
        }
    }
}
