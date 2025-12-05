package board;

public class Square {
    private final int file;  // 0–7 for a–h
    private final int rank;  // 0–7 for 1–8

    public Square(int file, int rank) {
        this.file = file;
        this.rank = rank;
    }

    public int getFile() {
        return file;
    }

    public int getRank() {
        return rank;
    }

    @Override
    public String toString() {
        char fileChar = (char) ('a' + file);
        int rankNum = rank + 1;
        return "" + fileChar + rankNum;
    }
}
