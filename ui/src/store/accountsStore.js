import create from 'zustand'
import api from '../lib/api'

const useAccounts = create((set, get) => ({
  accounts: [],
  yearsByAccount: {},
  monthsByYear: {},
  selectedAccount: null,
  selectedYear: null,
  selectedMonth: null,
  ledgerByMonth: {},
  ledgerLoading: false,

  fetchAccounts: async () => {
    try {
      const resp = await api.get('/accounts')
      const accounts = resp.data || []
      set({ accounts })
      return accounts
    } catch (err) {
      console.error('fetchAccounts error', err)
      set({ accounts: [] })
      return []
    }
  },

  selectAccount: (account) => {
    set({ selectedAccount: account, selectedYear: null, selectedMonth: null })
  },

  fetchYears: async (accountId) => {
    if (!accountId) return []
    try {
      const resp = await api.get(`/accounts/${accountId}/years`)
      const years = resp.data || []
      set(state => ({ yearsByAccount: { ...state.yearsByAccount, [accountId]: years } }))
      return years
    } catch (err) {
      console.error('fetchYears error', err)
      return []
    }
  },

  fetchMonths: async (yearId) => {
    if (!yearId) return []
    try {
      const resp = await api.get(`/years/${yearId}/months`)
      const months = resp.data || []
      set(state => ({ monthsByYear: { ...state.monthsByYear, [yearId]: months } }))
      return months
    } catch (err) {
      console.error('fetchMonths error', err)
      return []
    }
  },

  // Update a month (opening/closing balances or other editable fields)
  updateMonth: async (yearId, monthId, payload) => {
    if (!yearId || !monthId) throw new Error('yearId and monthId required')
    try {
      const resp = await api.patch(`/years/${yearId}/months/${monthId}`, payload)
      // refresh months cache for the year
      await get().fetchMonths(yearId)
      return resp.data
    } catch (err) {
      console.error('updateMonth error', err)
      throw err
    }
  },

  createAccount: async (payload) => {
    try {
      const resp = await api.post('/accounts', payload)
      // refresh accounts and return created
      await get().fetchAccounts()
      return resp.data
    } catch (err) {
      console.error('createAccount error', err)
      throw err
    }
  },

  selectYear: (year) => set({ selectedYear: year, selectedMonth: null }),
  selectMonth: (month) => set({ selectedMonth: month }),

  // Ledger operations
  fetchLedger: async (monthId) => {
    if (!monthId) return []
    set({ ledgerLoading: true })
    try {
      const resp = await api.get(`/months/${monthId}/ledger`)
      const entries = resp.data || []
      set(state => ({ ledgerByMonth: { ...state.ledgerByMonth, [monthId]: entries } }))
      set({ ledgerLoading: false })
      return entries
    } catch (err) {
      console.error('fetchLedger error', err)
      set({ ledgerLoading: false })
      return []
    }
  },

  createLedgerEntry: async (monthId, payload) => {
    if (!monthId) throw new Error('monthId required')
    try {
      const resp = await api.post(`/months/${monthId}/ledger`, payload)
      // reload ledger after create
      await get().fetchLedger(monthId)
      // refresh months list for selected year to pick up any recalculated balances
      if (get().selectedYear?.id) await get().fetchMonths(get().selectedYear.id)
      return resp.data
    } catch (err) {
      console.error('createLedgerEntry error', err)
      throw err
    }
  },

  deleteLedgerEntry: async (monthId, entryId) => {
    if (!monthId || !entryId) throw new Error('monthId and entryId required')
    try {
      await api.delete(`/months/${monthId}/ledger/${entryId}`)
      // update local cache
      set(state => ({ ledgerByMonth: { ...state.ledgerByMonth, [monthId]: (state.ledgerByMonth[monthId] || []).filter(e => e.id !== entryId) } }))
      // refresh months list for selected year to pick up any recalculated balances
      if (get().selectedYear?.id) await get().fetchMonths(get().selectedYear.id)
      return true
    } catch (err) {
      console.error('deleteLedgerEntry error', err)
      throw err
    }
  }
}))

export default useAccounts
