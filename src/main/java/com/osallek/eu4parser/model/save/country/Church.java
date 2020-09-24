package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.game.Game;

import java.util.List;

public class Church {

    private final Game game;

    private final ClausewitzItem item;

    public Church(ClausewitzItem item, Game game) {
        this.game = game;
        this.item = item;
    }

    public Double getPower() {
        return this.item.getVarAsDouble("power");
    }

    public void setPower(Double power) {
        this.item.setVariable("power", power);
    }

    public List<String> getAspects() {
        return this.item.getVarsAsStrings("aspect");
    }

    public void addAspect(String aspect) {
        List<String> aspects = this.item.getVarsAsStrings("aspect");

        if (!aspects.contains(aspect)) {
            if (aspects.size() >= this.game.getMaxAspects()) {
                removeAspect(aspects.get(aspects.size() - 1));
            }

            this.item.addVariable("aspect", aspect);
        }
    }

    public void removeAspect(String aspect) {
        this.item.removeVariable("aspect", aspect);
    }

}
