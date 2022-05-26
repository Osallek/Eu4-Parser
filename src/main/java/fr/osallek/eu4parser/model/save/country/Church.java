package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.ChurchAspect;
import fr.osallek.eu4parser.model.game.Game;

import java.util.List;
import java.util.Objects;

public record Church(ClausewitzItem item, Game game) {

    public Double getPower() {
        return this.item.getVarAsDouble("power");
    }

    public void setPower(Double power) {
        this.item.setVariable("power", power);
    }

    public List<ChurchAspect> getAspects() {
        return this.item.getVarsAsStrings("aspect").stream().map(this.game::getChurchAspect).filter(Objects::nonNull).toList();
    }

    public void addAspect(ChurchAspect aspect) {
        List<String> aspects = this.item.getVarsAsStrings("aspect");

        if (!aspects.contains(aspect.getName())) {
            if (aspects.size() >= this.game.getMaxAspects()) {
                removeAspect(aspects.get(aspects.size() - 1));
            }

            this.item.addVariable("aspect", aspect.getName());
        }
    }

    public void removeAspect(String aspect) {
        this.item.removeVariable("aspect", aspect);
    }

}
