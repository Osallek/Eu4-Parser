package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public record Ledger(ClausewitzItem item) {

    public Map<Income, Double> getIncome() {
        return getIncomeFromList(this.item.getList("income"));
    }

    public Map<Expense, Double> getExpense() {
        return getExpenseFromList(this.item.getList("expense"));
    }

    public Map<Income, Double> getThisMonthIncome() {
        return getIncomeFromList(this.item.getList("thismonthincome"));
    }

    public Map<Expense, Double> getThisMonthExpense() {
        return getExpenseFromList(this.item.getList("thismonthexpense"));
    }

    public Optional<Double> getLastMonthIncome() {
        return this.item.getVarAsDouble("lastmonthincome");
    }

    public Map<Income, Double> getLastMonthIncomeTable() {
        return getIncomeFromList(this.item.getList("lastmonthincometable"));
    }

    public Optional<Double> getLastMonthExpense() {
        return this.item.getVarAsDouble("lastmonthexpense");
    }

    public Map<Expense, Double> getLastMonthExpenseTable() {
        return getExpenseFromList(this.item.getList("lastmonthexpensetable"));
    }

    public Map<Expense, Double> getTotalExpenseTable() {
        return getExpenseFromList(this.item.getList("totalexpensetable"));
    }

    public Map<Income, Double> getLastYearIncome() {
        return getIncomeFromList(this.item.getList("lastyearincome"));
    }

    public Map<Expense, Double> getLastYearExpense() {
        return getExpenseFromList(this.item.getList("lastyearexpense"));
    }

    private Map<Income, Double> getIncomeFromList(Optional<ClausewitzList> list) {
        Map<Income, Double> incomes = new EnumMap<>(Income.class);

        if (list.isEmpty()) {
            return incomes;
        }

        for (Income income : Income.values()) {
            list.get().getAsDouble(income.ordinal()).ifPresent(aDouble -> incomes.put(income, aDouble));
        }

        return incomes;
    }

    private Map<Expense, Double> getExpenseFromList(Optional<ClausewitzList> list) {
        Map<Expense, Double> expenses = new EnumMap<>(Expense.class);

        if (list.isEmpty()) {
            return expenses;
        }

        for (Expense expense : Expense.values()) {
            list.get().getAsDouble(expense.ordinal()).ifPresent(aDouble -> expenses.put(expense, aDouble));
        }

        return expenses;
    }
}
