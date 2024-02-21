package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.Color;

public class Colors {

    private final ClausewitzItem item;

    public Colors(ClausewitzItem item) {
        this.item = item;
    }

    public CustomColors getCustomColors() {
        ClausewitzItem customColorsItem = this.item.getChild("custom_colors");

        return customColorsItem != null ? new CustomColors(customColorsItem) : null;
    }

    public Color getRevolutionaryColors() {
        ClausewitzList revolutionaryColorsList = this.item.getList("revolutionary_colors");

        return revolutionaryColorsList != null ? new Color(revolutionaryColorsList) : null;
    }

    public Color getMapColor() {
        ClausewitzList mapColorList = this.item.getList("map_color");

        return mapColorList != null ? new Color(mapColorList) : null;
    }

    public void setMapColor(int red, int green, int blue) {
        Color mapColor = getMapColor();
        if (mapColor != null) {
            mapColor.setRed(red);
            mapColor.setGreen(green);
            mapColor.setBlue(blue);
        } else {
            Color.addToItem(this.item, "map_color", red, green, blue);
        }
    }

    public Color getCountryColor() {
        ClausewitzList countryColorList = this.item.getList("country_color");

        return countryColorList != null ? new Color(countryColorList) : null;
    }
}
