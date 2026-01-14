package com.example.demo.dto;

public class FailureRow {

    private int rowNo;
    private String reference;
    private String error;

    public FailureRow(int rowNo, String reference, String error) {
        this.rowNo = rowNo;
        this.reference = reference;
        this.error = error;
    }
    
    public int getRowNo() {
        return rowNo;
    }   
    
    public String getReference() {
        return reference;
    }
    
    public String getError() {
        return error;       
    }
}