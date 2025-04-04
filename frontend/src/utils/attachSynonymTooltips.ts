import type cytoscape from 'cytoscape';
import { Filter } from '../types/BuildResponse';

export function attachSynonymTooltips(cy: cytoscape.Core | null, filters: Filter[]) {
  if (!cy) return;

  // Build a synonyms map
  const synonymsMap = new Map<string, string[]>(
    filters.filter(f => f.type === 'synonym').map(f => [f.decoratedName, f.values])
  );

  const showTooltip = (event: cytoscape.EventObject) => {
    const edge = event.target as cytoscape.EdgeSingular;
    const label = edge.data('label');
    if (!label) return;

    const parts = label.split("/");
    if (parts.length < 2) return;

    let inputPart = parts[0].trim();
    let outputPart = parts[1].trim();

    const inputKey = inputPart.includes("ƒ_in(") ? inputPart : inputPart;
    const outputKey = outputPart.includes("ƒ_out(") ? outputPart : outputPart;

    const inputValues = synonymsMap.get(inputKey);
    const outputValues = synonymsMap.get(outputKey);

    if (!inputValues && !outputValues) return;

    const content = document.createElement('div');
    content.innerHTML = `
      ${inputValues ? `<span style="font-weight: bold;">${inputKey}</span> ↦ {${inputValues.join(', ')}}<br/>` : ''}
      ${outputValues ? `<span style="font-weight: bold;">${outputKey}</span> ↦ {${outputValues.join(', ')}}` : ''}
    `;

    
    const tip = edge.popper({
      content: () => content,
    });

    tip.show();
    edge.once('mouseout', () => tip.destroy());
  };

  // Attach mouseover listener to edges whose label contains either "ƒ_in(" or "ƒ_out(".
  cy.edges().forEach((edge: cytoscape.EdgeSingular) => {
    const label = edge.data('label');
    if (label && (label.includes("ƒ_in(") || label.includes("ƒ_out("))) {
      edge.on('mouseover', showTooltip);
    }
  });

  return () => {
    cy.edges().forEach((edge: cytoscape.EdgeSingular) => {
      edge.off('mouseover', showTooltip);
    });
  };
}