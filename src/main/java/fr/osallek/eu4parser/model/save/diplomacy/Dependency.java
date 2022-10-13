package fr.osallek.eu4parser.model.save.diplomacy;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.game.SubjectType;
import fr.osallek.eu4parser.model.game.SubjectTypeUpgrade;
import fr.osallek.eu4parser.model.save.Save;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Dependency extends DatableRelation {

    public Dependency(ClausewitzItem item, Save save) {
        super(item, save);
    }

    public SubjectType getSubjectType() {
        return this.save.getGame().getSubjectType(ClausewitzUtils.removeQuotes(this.item.getVarAsString("subject_type")));
    }

    public void setSubjectType(SubjectType subjectType) {
        this.item.setVariable("subject_type", ClausewitzUtils.addQuotes(subjectType.getName()));
    }

    public List<SubjectTypeUpgrade> getSubjectTypeUpgrades() {
        return Optional.ofNullable(this.item.getList("subject_type_upgrades"))
                       .map(ClausewitzList::getValues)
                       .map(strings -> strings.stream()
                                              .map(s -> this.save.getGame().getSubjectTypeUpgrade(s))
                                              .filter(Objects::nonNull)
                                              .collect(Collectors.toList()))
                       .filter(CollectionUtils::isNotEmpty)
                       .orElse(null);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String first, String second, LocalDate startDate, SubjectType type) {
        ClausewitzItem toItem = DatableRelation.addToItem(parent, "dependency", first, second, startDate);
        toItem.addVariable("subject_type", ClausewitzUtils.addQuotes(type.getName()));

        return toItem;
    }
}
