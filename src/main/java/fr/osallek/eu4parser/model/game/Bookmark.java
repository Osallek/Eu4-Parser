package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import org.apache.commons.collections4.CollectionUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Bookmark extends Nodded {

    private final ClausewitzItem item;

    private final Game game;

    public Bookmark(FileNode fileNode, ClausewitzItem item, Game game) {
        super(fileNode);
        this.item = item;
        this.game = game;
    }

    public String getName() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("name"));
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public String getDesc() {
        return this.item.getVarAsString("desc");
    }

    public void setDesc(String desc) {
        this.item.setVariable("desc", desc);
    }

    public LocalDate getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(LocalDate date) {
        this.item.setVariable("date", date);
    }

    public Integer getCenter() {
        return this.item.getVarAsInt("center");
    }

    public void setCenter(Integer center) {
        this.item.setVariable("center", center);
    }

    public Boolean isDefault() {
        return this.item.getVarAsBool("default");
    }

    public void setDefault(Boolean isDefault) {
        if (isDefault == null) {
            this.item.removeVariable("default");
        } else {
            this.item.setVariable("default", isDefault);
        }
    }

    public List<Country> getCountries() {
        return this.item.getVarsAsStrings("country").stream().map(this.game::getCountry).collect(Collectors.toList());
    }

    public void addCountry(String country) {
        this.item.addVariable("country", country.toUpperCase(),
                              this.item.getVars("country").stream().mapToInt(ClausewitzVariable::getOrder).max().orElse(this.item.getMaxOrder()) + 1, true);
    }

    public void addCountry(Country country) {
        addCountry(country.getTag());
    }

    public void removeCountry(String country) {
        this.item.removeVariable("country", country.toUpperCase());
    }

    public void removeCountry(Country country) {
        removeCountry(country.getTag());
    }

    public void setCountries(Collection<Country> countries) {
        setCountries(countries.stream().map(Country::getTag).collect(Collectors.toList()));
    }

    public void setCountries(List<String> countries) {
        this.item.removeVariables("country");

        if (CollectionUtils.isNotEmpty(countries)) {
            countries.forEach(this::addCountry);
        }
    }

    public List<Country> getEasyCountries() {
        return this.item.getVarsAsStrings("easy_country").stream().map(this.game::getCountry).collect(Collectors.toList());
    }

    public void addEasyCountry(String country) {
        this.item.addVariable("easy_country", country.toUpperCase(),
                              this.item.getVars("easy_country").stream().mapToInt(ClausewitzVariable::getOrder).max().orElse(this.item.getMaxOrder()) + 1, true);
    }

    public void addEasyCountry(Country country) {
        addCountry(country.getTag());
    }

    public void removeEasyCountry(String country) {
        this.item.removeVariable("easy_country", country.toUpperCase());
    }

    public void removeEasyCountry(Country country) {
        removeCountry(country.getTag());
    }

    public void setEasyCountries(Collection<Country> countries) {
        setEasyCountries(countries.stream().map(Country::getTag).collect(Collectors.toList()));
    }

    public void setEasyCountries(List<String> countries) {
        this.item.removeVariables("easy_country");

        if (CollectionUtils.isNotEmpty(countries)) {
            countries.forEach(this::addEasyCountry);
        }
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        this.item.write(writer, true, 0, new HashMap<>());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
