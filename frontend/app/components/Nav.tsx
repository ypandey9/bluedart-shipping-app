import Link from "next/link";

export default function Nav() {
  return (
    <nav className="flex gap-6 p-4">
    <Link href="/">Book A Shipment</Link>
    <Link href="/profile">Profile</Link>
    <Link href="/tracking">Tracking</Link>
    <Link href="/cancel-waybill">Cancel Waybill</Link>
    <Link href="/bulk-waybill">Bulk Booking</Link>
    <Link href="/pickup-register">Register Pickup</Link>
    <Link href="/reports">Reports</Link>
    </nav>
  );
}