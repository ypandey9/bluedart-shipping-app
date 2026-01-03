"use client";

import { useState } from "react";

export default function BulkWaybillPage() {
  const [file, setFile] = useState<File | null>(null);
  const [labelSize, setLabelSize] = useState("A4");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  const BACKEND = process.env.NEXT_PUBLIC_BACKEND_URL;

  /* ---------------- DOWNLOAD TEMPLATE ---------------- */

  const downloadTemplate = () => {
    window.location.href =
      `${BACKEND}/api/bluedart/waybill/bulk/template`;
  };

  /* ---------------- BULK UPLOAD ---------------- */

  const uploadBulkFile = async () => {
    if (!file) {
      setError("Please select an XLSX or CSV file");
      return;
    }

    setLoading(true);
    setError(null);
    setSuccessMsg(null);

    const formData = new FormData();
    formData.append("file", file);
    formData.append("size", labelSize);

    try {
      const res = await fetch(
        `${BACKEND}/api/bluedart/waybill/bulk`,
        {
          method: "POST",
          body: formData,
        }
      );

      if (!res.ok) {
        const errText = await res.text();
        throw new Error(errText || "Bulk upload failed");
      }

      // ✅ PDF response
      const blob = await res.blob();

      // Try to extract filename from header
      const disposition = res.headers.get("Content-Disposition");
      let filename = "bulk-waybills.pdf";

      if (disposition?.includes("filename=")) {
        filename = disposition.split("filename=")[1].replace(/"/g, "");
      }

      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);

      setSuccessMsg("Waybills generated successfully. PDF downloaded.");

      // Reset file input
      setFile(null);
    } catch (err: any) {
      setError(err.message || "Unexpected error during bulk upload");
    } finally {
      setLoading(false);
    }
  };

  /* ---------------- UI ---------------- */

  return (
    <main className="max-w-4xl mx-auto p-6">
      <h1 className="text-2xl font-bold mb-6">
        Bluedart Bulk Waybill Generator
      </h1>

      {/* TEMPLATE */}
      <div className="mb-6">
        <button
          onClick={downloadTemplate}
          className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
        >
          ⬇️ Download XLSX Template
        </button>
      </div>

      {/* UPLOAD */}
      <div className="border p-4 rounded bg-gray-50">
        <h3 className="font-semibold mb-4">
          Upload Filled Template
        </h3>

        <div className="mb-4">
  <label className="block font-medium mb-2">
    Upload File (XLSX / CSV)
  </label>

  <div className="flex items-center gap-3">
    {/* Hidden file input */}
    <input
      id="bulkFile"
      type="file"
      accept=".xlsx,.csv"
      onChange={(e) =>
        setFile(e.target.files?.[0] || null)
      }
      className="hidden"
    />

    {/* Custom browse button */}
    <label
      htmlFor="bulkFile"
      className="cursor-pointer bg-gray-200 hover:bg-gray-300 px-4 py-2 rounded border"
    >
      Browse…
    </label>

    {/* File name display */}
    <span className="text-sm text-gray-700 truncate max-w-xs">
      {file ? file.name : "No file selected"}
    </span>
  </div>
</div>


        <div className="mb-4">
          <label className="mr-2 font-medium">
            Label Size:
          </label>
          <select
            value={labelSize}
            onChange={(e) => setLabelSize(e.target.value)}
            className="border p-2 rounded"
          >
            <option value="A4">A4</option>
            <option value="LABEL_4X6">4 x 6</option>
          </select>
        </div>

        <button
          onClick={uploadBulkFile}
          disabled={loading}
          className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700 disabled:opacity-60"
        >
          {loading ? "Processing..." : "Upload & Generate"}
        </button>

        {/* ERROR */}
        {error && (
          <div className="mt-4 bg-red-100 text-red-700 p-3 rounded">
            ❌ {error}
          </div>
        )}

        {/* SUCCESS */}
        {successMsg && (
          <div className="mt-4 bg-green-100 text-green-700 p-3 rounded">
            ✅ {successMsg}
          </div>
        )}
      </div>
    </main>
  );
}
