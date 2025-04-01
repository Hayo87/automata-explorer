import type cytoscape from 'cytoscape';

export function processSynonymLabels(cy: cytoscape.Core | null, synonyms: Map<string, string[]>) {
    if (!cy) return;

    cy.edges().forEach((edge: cytoscape.EdgeSingular) => {
      const label = edge.data('label');
      const [input, output] = label?.split('/')?.map((s: string) => s.trim()) ?? [];

      const hasInputSynonym = input && synonyms.has(input);
      const hasOutputSynonym = output && synonyms.has(output);

      if (hasInputSynonym || hasOutputSynonym) {
        const decoratedInput = hasInputSynonym ? `ƒ_in(${input})` : input;
        const decoratedOutput = hasOutputSynonym ? `ƒ_out(${output})` : output;
        const decoratedLabel = `${decoratedInput}/${decoratedOutput}`;

        edge.data('label', decoratedLabel);
        edge.addClass('synonym');

        if (hasInputSynonym) {
          edge.data('synonymInput', input);
        }
        if (hasOutputSynonym) {
          edge.data('synonymOutput', output);
        }
      }
    });
}
