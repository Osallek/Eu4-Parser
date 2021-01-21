package fr.osallek.eu4parser.model.save.empire;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.model.game.Continent;
import fr.osallek.eu4parser.model.game.ImperialReform;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.country.Country;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        if (reform.enableImperialBanAllowed()) {
            this.setImperialBanAllowed(true);
        }

        if (reform.enableInternalHreCb()) {
            this.setInternalHreCb(true);
        }

        if (reform.enableHreInheritable()) {
            this.setHreInheritable(true);
        }

        if (reform.enableEnableImperialRealmWar()) {
            this.setImperialBanAllowed(true);
        }
    }

    @Override
    public void removePassedReform(ImperialReform reform) {
        super.removePassedReform(reform);

        if (reform.disableImperialBanAllowed()) {
            this.setImperialBanAllowed(false);
        }

        if (reform.disableInternalHreCb()) {
            this.setInternalHreCb(false);
        }

        if (reform.disableHreInheritable()) {
            this.setHreInheritable(false);
        }

        if (reform.disableEnableImperialRealmWar()) {
            this.setImperialBanAllowed(false);
        }
    }

    public Continent getContinent() {
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

    public List<Country> getElectors() {
        if (dismantled()) {
            return new ArrayList<>();
        }

        ClausewitzList list = this.item.getList("electors");
        return list == null ? new ArrayList<>() : list.getValues()
                                                      .stream()
                                                      .map(this.save::getCountry)
                                                      .collect(Collectors.toList());
    }

    public void setElectors(List<Country> electors) {
        ClausewitzList list = this.item.getList("electors");

        if (list != null) {
            list.clear();
        }

        electors.forEach(this::addElector);
    }

    public void addElector(Country country) {
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

    public void removeElector(Country country) {
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

    protected void refreshAttributes() {
        ClausewitzItem incidentItem = this.item.getChild("active_incident");

        if (incidentItem != null) {
            this.activeIncident = new HreIncident(incidentItem);
        }
    }
}
