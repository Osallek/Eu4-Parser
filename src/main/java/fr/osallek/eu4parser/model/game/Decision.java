package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class Decision extends Nodded {

    private final ClausewitzItem item;

    public Decision(FileNode fileNode, ClausewitzItem item) {
        super(fileNode);
        this.item = item;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        this.item.write(writer, true, 0, new HashMap<>());
    }

    @Override
    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Boolean isMajor() {
        return this.item.getVarAsBool("major");
    }

    public void setMajor(Boolean major) {
        if (major == null) {
            this.item.removeVariable("major");
        } else {
            this.item.setVariable("major", major);
        }
    }

    public Integer getAiImportance() {
        return this.item.getVarAsInt("ai_importance");
    }

    public void setAiImportance(Integer aiImportance) {
        if (aiImportance == null) {
            this.item.removeVariable("ai_importance");
        } else {
            this.item.setVariable("ai_importance", aiImportance);
        }
    }

    public Integer getDoNotCore() {
        return this.item.getVarAsInt("do_not_core");
    }

    public void setDoNotCore(Integer doNotCore) {
        if (doNotCore == null) {
            this.item.removeVariable("do_not_core");
        } else {
            this.item.setVariable("do_not_core", doNotCore);
        }
    }

    public String getDoNotIntegrate() {
        return this.item.getVarAsString("do_not_integrate");
    }

    public void setDoNotIntegrate(String doNotIntegrate) {
        if (doNotIntegrate == null) {
            this.item.removeVariable("do_not_integrate");
        } else {
            this.item.setVariable("do_not_integrate", doNotIntegrate);
        }
    }

    public ConditionAnd getPotential() {
        return Optional.ofNullable(this.item.getChild("potential")).map(ConditionAnd::new).orElse(null);
    }

    public ConditionAnd getProvincesToHighlight() {
        return Optional.ofNullable(this.item.getChild("provinces_to_highlight")).map(ConditionAnd::new).orElse(null);
    }

    public ConditionAnd getAllow() {
        return Optional.ofNullable(this.item.getChild("allow")).map(ConditionAnd::new).orElse(null);
    }

    public Modifiers getEffects() {
        return new Modifiers(this.item.getChild("effect"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Decision decision)) {
            return false;
        }

        return Objects.equals(getName(), decision.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
