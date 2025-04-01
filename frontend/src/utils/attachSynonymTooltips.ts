import type cytoscape from 'cytoscape';

export function attachSynonymTooltips(cy: cytoscape.Core | null, synonyms: Map<string, string[]>) {
    if (!cy) return;

    const showTooltip = (event: cytoscape.EventObject) => {
      const edge = event.target;

      const inputTerm = edge.data('synonymInput');
      const outputTerm = edge.data('synonymOutput');

      const inputValues = inputTerm ? synonyms.get(inputTerm) : null;
      const outputValues = outputTerm ? synonyms.get(outputTerm) : null;

      if (!inputValues && !outputValues) return;

      const content = document.createElement('div');
      content.innerHTML = `
        ${inputValues ? `<span style="font-weight: bold;">${inputTerm}</span> ↦ {${inputValues.join(', ')}}<br/>` : ''}
        ${outputValues ? `<span style="font-weight: bold;">${outputTerm}</span> ↦ {${outputValues.join(', ')}}` : ''}
      `;

      const tip = edge.popper({
        content: () => content,
      });

      tip.show();

      edge.once('mouseout', () => tip.destroy());
    };

    cy.on('mouseover', 'edge.synonym', showTooltip);

    return () => {
      cy.off('mouseover', 'edge.synonym', showTooltip);
    };
}
