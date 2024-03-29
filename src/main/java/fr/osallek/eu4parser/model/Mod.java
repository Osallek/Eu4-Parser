package fr.osallek.eu4parser.model;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzPObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public record Mod(File file, ClausewitzItem item, LauncherSettings launcherSettings) {

    public String getName() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("name"));
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public String getVersion() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("version"));
    }

    public void setVersion(String version) {
        this.item.setVariable("version", ClausewitzUtils.addQuotes(version));
    }

    public String getSupportedVersion() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("supported_version"));
    }

    public void setSupportedVersion(String supportedVersion) {
        this.item.setVariable("supported_version", ClausewitzUtils.addQuotes(supportedVersion));
    }

    public String getRemoteFileId() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("remote_file_id"));
    }

    public void setRemoteFileId(String remoteFileId) {
        this.item.setVariable("remote_file_id", ClausewitzUtils.addQuotes(remoteFileId));
    }

    public String getPathString() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("path"));
    }

    public Path getPath() {
        return Path.of(getPathString()).isAbsolute() ? Path.of(getPathString()) : this.launcherSettings.getGameDataPath().resolve(getPathString());
    }

    public void setPath(Path path) {
        this.item.setVariable("path", ClausewitzUtils.addQuotes(path.toFile().getAbsolutePath()));
    }

    public void setPath(String path) {
        this.item.setVariable("path", ClausewitzUtils.addQuotes(path));
    }

    public String getArchiveString() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("archive"));
    }

    public Path getArchive() {
        return Path.of(getArchiveString()).isAbsolute() ? Path.of(getArchiveString()) : this.launcherSettings.getGameDataPath().resolve(getArchiveString());
    }

    public void setArchive(Path archive) {
        this.item.setVariable("archive", ClausewitzUtils.addQuotes(archive.toFile().getAbsolutePath()));
    }

    public void setArchive(String archive) {
        this.item.setVariable("archive", ClausewitzUtils.addQuotes(archive));
    }

    public List<String> getTags() {
        ClausewitzList list = this.item.getList("tags");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(ClausewitzUtils::removeQuotes).toList();
    }

    public void setTags(List<String> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            this.item.removeList("tags");
        } else {
            tags = tags.stream().map(ClausewitzUtils::addQuotes).toList();
            ClausewitzList list = this.item.getList("tags");

            if (list == null) {
                this.item.addList("tags", false, tags);
            } else {
                list.clear();
                list.addAll(tags);
            }
        }
    }

    public List<String> getDependencies() {
        ClausewitzList list = this.item.getList("dependencies");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(ClausewitzUtils::removeQuotes).toList();
    }

    public void setDependencies(List<String> dependencies) {
        if (CollectionUtils.isEmpty(dependencies)) {
            this.item.removeList("dependencies");
        } else {
            dependencies = dependencies.stream().map(ClausewitzUtils::addQuotes).toList();
            ClausewitzList list = this.item.getList("dependencies");

            if (list == null) {
                this.item.addList("dependencies", false, dependencies);
            } else {
                list.clear();
                list.addAll(dependencies);
            }
        }
    }

    public List<String> getReplacePath() {
        return this.item.getVarsAsStrings("replace_path").stream().map(ClausewitzUtils::removeQuotes).toList();
    }

    public void setReplacePath(List<String> replacePath) {
        replacePath = replacePath.stream().map(ClausewitzUtils::addQuotes).toList();
        this.item.removeVariables("replace_path");

        replacePath.forEach(s -> this.item.addVariable("replace_path", ClausewitzUtils.addQuotes(s)));
    }

    public String getPicture() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("picture"));
    }

    public void setPicture(String picture) {
        if (StringUtils.isBlank(picture)) {
            this.item.removeVariable("picture");
        } else {
            this.item.setVariable("picture", ClausewitzUtils.addQuotes(picture));
        }
    }

    public ModType getType() {
        return ModType.getByFileName(this.file.getName());
    }

    public void save() throws IOException {
        save(new HashMap<>());
    }

    public void save(Map<Predicate<ClausewitzPObject>, Consumer<String>> listeners) throws IOException {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(this.file.toPath(), StandardCharsets.UTF_8)) {
            this.item.write(bufferedWriter, 0, listeners);
        }
    }
}
