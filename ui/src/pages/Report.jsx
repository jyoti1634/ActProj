import React, { useEffect, useState } from 'react'
import useAccounts from '../store/accountsStore'
import useAuth from '../store/authStore'
export default function Report(){
  const accounts = useAccounts(state => state.accounts)
  const fetchAccounts = useAccounts(state => state.fetchAccounts)
  const fetchYears = useAccounts(state => state.fetchYears)
  const fetchMonths = useAccounts(state => state.fetchMonths)
  const fetchLedger = useAccounts(state => state.fetchLedger)
  const monthsByYear = useAccounts(state => state.monthsByYear)
  const yearsByAccount = useAccounts(state => state.yearsByAccount)

  const [reportType, setReportType] = useState('Monthly')
  const [selectedAccount, setSelectedAccount] = useState(null)
  const [selectedYear, setSelectedYear] = useState(null)
  const [selectedMonth, setSelectedMonth] = useState(null)

  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [result, setResult] = useState(null)

  const user = useAuth(state => state.user)
  // fetch accounts only when user is present (prevents 401/empty responses when anonymous)
  useEffect(()=>{ if (user) fetchAccounts() },[user, fetchAccounts])

  // auto-select first account when accounts load to speed up reports usage
  useEffect(()=>{ if (accounts.length && !selectedAccount) setSelectedAccount(accounts[0]) },[accounts])

  useEffect(()=>{
    if (selectedAccount?.id) fetchYears(selectedAccount.id)
  },[selectedAccount, fetchYears])

  useEffect(()=>{
    if (selectedYear?.id) fetchMonths(selectedYear.id)
  },[selectedYear, fetchMonths])

  async function generate(){
    setError(null)
    setResult(null)
    if (!selectedAccount) return setError('Select account')
    if (reportType === 'Monthly'){
      if (!selectedYear) return setError('Select year')
      if (!selectedMonth) return setError('Select month')
      setLoading(true)
      try {
        const entries = await fetchLedger(selectedMonth.id)
        // compute totals
        const totalCash = entries.reduce((s,e)=> s + Number(e.cashAmt || 0), 0)
        const totalBank = entries.reduce((s,e)=> s + Number(e.cshbankAmt || 0), 0)
        const totalExp = entries.reduce((s,e)=> s + Number(e.expAmt || 0) + Number(e.expbankAmt || 0), 0)
        setResult({ type:'monthly', month:selectedMonth, entries, totals:{ totalCash, totalBank, totalExp } })
      } catch (err) {
        setError(err.message || 'Generate failed')
      } finally { setLoading(false) }
    } else {
      // Financial Year summary: gather months and compute per-month totals
      if (!selectedYear) return setError('Select year')
      setLoading(true)
      try {
        const months = monthsByYear[selectedYear.id] || await fetchMonths(selectedYear.id)
        const rows = []
        for (const m of months){
          const entries = await fetchLedger(m.id)
          const receipts = entries.reduce((s,e)=> s + (Number(e.cashAmt||0) + Number(e.cshbankAmt||0)),0)
          const exp = entries.reduce((s,e)=> s + (Number(e.expAmt||0) + Number(e.expbankAmt||0)),0)
          rows.push({ month:m, receipts, exp, opening: Number(m.openingBalance || 0), closing: Number(m.closingBalance || 0) })
        }
        setResult({ type:'fy', year:selectedYear, rows })
      } catch (err) {
        setError(err.message || 'Generate failed')
      } finally { setLoading(false) }
    }
  }

  function downloadCSV(){
    if (!result) return
    let csv = ''
    if (result.type === 'monthly'){
      csv += `Date,Particular,Cash,Bank,Classification\n`
      result.entries.forEach(e => {
        const date = e.entryDate || ''
        const part = (e.particularCsh || e.particularExp || e.particular || '').replace(/,/g,'')
        csv += `${date},${part},${Number(e.cashAmt||0).toFixed(2)},${Number(e.cshbankAmt||0).toFixed(2)},${(e.classificationCsh||e.classificationExp||'')}\n`
      })
    } else {
      csv += `Month,Opening,Receipts,Expenditure,Closing\n`
      result.rows.forEach(r => {
        csv += `${r.month.monthName},${r.opening.toFixed(2)},${r.receipts.toFixed(2)},${r.exp.toFixed(2)},${r.closing.toFixed(2)}\n`
      })
    }
    const blob = new Blob([csv], { type: 'text/csv' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `report-${reportType.toLowerCase()}-${Date.now()}.csv`
    a.click()
    URL.revokeObjectURL(url)
  }

  // read years from store cache (fetchYears populates this)
  const years = selectedAccount ? (yearsByAccount[selectedAccount.id] || []) : []
  const months = selectedYear ? (monthsByYear[selectedYear.id] || []) : []

  // helper to generate fiscal months Apr..Mar for label matching and ordering
  function generateFiscalMonths(yearStart, yearEnd) {
    const months = []
    for (let m = 4; m <= 12; m++) {
      months.push(new Date(yearStart, m-1).toLocaleString(undefined, { month: 'long' }))
    }
    for (let m = 1; m <= 3; m++) {
      months.push(new Date(yearEnd, m-1).toLocaleString(undefined, { month: 'long' }))
    }
    return months
  }

  // Return months in fiscal order filtered to only include months that exist on server
  function orderedMonthsForYear(y) {
    if (!y) return []
    const fiscal = generateFiscalMonths(y.yearStart || y.start || y.yearStart, y.yearEnd || y.end || (y.yearStart + 1))
    const server = monthsByYear[y.id] || []
    const serverByName = {}
    server.forEach(m => {
      serverByName[(m.monthName || m.name || '').toLowerCase()] = m
    })
    const res = []
    fiscal.forEach(fname => {
      const key = fname.toLowerCase()
      if (serverByName[key]) res.push(serverByName[key])
    })
    // include any server months not matched at the end
    server.forEach(m => {
      const key = (m.monthName || m.name || '').toLowerCase()
      if (!fiscal.map(f=>f.toLowerCase()).includes(key)) res.push(m)
    })
    return res
  }

  return (
    <div>
      <h3>Reports</h3>
      <div className="card">
        <div className="controls">
          <label className="control-label">Report Type:
            <select className="select" value={reportType} onChange={e=>setReportType(e.target.value)} style={{marginLeft:8}}>
              <option>Monthly</option>
              <option>Financial Year</option>
            </select>
          </label>

          <label className="control-label">Account:
            <select className="select" style={{marginLeft:8}} value={selectedAccount?.id||''} onChange={e=>{
              const id = Number(e.target.value); setSelectedAccount(accounts.find(a=>a.id===id) || null); setSelectedYear(null); setSelectedMonth(null)
            }} disabled={!user}>
              {!user && <option value="">-- Login to see accounts --</option>}
              {user && <option value="">-- Select Account --</option>}
              {accounts.map(a=> <option value={a.id} key={a.id}>{a.accountName} ({a.id})</option>)}
            </select>
          </label>

          <label className="control-label">Year:
            <select className="select" style={{marginLeft:8}} value={selectedYear?.id||''} onChange={e=>{ const id = Number(e.target.value); setSelectedYear(years.find(y=>y.id===id) || null); setSelectedMonth(null) }}>
              <option value="">-- Select Year --</option>
              {years.map(y => <option key={y.id} value={y.id}>{y.yearStart}-{String(y.yearEnd).slice(-2)}</option>)}
            </select>
          </label>

          {reportType === 'Monthly' && (
            <label className="control-label">Month:
              <select className="select" style={{marginLeft:8}} value={selectedMonth?.id||''} onChange={e=>{ const id = Number(e.target.value); setSelectedMonth(months.find(m=>m.id===id) || null) }}>
                <option value="">-- Select Month --</option>
                {orderedMonthsForYear(selectedYear).map(m => <option key={m.id} value={m.id}>{m.monthName}</option>)}
              </select>
            </label>
          )}

          <div>
            <button className={`btn ${loading ? '' : 'btn-primary'}`} onClick={generate} disabled={loading} style={{padding:'8px 12px'}}>{loading ? 'Generating...' : 'Generate Report'}</button>
          </div>

          {result && <div style={{marginLeft:12}}>
            <button className="btn" onClick={downloadCSV}>Download CSV</button>
          </div>}
        </div>
      </div>

      <div style={{marginTop:16}}>
        {error && <div style={{color:'red'}}>{error}</div>}
        {result && result.type === 'monthly' && (
          <div>
            <h4>Monthly Report — {result.month.monthName}</h4>
            <div style={{display:'flex',gap:12}}>
              <div>Cash Total: <strong>{Number(result.totals.totalCash).toLocaleString(undefined,{minimumFractionDigits:2,maximumFractionDigits:2})}</strong></div>
              <div>Bank Total: <strong>{Number(result.totals.totalBank).toLocaleString(undefined,{minimumFractionDigits:2,maximumFractionDigits:2})}</strong></div>
              <div>Expenditure: <strong>{Number(result.totals.totalExp).toLocaleString(undefined,{minimumFractionDigits:2,maximumFractionDigits:2})}</strong></div>
            </div>

            <table className="table table-striped" style={{marginTop:12}}>
              <thead>
                <tr><th>Date</th><th>Particular</th><th className="text-right">Cash</th><th className="text-right">Bank</th><th>Classification</th></tr>
              </thead>
              <tbody>
                {result.entries.map(e => (
                  <tr key={e.id}>
                    <td>{e.entryDate || ''}</td>
                    <td>{e.particularCsh || e.particularExp || e.particular}</td>
                    <td className="text-right">{Number(e.cashAmt||0).toLocaleString(undefined,{minimumFractionDigits:2,maximumFractionDigits:2})}</td>
                    <td className="text-right">{Number(e.cshbankAmt||0).toLocaleString(undefined,{minimumFractionDigits:2,maximumFractionDigits:2})}</td>
                    <td>{e.classificationCsh || e.classificationExp || ''}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {result && result.type === 'fy' && (
          <div>
            <h4>Financial Year Report — {result.year.yearStart}-{String(result.year.yearEnd).slice(-2)}</h4>
            <table className="table table-striped" style={{marginTop:12}}>
              <thead><tr><th>Month</th><th className="text-right">Opening</th><th className="text-right">Receipts</th><th className="text-right">Expenditure</th><th className="text-right">Closing</th></tr></thead>
              <tbody>
                {result.rows.map(r => (
                  <tr key={r.month.id}>
                    <td>{r.month.monthName}</td>
                    <td className="text-right">{Number(r.opening||0).toLocaleString(undefined,{minimumFractionDigits:2,maximumFractionDigits:2})}</td>
                    <td className="text-right">{Number(r.receipts||0).toLocaleString(undefined,{minimumFractionDigits:2,maximumFractionDigits:2})}</td>
                    <td className="text-right">{Number(r.exp||0).toLocaleString(undefined,{minimumFractionDigits:2,maximumFractionDigits:2})}</td>
                    <td className="text-right">{Number(r.closing||0).toLocaleString(undefined,{minimumFractionDigits:2,maximumFractionDigits:2})}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}