import { useMemo } from "react";
import type cytoscape from 'cytoscape';
import { BuildResponse} from '../api/RequestResponse';

/**
 * @file useTransform.ts
 * 
 * Custom react hook that maps backend data into frontend ready format. 
 * @param backendData the rad backend data
 * @returns Cytoscape compatible nodes and edges
 */

const useTransformGraph = (backendData: BuildResponse | null) => {
  return useMemo(() => {
    if (!backendData) return { nodes: [], edges: [], dynamicTwinStyles: []};
    const { build: graphData } = backendData;

    let nodes = graphData.nodes.map((node) => {
      return {
        group: "nodes",
        data: {
          id: node.id,
          label: node.attributes?.label,
          NodeType:typeof node.attributes?.diffkind === "string"
          ? node.attributes.diffkind.toLowerCase()
          : "",
        },
        selectable: true,
        grabbable: true,
        locked: false,
        pannable: false,
        classes: typeof node.attributes?.diffkind === "string"
        ? node.attributes.diffkind.toLowerCase()
        : ""
      };
    });

    let edges = graphData.edges.map((edge) => {
      return {
        group: "edges",
        data: {
          id: edge.id,
          source: edge.source,
          target: edge.target,
          label: edge.attributes?.labeltext,
          edgeType: typeof edge.attributes?.diffkind === "string"
          ? edge.attributes.diffkind.toLowerCase()
          : "",
        },
        pannable: true,
        classes: typeof edge.attributes?.diffkind === "string"
        ? edge.attributes.diffkind.toLowerCase()
        : "",
      };
    });

  // Assign the classes to the (twin) groups
  let dynamicTwinStyles: cytoscape.Stylesheet[] = [];
  backendData.analysis?.groupedTwins.forEach(({ members, causes }, index) => {
    const className = `twin-${index}`;
    const hue = (index * 47) % 360;
    const fullClass = `twin-group ${className}`;

    // style
    dynamicTwinStyles.push({
          selector: `.${className}`,
          style: {
            'underlay-color': `hsl(${hue}, 70%, 60%)`,
            'underlay-padding': '4px',
          },
        });

    members.forEach((id: number) => {
      const node = nodes.find(n => n.data.id === id);
      if (node) node.classes += ` ${fullClass}`;
    })

    causes.forEach(({ source, target, id }) => {
      const edge = edges.find(
        e => e.data.source === source && e.data.target === target && e.data.label === id
      );
      if (edge) edge.classes += ` ${fullClass}`;
    });
  });

    return { nodes, edges, dynamicTwinStyles };
  }, [backendData]);
};

export default useTransformGraph;