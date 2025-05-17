import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/// https://github.com/ashishps1/awesome-low-level-design/blob/main/problems/splitwise.md
/// Requirements
/// - The system should allow users to create accounts and manage their profile information.
/// - Users should be able to create groups and add other users to the groups.
/// - Users should be able to add expenses within a group, specifying the amount, description, and participants.
/// - The system should automatically split the expenses among the participants based on their share.
/// - Users should be able to view their individual balances with other users and settle up the balances.
/// - The system should support different split methods, such as equal split, percentage split, and exact amounts.
/// - Users should be able to view their transaction history and group expenses.
/// - The system should handle concurrent transactions and ensure data consistency.

class User {
    private final UUID id;
    private String name;

    public User(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null
            || !(other instanceof User otherUser))
            return false;
        
        return id.equals(otherUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User [" +
                "Id=" + id +
                ", Name=" + name +
                "]";
    }

    public String name() {
        return name;
    }
}

class Group {
    private final UUID id;
    private String name;

    private final User admin;
    private final Set<User> members;

    private final List<Expense> expenses;
    private final Map<User, Map<User, Double>> totals;
    private int logPlayedTill = 0;
    

    public Group(String name, User admin, Set<User> members) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.admin = admin;
        this.members = new CopyOnWriteArraySet<>();

        this.members.add(admin);
        this.members.addAll(members);
        this.expenses = new CopyOnWriteArrayList<>();

        this.totals = new ConcurrentHashMap<>();
    }

    /**
     * 
     * @param user user to add
     * @return true if user was not there in group already
     */
    public boolean addUser(User user) {
        return members.add(user);
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null
            || !(other instanceof Group otherGroup))
            return false;
        
        return id.equals(otherGroup.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Group [" +
                "Id=" + id +
                ", Name=" + name +
                "]";
    }

    synchronized void playLogs() {
        while (logPlayedTill < expenses.size()) {
            Expense expense = expenses.get(logPlayedTill);
            Map<User, Map<User, Double>> userOwesTo = expense.userOwesTo();

            for (var entry: userOwesTo.entrySet()) {
                User payer = entry.getKey();

                for (var receiverAndAmount: entry.getValue().entrySet()) {
                    User receiver = receiverAndAmount.getKey();
                    double owedAmount = receiverAndAmount.getValue();

                    var paymentMap = totals.computeIfAbsent(payer, x -> new HashMap<>());

                    paymentMap.putIfAbsent(receiver, 0D);
                    paymentMap.put(receiver, paymentMap.get(receiver) + owedAmount);
                }
            }

            logPlayedTill++;
        }
    }

    double requiredAmountForSettlement(User payer, User receiver) {
        playLogs();

        var payersTotals =  totals.get(payer); 
        if (payersTotals == null || !payersTotals.containsKey(receiver)) return 0;
        return payersTotals.get(receiver);
    }

    // TODO compute and save
    public String groupSummary() {
        playLogs();
        Map<User, Map<User, Double>> result = totals;

        StringBuilder sb = new StringBuilder();

        for (var entry: result.entrySet()) {
            User payer = entry.getKey();
            sb.append(payer.name() + " owes to: \n");
            for (var receiverAndAmount: entry.getValue().entrySet()) {
                User receiver = receiverAndAmount.getKey();
                double amount = receiverAndAmount.getValue();

                sb.append("\t")
                    .append("to: ")
                    .append(receiver.name())
                    .append(" amount: ")
                    .append(amount)
                    .append("\n");
            }
        }

        return sb.toString();
    }

    public void printTransactions() {
        for (Expense expense: expenses)
            System.out.println(expense + "\n");
    }
}

sealed abstract class Expense {
    private final User paidByUser;
    private final double amount;
    private final String description;

    Expense(User paidByUser, double amount, String description) {
        if (amount <= 0) throw new IllegalArgumentException("Amount should be positive, but found to be: " + amount);
        this.paidByUser = paidByUser;
        this.amount = amount;
        this.description = description;
    }

