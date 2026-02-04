import React, { useEffect, useState } from 'react'
import useAuth from '../store/authStore'
import useAccounts from '../store/accountsStore'
import api from '../lib/api'

export default function AccountSelector(){
  const user = useAuth(state => state.user)
  const accounts = useAccounts(state => state.accounts)
  const selectedAccount = useAccounts(state => state.selectedAccount)
  const selectedYear = useAccounts(state => state.selectedYear)
  const selectedMonth = useAccounts(state => state.selectedMonth)
  const fetchAccounts = useAccounts(state => state.fetchAccounts)
  const fetchYears = useAccounts(state => state.fetchYears)
  const fetchMonths = useAccounts(state => state.fetchMonths)
  const selectAccount = useAccounts(state => state.selectAccount)
  const selectYear = useAccounts(state => state.selectYear)
  const selectMonth = useAccounts(state => state.selectMonth)
  const [creating, setCreating] = useState(false)

  // fetch accounts on login
  useEffect(() => {
    if (user) fetchAccounts()
  }, [user, fetchAccounts])

  // when account changes, load years
  useEffect(() => {
    if (selectedAccount?.id) fetchYears(selectedAccount.id)
  }, [selectedAccount, fetchYears])

  // when year changes, load months
  useEffect(() => {
    if (selectedYear?.id) fetchMonths(selectedYear.id)
  }, [selectedYear, fetchMonths])

  async function handleNewAccount(){
    const name = window.prompt('Enter new account name')
    if (!name) return
    // client-side duplicate check (case-insensitive)
    if (accounts.some(a => (a.accountName || a.name || '').toLowerCase() === name.trim().toLowerCase())) {
      return window.alert('An account with this name already exists')
    }
    setCreating(true)
    try {
      const resp = await api.post('/accounts', { accountName: name })
      // refresh and select
      await fetchAccounts()
      const created = resp.data
      if (created?.id) {
        selectAccount(created)
        // fetch years for the new account
        await fetchYears(created.id)
      }
    } catch (err) {
      console.error('create account error', err)
      window.alert(err.response?.data?.message || err.message || 'Failed to create account')
    } finally {
      setCreating(false)
    }
  }

  async function handleNewYear(){
    if (!selectedAccount?.id) return window.alert('Select an account first')
    const input = window.prompt('Enter new year (e.g., 2026-27 or 2026)')
    if (!input) return

    // parse input like "2026-27" or single year "2026"
    let yearStart = null
    let yearEnd = null
    const m = input.trim().match(/^(\d{4})(?:-(\d{2,4}))?$/)
    if (m) {
      yearStart = parseInt(m[1], 10)
      if (m[2]) {
        const y2 = m[2]
        yearEnd = y2.length === 2 ? Math.floor(yearStart / 100) * 100 + parseInt(y2, 10) : parseInt(y2, 10)
        if (yearEnd < yearStart) yearEnd = yearStart + 1
      } else {
        yearEnd = yearStart + 1
      }
    } else {
      const s = window.prompt('Enter start year (YYYY)')
      const e = window.prompt('Enter end year (YYYY)')
      if (!s || !e) return
      yearStart = parseInt(s, 10)
      yearEnd = parseInt(e, 10)
    }

    if (!yearStart || !yearEnd) return window.alert('Invalid year values')

    try {
      const payload = { accountId: selectedAccount.id, yearStart, yearEnd }
      const resp = await api.post(`/accounts/${selectedAccount.id}/years`, payload)
      await fetchYears(selectedAccount.id)
      const created = resp.data
      if (created?.id) selectYear(created)
    } catch (err) {
      console.error('create year error', err)
      window.alert(err.response?.data?.message || err.message || 'Failed to create year')
    }
  }

  async function handleNewMonth(){
    if (!selectedYear?.id) return window.alert('Select a year first')
    const name = window.prompt('Enter new month name (e.g., February)')
    if (!name) return
    try {
      const payload = { monthName: name, yearId: selectedYear.id }
      const resp = await api.post(`/years/${selectedYear.id}/months`, payload)
      await fetchMonths(selectedYear.id)
      const created = resp.data
      if (created?.id) selectMonth(created)
    } catch (err) {
      console.error('create month error', err)
      window.alert(err.response?.data?.message || err.message || 'Failed to create month')
    }
  }

  // local copies of years/months from store
  const yearsByAccount = useAccounts(state => state.yearsByAccount)
  const monthsByYear = useAccounts(state => state.monthsByYear)

  // helper: map month name to month number
  const MONTH_NAME_TO_NUM = {
    january:1, february:2, march:3, april:4, may:5, june:6,
    july:7, august:8, september:9, october:10, november:11, december:12
  }

  function generateFiscalMonths(yearStart, yearEnd) {
    // fiscal year runs Apr (4) of yearStart .. Mar (3) of yearEnd
    const months = []
    // Apr..Dec of yearStart
    for (let m=4; m<=12; m++) {
      months.push({ monthNumber: m, year: yearStart, label: new Date(yearStart, m-1).toLocaleString(undefined, { month: 'long' }) + ' ' + yearStart })
    }
    // Jan..Mar of yearEnd
    for (let m=1; m<=3; m++) {
      months.push({ monthNumber: m, year: yearEnd, label: new Date(yearEnd, m-1).toLocaleString(undefined, { month: 'long' }) + ' ' + yearEnd })
    }
    return months
  }

  function buildMonthsList() {
    const serverMonths = (monthsByYear[selectedYear?.id] || [])
    if (!selectedYear) return []
    // generate fiscal months
    const fiscal = generateFiscalMonths(selectedYear.yearStart || selectedYear.start || selectedYear.yearStart, selectedYear.yearEnd || selectedYear.end || (selectedYear.yearStart + 1))

    // map server months by normalized name
    const serverByName = {}
    serverMonths.forEach(sm => {
      const key = (sm.monthName || sm.name || '').toLowerCase()
      serverByName[key] = sm
    })

    // build list: for each fiscal month, attach server month if exists
    const result = fiscal.map(fm => {
      const key = fm.label.split(' ')[0].toLowerCase() // month name only
      const server = serverByName[key]
      return {
        id: server?.id,
        monthNumber: fm.monthNumber,
        year: fm.year,
        label: fm.label,
        existsOnServer: !!server
      }
    })

    // include any server months that don't match fiscal months at the end
    serverMonths.forEach(sm => {
      const key = (sm.monthName || sm.name || '').toLowerCase()
      const match = result.find(r => r.label.toLowerCase().startsWith(key))
      if (!match) result.push({ id: sm.id, monthNumber: MONTH_NAME_TO_NUM[key] || 0, year: selectedYear.yearStart, label: sm.monthName || sm.name })
    })

    return result
  }
  // helper: sort months into calendar order (Jan..Dec)
  function monthIndex(m) {
    if (!m) return 100
    // if numeric month (1-12) provided
    if (typeof m.month === 'number') return m.month - 1
    const val = (m.monthName || m.name || m.month || '').toString().toLowerCase()
    const names = ['jan','feb','mar','apr','may','jun','jul','aug','sep','oct','nov','dec']
    for (let i=0;i<names.length;i++){
      if (val.startsWith(names[i]) || val.includes(names[i])) return i
    }
    const n = parseInt(val,10)
    return Number.isFinite(n) ? (n-1) : 100
  }

  const sortedMonths = (monthsByYear[selectedYear?.id] || []).slice().sort((a,b)=> monthIndex(a) - monthIndex(b))

  return (
    <div className="account-selector controls" style={{marginBottom:12}}>
      {!user && (
        <div className="muted small">Please <a href="/login">login</a> to select an account</div>
      )}

      {user && (
        <>
          <div>
            <label style={{display:'block',fontSize:12,color:'#444'}}>Select Account:</label>
            <select className="select" value={selectedAccount?.id || ''} onChange={e=>{
              const a = accounts.find(x => String(x.id) === e.target.value)
              selectAccount(a)
              if (a?.id) fetchYears(a.id)
            }}>
              <option value="">-- Select account --</option>
              {accounts.map(a => <option key={a.id} value={a.id}>{a.accountName || a.name || `Account ${a.id}`}</option>)}
            </select>
          </div>

          <div>
            <label style={{display:'block',fontSize:12,color:'#444'}}>Select Year:</label>
            <div style={{display:'flex',gap:8,alignItems:'center'}}>
              <select className="select" value={selectedYear?.id || ''} onChange={e=>{
                const y = (yearsByAccount[selectedAccount?.id] || []).find(x => String(x.id) === e.target.value)
                selectYear(y)
                if (y?.id) fetchMonths(y.id)
              }} disabled={!selectedAccount}>
                <option value="">-- Select year --</option>
                {(yearsByAccount[selectedAccount?.id] || []).map(y => {
                const label = y.yearStart && y.yearEnd ? `${y.yearStart}-${String(y.yearEnd).slice(-2)}` : (y.name || `FY ${y.id}`)
                return <option key={y.id} value={y.id}>{label}</option>
              })}
              </select>
              <button className="btn" onClick={handleNewYear} disabled={!selectedAccount}>+ New Year</button>
            </div>
          </div>

          <div>
            <label style={{display:'block',fontSize:12,color:'#444'}}>Select Month:</label>
            <div style={{display:'flex',gap:8,alignItems:'center'}}>
              <select className="select" value={selectedMonth?.id || ''} onChange={e=>{
                const id = e.target.value
                const found = (monthsByYear[selectedYear?.id] || []).find(x => String(x.id) === id)
                if (found) selectMonth(found)
                else {
                  // month not on server yet
                  const parts = id.split(':')
                  // ignore
                }
              }} disabled={!selectedYear}>
                <option value="">-- Select month --</option>
                {buildMonthsList().map(m => (
                  <option key={(m.id||`gen-${m.monthNumber}-${m.year}`)} value={m.id || `gen-${m.monthNumber}-${m.year}`} disabled={!m.existsOnServer}>{m.label}{!m.existsOnServer ? ' (create)' : ''}</option>
                ))}
              </select>
              <button className="btn" onClick={handleNewMonth} disabled={!selectedYear}>+ New Month</button>
            </div>
          </div>

          <div style={{marginLeft:8}}>
            <button className="btn btn-primary" onClick={handleNewAccount} disabled={creating}>+ New Account</button>
          </div>
        </>
      )}

    </div>
  )
}