package com.example.demo.controller;

import com.example.demo.model.WaybillRecord;
import com.example.demo.repository.WaybillFileRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/waybills")
@CrossOrigin
public class WaybillHistoryController {

    private final WaybillFileRepository repository;

    public WaybillHistoryController(WaybillFileRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<WaybillRecord> getAll() {
        return repository.findAll();
    }
}
