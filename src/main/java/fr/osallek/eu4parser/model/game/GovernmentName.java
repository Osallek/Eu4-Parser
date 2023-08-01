package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;

import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

public class GovernmentName {

    private final ClausewitzItem item;

    public GovernmentName(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Map<Integer, String> getRanks() {
        ClausewitzItem child = this.item.getChild("rank");

        if (child != null) {
            SortedMap<Integer, String> ranks = new TreeMap<>();
            ClausewitzVariable variable;

            for (int i = 1; i <= child.getNbVariables(); i++) {
                if ((variable = child.getVar(String.valueOf(i))) != null) {
                    ranks.putIfAbsent(i, variable.getValue());
                }
            }

            return ranks;
        } else {
            return null;
        }
    }

    public String getRank(int level) {
        return getRanks().get(level);
    }

    public void setRanks(SortedMap<Integer, String> ranks) {
        ClausewitzItem child = this.item.getChild("rank");

        if (child == null) {
            child = this.item.addChild("rank");
        }

        child.removeAllVariables();

        for (Map.Entry<Integer, String> entry : ranks.entrySet()) {
            child.addVariable(String.valueOf(entry.getKey()), entry.getValue());
        }
    }

    public Map<Integer, String> getRulersMale() {
        ClausewitzItem child = this.item.getChild("ruler_male");

        if (child != null) {
            SortedMap<Integer, String> rulersMale = new TreeMap<>();
            ClausewitzVariable variable;

            for (int i = 1; i <= child.getNbVariables(); i++) {
                if ((variable = child.getVar(String.valueOf(i))) != null) {
                    rulersMale.putIfAbsent(i, variable.getValue());
                }
            }

            return rulersMale;
        } else {
            return null;
        }
    }

    public String getRulerMale(int level) {
        return getRulersMale().get(level);
    }

    public void setRulersMale(SortedMap<Integer, String> rulersMale) {
        ClausewitzItem child = this.item.getChild("ruler_male");

        if (child == null) {
            child = this.item.addChild("rank");
        }

        child.removeAllVariables();

        for (Map.Entry<Integer, String> entry : rulersMale.entrySet()) {
            child.addVariable(String.valueOf(entry.getKey()), entry.getValue());
        }
    }

    public Map<Integer, String> getRulersFemale() {
        ClausewitzItem child = this.item.getChild("ruler_female");

        if (child != null) {
            SortedMap<Integer, String> rulersFemale = new TreeMap<>();
            ClausewitzVariable variable;

            for (int i = 1; i <= child.getNbVariables(); i++) {
                if ((variable = child.getVar(String.valueOf(i))) != null) {
                    rulersFemale.putIfAbsent(i, variable.getValue());
                }
            }

            return rulersFemale;
        } else {
            return null;
        }
    }

    public String getRulerFemale(int level) {
        return getRulersFemale().get(level);
    }

    public void setRulersFemale(SortedMap<Integer, String> rulersFemale) {
        ClausewitzItem child = this.item.getChild("ruler_female");

        if (child == null) {
            child = this.item.addChild("rank");
        }

        child.removeAllVariables();

        for (Map.Entry<Integer, String> entry : rulersFemale.entrySet()) {
            child.addVariable(String.valueOf(entry.getKey()), entry.getValue());
        }
    }

    public Map<Integer, String> getConsortsMale() {
        ClausewitzItem child = this.item.getChild("consort_male");

        if (child != null) {
            SortedMap<Integer, String> consortsMale = new TreeMap<>();
            ClausewitzVariable variable;

            for (int i = 1; i <= child.getNbVariables(); i++) {
                if ((variable = child.getVar(String.valueOf(i))) != null) {
                    consortsMale.putIfAbsent(i, variable.getValue());
                }
            }

            return consortsMale;
        } else {
            return null;
        }
    }

    public String getConsortMale(int level) {
        return getConsortsMale().get(level);
    }

    public void setConsortsMale(SortedMap<Integer, String> consortsMale) {
        ClausewitzItem child = this.item.getChild("consort_male");

        if (child == null) {
            child = this.item.addChild("rank");
        }

        child.removeAllVariables();

        for (Map.Entry<Integer, String> entry : consortsMale.entrySet()) {
            child.addVariable(String.valueOf(entry.getKey()), entry.getValue());
        }
    }

    public Map<Integer, String> getConsortsFemale() {
        ClausewitzItem child = this.item.getChild("consort_female");

        if (child != null) {
            SortedMap<Integer, String> consortsFemale = new TreeMap<>();
            ClausewitzVariable variable;

            for (int i = 1; i <= child.getNbVariables(); i++) {
                if ((variable = child.getVar(String.valueOf(i))) != null) {
                    consortsFemale.putIfAbsent(i, variable.getValue());
                }
            }

            return consortsFemale;
        } else {
            return null;
        }
    }

    public String getConsortFemale(int level) {
        return getConsortsFemale().get(level);
    }

    public void setConsortsFemale(SortedMap<Integer, String> consortsFemale) {
        ClausewitzItem child = this.item.getChild("consort_female");

        if (child == null) {
            child = this.item.addChild("rank");
        }

        child.removeAllVariables();

        for (Map.Entry<Integer, String> entry : consortsFemale.entrySet()) {
            child.addVariable(String.valueOf(entry.getKey()), entry.getValue());
        }
    }

    public Map<Integer, String> getHeirsMale() {
        ClausewitzItem child = this.item.getChild("heir_male");

        if (child != null) {
            SortedMap<Integer, String> heirsMale = new TreeMap<>();
            ClausewitzVariable variable;

            for (int i = 1; i <= child.getNbVariables(); i++) {
                if ((variable = child.getVar(String.valueOf(i))) != null) {
                    heirsMale.putIfAbsent(i, variable.getValue());
                }
            }

            return heirsMale;
        } else {
            return null;
        }
    }

    public String getHeirMale(int level) {
        return getHeirsMale().get(level);
    }

    public void setHeirsMale(SortedMap<Integer, String> heirsMale) {
        ClausewitzItem child = this.item.getChild("heir_male");

        if (child == null) {
            child = this.item.addChild("rank");
        }

        child.removeAllVariables();

        for (Map.Entry<Integer, String> entry : heirsMale.entrySet()) {
            child.addVariable(String.valueOf(entry.getKey()), entry.getValue());
        }
    }

    public Map<Integer, String> getHeirsFemale() {
        ClausewitzItem child = this.item.getChild("heir_female");

        if (child != null) {
            SortedMap<Integer, String> heirsFemale = new TreeMap<>();
            ClausewitzVariable variable;

            for (int i = 1; i <= child.getNbVariables(); i++) {
                if ((variable = child.getVar(String.valueOf(i))) != null) {
                    heirsFemale.putIfAbsent(i, variable.getValue());
                }
            }

            return heirsFemale;
        } else {
            return null;
        }
    }

    public String getHeirFemale(int level) {
        return getHeirsFemale().get(level);
    }

    public void setHeirsFemale(SortedMap<Integer, String> heirsFemale) {
        ClausewitzItem child = this.item.getChild("heir_female");

        if (child == null) {
            child = this.item.addChild("rank");
        }

        child.removeAllVariables();

        for (Map.Entry<Integer, String> entry : heirsFemale.entrySet()) {
            child.addVariable(String.valueOf(entry.getKey()), entry.getValue());
        }
    }

    public ConditionAnd getTrigger() {
        ClausewitzItem child = this.item.getChild("trigger");
        return child == null ? null : new ConditionAnd(child);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof GovernmentName governmentName)) {
            return false;
        }

        return Objects.equals(getName(), governmentName.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
