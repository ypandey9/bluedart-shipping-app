"use client";
import { useState } from "react";
import axios from "axios";

function BulkCancelWaybill() {
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [progress, setProgress] = useState(0);
  const [result, setResult] = useState<any>(null);

  const BACKEND = process.env.NEXT_PUBLIC_BACKEND_URL;

  const downloadTemplate = async () => {
  try {
    const response = await axios.get(
      `${BACKEND}/api/bluedart/waybill/cancel/template`,
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



  const handleUpload = async () => {
    if (!file) {
      alert("Please select a file");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
      setLoading(true);
      setProgress(0);

      const response = await axios.post(
        `${BACKEND}/api/bluedart/cancel/bulk`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data"
          },
          onUploadProgress: (event) => {
            if (event.total) {
              const percent = Math.round(
                (event.loaded * 100) / event.total
              );
              setProgress(percent);
            }
          }
        }
      );

      setResult(response.data);
    } catch (err) {
      console.error(err);
      alert("Bulk cancellation failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2 className="text-xl font-bold ml-10">Bulk Waybill Cancellation</h2>
        <div>
        <button onClick={downloadTemplate} className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-green-700 ml-10">⬇️Download Bulk Template</button>
        

<div className="flex items-center gap-3 mt-5">
      <input
        id="bulkFile"
        type="file"
        accept=".xlsx"
        onChange={(e) => {
          const selectedFile = e.target.files?.[0];
          if (selectedFile) {
            setFile(selectedFile);
          }
        }}
        className="hidden"
      />

      <label
              htmlFor="bulkFile"
              className="cursor-pointer bg-gray-200 hover:bg-gray-300 px-4 py-2 ml-10 rounded border"
            >
              Browse…
            </label>

             <span className="text-sm text-gray-700 truncate max-w-xs">
              {file ? file.name : "No file selected"}
            </span>
            </div>

      <button onClick={handleUpload} disabled={loading} className="bg-blue-600 text-white px-6 py-2 mt-5 ml-15 rounded hover:bg-blue-700 disabled:opacity-60">
        Upload & Cancel
      </button>
      </div>

      {loading && <p>Uploading... {progress}%</p>}

      {result && (
        <>
          <h3>Summary</h3>
          <p>Total: {result.total}</p>
          <p>Success: {result.success}</p>
          <p>Failed: {result.failed}</p>

          <table border="1">
            <thead>
              <tr>
                <th>AWB No</th>
                <th>Status</th>
                <th>Message</th>
              </tr>
            </thead>
            <tbody>
              {result.results.map((r: any, idx: number) => (
                <tr key={idx}>
                  <td>{r.awbNo}</td>
                  <td>{r.status}</td>
                  <td>{r.message}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      )}
    </div>
  );
}

export default BulkCancelWaybill;
