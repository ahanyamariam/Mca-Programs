import { createContext, useContext, useMemo } from "react";
import { v4 as uuid } from "uuid";
import useLocalStorage from "../hooks/useLocalStorage";
import { seedBreeds } from "../data/seedBreeds";


const BreedContext = createContext();


export function BreedProvider({ children }) {
const [breeds, setBreeds] = useLocalStorage("breeds", seedBreeds);


const actions = useMemo(() => ({
addBreed: (payload) => {
const newBreed = { id: uuid(), ...payload };
setBreeds((prev) => [newBreed, ...prev]);
return newBreed.id;
},
updateBreed: (id, updates) => {
setBreeds((prev) => prev.map((b) => (b.id === id ? { ...b, ...updates } : b)));
},
deleteBreed: (id) => {
setBreeds((prev) => prev.filter((b) => b.id !== id));
},
getBreed: (id) => breeds.find((b) => b.id === id),
clearAll: () => setBreeds([]),
}), [breeds, setBreeds]);


return (
<BreedContext.Provider value={{ breeds, ...actions }}>
{children}
</BreedContext.Provider>
);
}


export function useBreeds() {
const ctx = useContext(BreedContext);
if (!ctx) throw new Error("useBreeds must be used within BreedProvider");
return ctx;
}