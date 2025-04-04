import React, { useState, useEffect } from 'react';
import { Filter } from '../types/BuildResponse';

interface Props {
  initialFilters: Filter[]; 
  onProcess: (filters: Filter[]) => void;
}

const filterTypes = ['synonym', 'hider'] as const;
const filterSubtypes = ['input', 'output'];
const hiderSubtypes = ['input', 'output', 'loop'];

const FilterInfo: React.FC<Props> = ({ initialFilters, onProcess }) => {
  const [filters, setFilters] = useState<Filter[]>([]);
  const [newFilter, setNewFilter] = useState<Filter>({
    type: 'synonym',
    subtype: 'output',
    name: '',
    values: [],
    order: 0,
    decoratedName: ''
  });
  const [valueInput, setValueInput] = useState('');

  useEffect(() => {
    setFilters(initialFilters);
  }, [initialFilters]);

  const handleAdd = () => {
    if (!valueInput.trim() || !newFilter.subtype) return;
    if (newFilter.type !== 'hider' && !newFilter.name.trim()) return;

    const splitValues = valueInput
      .split(',')
      .map((v) => v.trim())
      .filter((v) => v.length > 0);

    if (splitValues.length === 0) return;

    const newEntry: Filter = {
      type: newFilter.type,
      name: newFilter.name,
      values: splitValues,
      subtype: newFilter.subtype,
      order: filters.length + 1,
      decoratedName: '',
    };

    setFilters([...filters, newEntry]);
    setNewFilter({ type: 'synonym', name: '', values: [], subtype: 'output', order: 0, decoratedName: ''});
    setValueInput('');
  };

  const handleRemove = (index: number) => {
    const updated = [...filters];
    updated.splice(index, 1);
    const recalculated = updated.map((f, i) => ({ ...f, order: i + 1 }));
    setFilters(recalculated);
  };

  const handleProcess = () => {
    onProcess(filters);
  };

  return (
    <div style={{ minWidth: '500px', minHeight: '400px' }}>
      <h2>Active Filters</h2>

      {filters.length === 0 ? (
        <p>No filters added.</p>
      ) : (
        <ul style={{ padding: 0, listStyle: 'none' }}>
          {filters.map((filter, index) => (
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
                <strong>{filter.type}</strong>
                {filter.subtype && <> [{filter.subtype}]</>}
                {filter.name && <> → {filter.name}</>}
                {' = '}
                {filter.values.join(', ')}
              </span>
              <button
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

      <h3>Add Filter</h3>
      <div
        style={{
          display: 'flex',
          gap: '8px',
          alignItems: 'center',
          flexWrap: 'nowrap',
          marginBottom: '24px',
        }}
      >
        <select
          value={newFilter.type}
          onChange={(e) => {
            const selectedType = e.target.value as Filter['type'];
            setNewFilter({
              ...newFilter,
              type: selectedType,
              name: '',
              values: [],
              subtype: selectedType === 'synonym' ? 'output' : 'loop'
            });
            setValueInput('');
          }}
        >
          {filterTypes.map((type) => (
            <option key={type} value={type}>
              {type}
            </option>
          ))}
        </select>

        <select
          value={newFilter.subtype || ''}
          onChange={(e) => setNewFilter({ ...newFilter, subtype: e.target.value })}
          style={{ width: '100px' }}
        >
          <option value="">Subtype</option>
          {(newFilter.type === 'synonym' ? filterSubtypes : hiderSubtypes).map((sub) => (
            <option key={sub} value={sub}>
              {sub}
            </option>
          ))}
        </select>

        <input
          type="text"
          placeholder="Name"
          value={newFilter.name}
          disabled={newFilter.type === 'hider'}
          onChange={(e) => setNewFilter({ ...newFilter, name: e.target.value })}
          style={{
            width: '160px',
            backgroundColor: newFilter.type === 'hider' ? '#f0f0f0' : undefined,
            color: newFilter.type === 'hider' ? '#999' : undefined,
          }}
        />

        <input
          type="text"
          placeholder="Values (comma-separated)"
          value={valueInput}
          onChange={(e) => setValueInput(e.target.value)}
          style={{ width: '160px' }}
        />

        <button onClick={handleAdd}>Add</button>
      </div>

      <div style={{ display: 'flex', justifyContent: 'center', marginTop: '24px' }}>
        <button onClick={handleProcess}>Process</button>
      </div>
    </div>
  );
};

export default FilterInfo;
