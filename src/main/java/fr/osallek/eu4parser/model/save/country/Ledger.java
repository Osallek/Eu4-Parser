package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;

import java.util.EnumMap;
import java.util.Map;

public record Ledger(ClausewitzItem item) {

    public Map<Income, Double> getIncome() {
        ClausewitzList list = this.item.getList("income");
        return getIncomeFromList(list);
    }

    public Map<Expense, Double> getExpense() {
        ClausewitzList list = this.item.getList("expense");
        return getExpenseFromList(list);
    }

    public Map<Income, Double> getThisMonthIncome() {
        ClausewitzList list = this.item.getList("thismonthincome");
        return getIncomeFromList(list);
    }

    public Map<Expense, Double> getThisMonthExpense() {
        ClausewitzList list = this.item.getList("thismonthexpense");
        return getExpenseFromList(list);
    }

    public Double getLastMonthIncome() {
        return this.item.getVarAsDouble("lastmonthincome");
    }

    public Map<Income, Double> getLastMonthIncomeTable() {
        ClausewitzList list = this.item.getList("lastmonthincometable");
        return getIncomeFromList(list);
    }

    public Double getLastMonthExpense() {
        return this.item.getVarAsDouble("lastmonthexpense");
    }

    public Map<Expense, Double> getLastMonthExpenseTable() {
        ClausewitzList list = this.item.getList("lastmonthexpensetable");
        return getExpenseFromList(list);
    }

    public Map<Expense, Double> getTotalExpenseTable() {
        ClausewitzList list = this.item.getList("totalexpensetable");
        return getExpenseFromList(list);
    }

    public Map<Income, Double> getLastYearIncome() {
        ClausewitzList list = this.item.getList("lastyearincome");
        return getIncomeFromList(list);
    }

    public Map<Expense, Double> getLastYearExpense() {
        ClausewitzList list = this.item.getList("lastyearexpense");
        return getExpenseFromList(list);
    }

    private Map<Income, Double> getIncomeFromList(ClausewitzList list) {
        Map<Income, Double> incomes = new EnumMap<>(Income.class);

        if (list == null) {
            return incomes;
        }

        for (Income income : Income.values()) {
            incomes.put(income, list.getAsDouble(income.ordinal()));
        }

        return incomes;
    }

    private Map<Expense, Double> getExpenseFromList(ClausewitzList list) {
        Map<Expense, Double> expenses = new EnumMap<>(Expense.class);

        if (list == null) {
            return expenses;
        }

        for (Expense expense : Expense.values()) {
            expenses.put(expense, list.getAsDouble(expense.ordinal()));
        }

        return expenses;
    }
}
