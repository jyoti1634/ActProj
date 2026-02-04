import React, { useEffect, useState } from 'react'
import useAccounts from '../store/accountsStore'
import ExpenditureTable from '../components/ExpenditureTable'

export default function Expenditure(){
  const selectedMonth = useAccounts(state => state.selectedMonth)
  const selectedYear = useAccounts(state => state.selectedYear)
  const ledger = useAccounts(state => state.ledgerByMonth[selectedMonth?.id] || [])
  const fetchLedger = useAccounts(state => state.fetchLedger)
  const createLedgerEntry = useAccounts(state => state.createLedgerEntry)
  const deleteLedgerEntry = useAccounts(state => state.deleteLedgerEntry)
  const fetchMonths = useAccounts(state => state.fetchMonths)

  const [date, setDate] = useState(() => new Date().toISOString().slice(0,10))
  const [particular, setParticular] = useState('')
  const [amount, setAmount] = useState('')
  const [chequeNo, setChequeNo] = useState('')
  const [bankAmt, setBankAmt] = useState('')
  const [classification, setClassification] = useState('')
  const [error, setError] = useState(null)

  // filter ledger for expenditure entries (expAmt or expbankAmt present)
  const expEntries = ledger.filter(e => (Number(e.expAmt || 0) > 0) || (Number(e.expbankAmt || 0) > 0))

  useEffect(()=>{
    if (selectedMonth?.id) fetchLedger(selectedMonth.id)
  },[selectedMonth, fetchLedger])

  async function add(e){
    e.preventDefault(); setError(null)
    if (!selectedMonth?.id) return setError('Select account/year/month to save to server')
    if (!particular) return setError('Particular is required')
    if (!amount && !bankAmt) return setError('Enter amount or bank amount')

    try {
      const payload = {
        monthId: selectedMonth.id,
        entryDate: date,
        particularExp: particular,
        expAmt: amount ? Number(amount) : 0,
        chequeNo: chequeNo || null,
        expbankAmt: bankAmt ? Number(bankAmt) : 0,
        classificationExp: classification
      }
      await createLedgerEntry(selectedMonth.id, payload)
      // refresh months list for updated balances
      if (selectedYear?.id) fetchMonths(selectedYear.id)
      // clear
      setParticular('')
      setAmount('')
      setChequeNo('')
      setBankAmt('')
      setClassification('')
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Submit failed')
    }
  }

  // totals
  const pageTotal = expEntries.reduce((s,e)=>s + Number(e.expAmt || 0), 0)
  // month total: sum all ledger entries for month that are expenditures
  const monthTotal = ledger.reduce((s,e)=> s + Number(e.expAmt || 0), 0)

  return (
    <div>
      <h3>Expenditure/Cheque Entry</h3>

      <form onSubmit={add} style={{display:'grid',gridTemplateColumns:'1fr',gap:12,maxWidth:900}}>
        <label style={{display:'block'}}>
          Date:
          <input type="date" value={date} onChange={e=>setDate(e.target.value)} style={{width:'100%'}} />
        </label>

        <label style={{display:'block'}}>
          Particular:
          <input placeholder="Enter or select from list" value={particular} onChange={e=>setParticular(e.target.value)} style={{width:'100%'}} />
        </label>

        <label style={{display:'block'}}>
          Amount:
          <input placeholder="Enter amount" value={amount} onChange={e=>setAmount(e.target.value)} style={{width:'100%'}} />
        </label>

        <label style={{display:'block'}}>
          Cheque No.:
          <input placeholder="Enter cheque number (optional)" value={chequeNo} onChange={e=>setChequeNo(e.target.value)} style={{width:'100%'}} />
        </label>

        <label style={{display:'block'}}>
          Bank Amount:
          <input placeholder="Enter bank amount (optional)" value={bankAmt} onChange={e=>setBankAmt(e.target.value)} style={{width:'100%'}} />
        </label>

        <label style={{display:'block'}}>
          Classification:
          <input placeholder="Enter or select from list" value={classification} onChange={e=>setClassification(e.target.value)} style={{width:'100%'}} />
        </label>

        <div style={{marginTop:8}}>
          <button type="submit" style={{background:'#ffb703', padding:'10px 16px', fontWeight:700, borderRadius:6, border:'none'}}>Add Expenditure Entry</button>
        </div>
      </form>

      {error && <div style={{color:'red', marginTop:8}}>{error}</div>}

      <div style={{marginTop:12}}>
        <ExpenditureTable entries={expEntries} pageTotal={pageTotal} monthTotal={monthTotal} onDelete={async (id) => {
          if (selectedMonth?.id) {
            try { await deleteLedgerEntry(selectedMonth.id, id) } catch (e) { setError(e.message || 'Delete failed') }
          }
        }} />
      </div>
    </div>
  )
}