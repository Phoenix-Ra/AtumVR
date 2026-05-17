package me.phoenixra.atumvr.example;

import me.phoenixra.atumvr.api.AtumVRLogger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class ExampleResources {


    private static final List<String> BUNDLED = List.of(
            "shaders/vertex.vsh",
            "shaders/fragment.fsh",
            "textures/test.png"
    );

    private ExampleResources() {
    }


    public static void extractAll(@NotNull File dataFolder,
                                  @NotNull AtumVRLogger logger) {
        for (String resource : BUNDLED) {
            try {
                extract(dataFolder, resource, logger);
            } catch (IOException e) {
                logger.logError("Failed to extract resource '"
                        + resource + "': " + e.getMessage());
            }
        }
    }

    private static void extract(@NotNull File dataFolder,
                                @NotNull String resource,
                                @NotNull AtumVRLogger logger) throws IOException {
        Path target = dataFolder.toPath().resolve(resource);
        if (Files.exists(target)) {
            logger.logDebug("Resource already present, skipping: " + resource);
            return;
        }
        try (InputStream in = ExampleResources.class.getClassLoader()
                .getResourceAsStream(resource)) {
            if (in == null) {
                logger.logWarn("Bundled resource not found on classpath: " + resource);
                return;
            }
            Files.createDirectories(target.getParent());
            Files.copy(in, target);
            logger.logInfo("Extracted resource: " + resource);
        }
    }
}