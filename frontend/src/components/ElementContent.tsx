import cytoscape from 'cytoscape';

interface ElementInfoProps {
  element: cytoscape.SingularElementReturnValue;
}

const ElementInfo: React.FC<ElementInfoProps> = ({ element }) => {
  if (element.isNode()) {
    const label = element.data('label');

    return (
      <>
        <h2>Node Info: {label}</h2>
        <hr></hr>
        <strong>General:</strong>
        <p>Id: {element.id()}</p>
        <p>label: {label} </p>
        <hr></hr>
        
        <strong>Neighborhood:</strong>
        <p>Predecessors (direct): {element.incomers().filter((el: cytoscape.SingularElementReturnValue) => el.isNode() && el.id() != element.id()).map((node: cytoscape.NodeSingular) => node.id()).join(',')} </p>
        <p>Predecessors (all): {element.predecessors().filter((el: cytoscape.SingularElementReturnValue) => el.isNode() && el.id() != element.id()).map((node: cytoscape.NodeSingular) => node.id()).join(',')}</p>
        <p>Successors (direct): {element.outgoers().filter((el: cytoscape.SingularElementReturnValue) => el.isNode() && el.id() != element.id()).map((node: cytoscape.NodeSingular) => node.id()).join(',')} </p>
        <p>Successors (all): {element.successors().filter((el: cytoscape.SingularElementReturnValue) => el.isNode() && el.id() != element.id()).map((node: cytoscape.NodeSingular) => node.id()).join(',')} </p>
      </>
    );
  }

  if (element.isEdge()) {
    return (
      <>
        <h2>Edge Info</h2>
        <hr></hr>

        <strong>General:</strong>
        <p>Id: {element.id()}</p>
        <p>label: {element.data('label')} </p>
        <hr></hr>

        <strong>Neighborhood:</strong>
        <p>From: {element.source().id()}</p>
        <p>To: {element.target().id()}</p>
      </>
    );
  }

  return <p>No info available.</p>;
};

export default ElementInfo;
