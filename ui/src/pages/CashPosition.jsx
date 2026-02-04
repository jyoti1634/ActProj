import React, { useEffect, useState } from 'react'
import useAccounts from '../store/accountsStore'

export default function CashPosition(){
  const selectedMonth = useAccounts(state => state.selectedMonth)
  const selectedYear = useAccounts(state => state.selectedYear)
  const fetchLedger = useAccounts(state => state.fetchLedger)
  const updateMonth = useAccounts(state => state.updateMonth)
  const ledger = useAccounts(state => state.ledgerByMonth[selectedMonth?.id] || [])

  const [uncashed, setUncashed] = useState(0)
  const [uncredited, setUncredited] = useState(0)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState(null)

  useEffect(()=>{
    if (selectedMonth?.id) fetchLedger(selectedMonth.id)
  },[selectedMonth, fetchLedger])

  const opening = Number(selectedMonth?.openingBalance || 0)
  // total receipts: cashAmt + cshbankAmt for entries that are not expenditures
  const totalReceiptsFiltered = ledger.reduce((s,e)=> s + ((Number(e.expAmt || 0) === 0) ? (Number(e.cashAmt||0) + Number(e.cshbankAmt||0)) : 0), 0)
  const expenditure = ledger.reduce((s,e)=> s + (Number(e.expAmt || 0) + Number(e.expbankAmt || 0)), 0)

  const grossTotal = opening + totalReceiptsFiltered
  const cashBalance = grossTotal - expenditure
  const computedClosing = cashBalance - Number(uncashed || 0) - Number(uncredited || 0)

  async function saveClosing(){
    setSaving(true); setError(null)
    try {
      if (!selectedYear?.id || !selectedMonth?.id) throw new Error('Select year and month')
      // update month closingBalance on server
      await updateMonth(selectedYear.id, selectedMonth.id, { closingBalance: computedClosing })
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Save failed')
    } finally { setSaving(false) }
  }

  return (
    <div>
      <h3>Cash Position</h3>
      <div style={{maxWidth:900}}>
        <table className="table" style={{width:'100%'}}>
          <thead>
            <tr>
              <th style={{textAlign:'left'}}>Description</th>
              <th style={{textAlign:'right'}}>Amount</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Opening Balance</td>
              <td style={{textAlign:'right'}}>{opening.toFixed(2)}</td>
            </tr>
            <tr>
              <td>Total Receipts</td>
              <td style={{textAlign:'right'}}>{totalReceiptsFiltered.toFixed(2)}</td>
            </tr>
            <tr style={{background:'#eef6fb', fontWeight:700}}>
              <td>Gross Total (Opening Balance + Total Receipts)</td>
              <td style={{textAlign:'right'}}>{grossTotal.toFixed(2)}</td>
            </tr>
            <tr>
              <td>Expenditure</td>
              <td style={{textAlign:'right'}}>{expenditure.toFixed(2)}</td>
            </tr>
            <tr>
              <td>Cash Balance</td>
              <td style={{textAlign:'right'}}>{cashBalance.toFixed(2)}</td>
            </tr>
            <tr>
              <td>Uncashed Cheque Amount</td>
              <td style={{textAlign:'right'}}><input value={uncashed} onChange={e=>setUncashed(e.target.value)} style={{width:120,textAlign:'right'}} /></td>
            </tr>
            <tr>
              <td>Uncredited Cheque Amount</td>
              <td style={{textAlign:'right'}}><input value={uncredited} onChange={e=>setUncredited(e.target.value)} style={{width:120,textAlign:'right'}} /></td>
            </tr>
            <tr style={{background:'#dfe9ef', fontWeight:700}}>
              <td>Closing Balance (As per Bank Statement)</td>
              <td style={{textAlign:'right'}}>{computedClosing.toFixed(2)}</td>
            </tr>
          </tbody>
        </table>
        <div style={{marginTop:12}}>
          <button onClick={saveClosing} disabled={saving} style={{padding:'8px 12px'}}>{saving ? 'Saving...' : 'Save Closing Balance'}</button>
          {error && <div style={{color:'red', marginTop:8}}>{error}</div>}
        </div>
      </div>
    </div>
  )
}