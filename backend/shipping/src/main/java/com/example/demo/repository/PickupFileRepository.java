package com.example.demo.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Repository
public class PickupFileRepository {

    private static final String DATA_DIR = "data";
    private static final String FILE_NAME = "pickup-history.json";

    private final ObjectMapper objectMapper;
    private final Path filePath;

    public PickupFileRepository() throws IOException {

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Path baseDir = Paths.get(DATA_DIR);
        if (!Files.exists(baseDir)) {
            Files.createDirectories(baseDir);
        }

        this.filePath = baseDir.resolve(FILE_NAME);

        if (!Files.exists(filePath)) {
            Files.write(filePath, "[]".getBytes(StandardCharsets.UTF_8));
        }
    }

    public synchronized void save(JsonNode historyNode) {

        try {
            // read existing array
            ArrayNode arrayNode = (ArrayNode)
                objectMapper.readTree(filePath.toFile());

            // append new entry
            arrayNode.add(historyNode);

            // write back
            objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(filePath.toFile(), arrayNode);

            System.out.println("✅ Pickup history saved");

        } catch (Exception e) {
            throw new RuntimeException("Failed to save pickup history", e);
        }
    }
}
