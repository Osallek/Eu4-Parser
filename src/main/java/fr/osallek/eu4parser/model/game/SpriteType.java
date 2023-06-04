package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.Optional;

public class SpriteType {

    private final ClausewitzItem item;

    public SpriteType(ClausewitzItem item) {
        this.item = item;
    }

    public Optional<String> getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public Optional<String> getTextureFile() {
        return this.item.getVarAsString("texturefile");
    }

    public Optional<Path> getTextureFilePath() {
        return getTextureFile().map(ClausewitzUtils::removeQuotes).map(Path::of);
    }

    public Optional<Path> getTextureFilePath(String extension) {
        return getTextureFile().map(ClausewitzUtils::removeQuotes).map(FilenameUtils::removeExtension).map(s -> s + "." + extension).map(Path::of);
    }

    public void setTextureFile(String textureFile) {
        this.item.setVariable("texturefile", ClausewitzUtils.addQuotes(textureFile));
    }

    public Optional<Boolean> getTransparenceCheck() {
        return this.item.getVarAsBool("transparencecheck");
    }

    public void setTransparenceCheck(Boolean transparenceCheck) {
        if (transparenceCheck == null) {
            this.item.removeVariable("transparencecheck");
        } else {
            this.item.setVariable("transparencecheck", transparenceCheck);
        }
    }

    public Optional<String> getLoadType() {
        return this.item.getVarAsString("loadType");
    }

    public void setLoadType(String loadType) {
        if (StringUtils.isBlank(loadType)) {
            this.item.removeVariable("loadType");
        } else {
            this.item.setVariable("loadType", ClausewitzUtils.addQuotes(loadType));
        }
    }

    public Optional<String> getEffectFile() {
        return this.item.getVarAsString("effectFile");
    }

    public void setEffectFile(String effectFile) {
        if (StringUtils.isBlank(effectFile)) {
            this.item.removeVariable("effectFile");
        } else {
            this.item.setVariable("effectFile", ClausewitzUtils.addQuotes(effectFile));
        }
    }

    public Optional<Integer> getNoOfFrames() {
        return this.item.getVarAsInt("noofframes");
    }

    public void setNoOfFrames(Integer noOfFrames) {
        if (noOfFrames == null) {
            this.item.removeVariable("effectFile");
        } else {
            this.item.setVariable("effectFile", noOfFrames);
        }
    }

    public Optional<Boolean> getNoRefCount() {
        return this.item.getVarAsBool("norefcount");
    }

    public void setNoRefCount(Boolean noRefCount) {
        if (noRefCount == null) {
            this.item.removeVariable("effectFile");
        } else {
            this.item.setVariable("effectFile", noRefCount);
        }
    }

    @Override
    public String toString() {
        return getName().orElse("");
    }
}
