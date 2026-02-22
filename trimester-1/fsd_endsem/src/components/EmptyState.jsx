import { Link } from "react-router-dom";


export default function EmptyState() {
return (
<div className="rounded-2xl border bg-white p-10 text-center shadow-sm">
<p className="mb-3 text-lg font-semibold">No breeds yet</p>
<p className="mb-6 text-gray-600">Add your first dog breed to get started.</p>
<Link to="/breeds/new" className="rounded-xl bg-blue-600 px-4 py-2 font-medium text-white hover:bg-blue-700">Add Breed</Link>
</div>
);
}