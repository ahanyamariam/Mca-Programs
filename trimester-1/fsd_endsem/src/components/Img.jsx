import { useState, useMemo } from "react";

export default function Img({ src, alt, className = "" }) {
  const [error, setError] = useState(false);

  // Basic sanitization: require https and non-empty
  const cleanSrc = useMemo(() => {
    if (!src || typeof src !== "string") return "";
    const trimmed = src.trim();
    // force https if someone pasted http
    if (trimmed.startsWith("http://")) return trimmed.replace("http://", "https://");
    return trimmed;
  }, [src]);

  const fallback = "https://placehold.co/600x400?text=Dog+Breed";

  return (
    <img
      src={error || !cleanSrc ? fallback : cleanSrc}
      alt={alt || "image"}
      className={className}
      onError={() => setError(true)}
      referrerPolicy="no-referrer"
    />
  );
}
