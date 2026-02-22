import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useBreeds } from "../context/BreedContext.jsx";


function Input({ label, ...props }) {
return (
<label className="block">
<span className="mb-1 block text-sm font-medium text-gray-700">{label}</span>
<input {...props} className="w-full rounded-xl border px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-200" />
</label>
);
}


export default function BreedEdit() {
const { id } = useParams();
const { getBreed, updateBreed } = useBreeds();
const nav = useNavigate();
const existing = getBreed(id);


const [form, setForm] = useState(existing || {});
useEffect(() => { if (existing) setForm(existing); }, [existing]);


if (!existing) return <div className="p-8">Breed not found.</div>;


const handleChange = (e) => setForm(f => ({ ...f, [e.target.name]: e.target.value }));


const handleSubmit = (e) => {
e.preventDefault();
updateBreed(id, { ...form });
nav(`/breeds/${id}`);
};


return (
<div className="space-y-6">
<h1 className="text-2xl font-bold">Edit {existing.name}</h1>
<form onSubmit={handleSubmit} className="grid grid-cols-1 gap-4 rounded-2xl border bg-white p-6 shadow-sm md:grid-cols-2">
<Input label="Name" name="name" value={form.name || ""} onChange={handleChange} required />
<Input label="Origin" name="origin" value={form.origin || ""} onChange={handleChange} />
<Input label="Life Span" name="lifeSpan" value={form.lifeSpan || ""} onChange={handleChange} />
<Input label="Temperament" name="temperament" value={form.temperament || ""} onChange={handleChange} />
<Input label="Height" name="height" value={form.height || ""} onChange={handleChange} />
<Input label="Weight" name="weight" value={form.weight || ""} onChange={handleChange} />
<Input label="Image URL" name="imageUrl" value={form.imageUrl || ""} onChange={handleChange} className="md:col-span-2" />
<label className="md:col-span-2">
<span className="mb-1 block text-sm font-medium text-gray-700">Description</span>
<textarea name="description" value={form.description || ""} onChange={handleChange} rows={4} className="w-full rounded-xl border px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-200"></textarea>
</label>
<div className="md:col-span-2 flex gap-2">
<button className="rounded-xl bg-blue-600 px-4 py-2 font-medium text-white hover:bg-blue-700">Save</button>
<button type="button" onClick={() => nav(-1)} className="rounded-xl border px-4 py-2 hover:bg-gray-50">Cancel</button>
</div>
</form>
</div>
);
}