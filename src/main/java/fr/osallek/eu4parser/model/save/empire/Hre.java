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
import java.util.List;

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

        if (BooleanUtils.toBoolean(reform.getOnEffect().isImperialBanAllowed())) {
            this.setImperialBanAllowed(true);
        }

        if (BooleanUtils.toBoolean(reform.getOnEffect().isInternalHreCb())) {
            this.setInternalHreCb(true);
        }

        if (BooleanUtils.toBoolean(reform.getOnEffect().hreInheritable())) {
            this.setHreInheritable(true);
        }

        if (BooleanUtils.toBoolean(reform.getOnEffect().enableImperialRealmWar())) {
            this.setImperialBanAllowed(true);
        }
    }

    @Override
    public void removePassedReform(ImperialReform reform) {
        super.removePassedReform(reform);

        if (BooleanUtils.isFalse(reform.getOnEffect().isImperialBanAllowed())) {
            this.setImperialBanAllowed(false);
        }

        if (BooleanUtils.isFalse(reform.getOnEffect().isInternalHreCb())) {
            this.setInternalHreCb(false);
        }

        if (BooleanUtils.isFalse(reform.getOnEffect().hreInheritable())) {
            this.setHreInheritable(false);
        }

        if (BooleanUtils.isFalse(reform.getOnEffect().enableImperialRealmWar())) {
            this.setImperialBanAllowed(false);
        }
    }

    public ProvinceList getContinent() {
        if (dismantled()) {
            return null;
        }

        return this.save.getGame().getContinent(this.item.getVarAsInt("continent"));
    }

    public void setContinent(int continent) {
        if (dismantled()) {
            return;
        }

        this.item.setVariable("continent", continent);
    }

    public Boolean getImperialBanAllowed() {
        if (dismantled()) {
            return null;
        }

        return this.item.getVarAsBool("imperial_ban_allowed");
    }

    public void setImperialBanAllowed(boolean imperialBanAllowed) {
        if (dismantled()) {
            return;
        }

        ClausewitzVariable var = this.item.setVariable("imperial_ban_allowed", imperialBanAllowed);
    }

    public Boolean getInternalHreCb() {
        if (dismantled()) {
            return null;
        }

        return this.item.getVarAsBool("internal_hre_cb");
    }

    public void setInternalHreCb(boolean internalHreCb) {
        if (dismantled()) {
            return;
        }

        this.item.setVariable("internal_hre_cb", internalHreCb);
    }

    public Boolean getHreInheritable() {
        if (dismantled()) {
            return null;
        }

        return this.item.getVarAsBool("hre_inheritable");
    }

    public void setHreInheritable(boolean hreInheritable) {
        if (dismantled()) {
            return;
        }

        this.item.setVariable("hre_inheritable", hreInheritable);
    }

    public Boolean getAllowsFemaleEmperor() {
        if (dismantled()) {
            return null;
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

        ClausewitzList list = this.item.getList("electors");
        return list == null ? new ArrayList<>() : list.getValues()
                                                      .stream()
                                                      .map(this.save::getCountry)
                                                      .toList();
    }

    public void setElectors(List<SaveCountry> electors) {
        ClausewitzList list = this.item.getList("electors");

        if (list != null) {
            list.clear();
        }

        electors.forEach(this::addElector);
    }

    public void addElector(SaveCountry country) {
        if (dismantled()) {
            return;
        }

        String tag = country.getTag();
        ClausewitzList list = this.item.getList("electors");

        if (list != null) {
            if (!list.contains(tag)) {
                list.add(tag);
            }
        } else {
            this.item.addList("electors", tag);
        }
    }

    public void removeElector(SaveCountry country) {
        if (dismantled()) {
            return;
        }

        String tag = country.getTag();
        ClausewitzList list = this.item.getList("electors");

        if (list != null) {
            list.remove(tag);
        }
    }

    public Integer getEmperorPreviousRank() {
        if (dismantled()) {
            return null;
        }

        return this.item.getVarAsInt("emperor_previous_rank");
    }

    public void setEmperorPreviousRank(int id) {
        if (dismantled()) {
            return;
        }

        this.item.setVariable("emperor_previous_rank", id);
    }

    public Boolean getImperialRealmWar() {
        if (dismantled()) {
            return null;
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
        ClausewitzItem incidentItem = this.item.getChild("active_incident");

        if (incidentItem != null) {
            this.activeIncident = new HreIncident(incidentItem);
        }
    }
}
