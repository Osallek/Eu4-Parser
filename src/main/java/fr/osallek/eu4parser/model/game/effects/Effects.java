package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzObject;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.common.NumbersUtils;
import fr.osallek.eu4parser.model.game.ConditionAnd;
import fr.osallek.eu4parser.model.game.Game;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Effects {

    private final Game game;

    private final ClausewitzItem item;

    public Effects(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public ConditionAnd getLimit() {
        return this.item.hasChild("limit") ? new ConditionAnd(item.getChild("limit")) : null;
    }

    public Effects getIfEffect() {
        return this.item.hasChild("if") ? new Effects(this.item.getChild("if"), this.game) : null;
    }

    public List<Effects> getElseIfEffects() {
        return this.item.getChildren("else_if").stream().map(i -> new Effects(i, this.game)).toList();
    }

    public Effects getElseEffect() {
        return this.item.hasChild("else") ? new Effects(this.item.getChild("if"), this.game) : null;
    }

    public Map<String, Effects> getRegions() {
        return this.item.getChildren()
                        .stream()
                        .filter(i -> this.game.getRegion(i.getName()) != null)
                        .collect(Collectors.toMap(ClausewitzObject::getName, i -> new Effects(i, this.game)));
    }

    public Map<String, Effects> getAreas() {
        return this.item.getChildren()
                        .stream()
                        .filter(i -> this.game.getArea(i.getName()) != null)
                        .collect(Collectors.toMap(ClausewitzObject::getName, i -> new Effects(i, this.game)));
    }

    public Map<String, Effects> getCountries() {
        return this.item.getChildren()
                        .stream()
                        .filter(i -> this.game.getCountry(i.getName()) != null)
                        .collect(Collectors.toMap(ClausewitzObject::getName, i -> new Effects(i, this.game)));
    }

    public Map<Integer, Effects> getProvinces() {
        return this.item.getChildren()
                        .stream()
                        .filter(i -> NumbersUtils.parseInt(i.getName()).isPresent())
                        .filter(i -> this.game.getProvince(NumbersUtils.toInt(i.getName())) != null)
                        .collect(Collectors.toMap(i -> NumbersUtils.toInt(i.getName()), i -> new Effects(i, this.game)));
    }

    public Map<String, String> getUnits() {
        return this.item.getVariables()
                        .stream()
                        .filter(variable -> this.game.getUnit(variable.getName()) != null)
                        .collect(Collectors.toMap(ClausewitzObject::getName, ClausewitzVariable::getValue));
    }

    public List<Event> getCountryEvents() {
        return this.item.getChildren("country_event").stream().map(Event::new).toList();
    }

    public List<AddModifier> getAddCountryModifiers() {
        return this.item.getChildren("add_country_modifier").stream().map(AddModifier::new).toList();
    }

    public Map<String, String> getEffects() {
        return this.item.getVariables().stream().collect(Collectors.toMap(ClausewitzObject::getName, ClausewitzVariable::getValue));
    }
}
