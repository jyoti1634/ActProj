import create from 'zustand'
import api from '../lib/api'

const STORAGE_KEY = 'cashbook_auth'

const initial = (() => {
  try {
    const raw = typeof localStorage !== 'undefined' ? localStorage.getItem(STORAGE_KEY) : null
    if (raw) {
      const parsed = JSON.parse(raw)
      if (parsed?.token) api.defaults.headers.common['Authorization'] = `Bearer ${parsed.token}`
      return parsed
    }
  } catch (e) {
    // ignore
  }
  return { token: null, user: null }
})()

const useAuth = create((set) => ({
  token: initial.token,
  user: initial.user,
  setAuth: (data) => {
    const token = data?.token || null
    const user = data?.user || null
    if (token) api.defaults.headers.common['Authorization'] = `Bearer ${token}`
    else delete api.defaults.headers.common['Authorization']
    try { localStorage.setItem(STORAGE_KEY, JSON.stringify({ token, user })) } catch (e) {}
    set({ token, user })
  },
  logout: () => {
    delete api.defaults.headers.common['Authorization']
    try { localStorage.removeItem(STORAGE_KEY) } catch (e) {}
    set({ token: null, user: null })
  }
}))

export default useAuth
