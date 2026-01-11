"use client";

import { useState } from "react";

type BulkResult = {
  total: number;
  success: number;
  failed: number;
};

export default function BulkWaybillPage() {
  const BACKEND = process.env.NEXT_PUBLIC_BACKEND_URL;

  const [file, setFile] = useState<File | null>(null);
  const [labelSize, setLabelSize] = useState("A4");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [result, setResult] = useState<BulkResult | null>(null);

  /* ---------------- TEMPLATE ---------------- */

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
    setResult(null);

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
        throw new Error("Bulk upload failed. Please check the file.");
      }

      const data: BulkResult = await res.json();
      setResult(data);

      // reset file after success
      setFile(null);

    } catch (err: any) {
      setError(err.message || "Unexpected error occurred");
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

      {/* UPLOAD CARD */}
      <div className="border p-4 rounded bg-gray-50">
        <h3 className="font-semibold mb-4">
          Upload Filled Template
        </h3>

        {/* FILE PICKER */}
        <div className="mb-4">
          <label className="block font-medium mb-2">
            Upload File (XLSX / CSV)
          </label>

          <div className="flex items-center gap-3">
            <input
              id="bulkFile"
              type="file"
              accept=".xlsx,.csv"
              onChange={(e) =>
                setFile(e.target.files?.[0] || null)
              }
              className="hidden"
            />

            <label
              htmlFor="bulkFile"
              className="cursor-pointer bg-gray-200 hover:bg-gray-300 px-4 py-2 rounded border"
            >
              Browse…
            </label>

            <span className="text-sm text-gray-700 truncate max-w-xs">
              {file ? file.name : "No file selected"}
            </span>
          </div>
        </div>

        {/* LABEL SIZE */}
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

        {/* SUBMIT */}
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
      </div>

      {/* RESULT */}
      {result && (
        <div className="mt-6 border p-4 rounded bg-green-50">
          <h3 className="font-semibold mb-3">
            Bulk Processing Result
          </h3>

          <p>Total Records: <b>{result.total}</b></p>
          <p className="text-green-700">
            Success: <b>{result.success}</b>
          </p>
          <p className="text-red-700">
            Failed: <b>{result.failed}</b>
          </p>

          <div className="mt-4 space-y-2">
            {result.success > 0 && (
              <>
                <a
                  href={`${BACKEND}/api/bluedart/waybill/bulk/pdf`}
                  className="block text-blue-600 underline"
                  target="_blank"
                >
                  📄 Download Success Labels (PDF)
                </a>

                <a
                  href={`${BACKEND}/api/bluedart/waybill/bulk/success`}
                  className="block text-blue-600 underline"
                  target="_blank"
                >
                  📊 Download Success Records (Excel)
                </a>
              </>
            )}

            {result.failed > 0 && (
              <a
                href={`${BACKEND}/api/bluedart/waybill/bulk/failure`}
                className="block text-blue-600 underline"
                target="_blank"
              >
                📊 Download Failure Records (Excel)
              </a>
            )}
          </div>
        </div>
      )}
    </main>
  );
}
