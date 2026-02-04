import React, { useEffect, useState } from 'react'
import EntriesTable from '../components/EntriesTable'
import useAccounts from '../store/accountsStore'
import api from '../lib/api'

export default function Entry(){
  const accounts = useAccounts(state => state.accounts)
  const fetchYears = useAccounts(state => state.fetchYears)
  const fetchMonths = useAccounts(state => state.fetchMonths)
  const selectAccount = useAccounts(state => state.selectAccount)
  const selectYear = useAccounts(state => state.selectYear)
  const selectMonth = useAccounts(state => state.selectMonth)
  const selectedAccount = useAccounts(state => state.selectedAccount)
  const selectedYear = useAccounts(state => state.selectedYear)
  const selectedMonth = useAccounts(state => state.selectedMonth)
  const yearsByAccount = useAccounts(state => state.yearsByAccount)
  const monthsByYear = useAccounts(state => state.monthsByYear)

  const [particular, setParticular] = useState('')
  const [date, setDate] = useState(() => new Date().toISOString().slice(0,10))
  const [cashAmt, setCashAmt] = useState('')
  const [bankAmt, setBankAmt] = useState('')
  const [classification, setClassification] = useState('')
  const [entries, setEntries] = useState([])
  const [error, setError] = useState(null)
  const ledger = useAccounts(state => state.ledgerByMonth[selectedMonth?.id] || [])
  const ledgerLoading = useAccounts(state => state.ledgerLoading)
  const fetchLedger = useAccounts(state => state.fetchLedger)
  const createLedgerEntry = useAccounts(state => state.createLedgerEntry)
  const deleteLedgerEntry = useAccounts(state => state.deleteLedgerEntry)

  useEffect(() => {
    if (selectedAccount?.id) {
      fetchYears(selectedAccount.id)
    }
  }, [selectedAccount, fetchYears])

  useEffect(() => {
    if (selectedYear?.id) {
      fetchMonths(selectedYear.id)
    }
  }, [selectedYear, fetchMonths])

  useEffect(() => {
    if (selectedMonth?.id) {
      // fetch ledger entries for selected month
      fetchLedger(selectedMonth.id)
    }
  }, [selectedMonth, fetchLedger])

  async function add(e){
    e.preventDefault()
    setError(null)

    if (!selectedMonth?.id) return setError('Select account/year/month to save to server')
    if (!particular) return setError('Particular is required')
    if (!cashAmt && !bankAmt) return setError('Enter cash or bank amount')

    try {
      const payload = {
        monthId: selectedMonth.id,
        entryDate: date,
        particularCsh: particular,
        cashAmt: cashAmt ? Number(cashAmt) : 0,
        cshbankAmt: bankAmt ? Number(bankAmt) : 0,
        expAmt: 0,
        classificationCsh: classification
      }
      await createLedgerEntry(selectedMonth.id, payload)
      // refresh month metadata (opening / closing) after create
      if (selectedYear?.id) fetchMonths(selectedYear.id)
      // clear inputs
      setParticular('')
      setCashAmt('')
      setBankAmt('')
      setClassification('')
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Submit failed')
    }
  }

  return (
    <div>
      <h3>Entry</h3>

      <form onSubmit={add} style={{display:'grid',gridTemplateColumns:'1fr 1fr',gap:12,maxWidth:900}}>
        <label style={{display:'block'}}>
          Date:
          <input type="date" value={date} onChange={e=>setDate(e.target.value)} style={{width:'100%'}} />
        </label>
        <label style={{display:'block'}}>
          Particular:
          <input placeholder="Enter or select" value={particular} onChange={e=>setParticular(e.target.value)} style={{width:'100%'}} />
        </label>

        <label style={{display:'block'}}>
          Cash (Amount):
          <input placeholder="Enter cash amount or leave blank" value={cashAmt} onChange={e=>setCashAmt(e.target.value)} style={{width:'100%'}} />
        </label>
        <label style={{display:'block'}}>
          Bank (Amount):
          <input placeholder="Enter bank amount or leave blank" value={bankAmt} onChange={e=>setBankAmt(e.target.value)} style={{width:'100%'}} />
        </label>

        <label style={{display:'block',gridColumn:'1 / -1'}}>
          Classification:
          <input placeholder="Enter or select classification" value={classification} onChange={e=>setClassification(e.target.value)} style={{width:'100%'}} />
        </label>

        <div style={{gridColumn:'1 / -1'}}>
          <button type="submit">Add Entry</button>
        </div>
      </form>
      {error && <div style={{color:'red', marginTop:8}}>{error}</div>}
      <div style={{marginTop:12}}>
        {ledgerLoading ? <div>Loading ledger...</div> : <EntriesTable entries={ledger.length ? ledger : entries} month={selectedMonth} onDelete={async (id) => {
          if (selectedMonth?.id) {
            try { await deleteLedgerEntry(selectedMonth.id, id) } catch (e) { setError(e.message || 'Delete failed') }
            // refresh month metadata (opening/closing balances) after delete
            if (selectedYear?.id) fetchMonths(selectedYear.id)
          } else {
            setEntries(prev => prev.filter(e => e.id !== id))
          }
        }} />}
      </div>
    </div>
  )
}
