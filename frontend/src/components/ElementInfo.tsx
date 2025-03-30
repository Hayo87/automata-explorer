import React from 'react';
import cytoscape from 'cytoscape';

interface ElementInfoProps {
  element: cytoscape.SingularElementReturnValue;
}

const ElementInfo: React.FC<ElementInfoProps> = ({ element }) => {
  if (element.isNode()) {
    const label = element.data('label');
    const type = element.data('type');
    return (
      <>
        <h2>Node Info: {label}</h2>
        <p>ID: {element.id()}</p>
        <p>Type: {type}</p>
      </>
    );
  }

  if (element.isEdge()) {
    return (
      <>
        <h2>Edge Info</h2>
        <p>From: {element.source().id()}</p>
        <p>To: {element.target().id()}</p>
        <p>Label: {element.data('label')}</p>
      </>
    );
  }

  return <p>No info available.</p>;
};

export default ElementInfo;
