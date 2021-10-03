package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.common.Eu4Utils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class GovernmentName {

    private final String name;

    private Map<Integer, Pair<String, String>> ranks;

    private Map<Integer, Pair<String, String>> rulersMale;

    private Map<Integer, Pair<String, String>> rulersFemale;

    private Map<Integer, Pair<String, String>> consortsMale;

    private Map<Integer, Pair<String, String>> consortsFemale;

    private Map<Integer, Pair<String, String>> heirsMale;

    private Map<Integer, Pair<String, String>> heirsFemale;

    private Condition trigger;

    public GovernmentName(ClausewitzItem item, Game game) {
        this.name = item.getName();

        ClausewitzItem child;
        ClausewitzVariable variable;

        if ((child = item.getChild("rank")) != null) {
            this.ranks = new TreeMap<>();

            for (int i = 1; i <= game.getMaxGovRank(); i++) {
                if ((variable = child.getVar(String.valueOf(i))) != null) {
                    this.ranks.putIfAbsent(i, new ImmutablePair<>(variable.getValue(), game.getLocalisation(variable.getValue())));
                }
            }
        }

        if ((child = item.getChild("ruler_male")) != null) {
            this.rulersMale = new TreeMap<>();

            for (int i = 1; i <= game.getMaxGovRank(); i++) {
                if ((variable = child.getVar(String.valueOf(i))) != null) {
                    this.rulersMale.putIfAbsent(i, new ImmutablePair<>(variable.getValue(), game.getLocalisation(variable.getValue())));
                }
            }
        }

        if ((child = item.getChild("ruler_female")) != null) {
            this.rulersFemale = new TreeMap<>();

            for (int i = 1; i <= game.getMaxGovRank(); i++) {
                if ((variable = child.getVar(String.valueOf(i))) != null) {
                    this.rulersFemale.putIfAbsent(i, new ImmutablePair<>(variable.getValue(), game.getLocalisation(variable.getValue())));
                }
            }
        }

        if ((child = item.getChild("consort_male")) != null) {
            this.consortsMale = new TreeMap<>();

            for (int i = 1; i <= game.getMaxGovRank(); i++) {
                if ((variable = child.getVar(String.valueOf(i))) != null) {
                    this.consortsMale.putIfAbsent(i, new ImmutablePair<>(variable.getValue(), game.getLocalisation(variable.getValue())));
                }
            }
        }

        if ((child = item.getChild("consort_female")) != null) {
            this.consortsFemale = new TreeMap<>();

            for (int i = 1; i <= game.getMaxGovRank(); i++) {
                if ((variable = child.getVar(String.valueOf(i))) != null) {
                    this.consortsFemale.putIfAbsent(i, new ImmutablePair<>(variable.getValue(), game.getLocalisation(variable.getValue())));
                }
            }
        }

        if ((child = item.getChild("heir_male")) != null) {
            this.heirsMale = new TreeMap<>();

            for (int i = 1; i <= game.getMaxGovRank(); i++) {
                if ((variable = child.getVar(String.valueOf(i))) != null) {
                    this.heirsMale.putIfAbsent(i, new ImmutablePair<>(variable.getValue(), game.getLocalisation(variable.getValue())));
                }
            }
        }

        if ((child = item.getChild("heir_female")) != null) {
            this.heirsFemale = new TreeMap<>();

            for (int i = 1; i <= game.getMaxGovRank(); i++) {
                if ((variable = child.getVar(String.valueOf(i))) != null) {
                    this.heirsFemale.putIfAbsent(i, new ImmutablePair<>(variable.getValue(), game.getLocalisation(variable.getValue())));
                }
            }
        }

        if ((child = item.getChild("trigger")) != null) {
            this.trigger = new Condition(child);
        }
    }

    public void merge(GovernmentName governmentName) {
        this.ranks = Eu4Utils.mergeMaps(this.ranks, governmentName.ranks);
        this.rulersMale = Eu4Utils.mergeMaps(this.rulersMale, governmentName.rulersMale);
        this.rulersFemale = Eu4Utils.mergeMaps(this.rulersFemale, governmentName.rulersFemale);
        this.consortsMale = Eu4Utils.mergeMaps(this.consortsMale, governmentName.consortsMale);
        this.consortsFemale = Eu4Utils.mergeMaps(this.consortsFemale, governmentName.consortsFemale);
        this.heirsMale = Eu4Utils.mergeMaps(this.heirsMale, governmentName.heirsMale);
        this.heirsFemale = Eu4Utils.mergeMaps(this.heirsFemale, governmentName.heirsFemale);
    }

    public String getName() {
        return name;
    }

    public Map<Integer, Pair<String, String>> getRanks() {
        return ranks;
    }

    public Pair<String, String> getRank(int level) {
        return this.ranks.get(level);
    }

    public Map<Integer, Pair<String, String>> getRulersMale() {
        return rulersMale;
    }

    public Pair<String, String> getRulerMale(int level) {
        return this.rulersMale.get(level);
    }

    public Map<Integer, Pair<String, String>> getRulersFemale() {
        return rulersFemale;
    }

    public Pair<String, String> getRulerFemale(int level) {
        return this.rulersFemale.get(level);
    }

    public Map<Integer, Pair<String, String>> getConsortsMale() {
        return consortsMale;
    }

    public Pair<String, String> getConsortMale(int level) {
        return this.consortsMale.get(level);
    }

    public Map<Integer, Pair<String, String>> getConsortsFemale() {
        return consortsFemale;
    }

    public Pair<String, String> getConsortFemale(int level) {
        return this.consortsFemale.get(level);
    }

    public Map<Integer, Pair<String, String>> getHeirsMale() {
        return heirsMale;
    }

    public Pair<String, String> getHeirMale(int level) {
        return this.heirsMale.get(level);
    }

    public Map<Integer, Pair<String, String>> getHeirsFemale() {
        return heirsFemale;
    }

    public Pair<String, String> getHeirFemale(int level) {
        return this.heirsFemale.get(level);
    }

    public Condition getTrigger() {
        return trigger;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof GovernmentName)) {
            return false;
        }

        GovernmentName that = (GovernmentName) o;
        return Objects.equals(getName(), that.getName());
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
