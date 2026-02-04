import React, { useEffect } from 'react'
import useAccounts from '../store/accountsStore'

export default function Balance(){
  const selectedMonth = useAccounts(state => state.selectedMonth)
  const fetchLedger = useAccounts(state => state.fetchLedger)
  const ledger = useAccounts(state => state.ledgerByMonth[selectedMonth?.id] || [])

  useEffect(()=>{
    if (selectedMonth?.id) fetchLedger(selectedMonth.id)
  },[selectedMonth, fetchLedger])

  const opening = Number(selectedMonth?.openingBalance || 0)
  const cashReceipts = ledger.reduce((s,e)=> s + Number(e.cashAmt || 0), 0)
  const cashExpenditure = ledger.reduce((s,e)=> s + Number(e.expAmt || 0), 0)
  const bankReceipts = ledger.reduce((s,e)=> s + Number(e.cshbankAmt || 0), 0)
  const bankExpenditure = ledger.reduce((s,e)=> s + Number(e.expbankAmt || 0), 0)

  const cash = opening + cashReceipts - cashExpenditure
  const bank = bankReceipts - bankExpenditure
  const total = cash + bank

  return (
    <div>
      <h3>Current Balance</h3>
      <div style={{maxWidth:900, marginTop:12}}>
        <div style={{background:'#fff', borderRadius:8, padding:18, boxShadow:'0 6px 18px rgba(0,0,0,0.06)'}}>
          <h4 style={{marginTop:0}}>Account Balance</h4>
          <table style={{width:'100%'}} className="table">
            <tbody>
              <tr>
                <td style={{width:'80%'}}>Cash</td>
                <td style={{textAlign:'right'}}>{cash.toFixed(2)}</td>
              </tr>
              <tr>
                <td>Bank</td>
                <td style={{textAlign:'right'}}>{bank.toFixed(2)}</td>
              </tr>
              <tr style={{fontWeight:700}}>
                <td>Total</td>
                <td style={{textAlign:'right'}}>{total.toFixed(2)}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}