package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.common.ModifiersUtils;
import com.osallek.eu4parser.common.NumbersUtils;
import com.osallek.eu4parser.model.Power;
import com.osallek.eu4parser.model.game.Modifiers;
import com.osallek.eu4parser.model.game.Technology;
import com.osallek.eu4parser.model.save.Save;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Objects;
import java.util.stream.IntStream;

public class CountryTechnology {

    private final Save save;

    private final ClausewitzItem item;

    public CountryTechnology(Save save, ClausewitzItem item) {
        this.save = save;
        this.item = item;
    }

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

    public Modifiers getModifiers(Power power, Integer level) {
        Modifiers[] modifiers = IntStream.rangeClosed(0, level)
                                         .mapToObj(i -> this.save.getGame().getTechnology(power, i))
                                         .filter(Objects::nonNull)
                                         .map(Technology::getModifiers)
                                         .filter(Objects::nonNull)
                                         .map(Modifiers::getCountryModifiers)
                                         .toArray(Modifiers[]::new);

        return ModifiersUtils.sumModifiers(
                ArrayUtils.add(modifiers, this.save.getGame().getTechnology(power, level).getYear() > this.save.getDate().getYear()
                                          ? this.save.getGame().getTechnology(power, level).getAheadOfTime().getCountryModifiers() : null));
    }
}
