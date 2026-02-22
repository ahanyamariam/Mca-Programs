import { Routes, Route, Navigate } from "react-router-dom";
import Navbar from "./components/Navbar.jsx";
import BreedList from "./pages/BreedList.jsx";
import BreedCreate from "./pages/BreedCreate.jsx";
import BreedShow from "./pages/BreedShow.jsx";
import BreedEdit from "./pages/BreedEdit.jsx";


export default function App() {
return (
<div className="min-h-screen bg-gray-50 text-gray-900">
<Navbar />
<div className="mx-auto max-w-5xl p-4 sm:p-6">
<Routes>
<Route path="/" element={<Navigate to="/breeds" replace />} />
<Route path="/breeds" element={<BreedList />} />
<Route path="/breeds/new" element={<BreedCreate />} />
<Route path="/breeds/:id" element={<BreedShow />} />
<Route path="/breeds/:id/edit" element={<BreedEdit />} />
<Route path="*" element={<div className="p-8">404 Not Found</div>} />
</Routes>
</div>
</div>
);
}