import { Link } from "react-router-dom";
import { useBreeds } from "../context/BreedContext.jsx";
import BreedCard from "../components/BreedCard.jsx";
import EmptyState from "../components/EmptyState.jsx";
import { useMemo, useState } from "react";


export default function BreedList() {
const { breeds, clearAll } = useBreeds();
const [q, setQ] = useState("");


const filtered = useMemo(() => {
const t = q.trim().toLowerCase();
if (!t) return breeds;
return breeds.filter(b =>
[b.name, b.origin, b.temperament].some(x => x?.toLowerCase().includes(t))
);
}, [breeds, q]);


return (
<div className="space-y-4">
<div className="flex flex-col items-start justify-between gap-3 sm:flex-row sm:items-center">
<h1 className="text-2xl font-bold">All Dog Breeds</h1>
<div className="flex gap-2">
<input
value={q}
onChange={(e) => setQ(e.target.value)}
className="w-64 rounded-xl border px-3 py-2"
placeholder="Search by name, origin, temperament"
/>
<Link to="/breeds/new" className="rounded-xl bg-blue-600 px-4 py-2 font-medium text-white hover:bg-blue-700">+ Add</Link>
{breeds.length > 0 && (
<button onClick={clearAll} className="rounded-xl border px-3 py-2 hover:bg-gray-50">Clear All</button>
)}
</div>
</div>


{filtered.length === 0 ? (
<EmptyState />
) : (
<div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
{filtered.map((b) => (
<BreedCard key={b.id} breed={b} />
))}
</div>
)}
</div>
);
}