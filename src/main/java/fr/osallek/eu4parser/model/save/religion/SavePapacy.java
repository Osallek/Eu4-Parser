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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
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

    public Optional<Papacy> getGamePapacy() {
        return this.save.getGame().getReligion(this.religion.getName()).getPapacy();
    }

    public Optional<SaveCountry> getCrusadeTarget() {
        return this.item.getVarAsString("crusade_target").filter(Predicate.not(Eu4Utils.DEFAULT_TAG_QUOTES::equals)).map(this.save::getCountry);
    }

    public Optional<LocalDate> getCrusadeStart() {
        return this.item.getVarAsDate("crusade_start").filter(Predicate.not(Eu4Utils.DEFAULT_DATE::equals));
    }

    public void setCrusadeTarget(SaveCountry target) {
        if (getCrusadeTarget().isEmpty() || target != getCrusadeTarget().get()) {
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

    public Optional<Double> getReformDesire() {
        return this.item.getVarAsDouble("reform_desire");
    }

    public void setReformDesire(Double reformDesire) {
        this.item.setVariable("reform_desire", reformDesire);
    }

    public Optional<String> getControllerTag() {
        return this.item.getVarAsString("controller");
    }

    public Optional<SaveCountry> getController() {
        return getControllerTag().map(this.save::getCountry);
    }

    public void setController(SaveCountry controller) {
        this.item.setVariable("controller", ClausewitzUtils.addQuotes(controller.getTag()));
    }

    public Optional<SaveCountry> getPreviousController() {
        return this.item.getVarAsString("previous_controller").map(this.save::getCountry);
    }

    public void setPreviousController(SaveCountry previousController) {
        this.item.setVariable("previous_controller", ClausewitzUtils.addQuotes(previousController.getTag()));
    }

    public Optional<LocalDate> getLastExcommunication() {
        return this.item.getVarAsDate("last_excom").filter(Predicate.not(Eu4Utils.DEFAULT_DATE::equals));
    }

    public void setLastExcommunication(LocalDate lastExcommunication) {
        this.item.setVariable("last_excom", lastExcommunication);
    }

    public Optional<Boolean> getPapacyActive() {
        return this.item.getVarAsBool("papacy_active");
    }

    public void setPapacyActive(boolean papacyActive) {
        this.item.setVariable("papacy_active", papacyActive);
    }

    public Optional<Boolean> getCouncilActive() {
        return this.item.getVarAsBool("council_active");
    }

    public void setCouncilActive(boolean councilActive) {
        this.item.setVariable("council_active", councilActive);
    }

    public Optional<Boolean> getCouncilFinished() {
        return this.item.getVarAsBool("council_finished");
    }

    public void setCouncilFinished(boolean councilFinished) {
        this.item.setVariable("council_finished", councilFinished);
    }

    public Optional<Double> getPapalInvestment() {
        return this.item.getVarAsDouble("papal_investment");
    }

    public void setPapalInvestment(double papalInvestment) {
        if (papalInvestment < 0) {
            papalInvestment = 0;
        }

        this.item.setVariable("papal_investment", papalInvestment);
    }

    public Optional<Double> getCuriaTreasury() {
        return this.item.getVarAsDouble("curia_treasury");
    }

    public void setCuriaTreasury(double curiaTreasury) {
        if (curiaTreasury < 0) {
            curiaTreasury = 0;
        }

        this.item.setVariable("curia_treasury", curiaTreasury);
    }

    public Optional<GoldenBull> getGoldenBull() {
        return this.item.getVarAsString("golden_bull").map(s -> this.save.getGame().getGoldenBull(s));
    }

    public void setGoldenBull(GoldenBull goldenBull) {
        if (goldenBull == null || goldenBull.getName() == null) {
            this.item.removeVariable("golden_bull");
        } else {
            this.item.setVariable("golden_bull", ClausewitzUtils.addQuotes(goldenBull.getName()));
        }
    }

    public Map<Integer, Integer> getInvestInCardinals() {
        return this.item.getChild("invest_in_cardinal")
                        .map(ClausewitzItem::getVariables)
                        .stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsInt));
    }

    public void investInCardinals(Integer id, Integer value) {
        ClausewitzItem investItem = this.item.getChild("invest_in_cardinal").orElse(this.item.addChild("papal_investment"));
        investItem.setVariable(id.toString(), value);
    }

    public List<Cardinal> getCardinals() {
        return cardinals;
    }

    public void addCardinal(Integer provinceId) {
        this.item.getChild("active_cardinals").ifPresent(activeCardinalsItem -> {
            Integer id = getCardinals().stream()
                                       .map(Cardinal::getId)
                                       .filter(Optional::isPresent)
                                       .map(Optional::get)
                                       .map(Id::getId)
                                       .max(Integer::compareTo)
                                       .orElse(new Random().nextInt(90000));
            Cardinal.addToItem(activeCardinalsItem, id, provinceId);

            refreshAttributes();
        });

    }

    public void removeCardinal(Integer index) {
        this.item.getChild("active_cardinals").ifPresent(activeCardinalsItem -> {
            activeCardinalsItem.removeChild("cardinal", index);
            refreshAttributes();
        });
    }

    public List<String> getColonyClaims() {
        if (this.coloniesClaims == null) {
            return new ArrayList<>();
        }

        return this.coloniesClaims.getColonyClaims();
    }

    public Optional<String> getColonyClaim(int index) {
        return Optional.ofNullable(this.coloniesClaims).flatMap(c -> c.getColonyClaim(index));
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

        if (getGamePapacy().isEmpty() || !getCouncilActive().map(BooleanUtils::toBoolean).orElse(false)) {
            return concessions;
        }

        this.item.getList("concessions").ifPresent(list -> {
            Papacy papacy = getGamePapacy().get();
            for (int i = 0; i < list.size(); i++) {
                String choose = papacy.getConcessions().get(i).getName() + (list.getAsInt(i).filter(integer -> integer == 1).isPresent() ? "_harsh"
                                                                                                                                         : "_concilatory");
                concessions.put(choose,
                                Arrays.asList(papacy.getConcessions().get(i).getName() + "_harsh",
                                              papacy.getConcessions().get(i).getName() + "_concilatory"));
            }
        });

        return concessions;
    }

    public void setConcessions(List<String> concessions) {
        List<Integer> concessionsIds = concessions.stream()
                                                  .map(concession -> concession.endsWith("harsh") ? 1 : 2)
                                                  .toList();

        this.item.getList("concessions").ifPresentOrElse(list -> {
            list.clear();
            list.addAll(concessionsIds.toArray(new Integer[0]));
        }, () -> this.item.addList("concessions", concessionsIds.toArray(new Integer[0])));
    }

    public Map<PapacyConcession, Integer> getConcessionsChoices() {
        if (!getCouncilActive().map(BooleanUtils::toBoolean).orElse(false)) {
            return new LinkedHashMap<>();
        }

        Optional<ClausewitzList> list = this.item.getList("concessions");

        if (list.isEmpty() || getGamePapacy().isEmpty()) {
            return new LinkedHashMap<>();
        }

        return IntStream.range(0, list.get().size())
                        .boxed()
                        .collect(Collectors.toMap(i -> getGamePapacy().get().getConcessions().get(i), integer -> list.get().getAsInt(integer).get()));
    }

    public Double getConcessionsModifiers(Modifier modifier) {
        return ModifiersUtils.sumModifiers(modifier, getConcessionsChoices().entrySet()
                                                                            .stream()
                                                                            .filter(entry -> entry.getValue() != 0)
                                                                            .map(entry -> entry.getValue() == 1 ? entry.getKey().getHarshModifiers()
                                                                                                                : entry.getKey().getConcilatoryModifiers())
                                                                            .filter(Optional::isPresent)
                                                                            .map(Optional::get)
                                                                            .filter(m -> m.hasModifier(modifier))
                                                                            .map(m -> m.getModifier(modifier))
                                                                            .toList());
    }

    public List<SaveCountry> getConcilatory() {
        return this.item.getList("concilatory").map(ClausewitzList::getValues).stream().flatMap(Collection::stream).map(this.save::getCountry).toList();
    }

    public List<SaveCountry> getNeutral() {
        return this.item.getList("neutral").map(ClausewitzList::getValues).stream().flatMap(Collection::stream).map(this.save::getCountry).toList();
    }

    public List<SaveCountry> getHarsh() {
        return this.item.getList("harsh").map(ClausewitzList::getValues).stream().flatMap(Collection::stream).map(this.save::getCountry).toList();
    }

    private void refreshAttributes() {
        this.cardinals = this.item.getChild("active_cardinals")
                                  .map(i -> i.getChildren("cardinal"))
                                  .stream()
                                  .flatMap(Collection::stream)
                                  .map(i -> new Cardinal(i, this.save))
                                  .toList();
        this.coloniesClaims = this.item.getList("colony_claim").map(ColoniesClaims::new).orElse(null);
    }
}
