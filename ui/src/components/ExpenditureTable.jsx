import React from 'react'

export default function ExpenditureTable({ entries, onDelete, pageTotal, monthTotal }){
  function remove(id){ if(onDelete) return onDelete(id) }
  return (
    <div style={{marginTop:16}}>
      <table className="table">
        <thead>
          <tr>
            <th>Date</th>
            <th>Particular</th>
            <th>Amount</th>
            <th>Cheque No.</th>
            <th>Bank Amount</th>
            <th>Classification</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {entries.length === 0 && (
            <tr>
              <td colSpan={7} style={{textAlign:'center'}}>No expenditure entries found</td>
            </tr>
          )}
          {entries.map(r=> (
            <tr key={r.id}>
              <td>{r.entryDate ? new Date(r.entryDate).toLocaleDateString() : (r.createdAt ? new Date(r.createdAt).toLocaleDateString() : '')}</td>
              <td>{r.particularExp || r.particular}</td>
              <td>{r.expAmt != null ? Number(r.expAmt).toFixed(2) : ''}</td>
              <td>{r.chequeNo || ''}</td>
              <td>{r.expbankAmt != null ? Number(r.expbankAmt).toFixed(2) : ''}</td>
              <td>{r.classificationExp || ''}</td>
              <td>{onDelete && <button onClick={()=>remove(r.id)}>Delete</button>}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <div style={{marginTop:12, maxWidth:900}}>
        <div style={{background:'#f7f7f9',padding:12,borderRadius:4}}>
          <div style={{display:'flex',justifyContent:'space-between'}}>
            <div><strong>Page total (this page)</strong></div>
            <div style={{fontWeight:700}}>{Number(pageTotal || 0).toFixed(2)}</div>
          </div>
        </div>
        <div style={{background:'#f4fffb',padding:12,borderRadius:4, marginTop:8}}>
          <div style={{display:'flex',justifyContent:'space-between'}}>
            <div><strong>Month total (all pages)</strong></div>
            <div style={{fontWeight:700}}>{Number(monthTotal || 0).toFixed(2)}</div>
          </div>
        </div>
      </div>
    </div>
  )
}