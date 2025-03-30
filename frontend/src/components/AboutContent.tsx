import React from 'react';

const AboutContent: React.FC = () => {
  return (
    <>
      <h2>About This Application</h2>
      <p>The Automata Explorer is a visualization tool is developed by <strong>...</strong>.</p>
      <p>Version: <code>1.0.0</code></p>
      <p>© {new Date().getFullYear()} All rights reserved.</p>
      <p>
        Explore learned (finite) state machines with an interactive graph-based UI.
      </p>
    </>
  );
};

export default AboutContent;
