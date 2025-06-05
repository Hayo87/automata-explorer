import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import App from './App';
import './index.css';
import Modal from 'react-modal';

/**
 * @file main.tsx
 *
 * Application entry point. Configures React root rendering with React Router,
 * global modal accessibility, and the top-level <App /> component.
 */

Modal.setAppElement('#root');

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>
);
