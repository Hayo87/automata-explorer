import React, { useState, useRef } from 'react';
import Modal from 'react-modal';

interface InfoModalProps {
  isOpen: boolean;
  onClose: () => void;
  nodeData: any;
}

export function InfoModal({ isOpen, onClose, nodeData }: InfoModalProps) {
  if (!isOpen || !nodeData) return null;

  return (
    <Modal
      isOpen={isOpen}
      onRequestClose={onClose}
      contentLabel="Node Info"
      style={{
        overlay: {
          backgroundColor: 'transparent',
        },
        content: {
          position: 'static',
          inset: 'unset',
          padding: 0,
          border: 'none',
          background: 'none',
        },
      }}
    >
      {/* Center container with context menu prevention */}
      <div
        style={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '100vh',
        }}
        onContextMenu={(e) => e.preventDefault()}
      >
        <DraggableContainer>
          <div
            style={{
              backgroundColor: 'white',
              border: '1px solid #333',
              padding: '20px',
              boxShadow: '0 0 10px rgba(0,0,0,0.5)',
              maxWidth: '90%',
              maxHeight: '80%',
              overflowY: 'auto',
              cursor: 'move',
            }}
          >
            <div style={{ marginBottom: '10px' }}>
              <h2>{nodeData?.label || 'No Label'}</h2>
            </div>
            <div style={{ textAlign: 'center', marginTop: '10px' }}>
              <button onClick={onClose}>Close</button>
            </div>
          </div>
        </DraggableContainer>
      </div>
    </Modal>
  );
}

interface DraggableContainerProps {
  children: React.ReactNode;
}

const DraggableContainer: React.FC<DraggableContainerProps> = ({ children }) => {
  const [position, setPosition] = useState({ x: 0, y: 0 });
  const isDragging = useRef(false);
  const startPos = useRef({ x: 0, y: 0 });

  const onMouseDown = (e: React.MouseEvent) => {
    isDragging.current = true;
    startPos.current = {
      x: e.clientX - position.x,
      y: e.clientY - position.y,
    };
    document.addEventListener('mousemove', onMouseMove);
    document.addEventListener('mouseup', onMouseUp);
  };

  const onMouseMove = (e: MouseEvent) => {
    if (!isDragging.current) return;
    setPosition({
      x: e.clientX - startPos.current.x,
      y: e.clientY - startPos.current.y,
    });
  };

  const onMouseUp = () => {
    isDragging.current = false;
    document.removeEventListener('mousemove', onMouseMove);
    document.removeEventListener('mouseup', onMouseUp);
  };

  return (
    <div
      onMouseDown={onMouseDown}
      style={{
        position: 'relative',
        left: position.x,
        top: position.y,
      }}
    >
      {children}
    </div>
  );
};
