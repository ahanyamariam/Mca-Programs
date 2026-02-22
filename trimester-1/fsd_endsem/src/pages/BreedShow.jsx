import { Link, useNavigate, useParams } from "react-router-dom";
import { useBreeds } from "../context/BreedContext.jsx";
import Img from "../components/Img.jsx";

export default function BreedShow() {
  const { id } = useParams();
  const { getBreed, deleteBreed } = useBreeds();
  const nav = useNavigate();
  const breed = getBreed(id);

  if (!breed) return <div className="p-8">Breed not found.</div>;

  const onDelete = () => {
    if (confirm(`Delete ${breed.name}? This cannot be undone.`)) {
      deleteBreed(breed.id);
      nav("/breeds");
    }
  };

  return (
    <div className="grid grid-cols-1 gap-6 md:grid-cols-3">
      <div className="md:col-span-1 overflow-hidden rounded-2xl border bg-white shadow-sm">
        <Img src={breed.imageUrl} alt={breed.name} className="h-64 w-full object-cover" />
      </div>


<div className="md:col-span-2 space-y-3 rounded-2xl border bg-white p-6 shadow-sm">
<h1 className="text-3xl font-bold">{breed.name}</h1>
<p className="text-gray-700">{breed.description}</p>
<div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
<Info label="Origin" value={breed.origin} />
<Info label="Life Span" value={breed.lifeSpan} />
<Info label="Temperament" value={breed.temperament} />
<Info label="Height" value={breed.height} />
<Info label="Weight" value={breed.weight} />
</div>
<div className="flex gap-2 pt-2">
<Link to={`/breeds/${breed.id}/edit`} className="rounded-xl bg-yellow-500 px-4 py-2 font-medium text-white hover:bg-yellow-600">Edit</Link>
<button onClick={onDelete} className="rounded-xl bg-red-600 px-4 py-2 font-medium text-white hover:bg-red-700">Delete</button>
<Link to="/breeds" className="rounded-xl border px-4 py-2 hover:bg-gray-50">Back</Link>
</div>
</div>
</div>
);
}


function Info({ label, value }) {
return (
<div className="rounded-xl border p-3">
<div className="text-xs uppercase text-gray-500">{label}</div>
<div className="font-medium">{value || "—"}</div>
</div>
);
}