    public double amount() { return amount; }
    public String description() { return description; }
    public User paidByUser() { return paidByUser; }

    public abstract Map<User, Map<User, Double>> userOwesTo();

    public static final class SpiltEqual extends Expense {
        private final List<User> splitAmong;

        public SpiltEqual(double amount, String description, User paidByUser, List<User> splitAmong) {
            super(paidByUser, amount, description);
            this.splitAmong = List.copyOf(splitAmong);
        }

        public List<User> splitAmong() { return Collections.unmodifiableList(splitAmong); }

        @Override
        public Map<User, Map<User, Double>> userOwesTo() {
            Map<User, Map<User, Double>> result = new HashMap<>();

            double amountPerUser = this.amount() / this.splitAmong().size();
            for (User user: this.splitAmong()) {
                if (user.equals(paidByUser())) continue;

                result.put(user, Map.of(this.paidByUser(), amountPerUser));
            }

            return result;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Expense [")
                .append("Paid By: ")
                .append(paidByUser().name())
                .append(", Amount: ")
                .append(amount())
                .append(", Description: ")
                .append(description())
                .append(", Split Equal: ")
                .append(splitAmong())
                .append("]");
            return sb.toString();
        }
    }

    public static final class SpiltPercentWise extends Expense {
        private final Map<User, Integer> userAndPercentMap;

        SpiltPercentWise(double amount, String description, User paidByUser, Map<User, Integer> userAndPercentMap) {
            super(paidByUser, amount, description);

            int totalPercent = userAndPercentMap
                .values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();

            if (totalPercent != 100)
                throw new IllegalArgumentException("Sum of percent should be 100, but it was found: " + totalPercent);
            
            this.userAndPercentMap = Map.copyOf(userAndPercentMap);
        }

        public Map<User, Integer> userAndPercentMap() { return Collections.unmodifiableMap(userAndPercentMap); }

        @Override
        public Map<User, Map<User, Double>> userOwesTo() {
            Map<User, Map<User, Double>> result = new HashMap<>();

            for (var entry: userAndPercentMap.entrySet()) {
                User user = entry.getKey();
                int percent = entry.getValue();

                if (user.equals(paidByUser())) continue;

                double owedAmount = (amount() * percent) / 100D;
                result.put(user, Map.of(paidByUser(), owedAmount));
            }

            return result;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Expense [")
                .append("Paid By: ")
                .append(paidByUser())
                .append(", Amount: ")
                .append(amount())
                .append(", Description: ")
                .append(description())
                .append(", Split Percent Wise: ")
                .append(userAndPercentMap())
                .append("]");
            return sb.toString();
        }
    }

    public static final class SplitExact extends Expense {
        private final Map<User, Double> userAndAmount;

        public SplitExact(double amount, String description, User paidByUser, Map<User, Double> userAndAmount) {
            super(paidByUser, amount, description);

            double totalAmount = userAndAmount
                .values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();
            
            if (totalAmount != amount)
                throw new IllegalArgumentException("Sum of all amount should equal: " + amount + " but, it was found to be: " + totalAmount);

            this.userAndAmount = Map.copyOf(userAndAmount);
        }

        public Map<User, Double> userAndAmount() { return Collections.unmodifiableMap(userAndAmount); }
        
        @Override
        public Map<User, Map<User, Double>> userOwesTo() {
            Map<User, Map<User, Double>> result = new HashMap<>();

            for (var entry: userAndAmount.entrySet()) {
                User user = entry.getKey();
                double owedAmount = entry.getValue();

                if (user.equals(paidByUser())) continue;

                result.put(user, Map.of(paidByUser(), owedAmount));
            }

            return result;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Expense [")
                .append("Paid By: ")
                .append(paidByUser())
                .append(", Amount: ")
                .append(amount())
                .append(", Description: ")
                .append(description())
                .append(", Split Exact: ")
                .append(userAndAmount())
                .append("]");
            return sb.toString();
        }
    }

    public static final class Settlment extends Expense {
        private final User receiver;

