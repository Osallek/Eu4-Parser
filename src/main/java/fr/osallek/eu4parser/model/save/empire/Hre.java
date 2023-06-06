package fr.osallek.eu4parser.model.save.empire;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.model.game.ImperialReform;
import fr.osallek.eu4parser.model.game.ProvinceList;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Hre extends Empire {

    private HreIncident activeIncident;

    public Hre(ClausewitzItem item, Save save) {
        super(item, save);
    }

    @Override
    protected String getId() {
        return "hre";
    }

    @Override
    public void dismantle() {
        super.dismantle();
        this.item.removeList("electors");
        this.item.removeList("active_incident");
    }

    @Override
    public void addPassedReform(ImperialReform reform) {
        super.addPassedReform(reform);

        if (reform.getOnEffect().filter(e -> e.isImperialBanAllowed().filter(BooleanUtils::toBoolean).isPresent()).isPresent()) {
            setImperialBanAllowed(true);
        }

        if (reform.getOnEffect().filter(e -> e.isInternalHreCb().filter(BooleanUtils::toBoolean).isPresent()).isPresent()) {
            setInternalHreCb(true);
        }

        if (reform.getOnEffect().filter(e -> e.hreInheritable().filter(BooleanUtils::toBoolean).isPresent()).isPresent()) {
            setHreInheritable(true);
        }

        if (reform.getOnEffect().filter(e -> e.enableImperialRealmWar().filter(BooleanUtils::toBoolean).isPresent()).isPresent()) {
            setImperialBanAllowed(true);
        }
    }

    @Override
    public void removePassedReform(ImperialReform reform) {
        super.removePassedReform(reform);

        if (reform.getOnEffect().filter(e -> e.isImperialBanAllowed().filter(BooleanUtils::isFalse).isPresent()).isPresent()) {
            setImperialBanAllowed(false);
        }

        if (reform.getOnEffect().filter(e -> e.isInternalHreCb().filter(BooleanUtils::isFalse).isPresent()).isPresent()) {
            setInternalHreCb(false);
        }

        if (reform.getOnEffect().filter(e -> e.hreInheritable().filter(BooleanUtils::isFalse).isPresent()).isPresent()) {
            setHreInheritable(false);
        }

        if (reform.getOnEffect().filter(e -> e.enableImperialRealmWar().filter(BooleanUtils::isFalse).isPresent()).isPresent()) {
            setImperialBanAllowed(false);
        }
    }

    public Optional<ProvinceList> getContinent() {
        if (dismantled()) {
            return Optional.empty();
        }

        return this.item.getVarAsInt("continent").map(i -> this.save.getGame().getContinent(i));
    }

    public void setContinent(int continent) {
        if (dismantled()) {
            return;
        }

        this.item.setVariable("continent", continent);
    }

    public Optional<Boolean> getImperialBanAllowed() {
        if (dismantled()) {
            return Optional.empty();
        }

        return this.item.getVarAsBool("imperial_ban_allowed");
    }

    public void setImperialBanAllowed(boolean imperialBanAllowed) {
        if (dismantled()) {
            return;
        }

        ClausewitzVariable var = this.item.setVariable("imperial_ban_allowed", imperialBanAllowed);
    }

    public Optional<Boolean> getInternalHreCb() {
        if (dismantled()) {
            return Optional.empty();
        }

        return this.item.getVarAsBool("internal_hre_cb");
    }

    public void setInternalHreCb(boolean internalHreCb) {
        if (dismantled()) {
            return;
        }

        this.item.setVariable("internal_hre_cb", internalHreCb);
    }

    public Optional<Boolean> getHreInheritable() {
        if (dismantled()) {
            return Optional.empty();
        }

        return this.item.getVarAsBool("hre_inheritable");
    }

    public void setHreInheritable(boolean hreInheritable) {
        if (dismantled()) {
            return;
        }

        this.item.setVariable("hre_inheritable", hreInheritable);
    }

    public Optional<Boolean> getAllowsFemaleEmperor() {
        if (dismantled()) {
            return Optional.empty();
        }

        return this.item.getVarAsBool("allows_female_emperor");
    }

    public void setAllowsFemaleEmperor(boolean allowsFemaleEmperor) {
        if (dismantled()) {
            return;
        }

        ClausewitzVariable var = this.item.setVariable("allows_female_emperor", allowsFemaleEmperor);
    }

    public List<SaveCountry> getElectors() {
        if (dismantled()) {
            return new ArrayList<>();
        }

        return this.item.getList("electors").map(ClausewitzList::getValues).stream().flatMap(Collection::stream).map(this.save::getCountry).toList();
    }

    public void setElectors(List<SaveCountry> electors) {
        this.item.getList("electors").ifPresent(ClausewitzList::clear);
        electors.forEach(this::addElector);
    }

    public void addElector(SaveCountry country) {
        if (dismantled()) {
            return;
        }

        String tag = country.getTag();
        this.item.getList("electors").ifPresentOrElse(list -> {
            if (!list.contains(tag)) {
                list.add(tag);
            }
        }, () -> this.item.addList("electors", tag));
    }

    public void removeElector(SaveCountry country) {
        if (dismantled()) {
            return;
        }

        this.item.getList("electors").ifPresent(list -> list.remove(country.getTag()));
    }

    public Optional<Integer> getEmperorPreviousRank() {
        if (dismantled()) {
            return Optional.empty();
        }

        return this.item.getVarAsInt("emperor_previous_rank");
    }

    public void setEmperorPreviousRank(int id) {
        if (dismantled()) {
            return;
        }

        this.item.setVariable("emperor_previous_rank", id);
    }

    public Optional<Boolean> getImperialRealmWar() {
        if (dismantled()) {
            return Optional.empty();
        }

        return this.item.getVarAsBool("imperial_realm_war");
    }

    public void setImperialRealmWar(boolean imperialRealmWar) {
        if (dismantled()) {
            return;
        }

        this.item.setVariable("imperial_realm_war", imperialRealmWar);
    }

    public HreIncident getActiveIncident() {
        return activeIncident;
    }

    public void removeActiveIncident() {
        this.item.removeChild("active_incident");
        refreshAttributes();
    }

    @Override
    protected void refreshAttributes() {
        super.refreshAttributes();
        this.activeIncident = this.item.getChild("active_incident").map(HreIncident::new).orElse(null);
    }
}
