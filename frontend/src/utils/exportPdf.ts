import cytoscape from "cytoscape";
import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";
import orderBy from 'lodash/orderBy';

/**
 * @function exportPDF
 * 
 * Exports the current Cytoscape graph as a PDF using jsPDF and jsPDF-AutoTable.
 * Includes metadata like the reference and subject filenames.
 * 
 */

export async function exportPDF(cyInstance: cytoscape.Core, reference= 'Ref', subject = 'Sub') {
    if (!cyInstance) return;

    const doc = new jsPDF({
        orientation: "landscape",
        unit: "px",
        format: "a4",
      });
    const filename = `Report_DiffMachine_${reference}_${subject}.pdf`;
    const image = cyInstance.png({ full: true, bg: "white" });

    const colors = {
        added: [40, 167, 69] as [number, number, number],      
        removed: [220, 53, 69] as [number, number, number],    
        combined: [0, 123, 255] as [number, number, number],      
        common: [0, 0, 0] as [number, number, number],     
    }

    const unchangedEdges = cyInstance.edges('.unchanged');
    const combinedEdges = cyInstance.edges('.combined');
    const addedEdges = cyInstance.edges('.added');
    const removedEdges = cyInstance.edges('.removed');
    const match = ((unchangedEdges.length + combinedEdges.length) / cyInstance.edges().length) * 100

    const edges = cyInstance.edges().map((edge: cytoscape.EdgeSingular) => {
        const classes = edge.classes();
        let type = ""; 

        switch (true) {
            case classes.includes('added'):
            type = "added";
            break;

            case classes.includes('removed'):
            type = "removed";
            break;

            case classes.includes('combined'):
            type = "combined";
            break;
            
            default:
            type = "unchanged";
            break;
        }
      
        return {
          sourceId: Number(edge.source().id()),
          targetId: Number(edge.target().id()),
          source: edge.source().data('label'),
          target: edge.target().data('label'),
          type: type,
          label: edge.data('label') || '',
        };
      });

    const sortedEdges = orderBy(edges, ['sourceId', 'targetId', 'label'], ['asc', 'asc', 'asc']);
    
    // Start document
    // Image section
    await addImageAutoScaled(doc, image);
    
    // Overview section
    doc.addPage();
    doc.setFontSize(18);
    autoTable(doc, {
        head: [
            [
                { 
                    content: 'Match results', 
                    colSpan: 2, 
                    styles: { 
                        halign: 'center', 
                        fontStyle: 'bold', 
                        textColor: [0, 0, 0], 
                        fillColor: [220, 220, 220] 
                    } 
                }
            ]
        ],
        body: [
            [{ content: 'Reference',styles: { textColor: colors.removed }}, reference],
            [{ content: 'Subject',styles: { textColor: colors.added }}, subject],
            ['Total nodes diffmachine', cyInstance.nodes().length],
            ['Total edges diffmachine', cyInstance.edges().length],
            ['', ""],
            ['Match percentage', match + " %"],
            ['Full match', unchangedEdges.length],
            ['Partial match', combinedEdges.length],
            ['No match', (addedEdges.length + removedEdges.length)],

            ],
        theme: 'plain',
        startY: 50,
        styles: {
        fontSize: 14,
        cellPadding: 2,
        overflow: 'linebreak',
        },
    });    

    // Edges section
    doc.addPage();

    autoTable(doc, {
    head: [
        [
          { 
            content: 'Edges', 
            colSpan: 4, 
            styles: { 
              halign: 'center', 
              fontStyle: 'bold', 
              textColor: [0, 0, 0],
              fillColor: [220, 220, 220], 
            } 
          }
        ],
        [
            { content: 'Source', styles: { textColor: [0, 0, 0], fontStyle: 'bold', fillColor: [220, 220, 220] } },
            { content: 'Target', styles: { textColor: [0, 0, 0], fontStyle: 'bold', fillColor: [220, 220, 220] } },
            { content: 'Type', styles: { textColor: [0, 0, 0], fontStyle: 'bold', fillColor: [220, 220, 220] } },
            { content: 'Label', styles: { textColor: [0, 0, 0], fontStyle: 'bold', fillColor: [220, 220, 220] } },
          ],
      ],
    body: sortedEdges.map((edge: any) => [edge.source, edge.target, edge.type, edge.label]),
    didParseCell: (data) => {
        if (data.section === 'body') {
        const row = data.row.raw as string[]; 
        const type = row[2]; 
        
        switch (type) {
            case 'added':
            data.cell.styles.textColor = colors.added; 
            break;

            case 'removed':
            data.cell.styles.textColor = colors.removed; 
            break;

            case 'combined':
            data.cell.styles.textColor = colors.combined;
            break;

            default:
            data.cell.styles.textColor = colors.common; 
            break;
        }
     }
    },
    styles: {
        fontSize: 10,
        overflow: 'linebreak', 
        cellPadding: 2,
        
    },
    theme: 'striped',
    startY: 50, 
    });


    doc.save(filename);
}

// Image helper
async function addImageAutoScaled(doc: jsPDF, imageData: string) {
    return new Promise<void>((resolve) => {
      const img = new Image();
      img.onload = () => {
        const pageWidth = doc.internal.pageSize.getWidth();
        const pageHeight = doc.internal.pageSize.getHeight();
  
        let imgWidth = img.width;
        let imgHeight = img.height;
  
        const scale = Math.min(pageWidth / imgWidth, pageHeight / imgHeight);
  
        imgWidth *= scale;
        imgHeight *= scale;
  
        const marginX = (pageWidth - imgWidth) / 2;
        const marginY = (pageHeight - imgHeight) / 2;
  
        (doc as any).addImage(imageData, "PNG", marginX, marginY, imgWidth, imgHeight);
        resolve();
      };
      img.src = imageData;
    });
  }
