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

    public static Square fromString(String s) {
        if (s == null || s.length() != 2) return null;

        char f = s.charAt(0); // 'a'..'h'
        char r = s.charAt(1); // '1'..'8'

        int file = f - 'a';
        int rank = r - '1';

        if (file < 0 || file > 7 || rank < 0 || rank > 7) return null;

        return new Square(file, rank);
    }
    @Override
    public String toString() {
        char fileChar = (char) ('a' + file);
        int rankNum = rank + 1;
        return "" + fileChar + rankNum;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Square)) return false;
        Square other = (Square) obj;
        return file == other.file && rank == other.rank;
    }

    @Override
    public int hashCode() {
        return 31 * file + rank;
    }
}
