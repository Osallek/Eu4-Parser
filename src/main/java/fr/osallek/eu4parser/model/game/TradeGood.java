package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
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

    public Optional<Color> getColor() {
        if (this.item == null) {
            return Optional.empty();
        }

        return this.item.getList("color").map(l -> new Color(l, true));
    }

    public void setColor(Color color) {
        if (color == null) {
            this.item.removeList("color");
            return;
        }

        this.item.getList("color").ifPresentOrElse(list -> {
            Color actualColor = new Color(list, true);
            actualColor.setRed(color.getRed());
            actualColor.setGreen(color.getGreen());
            actualColor.setBlue(color.getBlue());
        }, () -> Color.addToItem(this.item, "color", color));
    }

    public Optional<Modifiers> getModifiers() {
        return this.item.getChild("modifier").map(Modifiers::new);
    }

    public Optional<Modifiers> getProvinceModifiers() {
        return this.item.getChild("province").map(Modifiers::new);
    }

    public double getBasePrice() {
        return this.priceItem.getVarAsDouble("base_price").orElse(1d);
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

    public Optional<Boolean> isGoldType() {
        return Optional.ofNullable(this.priceItem).flatMap(i -> i.getVarAsBool("goldtype"));
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
