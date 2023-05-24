package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class Institution implements Comparable<Institution> {

    private final ClausewitzItem item;

    private final Game game;

    private int index;

    private Path writenTo;

    public Institution(ClausewitzItem item, Game game, int index) {
        this.item = item;
        this.game = game;
        this.index = index;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public Modifiers getBonuses() {
        return new Modifiers(this.item.getChild("bonus"));
    }

    public Double getTradeCompanyEfficiency() {
        return this.item.getVarAsDouble("trade_company_efficiency");
    }

    public void setTradeCompanyEfficiency(Double tradeCompanyEfficiency) {
        if (tradeCompanyEfficiency == null) {
            this.item.removeVariable("trade_company_efficiency");
        } else {
            this.item.setVariable("trade_company_efficiency", tradeCompanyEfficiency);
        }
    }

    public Integer getStartChance() {
        return this.item.getVarAsInt("start_chance");
    }

    public void setStartChance(Integer startChance) {
        if (startChance == null) {
            this.item.removeVariable("start_chance");
        } else {
            this.item.setVariable("start_chance", startChance);
        }
    }

    public String getOnStart() {
        return this.item.getVarAsString("on_start");
    }

    public void setStartChance(String startChance) {
        if (startChance == null) {
            this.item.removeVariable("on_start");
        } else {
            this.item.setVariable("on_start", startChance);
        }
    }

    public Integer getHistoricalStartProvince() {
        return this.item.getVarAsInt("historical_start_province");
    }

    public void setHistoricalStartProvince(Integer historicalStartProvince) {
        if (historicalStartProvince == null) {
            this.item.removeVariable("historical_start_province");
        } else {
            this.item.setVariable("historical_start_province", historicalStartProvince);
        }
    }

    public LocalDate getHistoricalStartDate() {
        return this.item.getVarAsDate("historical_start_date");
    }

    public void setHistoricalStartDate(LocalDate historicalStartDate) {
        if (historicalStartDate == null) {
            this.item.removeVariable("historical_start_date");
        } else {
            this.item.setVariable("historical_start_date", historicalStartDate);
        }
    }

    public ConditionAnd getHistory() {
        return Optional.ofNullable(this.item.getChild("history")).map(ConditionAnd::new).orElse(null);
    }

    public ConditionAnd getCanStart() {
        return Optional.ofNullable(this.item.getChild("can_start")).map(ConditionAnd::new).orElse(null);
    }

    public ConditionAnd getCanEmbrace() {
        return Optional.ofNullable(this.item.getChild("can_embrace")).map(ConditionAnd::new).orElse(null);
    }

    public Modifiers getEmbracementSpeed() {
        return Optional.ofNullable(this.item.getChild("embracement_speed")).map(Modifiers::new).orElse(null);
    }

    public File getImage() {
        return this.game.getSpriteTypeImageFile("GFX_icon_institution_" + getName());
    }

    public void writeImageTo(Path dest) throws IOException {
        FileUtils.forceMkdirParent(dest.toFile());
        ImageIO.write(ImageIO.read(getImage()), "png", dest.toFile());
        Eu4Utils.optimizePng(dest, dest);
        this.writenTo = dest;
    }

    public Path getWritenTo() {
        return writenTo;
    }

    public void setWritenTo(Path writenTo) {
        this.writenTo = writenTo;
    }

    @Override
    public int compareTo(Institution o) {
        return Comparator.comparingInt(Institution::getIndex).compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Institution institution)) {
            return false;
        }

        return Objects.equals(getName(), institution.getName());
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
