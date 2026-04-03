import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TaxiObjectStorage {
    public void save(List<Taxi> taxis, Path path) throws IOException {
        FleetData fleetData = new FleetData(new ArrayList<>(taxis));
        try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(path))) {
            outputStream.writeObject(fleetData);
        }
    }

    public List<Taxi> load(Path path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(path))) {
            Object loadedObject = inputStream.readObject();
            if (!(loadedObject instanceof FleetData fleetData)) {
                throw new IOException("Unsupported objects file format.");
            }
            return new ArrayList<>(fleetData.taxis());
        }
    }

    private record FleetData(List<Taxi> taxis) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
}
