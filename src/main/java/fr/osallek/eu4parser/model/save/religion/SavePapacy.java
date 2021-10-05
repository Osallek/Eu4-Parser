package fr.osallek.eu4parser.model.save.religion;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.game.GoldenBull;
import fr.osallek.eu4parser.model.game.Modifier;
import fr.osallek.eu4parser.model.game.ModifiersUtils;
import fr.osallek.eu4parser.model.game.Papacy;
import fr.osallek.eu4parser.model.game.PapacyConcession;
import fr.osallek.eu4parser.model.save.Id;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.SaveReligion;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import org.apache.commons.lang3.BooleanUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SavePapacy {

    private final ClausewitzItem item;

    private final SaveReligion religion;

    private final Save save;

    private List<Cardinal> cardinals;

    private ColoniesClaims coloniesClaims;

    public SavePapacy(ClausewitzItem item, SaveReligion religion, Save save) {
        this.item = item;
        this.religion = religion;
        this.save = save;
        refreshAttributes();
    }

    public Papacy getGamePapacy() {
        return this.save.getGame().getReligion(this.religion.getName()).getPapacy();
    }

    public SaveCountry getCrusadeTarget() {
        String target = this.item.getVarAsString("crusade_target");

        if (target == null || Eu4Utils.DEFAULT_TAG_QUOTES.equals(target)) {
            return null;
        }

        return this.save.getCountry(ClausewitzUtils.removeQuotes(target));
    }

    public LocalDate getCrusadeStart() {
        LocalDate date = this.item.getVarAsDate("crusade_start");

        if (date == null || Eu4Utils.DEFAULT_DATE.equals(date)) {
            return null;
        }

        return date;
    }

    public void setCrusadeTarget(SaveCountry target) {
        if (target != getCrusadeTarget()) {
            if (target == null) {
                removeCrusade();
            } else {
                this.item.setVariable("crusade_target", ClausewitzUtils.addQuotes(target.getTag()));
                this.item.setVariable("crusade_start", this.save.getDate());
            }
        }
    }

    public void removeCrusade() {
        this.item.setVariable("crusade_target", Eu4Utils.DEFAULT_TAG_QUOTES);
        this.item.setVariable("crusade_start", Eu4Utils.DEFAULT_DATE);
    }

    public Double getReformDesire() {
        return this.item.getVarAsDouble("reform_desire");
    }

    public void setReformDesire(Double reformDesire) {
        this.item.setVariable("reform_desire", reformDesire);
    }

    public String getControllerTag() {
        return this.item.getVarAsString("controller");
    }

    public SaveCountry getController() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(getControllerTag()));
    }

    public void setController(SaveCountry controller) {
        this.item.setVariable("controller", ClausewitzUtils.addQuotes(controller.getTag()));
    }

    public SaveCountry getPreviousController() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(this.item.getVarAsString("previous_controller")));
    }

    public void setPreviousController(SaveCountry previousController) {
        this.item.setVariable("previous_controller", ClausewitzUtils.addQuotes(previousController.getTag()));
    }

    public LocalDate getLastExcommunication() {
        LocalDate date = this.item.getVarAsDate("last_excom");

        if (date == null || Eu4Utils.DEFAULT_DATE.equals(date)) {
            return null;
        }

        return date;
    }

    public void setLastExcommunication(LocalDate lastExcommunication) {
        this.item.setVariable("last_excom", lastExcommunication);
    }

    public Boolean getPapacyActive() {
        return this.item.getVarAsBool("papacy_active");
    }

    public void setPapacyActive(boolean papacyActive) {
        this.item.setVariable("papacy_active", papacyActive);
    }

    public Boolean getCouncilActive() {
        return this.item.getVarAsBool("council_active");
    }

    public void setCouncilActive(boolean councilActive) {
        this.item.setVariable("council_active", councilActive);
    }

    public Boolean getCouncilFinished() {
        return this.item.getVarAsBool("council_finished");
    }

    public void setCouncilFinished(boolean councilFinished) {
        this.item.setVariable("council_finished", councilFinished);
    }

    public Double getPapalInvestment() {
        return this.item.getVarAsDouble("papal_investment");
    }

    public void setPapalInvestment(double papalInvestment) {
        if (papalInvestment < 0) {
            papalInvestment = 0;
        }

        this.item.setVariable("papal_investment", papalInvestment);
    }

    public Double getCuriaTreasury() {
        return this.item.getVarAsDouble("curia_treasury");
    }

    public void setCuriaTreasury(double curiaTreasury) {
        if (curiaTreasury < 0) {
            curiaTreasury = 0;
        }

        this.item.setVariable("curia_treasury", curiaTreasury);
    }

    public GoldenBull getGoldenBull() {
        String bull = this.item.getVarAsString("golden_bull");

        if (bull == null) {
            return null;
        } else {
            return this.save.getGame().getGoldenBull(ClausewitzUtils.removeQuotes(bull));
        }
    }

    public void setGoldenBull(GoldenBull goldenBull) {
        if (goldenBull == null || goldenBull.getName() == null) {
            this.item.removeVariable("golden_bull");
        } else {
            this.item.setVariable("golden_bull", ClausewitzUtils.addQuotes(goldenBull.getName()));
        }
    }

    public Map<Integer, Integer> getInvestInCardinals() {
        ClausewitzItem investItem = this.item.getChild("invest_in_cardinal");

        if (investItem == null) {
            return new HashMap<>();
        }

        return investItem.getVariables()
                         .stream()
                         .collect(Collectors.toMap(var -> Integer.parseInt(var.getName()),
                                                   ClausewitzVariable::getAsInt));
    }

    public void investInCardinals(Integer id, Integer value) {
        ClausewitzItem investItem = this.item.getChild("invest_in_cardinal");

        if (investItem == null) {
            investItem = this.item.addChild("papal_investment");
        }

        investItem.setVariable(id.toString(), value);
    }

    public List<Cardinal> getCardinals() {
        return cardinals;
    }

    public void addCardinal(Integer provinceId) {
        ClausewitzItem activeCardinalsItem = this.item.getChild("active_cardinals");

        if (activeCardinalsItem != null) {
            Integer id = getCardinals().stream()
                                       .map(Cardinal::getId)
                                       .map(Id::getId)
                                       .max(Integer::compareTo)
                                       .orElse(new Random().nextInt(90000));
            Cardinal.addToItem(activeCardinalsItem, id, provinceId);
        }

        refreshAttributes();
    }

    public void removeCardinal(Integer index) {
        ClausewitzItem activeCardinalsItem = this.item.getChild("active_cardinals");

        if (activeCardinalsItem != null) {
            activeCardinalsItem.removeChild("cardinal", index);
        }

        refreshAttributes();
    }

    public List<String> getColonyClaims() {
        if (this.coloniesClaims == null) {
            return new ArrayList<>();
        }

        return this.coloniesClaims.getColonyClaims();
    }

    public String getColonyClaim(int index) {
        if (this.coloniesClaims == null) {
            return null;
        }

        return this.coloniesClaims.getColonyClaim(index);
    }

    public void setColonyClaim(int index, SaveCountry country) {
        if (this.coloniesClaims != null) {
            this.coloniesClaims.setColonyClaim(index, country);
        }
    }

    public void removeColonyClaim(int index) {
        if (this.coloniesClaims != null) {
            this.coloniesClaims.removeColonyClaim(index);
        }
    }

    public Map<String, List<String>> getConcessions() {
        Map<String, List<String>> concessions = new LinkedHashMap<>();

        if (!BooleanUtils.toBoolean(getCouncilActive())) {
            return concessions;
        }

        ClausewitzList list = this.item.getList("concessions");

        if (list == null) {
            return concessions;
        }

        for (int i = 0; i < list.size(); i++) {
            String choose = getGamePapacy().getConcessions().get(i).getName() + (list.getAsInt(i) == 1 ? "_harsh" : "_concilatory");
            concessions.put(choose,
                            Arrays.asList(getGamePapacy().getConcessions().get(i).getName() + "_harsh",
                                          getGamePapacy().getConcessions().get(i).getName() + "_concilatory"));
        }

        return concessions;
    }

    public void setConcessions(List<String> concessions) {
        ClausewitzList list = this.item.getList("concessions");
        List<Integer> concessionsIds = concessions.stream()
                                                  .map(concession -> concession.endsWith("harsh") ? 1 : 2)
                                                  .collect(Collectors.toList());

        if (list == null) {
            this.item.addList("concessions", concessionsIds.toArray(new Integer[0]));
        } else {
            list.clear();
            list.addAll(concessionsIds.toArray(new Integer[0]));
        }
    }

    public Map<PapacyConcession, Integer> getConcessionsChoices() {
        if (!BooleanUtils.toBoolean(getCouncilActive())) {
            return new LinkedHashMap<>();
        }

        ClausewitzList list = this.item.getList("concessions");

        if (list == null) {
            return new LinkedHashMap<>();
        }

        return IntStream.range(0, list.size()).boxed().collect(Collectors.toMap(i -> getGamePapacy().getConcessions().get(i), list::getAsInt));
    }

    public Double getConcessionsModifiers(Modifier modifier) {
        return ModifiersUtils.sumModifiers(modifier, getConcessionsChoices().entrySet()
                                                                            .stream()
                                                                            .filter(entry -> entry.getValue() != 0)
                                                                            .map(entry -> entry.getValue() == 1 ? entry.getKey().getHarshModifiers()
                                                                                                                : entry.getKey().getConcilatoryModifiers())
                                                                            .filter(m -> m.hasModifier(modifier))
                                                                            .map(m -> m.getModifier(modifier))
                                                                            .collect(Collectors.toList()));
    }

    public List<SaveCountry> getConcilatory() {
        ClausewitzList list = this.item.getList("concilatory");

        return list == null ? new ArrayList<>() : list.getValues().stream().map(this.save::getCountry).collect(Collectors.toList());
    }

    public List<SaveCountry> getNeutral() {
        ClausewitzList list = this.item.getList("neutral");

        return list == null ? new ArrayList<>() : list.getValues().stream().map(this.save::getCountry).collect(Collectors.toList());
    }

    public List<SaveCountry> getHarsh() {
        ClausewitzList list = this.item.getList("harsh");

        return list == null ? new ArrayList<>() : list.getValues().stream().map(this.save::getCountry).collect(Collectors.toList());
    }

    private void refreshAttributes() {
        ClausewitzItem activeCardinalsItem = this.item.getChild("active_cardinals");

        if (activeCardinalsItem != null) {
            List<ClausewitzItem> cardinalsItems = activeCardinalsItem.getChildren("cardinal");
            this.cardinals = cardinalsItems.stream().map(child -> new Cardinal(child, this.save)).collect(Collectors.toList());
        }

        ClausewitzList claimsList = this.item.getList("colony_claim");

        if (claimsList != null) {
            this.coloniesClaims = new ColoniesClaims(claimsList);
        }
    }
}
