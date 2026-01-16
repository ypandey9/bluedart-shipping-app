package com.example.demo.repository;

import com.example.demo.model.WaybillRecord;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Repository
public class WaybillFileRepository {

    private static final String FILE_PATH = "data/waybills.json";
    private final ObjectMapper mapper = new ObjectMapper();

    public synchronized void save(WaybillRecord record) {
        try {
            File file = new File(FILE_PATH);

            // ✅ Ensure parent directory exists
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            List<WaybillRecord> list;

            // ✅ Initialize file if missing OR empty
            if (!file.exists() || file.length() == 0) {
                list = new ArrayList<>();
            } else {
                list = mapper.readValue(
                        file,
                        new TypeReference<List<WaybillRecord>>() {}
                );
            }

            // ✅ Append new record
            list.add(record);

            // ✅ Write full array back
            mapper.writerWithDefaultPrettyPrinter()
                  .writeValue(file, list);

        } catch (Exception e) {
            throw new RuntimeException("Failed to save waybill", e);
        }
    }

    public WaybillRecord findByAwbNo(String awbNo) {
    return findAll().stream()
            .filter(w -> awbNo.equals(w.getAwbNo()))
            .findFirst()
            .orElse(null);
}


public synchronized void saveAll(List<WaybillRecord> records) {
    try {
        File file = new File(FILE_PATH);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        List<WaybillRecord> existing =
                file.exists() && file.length() > 0
                        ? mapper.readValue(file, new TypeReference<>() {})
                        : new ArrayList<>();

        existing.addAll(records);

        mapper.writerWithDefaultPrettyPrinter()
              .writeValue(file, existing);

    } catch (Exception e) {
        throw new RuntimeException("Failed to save waybills", e);
    }
}



    public List<WaybillRecord> findAll() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    file,
                    new TypeReference<List<WaybillRecord>>() {}
            );
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
