import React from 'react'

export default function TabNav({ tab, setTab, locked }){
  const tabs = [
    { id: 'entry', label: 'Entry' },
    { id: 'expenditure', label: 'Expenditure' },
    { id: 'cash', label: 'Cash Position' },
    { id: 'report', label: 'Report' },
    { id: 'balance', label: 'Balance' }
  ]

  return (
    <div className="card" style={{padding:10}}>
      <div className="tabnav">
        {tabs.map(t => (
          <button
            key={t.id}
            onClick={() => !locked && setTab(t.id)}
            className={"tab" + (t.id === tab ? ' active' : '') + (locked && t.id !== 'entry' ? ' disabled' : '')}
            disabled={locked && t.id !== 'entry'}
          >
            {t.label}
          </button>
        ))}
        {locked && <span className="small muted" style={{marginLeft:12}}>Select account / year / month to unlock tabs</span>}
      </div>
    </div>
  )
}