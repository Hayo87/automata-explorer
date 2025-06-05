import { Routes, Route } from 'react-router-dom';
import { createContext, useEffect, useMemo, useState } from "react";
import UploadPage from './pages/UploadPage';
import VisualizationPage from './pages/VisualizationPage';
import { setGlobalErrorHandler } from './api/SessionApi';
import InfoModal from './components/InfoModal';

/**
 * @file App.tsx
 *
 * Root component of the app, defines global context providers and sets up routing
 * for the main pages: upload and visualization. 
 */

export const ModalContext = createContext<{
  openModal: (content: React.ReactNode, showCloseButton?: boolean) => void;
  closeModal: () => void;
}>({
  openModal: () => {},
  closeModal: () => {},
});

const App = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalContent, setModalContent] = useState<any>(null);
  const [modalKey, setModalKey] = useState(0);
  const [showCloseButton, setShowCloseButton] = useState(true);

  const openModal = (modalContent: React.ReactNode, showCloseButton: boolean = true) => {
    closeModal();
    setModalContent(modalContent);
    setModalKey(prev => prev + 1);
    setIsModalOpen(true);
    setShowCloseButton(showCloseButton);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setModalContent(null);
  };
  
  useEffect(() => {
    setGlobalErrorHandler((errorContent: React.ReactNode) => {
      openModal(errorContent, true);
    });
  }, []);

  const modalContextValue = useMemo(() => ({ openModal, closeModal }), [openModal, closeModal]);

  return (
    <ModalContext.Provider value={ modalContextValue}>
  <Routes>
    <Route path="/" element={<UploadPage />} />
    <Route path="/visualization/:sessionId" element={<VisualizationPage />} />
  </Routes>
  <InfoModal
    isOpen={isModalOpen}
    onClose={closeModal}
    content={modalContent}
    contentKey={modalKey}
    showCloseButton={showCloseButton}
  />
</ModalContext.Provider>
)};

export default App;
