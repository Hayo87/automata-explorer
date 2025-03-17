import Modal from 'react-modal';
import Draggable from 'react-draggable';

Modal.setAppElement('#root');

interface InfoModalProps {
  isOpen: boolean;
  onClose: () => void;
  nodeData: any;
}

export function InfoModal({ isOpen, onClose, nodeData }: InfoModalProps) {
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
      <Draggable handle=".modal-header">
        <div
          style={{
            backgroundColor: 'white',
            border: '1px solid #333',
            padding: '20px',
            boxShadow: '0 0 10px rgba(0,0,0,0.5)',
            maxWidth: '90%',
            maxHeight: '80%',
            overflowY: 'auto',
          }}
        >
          <div className="modal-header" style={{ cursor: 'move', marginBottom: '10px' }}>
            <h2>Node Information</h2>
          </div>
          <div className="modal-content">
            {/* Render your node data here. For example, a table: */}
            <table border={1} style={{ borderCollapse: 'collapse', width: '100%' }}>
              <tbody>
                <tr>
                  <td>ID</td>
                  <td>{nodeData.id}</td>
                </tr>
                <tr>
                  <td>Label</td>
                  <td>{nodeData.label || 'N/A'}</td>
                </tr>
                <tr>
                  <td>Transitions</td>
                  <td>{nodeData.transitions || 'N/A'}</td>
                </tr>
                {/* Add additional rows as needed */}
              </tbody>
            </table>
          </div>
          <div style={{ textAlign: 'center', marginTop: '10px' }}>
            <button onClick={onClose}>Close</button>
          </div>
        </div>
      </Draggable>
    </Modal>
  );
}
