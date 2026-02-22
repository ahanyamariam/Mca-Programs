import { Link, NavLink } from "react-router-dom";


export default function Navbar() {
const base = "px-3 py-2 rounded-lg hover:bg-white/60";
const active = ({ isActive }) => isActive ? `${base} bg-white` : base;
return (
<header className="sticky top-0 z-10 border-b bg-white/80 backdrop-blur">
<div className="mx-auto flex max-w-5xl items-center justify-between p-4">
<Link to="/breeds" className="text-xl font-bold">🐶 Dog Breeds</Link>
<nav className="flex gap-2 text-sm">
<NavLink to="/breeds" className={active}>All Breeds</NavLink>
<NavLink to="/breeds/new" className={active}>Add New</NavLink>
</nav>
</div>
</header>
);
}