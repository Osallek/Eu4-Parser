package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.IntStream;

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
        return this.item.getChild("rank").map(child -> {
            SortedMap<Integer, String> ranks = new TreeMap<>();
            IntStream.rangeClosed(1, child.getNbVariables()).forEach(i -> child.getVar(String.valueOf(i)).ifPresent(v -> ranks.putIfAbsent(i, v.getValue())));
            return ranks;
        }).orElse(new TreeMap<>());
    }

    public String getRank(int level) {
        return getRanks().get(level);
    }

    public void setRanks(SortedMap<Integer, String> ranks) {
        ClausewitzItem child = this.item.getChild("rank").orElse(this.item.addChild("rank"));
        child.removeAllVariables();

        for (Map.Entry<Integer, String> entry : ranks.entrySet()) {
            child.addVariable(String.valueOf(entry.getKey()), entry.getValue());
        }
    }

    public Map<Integer, String> getRulersMale() {
        return this.item.getChild("ruler_male").map(child -> {
            SortedMap<Integer, String> rulers = new TreeMap<>();
            IntStream.rangeClosed(1, child.getNbVariables()).forEach(i -> child.getVar(String.valueOf(i)).ifPresent(v -> rulers.putIfAbsent(i, v.getValue())));
            return rulers;
        }).orElse(new TreeMap<>());
    }

    public String getRulerMale(int level) {
        return getRulersMale().get(level);
    }

    public void setRulersMale(SortedMap<Integer, String> rulers) {
        ClausewitzItem child = this.item.getChild("ruler_male").orElse(this.item.addChild("ruler_male"));
        child.removeAllVariables();

        for (Map.Entry<Integer, String> entry : rulers.entrySet()) {
            child.addVariable(String.valueOf(entry.getKey()), entry.getValue());
        }
    }

    public Map<Integer, String> getRulersFemale() {
        return this.item.getChild("ruler_female").map(child -> {
            SortedMap<Integer, String> rulers = new TreeMap<>();
            IntStream.rangeClosed(1, child.getNbVariables()).forEach(i -> child.getVar(String.valueOf(i)).ifPresent(v -> rulers.putIfAbsent(i, v.getValue())));
            return rulers;
        }).orElse(new TreeMap<>());
    }

    public String getRulerFemale(int level) {
        return getRulersFemale().get(level);
    }

    public void setRulersFemale(SortedMap<Integer, String> rulers) {
        ClausewitzItem child = this.item.getChild("ruler_female").orElse(this.item.addChild("ruler_female"));
        child.removeAllVariables();

        for (Map.Entry<Integer, String> entry : rulers.entrySet()) {
            child.addVariable(String.valueOf(entry.getKey()), entry.getValue());
        }
    }

    public Map<Integer, String> getConsortsMale() {
        return this.item.getChild("consort_male").map(child -> {
            SortedMap<Integer, String> rulers = new TreeMap<>();
            IntStream.rangeClosed(1, child.getNbVariables()).forEach(i -> child.getVar(String.valueOf(i)).ifPresent(v -> rulers.putIfAbsent(i, v.getValue())));
            return rulers;
        }).orElse(new TreeMap<>());
    }

    public String getConsortMale(int level) {
        return getConsortsMale().get(level);
    }

    public void setConsortsMale(SortedMap<Integer, String> consorts) {
        ClausewitzItem child = this.item.getChild("consort_male").orElse(this.item.addChild("consort_male"));
        child.removeAllVariables();

        for (Map.Entry<Integer, String> entry : consorts.entrySet()) {
            child.addVariable(String.valueOf(entry.getKey()), entry.getValue());
        }
    }

    public Map<Integer, String> getConsortsFemale() {
        return this.item.getChild("consort_female").map(child -> {
            SortedMap<Integer, String> rulers = new TreeMap<>();
            IntStream.rangeClosed(1, child.getNbVariables()).forEach(i -> child.getVar(String.valueOf(i)).ifPresent(v -> rulers.putIfAbsent(i, v.getValue())));
            return rulers;
        }).orElse(new TreeMap<>());
    }

    public String getConsortFemale(int level) {
        return getConsortsFemale().get(level);
    }

    public void setConsortsFemale(SortedMap<Integer, String> consorts) {
        ClausewitzItem child = this.item.getChild("consort_female").orElse(this.item.addChild("consort_female"));
        child.removeAllVariables();

        for (Map.Entry<Integer, String> entry : consorts.entrySet()) {
            child.addVariable(String.valueOf(entry.getKey()), entry.getValue());
        }
    }

    public Map<Integer, String> getHeirsMale() {
        return this.item.getChild("heir_male").map(child -> {
            SortedMap<Integer, String> rulers = new TreeMap<>();
            IntStream.rangeClosed(1, child.getNbVariables()).forEach(i -> child.getVar(String.valueOf(i)).ifPresent(v -> rulers.putIfAbsent(i, v.getValue())));
            return rulers;
        }).orElse(new TreeMap<>());
    }

    public String getHeirMale(int level) {
        return getHeirsMale().get(level);
    }

    public void setHeirsMale(SortedMap<Integer, String> heirs) {
        ClausewitzItem child = this.item.getChild("heir_male").orElse(this.item.addChild("heir_male"));
        child.removeAllVariables();

        for (Map.Entry<Integer, String> entry : heirs.entrySet()) {
            child.addVariable(String.valueOf(entry.getKey()), entry.getValue());
        }
    }

    public Map<Integer, String> getHeirsFemale() {
        return this.item.getChild("heir_female").map(child -> {
            SortedMap<Integer, String> rulers = new TreeMap<>();
            IntStream.rangeClosed(1, child.getNbVariables()).forEach(i -> child.getVar(String.valueOf(i)).ifPresent(v -> rulers.putIfAbsent(i, v.getValue())));
            return rulers;
        }).orElse(new TreeMap<>());
    }

    public String getHeirFemale(int level) {
        return getHeirsFemale().get(level);
    }

    public void setHeirsFemale(SortedMap<Integer, String> heirs) {
        ClausewitzItem child = this.item.getChild("heir_female").orElse(this.item.addChild("heir_female"));
        child.removeAllVariables();

        for (Map.Entry<Integer, String> entry : heirs.entrySet()) {
            child.addVariable(String.valueOf(entry.getKey()), entry.getValue());
        }
    }

    public Optional<ConditionAnd> getTrigger() {
        return this.item.getChild("trigger").map(ConditionAnd::new);
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