        public Settlment(double amount, String description, User paidByUser, User receiver) {
            super(paidByUser, amount, description);
            this.receiver = receiver;
        }

        @Override
        public Map<User, Map<User, Double>> userOwesTo() {
            return Map.of(receiver, Map.of(paidByUser(), amount()));
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Expense [")
                .append("Paid By: ")
                .append(paidByUser())
                .append(", Amount: ")
                .append(amount())
                .append(", Description: ")
                .append(description())
                .append(", Receiver: ")
                .append(receiver)
                .append("]");
            return sb.toString();
        }
    }
}

class SplitwiseSystem {
    private final Set<User> users;
    private final Set<Group> groups;

    public SplitwiseSystem() {
        users = new CopyOnWriteArraySet<>();
        groups = new CopyOnWriteArraySet<>();
    }

    public User addUser(String userName) {
        User user = new User(userName);
        users.add(user);
        return user;
    }

    public Group createGroup(User admin, String groupName, List<User> otherMembers) {
        // TODO validate admin, otherMembers, and add adming in group;

        Set<User> usersInGroup = new HashSet<>();
        usersInGroup.add(admin);
        usersInGroup.addAll(otherMembers);
        Group group = new Group(groupName, admin, usersInGroup);
        return group;
    }

    public Group createGroup(User admin, String groupName) {
        return createGroup(admin, groupName, Collections.emptyList());
    }

    public void splitEqual(User paidByUser, Group group, double amount, String description, List<User> splitAmong) {
        Expense expense = new Expense.SpiltEqual(amount, description, paidByUser, splitAmong);
        group.addExpense(expense);
    }

    public void splitPercentWise(User paidByUser, Group group, double amount, String description, Map<User, Integer> userAndPercentMap) {
        Expense expense = new Expense.SpiltPercentWise(amount, description, paidByUser, userAndPercentMap);
        group.addExpense(expense);
    }

    public void splitExact(User paidByUser, Group group, double amount, String description, Map<User, Double> userAndAmountMap) {
        Expense expense = new Expense.SplitExact(amount, description, paidByUser, userAndAmountMap);
        group.addExpense(expense);
    }

    public void settleExpense(Group group, User user1, User user2) {
        double user1ToUser2 = group.requiredAmountForSettlement(user1, user2);
        double user2ToUser1 = group.requiredAmountForSettlement(user2, user1);
        if (Math.abs(user1ToUser2 - user2ToUser1) == 0) return;

        User payer = user1ToUser2 > user2ToUser1 ? user1 : user2;
        User reciver = user1ToUser2 > user2ToUser1 ? user2 : user1;
        double amount = Math.abs(user1ToUser2 - user2ToUser1);

        group.addExpense(new Expense.Settlment(amount, "Settlement", payer, reciver));
    }

    public String groupSummary(Group himalaya) {
        return himalaya.groupSummary();
    }

    public void printTransactions(Group group) {
        group.printTransactions();
    }
}

class UserNotFound extends RuntimeException {
    public UserNotFound(String message) { super(message); }
}

interface Splitwise {
    static void main(String... args) {
        SplitwiseSystem splitwiseSystem = new SplitwiseSystem();
        User alice = splitwiseSystem.addUser("Alice");
        User bob = splitwiseSystem.addUser("Bob");
        User charlie = splitwiseSystem.addUser("Charlie");

        Group himalaya = splitwiseSystem.createGroup(alice, "Himalayas", List.of(alice, bob));

        splitwiseSystem.splitEqual(charlie, himalaya, 10, "First Payment", List.of(alice, bob, charlie));
        // splitwiseSystem.splitEqual(alice, himalaya, 10, "First Payment", List.of(alice, bob, charlie));
        splitwiseSystem.settleExpense(himalaya, alice, charlie);
        splitwiseSystem.printTransactions(himalaya);

        System.out.println(splitwiseSystem.groupSummary(himalaya));
        System.out.println(splitwiseSystem.groupSummary(himalaya));
    }
}
