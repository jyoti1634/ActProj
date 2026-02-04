import axios from 'axios'

const baseURL = import.meta.env.VITE_API_URL 
const api = axios.create({
  baseURL,
  headers: { 'Content-Type': 'application/json' }
})

// If there's a saved token in localStorage, set Authorization header on startup
try {
  const raw = typeof localStorage !== 'undefined' ? localStorage.getItem('cashbook_auth') : null
  if (raw) {
    const parsed = JSON.parse(raw)
    if (parsed?.token) api.defaults.headers.common['Authorization'] = `Bearer ${parsed.token}`
  }
} catch (e) {
  // ignore errors
}

export default api
