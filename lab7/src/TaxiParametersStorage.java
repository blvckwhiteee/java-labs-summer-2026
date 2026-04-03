import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaxiParametersStorage {
    private static final Pattern TAXI_OBJECT_PATTERN = Pattern.compile("\\{(.*?)\\}", Pattern.DOTALL);
    private static final Pattern FIELD_PATTERN =
            Pattern.compile("\"(.*?)\"\\s*:\\s*(\"(?:\\\\.|[^\"])*\"|-?\\d+(?:\\.\\d+)?)");

    public void save(List<Taxi> taxis, Path path) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("[\n");

        for (int i = 0; i < taxis.size(); i++) {
            Taxi taxi = taxis.get(i);
            json.append("  {\n");
            json.append("    \"manufacturer\": \"").append(escapeJson(taxi.getManufacturer())).append("\",\n");
            json.append("    \"model\": \"").append(escapeJson(taxi.getModel())).append("\",\n");
            json.append("    \"color\": \"").append(escapeJson(taxi.getColor())).append("\",\n");
            json.append("    \"tankCapacity\": ").append(taxi.getTankCapacity()).append(",\n");
            json.append("    \"producingYear\": ").append(taxi.getProducingYear()).append(",\n");
            json.append("    \"taxiId\": \"").append(escapeJson(taxi.getTaxiId())).append("\",\n");
            json.append("    \"driverName\": \"").append(escapeJson(taxi.getDriverName())).append("\"\n");
            json.append("  }");
            if (i < taxis.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]\n");

        Files.writeString(path, json.toString(), StandardCharsets.UTF_8);
    }

    public List<Taxi> load(Path path) throws IOException {
        String content = Files.readString(path, StandardCharsets.UTF_8).trim();
        List<Taxi> taxis = new ArrayList<>();

        if (content.isEmpty() || content.equals("[]")) {
            return taxis;
        }
        if (!content.startsWith("[") || !content.endsWith("]")) {
            throw new IOException("Invalid JSON parameters format.");
        }

        Matcher objectMatcher = TAXI_OBJECT_PATTERN.matcher(content);
        while (objectMatcher.find()) {
            JsonTaxiData data = parseTaxiData(objectMatcher.group(1));
            taxis.add(new Taxi(
                    data.manufacturer(),
                    data.model(),
                    data.color(),
                    data.tankCapacity(),
                    data.producingYear(),
                    data.taxiId(),
                    data.driverName()
            ));
        }

        return taxis;
    }

    private JsonTaxiData parseTaxiData(String objectBody) throws IOException {
        String manufacturer = null;
        String model = null;
        String color = null;
        Double tankCapacity = null;
        Integer producingYear = null;
        String taxiId = null;
        String driverName = null;

        Matcher fieldMatcher = FIELD_PATTERN.matcher(objectBody);
        while (fieldMatcher.find()) {
            String fieldName = fieldMatcher.group(1);
            String rawValue = fieldMatcher.group(2);

            switch (fieldName) {
                case "manufacturer" -> manufacturer = parseJsonString(rawValue);
                case "model" -> model = parseJsonString(rawValue);
                case "color" -> color = parseJsonString(rawValue);
                case "tankCapacity" -> tankCapacity = Double.parseDouble(rawValue);
                case "producingYear" -> producingYear = Integer.parseInt(rawValue);
                case "taxiId" -> taxiId = parseJsonString(rawValue);
                case "driverName" -> driverName = parseJsonString(rawValue);
                default -> {
                }
            }
        }

        if (manufacturer == null || model == null || color == null
                || tankCapacity == null || producingYear == null
                || taxiId == null || driverName == null) {
            throw new IOException("JSON object does not contain all required taxi fields.");
        }

        return new JsonTaxiData(manufacturer, model, color, tankCapacity, producingYear, taxiId, driverName);
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    private String parseJsonString(String rawValue) {
        String trimmed = rawValue.substring(1, rawValue.length() - 1);
        return trimmed
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    private record JsonTaxiData(
            String manufacturer,
            String model,
            String color,
            double tankCapacity,
            int producingYear,
            String taxiId,
            String driverName
    ) {
    }
}
