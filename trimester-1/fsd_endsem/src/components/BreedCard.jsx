import { Link } from "react-router-dom";


export default function BreedCard({ breed }) {
return (
<div className="group overflow-hidden rounded-2xl border bg-white shadow-sm transition hover:shadow-md">
<img
src={breed.imageUrl}
alt={breed.name}
className="h-48 w-full object-cover"
onError={(e) => { e.currentTarget.src = "https://placehold.co/600x400?text=Dog+Breed"; }}
/>
<div className="p-4">
<h3 className="mb-1 text-lg font-semibold">{breed.name}</h3>
<p className="mb-3 line-clamp-2 text-sm text-gray-600">{breed.description}</p>
<div className="flex items-center justify-between text-sm">
<span className="text-gray-600">{breed.origin}</span>
<Link to={`/breeds/${breed.id}`} className="rounded-lg px-3 py-1 font-medium text-blue-700 hover:bg-blue-50">View</Link>
</div>
</div>
</div>
);
}