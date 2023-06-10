package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.Color;

import java.util.Optional;

public class Colors {

    private final ClausewitzItem item;

    public Colors(ClausewitzItem item) {
        this.item = item;
    }

    public Optional<CustomColors> getCustomColors() {
        return this.item.getChild("custom_colors").map(CustomColors::new);
    }

    public Optional<Color> getRevolutionaryColors() {
        return this.item.getList("revolutionary_colors").map(Color::new);
    }

    public Optional<Color> getMapColor() {
        return this.item.getList("map_color").map(Color::new);
    }

    public void setMapColor(int red, int green, int blue) {
        Optional<Color> mapColor = getMapColor();
        if (mapColor.isPresent()) {
            mapColor.get().setRed(red);
            mapColor.get().setGreen(green);
            mapColor.get().setBlue(blue);
        } else {
            Color.addToItem(this.item, "map_color", red, green, blue);
        }
    }

    public Optional<Color> getCountryColor() {
        return this.item.getList("country_color").map(Color::new);
    }
}
