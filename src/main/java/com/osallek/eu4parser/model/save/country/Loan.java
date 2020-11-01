package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.common.Eu4Utils;
import com.osallek.eu4parser.model.save.Id;

import java.time.LocalDate;

public class Loan {

    private final ClausewitzItem item;

    private Id id;

    public Loan(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public Id getId() {
        return id;
    }

    public String getLender() {
        String lender = this.item.getVarAsString("lender");

        if (lender != null) {
            return Eu4Utils.DEFAULT_TAG_QUOTES.equals(lender) ? null : lender;
        }

        return null;
    }

    public void setLender(String lender) {
        if (lender == null) {
            lender = Eu4Utils.DEFAULT_TAG_QUOTES;
        }

        this.item.setVariable("lender", ClausewitzUtils.addQuotes(lender));
    }

    public Double getInterest() {
        return this.item.getVarAsDouble("interest");
    }

    public void setInterest(double interest) {
        this.item.setVariable("interest", interest);
    }

    public Boolean fixedInterest() {
        return this.item.getVarAsBool("fixed_interest");
    }

    public void setFixedInterest(boolean fixedInterest) {
        this.item.setVariable("fixed_interest", fixedInterest);
    }

    public Integer getAmount() {
        return this.item.getVarAsInt("amount");
    }

    public void setAmount(int amount) {
        this.item.setVariable("amount", amount);
    }

    public LocalDate getExpiryDate() {
        return this.item.getVarAsDate("expiry_date");
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.item.setVariable("expiry_date", expiryDate);
    }

    public Boolean getSpawned() {
        return this.item.getVarAsBool("spawned");
    }

    public void setSpawned(boolean spawned) {
        this.item.setVariable("spawned", spawned);
    }

    private void refreshAttributes() {
        ClausewitzItem idItem = this.item.getChild("id");

        if (idItem != null) {
            this.id = new Id(idItem);
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, int id, double interest, boolean fixedInterest, int amount, LocalDate expiryDate) {
        return addToItem(parent, id, Eu4Utils.DEFAULT_TAG_QUOTES, interest, fixedInterest, amount, expiryDate);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, int id, String lender, double interest, boolean fixedInterest, int amount, LocalDate expiryDate) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "loan", parent.getOrder() + 1);
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

        parent.addChild(toItem);

        return toItem;
    }
}
