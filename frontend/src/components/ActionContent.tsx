import React, { useEffect, useState } from "react";
import { ProcessAction, ProcessOption } from "../api/RequestResponse";

/**
 * @file ActionContent.tsx
 * 
 * Provides and processes the content used in the action modal. It includes loading the correct processing options
 * for the automata type, removing/adding actions and displaying the current actions applied including the order. 
 */

interface ActionModalProps {
  setActions: ProcessAction[];
  options: ProcessOption[];
  onProcess: (filters: ProcessAction[]) => void;
}

const ActionModal: React.FC<ActionModalProps> = ({
  setActions: appliedActions,
  options,
  onProcess,
}) => {

  // Actions already applied 
  const [actions, setActions] = useState<ProcessAction[]>([]);

  // Extract dropdown values from options
  const allStages = Array.from(new Set(options.map(o => o.stage)));
  const allTypes = Array.from(new Set(options.map(o => o.type)));
  const allSubtypes = Array.from(new Set(options.flatMap(o => o.subtypes)));

  // New action template
  const [newAction, setNewAction] = useState<ProcessAction>({
    stage: allStages[0] || '',
    type: allTypes[0] || '',
    subtype: allSubtypes[0] || '',
    name: '',
    values: [],
    order: 0,
  });
  const [valueInput, setValueInput] = useState('');
  
  useEffect(() => {
    setActions(appliedActions);
  }, [appliedActions]);

  const handleAdd = () => {
    // Extract inputs 
    const splitValues = valueInput
      .split(',')
      .map((v) => v.trim())
      .filter((v) => v.length > 0);

    // Create new action
    const newEntry: ProcessAction = {
      stage: newAction.stage,
      type: newAction.type,
      name: newAction.name,
      values: splitValues,
      subtype: newAction.subtype,
      order: actions.length + 1,
    };

    // Check if valid
    if(!isValidAction(String(newAction.stage), newAction.type, String(newAction.subtype))){
      alert("Invalid action combination");
      return;
    }
    console.log(newEntry);

    // Add entry to actions
    setActions([...actions, newEntry]);

    // Reset the input fields
    setNewAction({...newAction, name: '', values: [], order: 0});
    setValueInput('');
  };

  const isValidAction = (stage: string, type: string, subtype: string): boolean => {
    return options.some(
      (o) => o.stage === stage && o.type === type && o.subtypes.includes(subtype));
  };

  const handleRemove = (index: number) => {
    const updated = [...actions];
    updated.splice(index, 1);
    const recalculated = updated.map((f, i) => ({ ...f, order: i + 1 }));
    setActions(recalculated);
  };

  const handleProcess = () => {
    onProcess(actions);
  };

  return (
    <div style={{ minWidth: '600px', minHeight: '500px' }}>
      <h2>Active actions</h2>

      {actions.length === 0 ? (
        <p>No modifications made.</p>
      ) : (
        <ul style={{ padding: 0, listStyle: 'none' }}>
          {actions.map((filter, index) => (
            <li
              key={index}
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                padding: '4px 0',
                borderBottom: '1px solid #eee',
              }}
            >
              <span>
                <strong>{filter.stage}</strong> - <strong>{filter.type}</strong>
                {filter.subtype && <> [{filter.subtype}]</>}
                {filter.name && <> → {filter.name}</>}
                {' = '}
                {filter.values!.join(', ')}
                {' (Order: ' + filter.order + ')'}
              </span>
              <button 
                className='button'
                onClick={() => handleRemove(index)}
                style={{
                  background: 'transparent',
                  border: 'none',
                  color: '#999',
                  cursor: 'pointer',
                  fontSize: '14px',
                }}
              >
                ✕
              </button>
            </li>
          ))}
        </ul>
      )}

      <hr style={{ margin: '12px 0' }} />

      <h3>Add actions</h3>
      <div style={{ display: 'flex', gap: '8px', alignItems: 'center', flexWrap: 'nowrap', marginBottom: '24px' }}>
        
        {/* Stage dropdown*/}
        <select
          value={String(newAction.stage)}
          onChange={(e) => setNewAction({...newAction, stage: e.target.value})}              
        >
          {allStages.map(stage => (
            <option key={stage} value={stage}>{stage}</option>
          ))}
        </select>

        {/* Type dropdown*/}
        <select
          value={newAction.type}
          onChange={(e) => { setNewAction({...newAction, type: e.target.value});
            setValueInput('');
          }}
        >
          {allTypes .map(type => (
              <option key={type} value={type}>{type}</option>
            ))}
        </select>

        {/* Subtype dropdown*/}
        <select
          value={newAction.subtype ?? ''}
          onChange={(e) => setNewAction({ ...newAction, subtype: e.target.value as ProcessAction['subtype'] })}
        >
          {allSubtypes.map((sub) => (
            <option key={sub} value={sub}>{sub}</option>
          ))}
        </select>

        {/* Name textfield*/}  
        <input
          type="text"
          placeholder="Name"
          value={newAction.name ?? ''}
          onChange={(e) => setNewAction({ ...newAction, name: e.target.value })}
          style={{ width: '160px' }}
        />

        {/* Values textfield*/}  
        <input
          type="text"
          placeholder="Values (comma-separated)"
          value={valueInput}
          onChange={(e) => setValueInput(e.target.value)}
          style={{ width: '160px' }}
        />

        <button className='button' onClick={handleAdd}>Add</button>
      </div>

      <div style={{ display: 'flex', justifyContent: 'center', marginTop: '24px' }}>
        <button className='button' onClick={handleProcess}>Process</button>
      </div>
    </div>
  );
};

export default ActionModal;
