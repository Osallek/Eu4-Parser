package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Optional;

public record CustomNationalIdea(ClausewitzItem item) {

    public Optional<Integer> getLevel() {
        return this.item.getVarAsInt("level");
    }

    public void setProsperity(int level) {
        this.item.setVariable("level", Math.max(0, level));
    }

    public Optional<Integer> getIndex() {
        return this.item.getVarAsInt("index");
    }

    public void setIndex(int index) {
        this.item.setVariable("index", index);
    }

    public Optional<String> getCountry() {
        return this.item.getVarAsString("country");
    }

    public Optional<String> getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", name);
    }

    public Optional<String> getDesc() {
        return this.item.getVarAsString("desc");
    }

    public void setDesc(String desc) {
        this.item.setVariable("desc", desc);
    }
}
