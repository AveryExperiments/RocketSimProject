import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class CustomRocketStorage {

    private static final Path STORAGE_DIR = Path.of("custom_rockets");

    static {
        try {
            Files.createDirectories(STORAGE_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create custom rocket storage directory", e);
        }
    }

    public static void saveRocket(RocketMaker maker, String name) throws IOException {
        if (maker == null) {
            throw new IllegalArgumentException("Rocket maker is null");
        }
        String cleanName = name == null ? "" : name.trim();
        if (cleanName.isEmpty()) {
            throw new IllegalArgumentException("Rocket must have a name");
        }

        String safeName = cleanName.replaceAll("[^a-zA-Z0-9_-]", "_");
        Path file = STORAGE_DIR.resolve(safeName + ".properties");

        Properties props = new Properties();
        props.setProperty("name", cleanName);
        props.setProperty("noseConeType", maker.noseConeType == null ? "" : maker.noseConeType.name());
        props.setProperty("bodyType", maker.bodyType == null ? "" : maker.bodyType.name());
        props.setProperty("finType", maker.finType == null ? "" : maker.finType.name());
        props.setProperty("engineType", maker.engineType == null ? "" : maker.engineType.name());
        props.setProperty("bodyLength", Double.toString(maker.getBodyLength()));
        props.setProperty("noseConeDensity", Double.toString(maker.getNoseConeDensity()));
        props.setProperty("bodyTubeDensity", Double.toString(maker.getBodyTubeDensity()));
        props.setProperty("finSizeScale", Double.toString(maker.getFinSizeScale()));
        props.setProperty("finLengthScale", Double.toString(maker.getFinLengthScale()));

        try (OutputStream out = Files.newOutputStream(file)) {
            props.store(out, "custom rocket");
        }
    }

    public static RocketMaker loadRocket(String name) throws IOException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Rocket name is required");
        }

        String safeName = name.trim().replaceAll("[^a-zA-Z0-9_-]", "_");
        Path file = STORAGE_DIR.resolve(safeName + ".properties");
        if (!Files.exists(file)) {
            throw new IOException("Saved rocket not found: " + name);
        }

        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(file)) {
            props.load(in);
        }

        RocketMaker maker = new RocketMaker();

        String noseType = props.getProperty("noseConeType");
        if (noseType != null && !noseType.isEmpty()) {
            maker.chooseNoseConeType(CustomConstants.noseCone.valueOf(noseType));
        }

        String bodyType = props.getProperty("bodyType");
        if (bodyType != null && !bodyType.isEmpty()) {
            maker.chooseBodyType(CustomConstants.body.valueOf(bodyType));
        }

        String finType = props.getProperty("finType");
        if (finType != null && !finType.isEmpty()) {
            maker.chooseFinType(CustomConstants.finType.valueOf(finType));
        }

        String engineType = props.getProperty("engineType");
        if (engineType != null && !engineType.isEmpty()) {
            maker.chooseEngine(CustomConstants.rocketEngine.valueOf(engineType));
        }

        String bodyLength = props.getProperty("bodyLength");
        if (bodyLength != null && !bodyLength.isEmpty()) {
            maker.setBodyLength(Double.parseDouble(bodyLength));
        }

        String noseDensity = props.getProperty("noseConeDensity");
        if (noseDensity != null && !noseDensity.isEmpty()) {
            maker.setNoseConeDensity(Double.parseDouble(noseDensity));
        }

        String bodyDensity = props.getProperty("bodyTubeDensity");
        if (bodyDensity != null && !bodyDensity.isEmpty()) {
            maker.setBodyTubeDensity(Double.parseDouble(bodyDensity));
        }

        String finSizeScale = props.getProperty("finSizeScale");
        if (finSizeScale != null && !finSizeScale.isEmpty()) {
            maker.setFinSizeScale(Double.parseDouble(finSizeScale));
        }

        String finLengthScale = props.getProperty("finLengthScale");
        if (finLengthScale != null && !finLengthScale.isEmpty()) {
            maker.setFinLengthScale(Double.parseDouble(finLengthScale));
        }

        return maker;
    }

    public static List<String> listRocketNames() {
        List<String> names = new ArrayList<>();
        if (!Files.exists(STORAGE_DIR)) {
            return names;
        }

        try {
            Files.list(STORAGE_DIR)
                    .filter(path -> path.getFileName().toString().endsWith(".properties"))
                    .forEach(path -> {
                        String fileName = path.getFileName().toString();
                        fileName = fileName.substring(0, fileName.length() - ".properties".length());
                        names.add(fileName.replace('_', ' '));
                    });
        } catch (IOException e) {
            return Collections.emptyList();
        }

        Collections.sort(names);
        return names;
    }
}
