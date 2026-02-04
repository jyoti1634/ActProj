import React, { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import useAuth from '../store/authStore'
import api from '../lib/api'

export default function Login(){
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const setAuth = useAuth(state => state.setAuth)
  const navigate = useNavigate()

  async function submit(e){
    e.preventDefault()
    try {
      const resp = await api.post('/auth/login', { usernameOrEmail: username, password })
      if (!resp.data) throw new Error('Invalid response')
      setAuth(resp.data)
      navigate('/')
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Login failed')
    }
  }

  return (
    <div>
      <header style={{marginBottom:12}}><div className="container"><div className="brand"><a href="/">CashBook</a></div></div></header>
      <div className="container">
        <div className="card form-center">
          <h2 className="form-legend">Login</h2>
          <form onSubmit={submit}>
            <div><input className="input" placeholder="username or email" value={username} onChange={e=>setUsername(e.target.value)} autoFocus /></div>
            <div style={{marginTop:8}}><input className="input" placeholder="password" type="password" value={password} onChange={e=>setPassword(e.target.value)} /></div>
            <div className="form-actions"><button className="btn btn-primary" type="submit">Login</button></div>
            {error && <div className="error-text">{error}</div>}

            <div className="small muted" style={{marginTop:8}}>Don't have an account? <Link to="/register">Register</Link></div>
          </form>
        </div>
      </div>
    </div>
  )
}
