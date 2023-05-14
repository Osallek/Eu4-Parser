package fr.osallek.eu4parser.model.save.religion;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ColoniesClaims(ClausewitzList list) {

    public List<String> getColonyClaims() {
        return this.list == null ? new ArrayList<>() : this.list.getValues();
    }

    public Optional<String> getColonyClaim(int index) {
        return Optional.ofNullable(this.list).flatMap(l -> l.get(index));
    }

    public void setColonyClaim(int index, SaveCountry country) {
        if (this.list != null) {
            this.list.set(index, ClausewitzUtils.isBlank(country.getTag()) ? Eu4Utils.DEFAULT_TAG : country.getTag());
        }
    }

    public void removeColonyClaim(int index) {
        if (this.list != null) {
            this.list.set(index, Eu4Utils.DEFAULT_TAG);
        }
    }
}
