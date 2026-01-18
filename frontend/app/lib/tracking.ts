// app/lib/tracking.ts

export async function trackShipment(awb: string) {
  const res = await fetch(`http://localhost:8080/api/tracking/${awb}`, {
    method: "GET",
  });

  if (!res.ok) {
    throw new Error("Tracking failed");
  }

  return await res.json(); // XML from Spring Boot
}
