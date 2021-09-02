package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.lang3.StringUtils;

public class ProvinceRevolt {

    private final ClausewitzItem item;

    public ProvinceRevolt(ClausewitzItem item) {
        this.item = item;
    }

    public String getType() {
        return this.item.getVarAsString("type");
    }

    public void setType(String type) {
        this.item.setVariable("type", type);
    }

    public String getLeader() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("leader"));
    }

    public void setLeader(String leader) {
        if (StringUtils.isBlank(leader)) {
            this.item.removeVariable("leader");
        } else {
            this.item.setVariable("leader", ClausewitzUtils.addQuotes(leader));
        }
    }

    public Integer getSize() {
        return this.item.getVarAsInt("size");
    }

    public void setSize(Integer size) {
        if (size == null) {
            this.item.removeVariable("size");
        } else {
            this.item.setVariable("size", size);
        }
    }

    public boolean isClearing() {
        return getSize() == null;
    }
}
