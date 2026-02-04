import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Header from '../components/Header'
import AccountSelector from '../components/AccountSelector'
import TabNav from '../components/TabNav'
import Entry from './Entry'
import Expenditure from './Expenditure'
import CashPosition from './CashPosition'
import Report from './Report'
import Balance from './Balance'
import useAuth from '../store/authStore'
import useAccounts from '../store/accountsStore'

export default function Main(){
  const user = useAuth(state => state.user)
  const navigate = useNavigate()
  const [tab, setTab] = useState('entry')
  const selectedAccount = useAccounts(state => state.selectedAccount)
  const selectedYear = useAccounts(state => state.selectedYear)
  const selectedMonth = useAccounts(state => state.selectedMonth)

  useEffect(() => {
    if (!user) navigate('/login')
  }, [user, navigate])

  // Fetch accounts when the user is present
  useEffect(() => {
    if (!user) return
    import('../store/accountsStore').then(mod => {
      const fetchAccounts = mod.default.getState().fetchAccounts
      fetchAccounts()
    })
  }, [user])

  return (
    <div>
      <Header />
      <div className="container">
        <AccountSelector />
        <TabNav tab={tab} setTab={setTab} locked={!(selectedAccount && selectedYear && selectedMonth)} />
        <div className="main-content" style={{marginTop:12}}>
          {tab === 'entry' && <Entry />}
          {tab === 'expenditure' && <Expenditure />}
          {tab === 'cash' && <CashPosition />}
          {tab === 'report' && <Report />}
          {tab === 'balance' && <Balance />}
        </div>
      </div>
    </div>
  )
}
