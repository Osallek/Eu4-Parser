package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.common.ImageReader;
import fr.osallek.eu4parser.model.Color;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class TradeGood {

    private final ClausewitzItem item;

    private final int index;

    private final Game game;

    private ClausewitzItem priceItem;

    private Path writenTo;

    public TradeGood(ClausewitzItem item, int index, Game game) {
        this.item = item;
        this.index = index;
        this.game = game;
    }

    public void setPriceItem(ClausewitzItem priceItem) {
        this.priceItem = priceItem;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Color getColor() {
        if (this.item == null) {
            return null;
        }

        ClausewitzList clausewitzList = this.item.getList("color");
        return clausewitzList == null ? null : new Color(clausewitzList, true);
    }

    public void setColor(Color color) {
        if (color == null) {
            this.item.removeList("color");
            return;
        }

        ClausewitzList list = this.item.getList("color");

        if (list != null) {
            Color actualColor = new Color(list, true);
            actualColor.setRed(color.getRed());
            actualColor.setGreen(color.getGreen());
            actualColor.setBlue(color.getBlue());
        } else {
            Color.addToItem(this.item, "color", color);
        }
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getChild("modifier"));
    }

    public Modifiers getProvinceModifiers() {
        return new Modifiers(this.item.getChild("province"));
    }

    public Double getBasePrice() {
        return this.priceItem == null ? 1 : this.priceItem.getVarAsDouble("base_price");
    }

    public void setBasePrice(Double basePrice) {
        if (this.priceItem == null) {
            return;
        }

        if (basePrice == null) {
            this.priceItem.removeVariable("base_price");
        } else {
            this.priceItem.setVariable("base_price", basePrice);
        }
    }

    public Boolean isGoldType() {
        return Optional.ofNullable(this.priceItem).map(i -> i.getVarAsBool("goldtype")).orElse(null);
    }

    public void setGoldType(Boolean goldType) {
        if (this.priceItem == null) {
            return;
        }

        if (goldType == null) {
            this.priceItem.removeVariable("goldtype");
        } else {
            this.priceItem.setVariable("goldtype", goldType);
        }
    }

    public BufferedImage getImage() throws IOException {
        return getSubImage(ImageReader.convertFileToImage(this.game.getResourcesImage()));
    }

    public BufferedImage getSubImage(BufferedImage image) {
        double size = (double) image.getWidth() / this.game.getResourcesNbFrames();
        return image.getSubimage((int) (this.index * size), 0, (int) size, image.getHeight());
    }

    public void writeImageTo(Path dest) throws IOException {
        FileUtils.forceMkdirParent(dest.toFile());
        ImageIO.write(getImage(), "png", dest.toFile());
        Eu4Utils.optimizePng(dest, dest);
        this.writenTo = dest;
    }

    public void setWritenTo(Path writenTo) {
        this.writenTo = writenTo;
    }

    public Path getWritenTo() {
        return writenTo;
    }

    public Game getGame() {
        return game;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TradeGood tradeGood)) {
            return false;
        }

        return Objects.equals(getName(), tradeGood.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
