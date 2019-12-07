package blockchain.vc;

import java.util.List;

public class Transaction {

    public final String fromName;
    public final List<Entry> toEntries;

    public Transaction(String fromName, Entry... entries) {
        this.fromName = fromName;
        this.toEntries = List.of(entries);
    }


    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < toEntries.size(); i++) {
            out.append(fromName).append(" ").append(toEntries.get(i));
            if (i < toEntries.size() - 1) {
                out.append("\n");
            }
        }
        return out.toString();
    }


    public static class Entry {
        public final String toName;
        public final int vcAmount;

        public Entry(String toName, int vcAmount) {
            this.toName = toName;
            this.vcAmount = vcAmount;
        }

        @Override
        public String toString() {
            return "sent " + vcAmount + " VC to " + toName;
        }
    }
}
