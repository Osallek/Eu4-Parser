package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzObject;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.game.IdeaGroup;
import com.osallek.eu4parser.model.save.Save;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class IdeaGroups {

    private final Save save;

    private final ClausewitzItem item;

    public IdeaGroups(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
    }

    public Map<IdeaGroup, Integer> getIdeaGroups() {
        return this.item.getVariables()
                        .stream()
                        .collect(Collectors.toMap(var -> this.save.getGame().getIdeaGroup(var.getName()),
                                                  ClausewitzVariable::getAsInt,
                                                  (a, b) -> b,
                                                  LinkedHashMap::new));
    }

    public Map<String, Integer> getIdeaGroupsNames() {
        return this.item.getVariables()
                        .stream()
                        .collect(Collectors.toMap(ClausewitzObject::getName, ClausewitzVariable::getAsInt, (a, b) -> b, LinkedHashMap::new));
    }

    public void setIdeaGroup(IdeaGroup ideaGroup, int level) {
        if (level < 0) {
            level = 0;
        } else if (level > ideaGroup.getIdeas().size()) {
            level = ideaGroup.getIdeas().size();
        }

        this.item.setVariable(ideaGroup.getName(), level);
    }

    public void removeIdeaGroup(int index) {
        this.item.removeVariable(index);
    }

    public void removeIdeaGroup(IdeaGroup ideaGroup) {
        this.item.removeVariable(ideaGroup.getName());
    }

    public boolean hasIdea(String name) {
        return this.getIdeaGroups()
                   .entrySet()
                   .stream()
                   .anyMatch(entry -> entry.getKey().getIdeas().containsKey(name)
                                      && entry.getKey().getIdeas().keySet().stream().limit(entry.getValue()).anyMatch(name::equals));
    }
}
