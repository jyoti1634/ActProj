import React from 'react'

export default function EntriesTable({ entries, onDelete, month }){
  function remove(id){ if(onDelete) return onDelete(id) }
  const totalCash = entries.reduce((s,e)=>s+Number(e.cashAmt||0),0)
  const totalBank = entries.reduce((s,e)=>s+Number(e.cshbankAmt||0),0)
  const opening = Number(month?.openingBalance || 0)
  const monthTotal = totalCash + totalBank
  const grossTotal = opening + monthTotal
  return (
    <div style={{marginTop:16}}>
      <table className="table">
        <thead>
          <tr>
            <th>Date</th>
            <th>Particular</th>
            <th>Cash</th>
            <th>Bank</th>
            <th>Classification</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {entries.map(r=> (
            <tr key={r.id}>
              <td>{r.entryDate ? new Date(r.entryDate).toLocaleDateString() : (r.createdAt ? new Date(r.createdAt).toLocaleDateString() : '')}</td>
              <td>{r.particularCsh || r.particularExp || r.particular}</td>
              <td>{r.cashAmt != null ? Number(r.cashAmt).toFixed(2) : ''}</td>
              <td>{r.cshbankAmt != null ? Number(r.cshbankAmt).toFixed(2) : ''}</td>
              <td>{r.classificationCsh || r.classificationExp || ''}</td>
              <td>{onDelete && <button onClick={()=>remove(r.id)}>Delete</button>}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Summary section under the table (Opening balance / Month totals / Gross total) */}
      <div style={{marginTop:12, maxWidth:900, borderTop:'1px solid #eee', paddingTop:12}}>
        <div style={{display:'flex',alignItems:'flex-start', gap:16}}>
          <div style={{flex:1}}>
            <div style={{background:'#fff6f6',padding:12,borderRadius:4}}>
              <strong>OPENING BALANCE</strong>
              <div style={{marginTop:6}}>Opening Balance: <strong style={{color:'#b22222'}}>{opening.toFixed(2)}</strong></div>
            </div>

            <div style={{background:'#f4fffb',padding:12,borderRadius:4, marginTop:8}}>
              <strong>MONTH TOTAL</strong>
              <div style={{display:'flex',justifyContent:'space-between', marginTop:6}}>
                <div>Cash: <strong>{totalCash.toFixed(2)}</strong></div>
                <div>Bank: <strong>{totalBank.toFixed(2)}</strong></div>
              </div>
            </div>

            <div style={{background:'#f1fbff',padding:12,borderRadius:4, marginTop:8}}>
              <strong>GROSS TOTAL (Month)</strong>
              <div style={{marginTop:6, color:'#0b846d', fontSize:18, fontWeight:700}}>{grossTotal.toFixed(2)}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
