import React, { useEffect, useRef } from "react";
import * as d3 from "d3";

interface Transition {
  from: number;
  to: number;
  label: string;
  diffKind: string;
}

interface State {
  id: number;
  initial: boolean;
  diffKind: string;
}

interface GraphData {
  transitions: Transition[];
  states: State[];
}

interface D3VisualizationProps {
  data: GraphData;
}

// Helper function to map diffKind to a color
const getColor = (diffKind: string): string => {
  switch (diffKind) {
    case "REMOVED":
      return "#00cc00"; // green
    case "ADDED":
      return "#ff4040"; // red
    default:
      return "#000000"; // black
  }
};

const D3Visualization: React.FC<D3VisualizationProps> = ({ data }) => {
  const svgRef = useRef<SVGSVGElement | null>(null);

  useEffect(() => {
    if (!data || !svgRef.current) return;

    // Clear previous SVG content
    const svg = d3.select(svgRef.current);
    svg.selectAll("*").remove();

    // Set dimensions
    const width = 800;
    const height = 600;
    svg.attr("width", width).attr("height", height);

    // Create nodes with diffKind info
    const nodes = data.states.map((state) => ({
      id: state.id,
      diffKind: state.diffKind,
    }));

    // Create links with diffKind info
    const links = data.transitions.map((transition) => ({
      source: transition.from,
      target: transition.to,
      label: transition.label,
      diffKind: transition.diffKind,
    }));

    // Create a force simulation
    const simulation = d3.forceSimulation(nodes)
      .force("link", d3.forceLink(links).id((d: any) => d.id).distance(150))
      .force("charge", d3.forceManyBody().strength(-400))
      .force("center", d3.forceCenter(width / 2, height / 2));

    // Draw links (lines)
    const linkGroup = svg.append("g")
      .attr("stroke-opacity", 0.6)
      .selectAll("line")
      .data(links)
      .join("line")
      .attr("stroke", d => getColor(d.diffKind))  // Color links based on diffKind
      .attr("stroke-width", 2);

    // Draw nodes (circles)
    const nodeGroup = svg.append("g")
      .selectAll("circle")
      .data(nodes)
      .join("circle")
      .attr("r", 15)
      .attr("fill", d => getColor(d.diffKind)) // Color nodes based on diffKind
      .call(
        d3.drag()
          .on("start", dragstarted)
          .on("drag", dragged)
          .on("end", dragended)
      );

    // Add labels for nodes (showing the state id)
    const nodeLabels = svg.append("g")
      .selectAll("text")
      .data(nodes)
      .join("text")
      .attr("dy", 4)
      .attr("dx", -10)
      .attr("font-size", 12)
      .text(d => d.id);

    // Add labels for links (showing transition label)
    const linkLabels = svg.append("g")
      .selectAll("text")
      .data(links)
      .join("text")
      .attr("font-size", 12)
      .attr("fill", "#000")
      .text(d => d.label);

    // Update positions on each simulation tick
    simulation.on("tick", () => {
      linkGroup
        .attr("x1", d => (d.source as any).x)
        .attr("y1", d => (d.source as any).y)
        .attr("x2", d => (d.target as any).x)
        .attr("y2", d => (d.target as any).y);

      nodeGroup
        .attr("cx", d => (d as any).x)
        .attr("cy", d => (d as any).y);

      nodeLabels
        .attr("x", d => (d as any).x)
        .attr("y", d => (d as any).y);

      // Position link labels at the midpoint of each link
      linkLabels
        .attr("x", d => (((d.source as any).x + (d.target as any).x) / 2))
        .attr("y", d => (((d.source as any).y + (d.target as any).y) / 2));
    });

    // Drag event handlers
    function dragstarted(event: any, d: any) {
      if (!event.active) simulation.alphaTarget(0.3).restart();
      d.fx = d.x;
      d.fy = d.y;
    }
    function dragged(event: any, d: any) {
      d.fx = event.x;
      d.fy = event.y;
    }
    function dragended(event: any, d: any) {
      if (!event.active) simulation.alphaTarget(0);
      d.fx = null;
      d.fy = null;
    }
  }, [data]);

  return <svg ref={svgRef}></svg>;
};

export default D3Visualization;
