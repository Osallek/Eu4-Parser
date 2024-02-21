package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;

public class SpriteType {

    private final ClausewitzItem item;

    public SpriteType(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public String getTextureFile() {
        return this.item.getVarAsString("texturefile");
    }

    public Path getTextureFilePath() {
        return Path.of(ClausewitzUtils.removeQuotes(getTextureFile()));
    }

    public Path getTextureFilePath(String extension) {
        return Path.of(FilenameUtils.removeExtension(ClausewitzUtils.removeQuotes(getTextureFile())) + "." + extension);
    }

    public void setTextureFile(String textureFile) {
        this.item.setVariable("texturefile", ClausewitzUtils.addQuotes(textureFile));
    }

    public Boolean getTransparenceCheck() {
        return this.item.getVarAsBool("transparencecheck");
    }

    public void setTransparenceCheck(Boolean transparenceCheck) {
        if (transparenceCheck == null) {
            this.item.removeVariable("transparencecheck");
        } else {
            this.item.setVariable("transparencecheck", transparenceCheck);
        }
    }

    public String getLoadType() {
        return this.item.getVarAsString("loadType");
    }

    public void setLoadType(String loadType) {
        if (StringUtils.isBlank(loadType)) {
            this.item.removeVariable("loadType");
        } else {
            this.item.setVariable("loadType", ClausewitzUtils.addQuotes(loadType));
        }
    }

    public String getEffectFile() {
        return this.item.getVarAsString("effectFile");
    }

    public void setEffectFile(String effectFile) {
        if (StringUtils.isBlank(effectFile)) {
            this.item.removeVariable("effectFile");
        } else {
            this.item.setVariable("effectFile", ClausewitzUtils.addQuotes(effectFile));
        }
    }

    public Integer getNoOfFrames() {
        return this.item.getVarAsInt("noOfFrames");
    }

    public void setNoOfFrames(Integer noOfFrames) {
        if (noOfFrames == null) {
            this.item.removeVariable("effectFile");
        } else {
            this.item.setVariable("effectFile", noOfFrames);
        }
    }

    public Boolean getNoRefCount() {
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
        return getName();
    }
}
