import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useBreeds } from "../context/BreedContext.jsx";

function Input({ label, ...props }) {
  return (
    <label className="block">
      <span className="mb-1 block text-sm font-medium text-gray-700">{label}</span>
      <input
        {...props}
        className="w-full rounded-xl border px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-200"
      />
    </label>
  );
}

export default function BreedCreate() {
  const { addBreed } = useBreeds();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: "",
    origin: "",
    lifeSpan: "",
    temperament: "",
    height: "",
    weight: "",
    imageUrl: "",
    description: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!form.name.trim()) {
      alert("Name is required");
      return;
    }
    const id = addBreed(form);          // ← must return the new id
    navigate(`/breeds/${id}`);          // go to detail page
  };

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Add New Breed</h1>

      <form
        onSubmit={handleSubmit}
        className="grid grid-cols-1 gap-4 rounded-2xl border bg-white p-6 shadow-sm md:grid-cols-2"
      >
        <Input label="Name" name="name" value={form.name} onChange={handleChange} required />
        <Input label="Origin" name="origin" value={form.origin} onChange={handleChange} />
        <Input label="Life Span" name="lifeSpan" value={form.lifeSpan} onChange={handleChange} placeholder="e.g., 10-12 years" />
        <Input label="Temperament" name="temperament" value={form.temperament} onChange={handleChange} />
        <Input label="Height" name="height" value={form.height} onChange={handleChange} placeholder="e.g., 55-62 cm" />
        <Input label="Weight" name="weight" value={form.weight} onChange={handleChange} placeholder="e.g., 25-36 kg" />
        <Input label="Image URL" name="imageUrl" value={form.imageUrl} onChange={handleChange} />

        <label className="md:col-span-2">
          <span className="mb-1 block text-sm font-medium text-gray-700">Description</span>
          <textarea
            name="description"
            value={form.description}
            onChange={handleChange}
            rows={4}
            className="w-full rounded-xl border px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-200"
          />
        </label>

        <div className="md:col-span-2 flex gap-2">
          <button type="submit" className="rounded-xl bg-blue-600 px-4 py-2 font-medium text-white hover:bg-blue-700">
            Create
          </button>
          <button type="button" onClick={() => navigate(-1)} className="rounded-xl border px-4 py-2 hover:bg-gray-50">
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
}
