import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class StoragePaths {
    private static final Path OUTPUT_DIRECTORY = Paths.get("out");
    private static final Path OBJECTS_FILE = OUTPUT_DIRECTORY.resolve("fleet-objects.ser");
    private static final Path PARAMETERS_FILE = OUTPUT_DIRECTORY.resolve("fleet-parameters.json");

    private StoragePaths() {
    }

    public static Path getObjectsFile() throws IOException {
        ensureOutputDirectory();
        return OBJECTS_FILE;
    }

    public static Path getParametersFile() throws IOException {
        ensureOutputDirectory();
        return PARAMETERS_FILE;
    }

    private static void ensureOutputDirectory() throws IOException {
        Files.createDirectories(OUTPUT_DIRECTORY);
    }
}
