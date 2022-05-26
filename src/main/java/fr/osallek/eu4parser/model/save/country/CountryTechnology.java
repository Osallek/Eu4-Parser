package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.common.NumbersUtils;
import fr.osallek.eu4parser.model.Power;
import fr.osallek.eu4parser.model.game.Modifier;
import fr.osallek.eu4parser.model.game.ModifiersUtils;
import fr.osallek.eu4parser.model.game.Technology;
import fr.osallek.eu4parser.model.save.Save;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public record CountryTechnology(Save save, ClausewitzItem item) {

    public Integer getAdm() {
        return this.item.getVarAsInt("adm_tech");
    }

    public void setAdm(Integer adm) {
        this.item.setVariable("adm_tech", adm);
    }

    public Integer getDip() {
        return this.item.getVarAsInt("dip_tech");
    }

    public void setDip(Integer dip) {
        this.item.setVariable("dip_tech", dip);
    }

    public Integer getMil() {
        return this.item.getVarAsInt("mil_tech");
    }

    public void setMil(Integer mil) {
        this.item.setVariable("mil_tech", mil);
    }

    public Integer getTotal() {
        return NumbersUtils.intOrDefault(getAdm()) + NumbersUtils.intOrDefault(getDip()) + NumbersUtils.intOrDefault(getMil());
    }

    public Double getModifier(Power power, Integer level, Modifier modifier) {
        List<Double> modifiers = IntStream.rangeClosed(0, level)
                                          .mapToObj(i -> this.save.getGame().getTechnology(power, i))
                                          .filter(Objects::nonNull)
                                          .map(Technology::getModifiers)
                                          .filter(Objects::nonNull)
                                          .filter(m -> m.hasModifier(modifier))
                                          .map(m -> m.getModifier(modifier))
                                          .toList();

        if (this.save.getGame().getTechnology(power, level).getYear() > this.save.getDate().getYear()
            && this.save.getGame().getTechnology(power, level).getAheadOfTime().hasModifier(modifier)) {
            modifiers.add(this.save.getGame().getTechnology(power, level).getAheadOfTime().getModifier(modifier));
        }

        return ModifiersUtils.sumModifiers(modifier, modifiers);
    }
}
