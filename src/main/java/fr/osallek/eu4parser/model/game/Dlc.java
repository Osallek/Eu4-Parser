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
        return this.item.getVarAsString("name").orElse(null);
    }

    public Integer getId() {
        return this.item.getVarAsInt("id").orElse(null);
    }

    public String getCategory() {
        return this.item.getVarAsString("category").orElse(null);
    }

    public Optional<String> getHideIfDlcOwned() {
        return this.item.getVarAsString("hide_if_dlc_owned");
    }

    public Optional<String> getChangeLandingPage() {
        return this.item.getVarAsString("change_landingpage");
    }
    public boolean getShowExampleImage() {
        return this.item.getVarAsBool("show_example_image").orElse(false);
    }

    public boolean getShowExampleImageTooltips() {
        return this.item.getVarAsBool("show_example_image_tooltips").orElse(false);
    }

    public boolean getHideIfUnowned() {
        return this.item.getVarAsBool("hide_if_unowned").orElse(false);
    }

    public List<String> getRecommendations() {
        return this.item.getList("recommendations").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public List<String> getInterestingCountries() {
        return this.item.getList("interesting_countries").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public List<String> getRequiredDlc() {
        return this.item.getList("required_dlc").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public Optional<String> getIcon() {
        return this.item.getVarAsString("icon").map(ClausewitzUtils::removeQuotes);
    }

    public Optional<File> getImage() {
        return getIcon().map(this.game::getSpriteTypeImageFile);
    }

    public void writeImageTo(Path dest) throws IOException {
        if (getImage().isPresent()) {
            FileUtils.forceMkdirParent(dest.toFile());
            ImageIO.write(ImageIO.read(getImage().get()), "png", dest.toFile());
            Eu4Utils.optimizePng(dest, dest);
            this.writenTo = dest;
        }
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

        if (!(o instanceof Dlc dlc)) {
            return false;
        }

        return Objects.equals(getName(), dlc.getName());
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
