import { useMemo } from "react";
import type cytoscape from 'cytoscape';
import { BuildResponse} from '../api/RequestResponse';
import { UnionFind } from '../utils/unionfind';

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

    
    // Find groups
    const uf = new UnionFind();

    // Union twin pairs
    backendData.analysis?.twins?.forEach(({ left, right }) => {
      uf.union(left, right);
    });

    // Map from root group ID to class name
    const groupNames = new Map<number, string>();
    let groupIndex = 0;

    const twinGroupMap = new Map<number, string>();
    uf.nodes().forEach((id:number) => {
      const root = uf.find(id);
      if (!groupNames.has(root)) groupNames.set(root, `twin-${groupIndex++}`);
      twinGroupMap.set(id, groupNames.get(root)!);
    });

    // Assign styles per group
    let dynamicTwinStyles: cytoscape.Stylesheet[] = [];
    const usedTwinClasses = new Set<string>();

    // Generate styles for each unique twin class
    twinGroupMap.forEach((className, nodeId) => {
      if (!usedTwinClasses.has(className)) {
        usedTwinClasses.add(className);
        const index = parseInt(className.split('-')[1]);
        const hue = (index * 47) % 360;

        dynamicTwinStyles.push({
          selector: `.${className}`,
          style: {
            'underlay-color': `hsl(${hue}, 70%, 60%)`,
            'underlay-padding': '4px',
          },
        });
      }
    });

  // Assign twin classes to nodes and edges
  backendData.analysis?.twins?.forEach(({ left, right, causes }) => {
    const twinClass = twinGroupMap.get(left) ?? twinGroupMap.get(right);
    if (!twinClass) return;

    const fullClass = `twin-group ${twinClass}`;

    const leftNode = nodes.find(n => n.data.id === left);
    if (leftNode) leftNode.classes += ` ${fullClass}`;

    const rightNode = nodes.find(n => n.data.id === right);
    if (rightNode) rightNode.classes += ` ${fullClass}`;

    causes.forEach(({ source, target, label }) => {
      const edge = edges.find(
        e => e.data.source === source && e.data.target === target && e.data.label === label
      );
      if (edge) edge.classes += ` ${fullClass}`;
    });
  });


    return { nodes, edges, dynamicTwinStyles };
  }, [backendData]);
};

export default useTransformGraph;