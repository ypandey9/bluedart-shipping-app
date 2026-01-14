"use client";
import axios from "axios";
import { useState } from "react";
function CancelWaybill() { 
const [awbNo, setAwbNo] = useState("");
const[message, setMessage]=useState("");

const cancelWaybill = async () => {

    try {
        const response = await axios.post(`http://localhost:8080/api/bluedart/cancel?awbNo=${awbNo}`);
        const result=response.data.CancelWaybillResult;
        setMessage(result.Status[0].StatusInformation);
    } catch (error) {
        setMessage("Error cancelling waybill");
    } 
   
};

const downloadTemplate = async () => {
  try {
    const response = await axios.get(
      "http://localhost:8080/api/bluedart/waybill/cancel/template",
      {
        responseType: "blob"   // 🔴 REQUIRED
      }
    );

    const url = window.URL.createObjectURL(
      new Blob([response.data])
    );

    const link = document.createElement("a");
    link.href = url;
    link.setAttribute(
      "download",
      "Bluedart_Cancel_Waybill_Template.xlsx"
    );

    document.body.appendChild(link);
    link.click();
    link.remove();
  } catch (error) {
    alert("Failed to download template");
  }
};



return (
    <div className="m-5">
        <h1 className="text-xl font-bold ml-10">Cancel Waybill</h1>
        <div>
        <button onClick={downloadTemplate} className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-green-700 ml-10">Download Bulk Template</button>
        </div>
        <input type="text"
            value={awbNo}
            onChange={(e) => setAwbNo(e.target.value)}
            placeholder="Enter AWB Number"
            className="mt-4 ml-5 p-2 border border-gray-300 rounded"
            minLength={10}
            required
        />
        <div>
        <button onClick={cancelWaybill} className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 ml-10 mt-3">Cancel Waybill</button>
        </div>
        {message && <p>{message}</p>}
    </div>
);
}
export default CancelWaybill;
