package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class ProvinceRevolt {

    private final String type;

    private final String leader;

    private Integer size;

    public ProvinceRevolt(ClausewitzItem item) {
        this.type = item.getVarAsString("type");
        this.leader = item.getVarAsString("leader");

        if (item.hasVar("size")) {
            this.size = item.getVarAsInt("size");
        }
    }

    public String getType() {
        return type;
    }

    public String getLeader() {
        return leader;
    }

    public int getSize() {
        return size;
    }

    public boolean isClearing() {
        return this.size == null;
    }
}
