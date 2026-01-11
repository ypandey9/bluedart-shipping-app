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

return (
    <div className="m-5">
        <h1 className="text-xl font-bold ml-10">Cancel Waybill</h1>
        <input type="text"
            value={awbNo}
            onChange={(e) => setAwbNo(e.target.value)}
            placeholder="Enter AWB Number"
            className="m-4 p-2 border border-gray-300 rounded"
            minLength={10}
            required
        />
        <div>
        <button onClick={cancelWaybill} className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 ml-10">Cancel Waybill</button>
        </div>
        {message && <p>{message}</p>}
    </div>
);
}
export default CancelWaybill;
