import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import App from "./App.jsx";
import "./index.css";
import { BreedProvider } from "./context/BreedContext.jsx";


ReactDOM.createRoot(document.getElementById("root")).render(
<React.StrictMode>
<BrowserRouter>
<BreedProvider>
<App />
</BreedProvider>
</BrowserRouter>
</React.StrictMode>
);