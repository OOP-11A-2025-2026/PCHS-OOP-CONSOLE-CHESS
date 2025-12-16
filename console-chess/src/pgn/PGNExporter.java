package pgn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Simple PGN exporter that formats tags and a list of SAN tokens into PGN text.
 * Supports generation and file saving.
 */
public class PGNExporter {

    /**
     * Generate PGN text from tags and SAN moves.
     */
    public static String generate(Map<String, String> tags, List<String> sanMoves) {
        StringBuilder sb = new StringBuilder();
        if (tags != null) {
            for (Map.Entry<String, String> e : tags.entrySet()) {
                sb.append("[").append(e.getKey()).append(" \"").append(e.getValue()).append("\"]\n");
            }
            sb.append('\n');
        }
        if (sanMoves != null && !sanMoves.isEmpty()) {
            int moveNum = 1;
            for (int i = 0; i < sanMoves.size(); i += 2) {
                sb.append(moveNum).append(". ");
                sb.append(sanMoves.get(i));
                if (i + 1 < sanMoves.size()) {
                    sb.append(' ').append(sanMoves.get(i + 1));
                }
                sb.append(' ');
                moveNum++;
            }
        }
        return sb.toString().trim() + "\n";
    }

    /**
     * Generate PGN text and save it to a file.
     */
    public static void saveToFile(Path filePath, Map<String, String> tags, List<String> sanMoves) throws IOException {
        String pgnText = generate(tags, sanMoves);
        Files.writeString(filePath, pgnText);
    }

    /**
     * Generate PGN text with a result and save to file.
     */
    public static void saveToFile(Path filePath, Map<String, String> tags, List<String> sanMoves, String result) throws IOException {
        String pgnText = generate(tags, sanMoves);
        if (result != null && !result.isEmpty()) {
            pgnText = pgnText.trim() + " " + result + "\n";
        }
        Files.writeString(filePath, pgnText);
    }
}
