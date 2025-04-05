import React from 'react';
import Modal from 'react-modal';

// Make sure to set the app element in your entry point, e.g.,
// Modal.setAppElement('#root');

interface InfoModalProps {
  isOpen: boolean;
  onClose: () => void;
  content: React.ReactNode;
}

export function InfoModal({ isOpen, onClose, content }: InfoModalProps) {
  if (!isOpen) return null;

  return (
    <Modal
      isOpen={isOpen}
      onRequestClose={onClose}
      parentSelector={() => document.getElementById('modal-root') || document.body}
      contentLabel="Info Modal"
      className="modal-content"
      overlayClassName="modal-overlay"
    >
      <div onContextMenu={(e) => e.preventDefault()}>
        <DraggableContainer>
          <div className="modal-inner-content">
            <div>{content}</div>
            <div className="modal-footer">
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
  const [position, setPosition] = React.useState({ x: 0, y: 0 });
  const isDragging = React.useRef(false);
  const startPos = React.useRef({ x: 0, y: 0 });

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

  React.useEffect(() => {
    return () => {
      document.removeEventListener('mousemove', onMouseMove);
      document.removeEventListener('mouseup', onMouseUp);
    };
  }, []);

  return (
    <div
      onMouseDown={onMouseDown}
      style={{ position: 'relative', left: position.x, top: position.y }}
    >
      {children}
    </div>
  );
};

export default InfoModal;
