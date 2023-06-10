package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.Color;

import java.util.Optional;

public class CustomColors {

    private final ClausewitzItem item;

    public CustomColors(ClausewitzItem item) {
        this.item = item;
    }

    public Optional<Color> getFlagColors() {
        return this.item.getList("flag_colors").map(Color::new);
    }

    public Optional<Integer> getFlag() {
        return this.item.getVarAsInt("flag");
    }

    public void setFlag(int flag) {
        this.item.setVariable("flag", flag);
    }

    public Optional<Integer> getColor() {
        return this.item.getVarAsInt("color").filter(integer -> -1 != integer);
    }

    public void setColor(Integer color) {
        this.item.setVariable("color", color == null ? -1 : color);
    }

    public Optional<Integer> getSymbolIndex() {
        return this.item.getVarAsInt("symbol_index");
    }

    public void setSymbolIndex(int symbolIndex) {
        this.item.setVariable("symbol_index", symbolIndex);
    }
}
