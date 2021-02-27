package fr.osallek.eu4parser.model;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Mod {

    private final File file;

    private final ClausewitzItem item;

    public Mod(File file, ClausewitzItem item) {
        this.file = file;
        this.item = item;
    }

    public String getName() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("name"));
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public String getSupportedVersion() {
        return this.item.getVarAsString("supported_version");
    }

    public void setSupportedVersion(String supportedVersion) {
        this.item.setVariable("supported_version", supportedVersion);
    }

    public String getRemoteFileId() {
        return this.item.getVarAsString("remote_file_id");
    }

    public void setRemoteFileId(String remoteFileId) {
        this.item.setVariable("remote_file_id", remoteFileId);
    }

    public File getPath() {
        return new File(ClausewitzUtils.removeQuotes(this.item.getVarAsString("path")));
    }

    public void setPath(File path) {
        this.item.setVariable("path", ClausewitzUtils.addQuotes(path.getAbsolutePath()));
    }

    public List<String> getTags() {
        ClausewitzList list = this.item.getList("tags");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public void setTags(List<String> tags) {
        ClausewitzList list = this.item.getList("tags");

        if (list == null) {
            list = this.item.addList("tags", false, tags);
        } else {
            list.clear();
            list.addAll(tags);
        }
    }

    public List<String> getDependencies() {
        ClausewitzList list = this.item.getList("dependencies");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public void setDependencies(List<String> dependencies) {
        ClausewitzList list = this.item.getList("dependencies");

        if (list == null) {
            list = this.item.addList("dependencies", false, dependencies);
        } else {
            list.clear();
            list.addAll(dependencies);
        }
    }

    public List<String> getReplacePath() {
        return this.item.getVarsAsStrings("replace_path");
    }

    public void setReplacePath(List<String> replacePath) {
        this.item.removeVariables("replace_path");

        replacePath.forEach(s -> this.item.addVariable("replace_path", ClausewitzUtils.addQuotes(s)));
    }

    public String getPicture() {
        return this.item.getVarAsString("picture");
    }

    public void setPicture(String picture) {
        this.item.setVariable("picture", picture);
    }

    public File getFile() {
        return file;
    }
}
