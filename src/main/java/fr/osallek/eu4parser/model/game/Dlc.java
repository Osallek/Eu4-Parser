package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.Eu4Utils;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Dlc extends Nodded {

    private final ClausewitzItem item;

    private final Game game;

    private Path writenTo;

    public Dlc(ClausewitzItem item, Game game, FileNode fileNode) {
        super(fileNode);
        this.item = item;
        this.game = game;
    }

    public String getInternalName() {
        return this.item.getName();
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public Integer getId() {
        return this.item.getVarAsInt("id");
    }

    public String getCategory() {
        return this.item.getVarAsString("category");
    }

    public String getHideIfDlcOwned() {
        return this.item.getVarAsString("hide_if_dlc_owned");
    }

    public String getChangeLandingPage() {
        return this.item.getVarAsString("change_landingpage");
    }
    public boolean getShowExampleImage() {
        return Optional.ofNullable(this.item.getVarAsBool("show_example_image")).orElse(false);
    }

    public boolean getShowExampleImageTooltips() {
        return Optional.ofNullable(this.item.getVarAsBool("show_example_image_tooltips")).orElse(false);
    }

    public boolean getHideIfUnowned() {
        return Optional.ofNullable(this.item.getVarAsBool("hide_if_unowned")).orElse(false);
    }

    public List<String> getRecommendations() {
        return Optional.ofNullable(this.item.getList("recommendations")).map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public List<String> getInterestingCountries() {
        return Optional.ofNullable(this.item.getList("interesting_countries")).map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public List<String> getRequiredDlc() {
        return Optional.ofNullable(this.item.getList("required_dlc")).map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public String getIcon() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("icon"));
    }

    public File getImage() {
        return this.game.getSpriteTypeImageFile(getIcon());
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
    public void write(BufferedWriter writer) throws IOException {
        this.item.write(writer, true, 0, new HashMap<>());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Dlc)) {
            return false;
        }

        Dlc area = (Dlc) o;

        return Objects.equals(getName(), area.getName());
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
