package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.save.Id;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Predicate;

public class Loan {

    private final ClausewitzItem item;

    public Loan(ClausewitzItem item) {
        this.item = item;
    }

    public Optional<Id> getId() {
        return this.item.getChild("id").map(Id::new);
    }

    public Optional<String> getLender() {
        return this.item.getVarAsString("lender").filter(Predicate.not(Eu4Utils.DEFAULT_TAG_QUOTES::equals));
    }

    public void setLender(String lender) {
        if (lender == null) {
            lender = Eu4Utils.DEFAULT_TAG_QUOTES;
        }

        this.item.setVariable("lender", ClausewitzUtils.addQuotes(lender));
    }

    public Optional<Double> getInterest() {
        return this.item.getVarAsDouble("interest");
    }

    public void setInterest(double interest) {
        this.item.setVariable("interest", interest);
    }

    public Optional<Boolean> fixedInterest() {
        return this.item.getVarAsBool("fixed_interest");
    }

    public void setFixedInterest(boolean fixedInterest) {
        this.item.setVariable("fixed_interest", fixedInterest);
    }

    public Optional<Integer> getAmount() {
        return this.item.getVarAsInt("amount");
    }

    public void setAmount(int amount) {
        this.item.setVariable("amount", amount);
    }

    public Optional<LocalDate> getExpiryDate() {
        return this.item.getVarAsDate("expiry_date");
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.item.setVariable("expiry_date", expiryDate);
    }

    public Optional<Boolean> getSpawned() {
        return this.item.getVarAsBool("spawned");
    }

    public void setSpawned(boolean spawned) {
        this.item.setVariable("spawned", spawned);
    }

    public Optional<Boolean> getEstateLoan() {
        return this.item.getVarAsBool("estate_loan");
    }

    public void setEstateLoan(boolean estateLoan) {
        this.item.setVariable("estate_loan", estateLoan);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, int id, double interest, boolean fixedInterest, int amount, LocalDate expiryDate) {
        return addToItem(parent, id, Eu4Utils.DEFAULT_TAG_QUOTES, interest, fixedInterest, amount, expiryDate);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, int id, String lender, double interest, boolean fixedInterest, int amount,
                                           LocalDate expiryDate) {
        Optional<ClausewitzItem> otherLoan = parent.getLastChild("loan");
        ClausewitzItem toItem = new ClausewitzItem(parent, "loan",
                                                   otherLoan.map(clausewitzItem -> clausewitzItem.getOrder() + 1).orElse(parent.getOrder() + 1));
        Id.addToItem(toItem, id, 4713);

        if (lender == null) {
            lender = Eu4Utils.DEFAULT_TAG_QUOTES;
        }

        toItem.addVariable("lender", ClausewitzUtils.addQuotes(lender));
        toItem.addVariable("interest", interest);
        toItem.addVariable("fixed_interest", fixedInterest);
        toItem.addVariable("amount", amount);
        toItem.addVariable("expiry_date", expiryDate);
        toItem.addVariable("spawned", false);
        toItem.addVariable("estate_loan", false);

        parent.addChild(toItem, otherLoan.isPresent());

        return toItem;
    }
}
