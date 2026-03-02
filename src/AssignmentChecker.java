import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;

public class AssignmentChecker {

    private static String CANVAS_URL; // Will be loaded from config
    private static String API_TOKEN;  // Will be loaded from file
    private static final ZoneId PST_ZONE = ZoneId.of("America/Los_Angeles");

    public static void main(String[] args) {
        try {
            // Check if config file path was provided
            if (args.length < 2) {
                System.err.println("Error: Please provide the path to your config and token files");
                System.err.println("Usage: java AssignmentChecker <path-to-config-file> <path-to-token-file>");
                System.exit(1);
            }

            // Load config from file
            String configFilePath = args[0];
            String tokenFilePath = args[1];
            
            try {
                String configContent = Files.readString(Paths.get(configFilePath)).trim();
                JSONObject config = new JSONObject(configContent);
                CANVAS_URL = config.getJSONObject("canvas").getString("url");
                
                API_TOKEN = Files.readString(Paths.get(tokenFilePath)).trim();
                
                if (API_TOKEN.isEmpty()) {
                    System.err.println("Error: Token file is empty");
                    System.exit(1);
                }
            } catch (IOException e) {
                System.err.println("Error: Could not read config or token file");
                System.err.println(e.getMessage());
                System.exit(1);
            }
            
            // Rest of the code remains the same...