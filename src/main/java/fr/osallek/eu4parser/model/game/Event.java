package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class Event {

    private final ClausewitzItem item;

    public Event(ClausewitzItem item) {
        this.item = item;
    }

    public String getId() {
        return this.item.getVarAsString("id");
    }

    public void setId(String id) {
        this.item.setVariable("id", id);
    }

    public String getTitle() {
        return this.item.getVarAsString("title");
    }

    public void setTitle(String title) {
        this.item.setVariable("id", title);
    }

    public String getDesc() {
        return this.item.getVarAsString("desc");
    }

    public void setDesc(String desc) {
        this.item.setVariable("desc", desc);
    }

    public String getPicture() {
        return this.item.getVarAsString("picture");
    }

    public void setPicture(String picture) {
        if (StringUtils.isBlank(picture)) {
            this.item.removeVariable("picture");
        } else {
            this.item.setVariable("picture", picture);
        }
    }

    public EventType getType() {
        return EventType.getByType(this.item.getName());
    }

    public void setType(EventType type) {
        this.item.setName(type.type);
    }

    public Boolean fireOnlyOnce() {
        return this.item.getVarAsBool("fire_only_once");
    }

    public void setFireOnlyOnce(Boolean fireOnlyOnce) {
        if (fireOnlyOnce == null) {
            this.item.removeVariable("fire_only_once");
        } else {
            this.item.setVariable("fire_only_once", fireOnlyOnce);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Event event)) {
            return false;
        }

        return Objects.equals(getId(), event.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return getId();
    }
}
