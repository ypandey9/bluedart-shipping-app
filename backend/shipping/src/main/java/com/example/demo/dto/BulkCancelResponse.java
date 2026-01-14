package com.example.demo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkCancelResponse {
    
    private int total;
    private  int success;
    private int failed;
    private List<BulkCancelResult> results;

    
}
