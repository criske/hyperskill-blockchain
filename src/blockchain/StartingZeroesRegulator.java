package blockchain;

public abstract class StartingZeroesRegulator {

    protected volatile int n;

    protected String status = "";

    public StartingZeroesRegulator(int startingZeroes) {
        n = startingZeroes;
    }

    abstract void regulate(int timeSpentSeconds);

    public int getStartingZeroes() {
        return n;
    }

    public String status() {
        return status;
    }

    public static class DefaultStartingZeroesRegulator extends StartingZeroesRegulator {

        public DefaultStartingZeroesRegulator(int startingZeroes) {
            super(startingZeroes);
        }

        @Override
        synchronized void regulate(int timeSpentSeconds) {
            if (timeSpentSeconds <= 1) {
                int lastN = n;
                n = Math.min(n + 1, 4);
                if (lastN == n) {
                    status = "N stays the same";
                } else {
                    status = "N was increased to " + n;
                }
            } else if (timeSpentSeconds >=5) {
                n = Math.max(n - 1, 0);
                status = "N was decreased by 1";
            } else {
                status = "N stays the same";
            }
        }
    }
}
