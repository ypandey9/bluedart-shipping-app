"use client";

import { useState } from "react";
import { trackShipment } from "../lib/tracking";

export default function TrackingPage() {
  const [awb, setAwb] = useState("");
  const [data, setData] = useState<any>(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleTrack = async () => {
    try {
      setLoading(true);
      setError("");
      const result = await trackShipment(awb);
      console.log("TRACKING RESPONSE:", result); // 👈 DEBUG
      setData(result);
    } catch (e) {
      setError("Unable to fetch tracking details");
    } finally {
      setLoading(false);
    }
  };

  // ✅ CORRECT DATA ACCESS
  const shipment = data?.Shipment;

  // ✅ SAFETY: ScanDetail can be array OR object
  const scansRaw = shipment?.Scans?.ScanDetail;
  const scans = Array.isArray(scansRaw)
    ? scansRaw
    : scansRaw
    ? [scansRaw]
    : [];

  return (
    <div style={{ padding: 20 }}>
      <h1>Track Your Shipment</h1>

      <input
        value={awb}
        onChange={(e) => setAwb(e.target.value)}
        placeholder="Enter AWB number"
      />
      <button onClick={handleTrack}>Track</button>

      {loading && <p>Tracking the shipment, please wait...</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}

      {/* 👇 TEMP DEBUG (remove later) */}
      {data && <pre>{JSON.stringify(data, null, 2)}</pre>}

      {shipment && (
        <>
          <h2>Status: {shipment.Status}</h2>
          <p>
            {shipment.Origin} ({shipment.OriginAreaCode}) →{" "}
            {shipment.Destination} ({shipment.DestinationAreaCode})
          </p>
          <p>Expected Delivery: {shipment.ExpectedDeliveryDate}</p>
          <p>{shipment.Instructions}</p>

          <h3>Tracking History</h3>
          <ul>
            {scans.map((scan: any, idx: number) => (
              <li key={idx} style={{ marginBottom: 12 }}>
                <strong>{scan.Scan}</strong>
                <br />
                {scan.ScanDate} {scan.ScanTime}
                <br />
                {scan.ScannedLocation}
              </li>
            ))}
          </ul>
        </>
      )}
    </div>
  );
}
