package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.lang3.StringUtils;

public class SpriteType {

    private final ClausewitzItem item;

    public SpriteType(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", name);
    }

    public String getTextureFile() {
        return this.item.getVarAsString("texturefile");
    }

    public void setTextureFile(String textureFile) {
        this.item.setVariable("texturefile", textureFile);
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
            this.item.setVariable("loadType", loadType);
        }
    }

    @Override
    public String toString() {
        return getName();
    }
}
