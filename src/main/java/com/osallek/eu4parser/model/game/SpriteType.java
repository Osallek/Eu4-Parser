package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;

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

    public String getLoadType() {
        return this.item.getVarAsString("loadType");
    }

    public void setLoadType(String loadType) {
        this.item.setVariable("loadType", loadType);
    }

    @Override
    public String toString() {
        return getName();
    }
}
