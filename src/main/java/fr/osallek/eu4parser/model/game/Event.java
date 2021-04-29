package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class Event {

    private final String id;

    private String localizedName;

    private final String title;

    private final String desc;

    private final String picture;

    private final EventType type;

    private final boolean fireOnlyOnce;

    public Event(ClausewitzItem item) {
        this.id = item.getVarAsString("id");
        this.localizedName = this.id;
        this.title = item.getVarAsString("title");
        this.desc = item.getVarAsString("desc");
        this.picture = item.getVarAsString("picture");
        this.type = EventType.getByType(item.getName());
        this.fireOnlyOnce = BooleanUtils.toBoolean(item.getVarAsBool("fire_only_once"));
    }

    public String getId() {
        return this.id;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        if (StringUtils.isNotBlank(localizedName)) {
            this.localizedName = localizedName;
        }
    }

    public String getTitle() {
        return this.title;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getPicture() {
        return this.picture;
    }

    public EventType getType() {
        return this.type;
    }

    public boolean fireOnlyOnce() {
        return this.fireOnlyOnce;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Event)) {
            return false;
        }

        Event tradeGood = (Event) o;
        return Objects.equals(getId(), tradeGood.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return this.localizedName;
    }
}
