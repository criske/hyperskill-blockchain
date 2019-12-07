package blockchain;

public interface Hasher {

     Result hash(int startingZeros, String... inputs);

    class SHA256Hasher implements Hasher {

        @Override
        public Result hash(int startingZeros, String... inputs) {
            if (startingZeros < 0) {
                throw new IllegalArgumentException("Starting zeroes must >=0");
            }
            String joinedInputs = String.join("", inputs);
            int magicNumber = 1;
            String hash;
            long start = System.currentTimeMillis();
            while (true) {
                hash = StringUtil.applySha256(magicNumber + joinedInputs);
                if (hash.substring(0, startingZeros).equals("0".repeat(startingZeros))){
                    break;
                }
                magicNumber++;
            }
            long end = System.currentTimeMillis();
            int timeSpent = (int)(end - start) / 1000;
            return new Result(magicNumber, hash, timeSpent);
        }
    }


    class Result {
        final int magicNumber;
        final String hash;
        final int timeSpentSeconds;

        public Result(final int magicNumber, final String hash, int timeSpentSeconds) {
            this.magicNumber = magicNumber;
            this.hash = hash;
            this.timeSpentSeconds = timeSpentSeconds;
        }
    }

}

