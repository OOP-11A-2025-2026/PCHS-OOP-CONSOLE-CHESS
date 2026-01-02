package board;

/**
 * Represents a square on the chess board.
 * Uses file (column a-h = 0-7) and rank (row 1-8 = 0-7) coordinates.
 */
public class Square {
    private final int file;  // 0–7 for a–h
    private final int rank;  // 0–7 for 1–8

    /**
     * Creates a new Square with the specified file and rank.
     * @param file The file (column) index, 0-7 corresponding to a-h
     * @param rank The rank (row) index, 0-7 corresponding to 1-8
     */
    public Square(int file, int rank) {
        this.file = file;
        this.rank = rank;
    }

    /**
     * Gets the file (column) index of this square.
     * @return File index (0-7 for a-h)
     */
    public int getFile() {
        return file;
    }

    /**
     * Gets the rank (row) index of this square.
     * @return Rank index (0-7 for 1-8)
     */
    public int getRank() {
        return rank;
    }

    /**
     * Parses a square from algebraic notation string (e.g., "e4", "a1").
     * @param s The algebraic notation string (2 characters: file letter + rank number)
     * @return The corresponding Square object, or null if invalid input
     */
    public static Square fromString(String s) {
        if (s == null || s.length() != 2) return null;

        char f = s.charAt(0); // 'a'..'h'
        char r = s.charAt(1); // '1'..'8'

        int file = f - 'a';
        int rank = r - '1';

        if (file < 0 || file > 7 || rank < 0 || rank > 7) return null;

        return new Square(file, rank);
    }

    /**
     * Converts this square to algebraic notation string.
     * @return String representation (e.g., "e4", "a1")
     */
    @Override
    public String toString() {
        char fileChar = (char) ('a' + file);
        int rankNum = rank + 1;
        return "" + fileChar + rankNum;
    }

    /**
     * Checks equality with another object.
     * @param obj Object to compare with
     * @return true if both squares have the same file and rank
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Square)) return false;
        Square other = (Square) obj;
        return file == other.file && rank == other.rank;
    }

    /**
     * Generates a hash code for this square.
     * @return Hash code based on file and rank
     */
    @Override
    public int hashCode() {
        return 31 * file + rank;
    }
}
