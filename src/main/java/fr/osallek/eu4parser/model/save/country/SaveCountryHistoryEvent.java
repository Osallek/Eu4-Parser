package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.Color;
import fr.osallek.eu4parser.model.Power;
import fr.osallek.eu4parser.model.game.IdeaGroup;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SaveCountryHistoryEvent {

    protected final ClausewitzItem item;

    protected final SaveCountry country;

    public SaveCountryHistoryEvent(ClausewitzItem item, SaveCountry country) {
        this.item = item;
        this.country = country;
    }

    public LocalDate getDate() {
        return Eu4Utils.stringToDate(this.item.getName());
    }

    public Boolean getAbolishedSerfdom() {
        return this.item.getLastVarAsBool("abolished_serfdom");
    }

    public Leader getLeader() {
        if (this.item.hasChild("leader")) {
            return new Leader(this.item.getLastChild("leader"), this.country);
        }

        return null;
    }

    public Map<String, Boolean> getIdeasLevel() {
        return this.country.getSave()
                           .getGame()
                           .getIdeaGroups()
                           .stream()
                           .map(IdeaGroup::getIdeas)
                           .map(Map::keySet)
                           .flatMap(Collection::stream)
                           .filter(this.item::hasVar)
                           .collect(Collectors.toMap(Function.identity(), this.item::getVarAsBool));
    }

    public List<String> getAddAcceptedCultures() {
        return this.item.getVarsAsStrings("add_accepted_culture").stream().map(ClausewitzUtils::removeQuotes).toList();
    }

    public List<String> getRemoveAcceptedCultures() {
        return this.item.getVarsAsStrings("remove_accepted_culture").stream().map(ClausewitzUtils::removeQuotes).toList();
    }

    public Integer getGovernmentRank() {
        return this.item.getLastVarAsInt("government_rank");
    }

    public Integer getCapital() {
        return this.item.getLastVarAsInt("capital");
    }

    public String getChangedCountryNameFrom() {
        return ClausewitzUtils.removeQuotes(this.item.getLastVarAsString("changed_country_name_from"));
    }

    public String getChangedCountryAdjectiveFrom() {
        return ClausewitzUtils.removeQuotes(this.item.getLastVarAsString("changed_country_adjective_from"));
    }

    public Color getChangedCountryMapcolorFrom() {
        if (this.item.hasList("changed_country_mapcolor_from")) {
            return new Color(this.item.getList("changed_country_mapcolor_from"));
        }

        return null;
    }

    public String getAddGovernmentReform() {
        return this.item.getLastVarAsString("add_government_reform");
    }

    public String getPrimaryCulture() {
        return this.item.getLastVarAsString("primary_culture");
    }

    public String getGovernment() {
        return this.item.getLastVarAsString("government");
    }

    public String getReligion() {
        return this.item.getLastVarAsString("religion");
    }

    public String getSecondaryReligion() {
        return this.item.getLastVarAsString("secondary_religion");
    }

    public String getTechnologyGroup() {
        return this.item.getLastVarAsString("technology_group");
    }

    public String getUnitType() {
        return this.item.getLastVarAsString("unit_type");
    }

    public String getChangedTagFrom() {
        return ClausewitzUtils.removeQuotes(this.item.getLastVarAsString("changed_tag_from"));
    }

    public String getReligiousSchool() {
        return ClausewitzUtils.removeQuotes(this.item.getLastVarAsString("religious_school"));
    }

    public String getSetCountryFlag() {
        return ClausewitzUtils.removeQuotes(this.item.getLastVarAsString("set_country_flag"));
    }

    public String getDecision() {
        return ClausewitzUtils.removeQuotes(this.item.getLastVarAsString("decision"));
    }

    public Queen getQueen() {
        if (this.item.hasChild("queen")) {
            return new Queen(this.item.getLastChild("queen"), this.country);
        }

        return null;
    }

    public Queen getMonarchConsort() {
        if (this.item.hasChild("monarch_consort")) {
            return new Queen(this.item.getLastChild("monarch_consort"), this.country);
        }

        return null;
    }

    public Monarch getMonarch() {
        if (this.item.hasChild("monarch")) {
            return new Monarch(this.item.getLastChild("monarch"), this.country);
        }

        return null;
    }

    public Heir getMonarchHeir() {
        if (this.item.hasChild("monarch_heir")) {
            return new Heir(this.item.getLastChild("monarch_heir"), this.country);
        }

        return null;
    }

    public Heir getHeir() {
        if (this.item.hasChild("heir")) {
            return new Heir(this.item.getLastChild("heir"), this.country);
        }

        return null;
    }

    public Heir getMonarchForeignHeir() {
        if (this.item.hasChild("monarch_foreign_heir")) {
            return new Heir(this.item.getLastChild("monarch_foreign_heir"), this.country);
        }

        return null;
    }

    public Integer getUnion() {
        return this.item.getLastVarAsInt("union");
    }

    public Integer getTradePort() {
        return this.item.getLastVarAsInt("trade_port");
    }

    public Boolean getElector() {
        return this.item.getLastVarAsBool("elector");
    }

    public Power getNationalFocus() {
        if (this.item.hasVar("national_focus")) {
            return Power.byName(this.item.getLastVarAsString("national_focus"));
        }

        return null;
    }

    public String getSetEstatePrivilege() {
        return ClausewitzUtils.removeQuotes(this.item.getLastVarAsString("set_estate_privilege"));
    }
}
