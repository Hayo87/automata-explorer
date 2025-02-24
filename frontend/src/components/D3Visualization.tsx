import React, { useEffect, useRef } from "react";
import * as d3 from "d3";
import "../index.css"; // Use global styles

const D3Visualization: React.FC = () => {
  const svgRef = useRef<SVGSVGElement | null>(null);

  useEffect(() => {
    if (!svgRef.current) return;

    // Clear previous SVG content
    d3.select(svgRef.current).selectAll("*").remove();

    // Define SVG properties
    const width = 600;
    const height = 400;

    const svg = d3.select(svgRef.current)
      .attr("width", width)
      .attr("height", height);

    // Example D3 Visualization
    const data = Array.from({ length: 10 }, () => ({
      x: Math.random() * width,
      y: Math.random() * height,
      r: Math.random() * 30 + 10,
    }));

    svg.selectAll("circle")
      .data(data)
      .enter()
      .append("circle")
      .attr("cx", d => d.x)
      .attr("cy", d => d.y)
      .attr("r", d => d.r)
      .attr("fill", "#4CAF50");

  }, []);

  return <svg ref={svgRef} className="d3-visualization"></svg>;
};

export default D3Visualization;
