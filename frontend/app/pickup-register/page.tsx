"use client";
import { useState } from "react";

export default function PickupRegister() {

  const [pickupStatus, setPickupStatus] = useState<string | null>(null);
const [tokenNumber, setTokenNumber] = useState<string | null>(null);


  const [form, setForm] = useState({
    customerCode: "",
    customerName: "",
    customerAddress: "",
    contactNo: "",
    weight: "",
    isToPay: false,
    productCode: "A",
    packType: "",
    officeCloseTime: "1800",
    noOfPieces: "1",
    pickupDate: "",
    pickupTime: "1600"
  });

  const handleChange = (e: any) => {
    const { name, value, type, checked } = e.target;
    setForm({
      ...form,
      [name]: type === "checkbox" ? checked : value
    });
  };

  const submitPickup = async () => {

  // ✅ validation
  if (!form.customerCode || !form.customerName || !form.weight) {
    alert("Please fill mandatory fields");
    return;
  }

const pickupEpoch = new Date(form.pickupDate + "T00:00:00").getTime();

if (isNaN(pickupEpoch)) {
  alert("Invalid pickup date");
  return;
}


  // ✅ payload
  const payload = {
    request: {
      AWBNo: [""],
      AreaCode: "DEL",
      CISDDN: false,
      ContactPersonName: form.customerName,
      CustomerAddress1: form.customerAddress,
      CustomerAddress2: "Test address 2",
      CustomerAddress3: "Test address 3",
      CustomerCode: form.customerCode,
      CustomerName: form.customerName,
      CustomerPincode: "110020",
      CustomerTelephoneNumber: "",
      DoxNDox: "?",
      EmailID: "",
      IsForcePickup: false,
      IsReversePickup: false,
      MobileTelNo: form.contactNo,
      NumberofPieces: form.noOfPieces,
      OfficeCloseTime: form.officeCloseTime,
      PackType: form.packType,
      ProductCode: form.productCode,
      ReferenceNo: "",
      Remarks: "",
      RouteCode: "",
      ShipmentPickupDate:`/Date(${pickupEpoch})/`,
      ShipmentPickupTime: "1600", 
      SubProducts: ["E-Tailing"],
      VolumeWeight: parseFloat(form.weight),
      WeightofShipment: parseFloat(form.weight),
      isToPayShipper: form.isToPay
    },
    profile: {
      LoginID: "GG940111",
      LicenceKey: "kh7mnhqkmgegoksipxr0urmqesesseup",
      Api_type: "S"
    }
  };

  // ✅ API call (THIS WAS MISSING)
  const response = await fetch("http://localhost:8080/api/pickup/register", {
  method: "POST",
  headers: {
    "Content-Type": "application/json"
  },
  body: JSON.stringify(payload)
});

const data = await response.json();

const result = data.RegisterPickupResult;

const statusInfoRaw =
  result?.Status && result.Status.length > 0
    ? result.Status[0].StatusInformation
    : "";

    const statusInfo =
  statusInfoRaw && statusInfoRaw.trim().length > 0
    ? statusInfoRaw
    : "Pickup registered successfully";

const token = result?.TokenNumber ?? "";

setPickupStatus(statusInfo);
setTokenNumber(token);


  // const data = await response.json();
  // console.log("Pickup Response:", data);
  // alert("Pickup registered successfully");
};

  return (
    <div style={{ padding: 20 }}>
      <h2>Pickup Registration</h2>

      <input name="customerCode" placeholder="Customer Code"
        onChange={handleChange} /><br/>

      <input name="customerName" placeholder="Customer Name"
        onChange={handleChange} /><br/>

      <textarea name="customerAddress" placeholder="Customer Address"
        onChange={handleChange} /><br/>

      <input name="contactNo" placeholder="Contact No"
        onChange={handleChange} /><br/>

      <input name="weight" placeholder="Weight (kg)"
        onChange={handleChange} /><br/>

      <label>
        <input type="checkbox" name="isToPay"
          onChange={handleChange} /> Is To Pay
      </label><br/>

      <select name="productCode" onChange={handleChange}>
        <option value="A">Air</option>
        <option value="E">Surface</option>
        <option value="D">DP</option>
      </select><br/>

      <input name="packType" placeholder="Pack Type"
        onChange={handleChange} /><br/>

      <input name="officeCloseTime" placeholder="Office Close Time (HHmm)"
        onChange={handleChange} /><br/>

      <input name="noOfPieces" placeholder="No of Pieces"
        onChange={handleChange} /><br/>

      <input type="date" name="pickupDate"
        onChange={handleChange} value={form.pickupDate}/><br/>

      <input type="time" name="pickupTime"
        onChange={handleChange} /><br/>

      <button onClick={submitPickup}>Register Pickup</button>
      {pickupStatus && (
  <div style={{ marginTop: 20, padding: 12, border: "1px solid green" }}>
    <p><strong>Status:</strong> {pickupStatus}</p>
    <p><strong>Token Number:</strong> {tokenNumber}</p>
  </div>
)}

    </div>

    
  );
}
