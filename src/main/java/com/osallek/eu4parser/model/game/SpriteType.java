package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;

public class SpriteType {

    private String name;

    private String textureFile;

    private String loadType;

    public SpriteType(ClausewitzItem item) {
        this.name = item.getVarAsString("name");
        this.textureFile = item.getVarAsString("texturefile");
        this.loadType = item.getVarAsString("loadType");
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTextureFile() {
        return this.textureFile;
    }

    public void setTextureFile(String textureFile) {
        this.textureFile = textureFile;
    }

    public String getLoadType() {
        return this.loadType;
    }

    public void setLoadType(String loadType) {
        this.loadType = loadType;
    }

    @Override
    public String toString() {
        return getName();
    }
}
