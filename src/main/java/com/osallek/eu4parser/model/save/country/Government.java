package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.model.game.Game;
import com.osallek.eu4parser.model.game.GovernmentReform;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Government {

    private final Game game;

    private final ClausewitzItem item;

    public Government(ClausewitzItem item, Game game) {
        this.game = game;
        this.item = item;
    }

    public String getType() {
        return this.item.getVarAsString("government");
    }

    public void setType(String type) {
        this.item.setVariable("government", type);
    }

    public List<GovernmentReform> getReforms() {
        ClausewitzItem reformStack = this.item.getChild("reform_stack");

        if (reformStack != null) {
            ClausewitzList list = reformStack.getList("reforms");

            if (list != null) {
                return list.getValues().stream().map(ClausewitzUtils::removeQuotes).map(this.game::getGovernmentReform).collect(Collectors.toList());
            }
        }

        return new ArrayList<>();
    }

    public List<GovernmentReform> getHistory() {
        ClausewitzItem reformStack = this.item.getChild("reform_stack");

        if (reformStack != null) {
            ClausewitzList list = reformStack.getList("history");

            if (list != null) {
                return list.getValues().stream().map(ClausewitzUtils::removeQuotes).map(this.game::getGovernmentReform).collect(Collectors.toList());
            }
        }

        return new ArrayList<>();
    }

    public void addReform(GovernmentReform reform) {
        ClausewitzItem reformStack = this.item.getChild("reform_stack");

        if (reformStack == null) {
            reformStack = this.item.addChild("reform_stack");
        }

        ClausewitzList reforms = reformStack.getList("reforms");

        if (reforms != null) {
            reforms.add(ClausewitzUtils.addQuotes(reform.getName()));
        } else {
            reformStack.addList("reforms", ClausewitzUtils.addQuotes(reform.getName()));
        }

        ClausewitzList history = reformStack.getList("history");

        if (history != null) {
            history.add(ClausewitzUtils.addQuotes(reform.getName()));
        } else {
            reformStack.addList("history", ClausewitzUtils.addQuotes(reform.getName()));
        }
    }

    public void changeReform(GovernmentReform previous, GovernmentReform newOne) {
        ClausewitzItem reformStack = this.item.getChild("reform_stack");

        if (reformStack != null) {
            ClausewitzList reforms = reformStack.getList("reforms");

            if (reforms != null) {
                reforms.change(ClausewitzUtils.addQuotes(previous.getName()), ClausewitzUtils.addQuotes(newOne.getName()));
            }

            ClausewitzList history = reformStack.getList("history");

            if (history != null) {
                history.change(ClausewitzUtils.addQuotes(previous.getName()), ClausewitzUtils.addQuotes(newOne.getName()));
            }
        }
    }

    public void removeReform(GovernmentReform reform) {
        ClausewitzItem reformStack = this.item.getChild("reform_stack");

        if (reformStack != null) {
            ClausewitzList reforms = reformStack.getList("reforms");

            if (reforms != null) {
                reforms.removeLast(ClausewitzUtils.addQuotes(reform.getName()));
            }

            ClausewitzList history = reformStack.getList("history");

            if (history != null) {
                history.removeLast(ClausewitzUtils.addQuotes(reform.getName()));
            }
        }
    }

    public boolean hasMechanic(String mechanic) {
        return this.item.hasChild(mechanic);
    }
}
