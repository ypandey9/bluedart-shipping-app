package com.example.demo.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PickupRequest {

    @JsonProperty("AWBNo")
    private List<String> awbNo;

    @JsonProperty("AreaCode")
    private String areaCode;

    @JsonProperty("CISDDN")
    private Boolean cisddn;

    @JsonProperty("ContactPersonName")
    private String contactPersonName;

    @JsonProperty("CustomerAddress1")
    private String customerAddress1;

    @JsonProperty("CustomerAddress2")
    private String customerAddress2;

    @JsonProperty("CustomerAddress3")
    private String customerAddress3;

    @JsonProperty("CustomerCode")
    private String customerCode;

    @JsonProperty("CustomerName")
    private String customerName;

    @JsonProperty("CustomerPincode")
    private String customerPincode;

    @JsonProperty("CustomerTelephoneNumber")
    private String customerTelephoneNumber;

    @JsonProperty("DoxNDox")
    private String doxNDox;

    @JsonProperty("EmailID")
    private String emailId;

    @JsonProperty("IsForcePickup")
    private Boolean isForcePickup;

    @JsonProperty("IsReversePickup")
    private Boolean isReversePickup;

    @JsonProperty("MobileTelNo")
    private String mobileTelNo;

    @JsonProperty("NumberofPieces")
    private String numberOfPieces;

    @JsonProperty("OfficeCloseTime")
    private String officeCloseTime;

    @JsonProperty("PackType")
    private String packType;

    @JsonProperty("ProductCode")
    private String productCode;

    @JsonProperty("ReferenceNo")
    private String referenceNo;

    @JsonProperty("Remarks")
    private String remarks;

    @JsonProperty("RouteCode")
    private String routeCode;

    @JsonProperty("ShipmentPickupDate")
    private String shipmentPickupDate;

    @JsonProperty("ShipmentPickupTime")
    private String shipmentPickupTime;

    @JsonProperty("SubProducts")
    private List<String> subProducts;

    @JsonProperty("VolumeWeight")
    private Double volumeWeight;

    @JsonProperty("WeightofShipment")
    private Double weightOfShipment;

    @JsonProperty("isToPayShipper")
    private Boolean isToPayShipper;
}
