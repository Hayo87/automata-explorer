import React, { useState, useEffect } from 'react';
import { Filter } from '../types/Filter';

interface Props {
  initialFilters: Filter[]; 
  onProcess: (filters: Filter[]) => void;
}

const filterTypes = ['synonym', 'loop', 'outputHider'] as const;
const loopValues = ['common', 'reference', 'subject'];

const FilterInfo: React.FC<Props> = ({ initialFilters, onProcess }) => {
  const [filters, setFilters] = useState<Filter[]>([]);
  const [newFilter, setNewFilter] = useState<Filter>({
    type: 'synonym',
    name: '',
    values: [],
  });
  const [valueInput, setValueInput] = useState('');

  const isLoop = newFilter.type === 'loop';

  useEffect(() => {
    setFilters(initialFilters);
  }, [initialFilters]);

  const handleAdd = () => {
    if (!valueInput.trim()) return;
    if (!isLoop && !newFilter.name.trim()) return;

    const splitValues = valueInput
      .split(',')
      .map((v) => v.trim())
      .filter((v) => v.length > 0);

    if (splitValues.length === 0) return;

    const newEntry: Filter = {
      type: newFilter.type,
      name: newFilter.name,
      values: splitValues,
    };

    setFilters([...filters, newEntry]);
    setNewFilter({ type: 'synonym', name: '', values: [] });
    setValueInput('');
  };

  const handleRemove = (index: number) => {
    const updated = [...filters];
    updated.splice(index, 1);
    setFilters(updated);
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
            setNewFilter({ ...newFilter, type: e.target.value as Filter['type'], name: '', values: [] });
            setValueInput('');
          }}
        >
          {filterTypes.map((type) => (
            <option key={type} value={type}>
              {type}
            </option>
          ))}
        </select>

        <input
          type="text"
          placeholder="Name"
          value={newFilter.name}
          disabled={isLoop}
          onChange={(e) => setNewFilter({ ...newFilter, name: e.target.value })}
          style={{
            width: '160px',
            backgroundColor: isLoop ? '#f0f0f0' : undefined,
            color: isLoop ? '#999' : undefined,
          }}
        />

        {isLoop ? (
          <select
            value={newFilter.values[0] || ''}
            onChange={(e) => setNewFilter({ ...newFilter, values: [e.target.value] })}
            style={{ width: '160px' }}
          >
            <option value="">Select</option>
            {loopValues.map((val) => (
              <option key={val} value={val}>
                {val}
              </option>
            ))}
          </select>
        ) : (
          <input
            type="text"
            placeholder="Values (comma-separated)"
            value={valueInput}
            onChange={(e) => setValueInput(e.target.value)}
            style={{ width: '160px' }}
          />
        )}

        <button onClick={handleAdd}>Add</button>
      </div>

      <div style={{ display: 'flex', justifyContent: 'center', marginTop: '24px' }}>
        <button onClick={handleProcess}>Process</button>
      </div>
    </div>
  );
};

export default FilterInfo;
