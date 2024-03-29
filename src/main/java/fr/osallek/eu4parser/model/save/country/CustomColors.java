package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.Color;

public class CustomColors {

    private final ClausewitzItem item;

    public CustomColors(ClausewitzItem item) {
        this.item = item;
    }

    public Color getFlagColors() {
        ClausewitzList flagColorsList = this.item.getList("flag_colors");

        return flagColorsList != null ? new Color(flagColorsList) : null;
    }

    public Integer getFlag() {
        return this.item.getVarAsInt("flag");
    }

    public void setFlag(int flag) {
        this.item.setVariable("flag", flag);
    }

    public Integer getColor() {
        Integer color = this.item.getVarAsInt("color");

        return color == null ? null : color == -1 ? null : color;
    }

    public void setColor(Integer color) {
        this.item.setVariable("color", color == null ? -1 : color);
    }

    public Integer getSymbolIndex() {
        return this.item.getVarAsInt("symbol_index");
    }

    public void setSymbolIndex(int symbolIndex) {
        this.item.setVariable("symbol_index", symbolIndex);
    }
}
