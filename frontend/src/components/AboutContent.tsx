import React from 'react';

const AboutContent: React.FC = () => {
  return (
    <>
    <div style={{maxWidth: "600px"}}> 
        <h2>About This Application</h2>
        <hr></hr>
        <p>The Automata Explorer is a visualization tool is developed by <strong>Richard Koopmans</strong> and <strong>Marijn Verheul </strong> 
         as part of Bachelor graduation project. </p>
         
        <p> The tool offers a way to expore learned (finite) state machines with an 
        interactive graph-based UI and uses the gLTSDiff library and Cytoscape libraries. For more detailed information visit the{' '}
 
        <a href="https://github.com/Hayo87/automata-explorer" target="_blank" rel="noopener noreferrer">
         github page
        </a>.</p>
        <p>Version: <code>1.0.0</code>, 2025</p>
      </div>
    </>
  );
};

export default AboutContent;
