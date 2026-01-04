package com.example.demo.dto;

import com.example.demo.model.WaybillRecord;
import java.util.List;
import java.util.ArrayList;
import com.example.demo.dto.FailureRow;


public class BulkWaybillResult {
    
private int total;
private int success;
private int failed;

private List<WaybillRecord> successRecords=new ArrayList<>();
private List<FailureRow> failures=new ArrayList<>();

public BulkWaybillResult() {

}

public BulkWaybillResult(int total, int success, int failed,
                        List<WaybillRecord> successRecords,
                        List<FailureRow> failures) {
    this.total = total;
    this.success = success;
    this.failed = failed;
    this.successRecords = successRecords;
    this.failures = failures;

}

public int getTotal() {
    return total;
} 

public int getSuccess() {
    return success;
}

public int getFailed() {
    return failed;
}

public List<WaybillRecord> getSuccessRecords() {
    return successRecords;
}

public List<FailureRow> getFailures() {
    return failures;
}

public void setFailed(int failed) {
    this.failed = failed;
}

public void setSuccess(int success) {
    this.success = success;
}

public void setTotal(int total) {
    this.total = total;

}   
public void setSuccessRecords(List<WaybillRecord> successRecords) {
    this.successRecords = successRecords;
}
public void setFailures(List<FailureRow> failures) {
    this.failures = failures;
} 

}
