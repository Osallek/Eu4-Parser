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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public record Mod(File file, ClausewitzItem item, LauncherSettings launcherSettings) {

    public Optional<String> getName() {
        return this.item.getVarAsString("name").map(ClausewitzUtils::removeQuotes);
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public Optional<String> getVersion() {
        return this.item.getVarAsString("version").map(ClausewitzUtils::removeQuotes);
    }

    public void setVersion(String version) {
        this.item.setVariable("version", ClausewitzUtils.addQuotes(version));
    }

    public Optional<String> getSupportedVersion() {
        return this.item.getVarAsString("supported_version").map(ClausewitzUtils::removeQuotes);
    }

    public void setSupportedVersion(String supportedVersion) {
        this.item.setVariable("supported_version", ClausewitzUtils.addQuotes(supportedVersion));
    }

    public Optional<String> getRemoteFileId() {
        return this.item.getVarAsString("remote_file_id").map(ClausewitzUtils::removeQuotes);
    }

    public void setRemoteFileId(String remoteFileId) {
        this.item.setVariable("remote_file_id", ClausewitzUtils.addQuotes(remoteFileId));
    }

    public Optional<String> getPathString() {
        return this.item.getVarAsString("path").map(ClausewitzUtils::removeQuotes);
    }

    public Optional<Path> getPath() {
        return getPathString().map(Path::of).map(path -> path.isAbsolute() ? path : this.launcherSettings.getGameDataPath().resolve(path));
    }

    public void setPath(Path path) {
        this.item.setVariable("path", ClausewitzUtils.addQuotes(path.toFile().getAbsolutePath()));
    }

    public void setPath(String path) {
        this.item.setVariable("path", ClausewitzUtils.addQuotes(path));
    }

    public Optional<String> getArchiveString() {
        return this.item.getVarAsString("archive").map(ClausewitzUtils::removeQuotes);
    }

    public Optional<Path> getArchive() {
        return getArchiveString().map(Path::of).map(path -> path.isAbsolute() ? path : this.launcherSettings.getGameDataPath().resolve(path));
    }

    public void setArchive(Path archive) {
        this.item.setVariable("archive", ClausewitzUtils.addQuotes(archive.toFile().getAbsolutePath()));
    }

    public void setArchive(String archive) {
        this.item.setVariable("archive", ClausewitzUtils.addQuotes(archive));
    }

    public List<String> getTags() {
        return this.item.getList("tags").map(ClausewitzList::getValues).stream().flatMap(Collection::stream).map(ClausewitzUtils::removeQuotes).toList();
    }

    public void setTags(List<String> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            this.item.removeList("tags");
        } else {
            tags = tags.stream().map(ClausewitzUtils::addQuotes).toList();
            List<String> finalTags = tags;
            this.item.getList("tags").ifPresentOrElse(list -> {
                list.clear();
                list.addAll(finalTags);
            }, () -> this.item.addList("tags", false, finalTags));
        }
    }

    public List<String> getDependencies() {
        return this.item.getList("dependencies")
                        .map(ClausewitzList::getValues)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(ClausewitzUtils::removeQuotes)
                        .toList();
    }

    public void setDependencies(List<String> dependencies) {
        if (CollectionUtils.isEmpty(dependencies)) {
            this.item.removeList("dependencies");
        } else {
            dependencies = dependencies.stream().map(ClausewitzUtils::addQuotes).toList();
            List<String> finalDependencies = dependencies;
            this.item.getList("tags").ifPresentOrElse(list -> {
                list.clear();
                list.addAll(finalDependencies);
            }, () -> this.item.addList("tags", false, finalDependencies));
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

    public Optional<String> getPicture() {
        return this.item.getVarAsString("picture").map(ClausewitzUtils::removeQuotes).filter(StringUtils::isNotBlank);
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
