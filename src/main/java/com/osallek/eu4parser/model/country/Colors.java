package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.model.Color;

public class Colors {

    private final ClausewitzItem item;

    private Color revolutionaryColors;

    private Color mapColor;

    private Color countryColor;

    public Colors(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public Color getRevolutionaryColors() {
        return revolutionaryColors;
    }

    public Color getMapColor() {
        return mapColor;
    }

    public Color getCountryColor() {
        return countryColor;
    }

    private void refreshAttributes() {
        ClausewitzList revolutionaryColorsList = this.item.getList("revolutionary_colors");

        if (revolutionaryColorsList != null) {
            this.revolutionaryColors = new Color(revolutionaryColorsList);
        }

        ClausewitzList mapColorList = this.item.getList("map_color");

        if (mapColorList != null) {
            this.mapColor = new Color(mapColorList);
        }

        ClausewitzList countryColorList = this.item.getList("country_color");

        if (countryColorList != null) {
            this.countryColor = new Color(countryColorList);
        }
    }
}
