import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../lib/api'

export default function Register(){
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(null)
  const navigate = useNavigate()

  async function submit(e){
    e.preventDefault()
    try {
      const resp = await api.post('/auth/register', { username, email, password })
      setSuccess('Registration successful — please log in')
      setError(null)
      setTimeout(() => navigate('/login'), 1200)
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Registration failed')
      setSuccess(null)
    }
  }

  return (
    <div>
      <header style={{marginBottom:12}}><div className="container"><div className="brand"><a href="/">CashBook</a></div></div></header>
      <div className="container">
        <div className="card form-center">
          <h2 className="form-legend">Register</h2>
          <form onSubmit={submit}>
            <div><input className="input" placeholder="username" value={username} onChange={e=>setUsername(e.target.value)} autoFocus /></div>
            <div style={{marginTop:8}}><input className="input" placeholder="email" value={email} onChange={e=>setEmail(e.target.value)} /></div>
            <div style={{marginTop:8}}><input className="input" placeholder="password" type="password" value={password} onChange={e=>setPassword(e.target.value)} /></div>
            <div className="form-actions"><button className="btn btn-primary" type="submit">Register</button></div>
            {error && <div className="error-text">{error}</div>}
            {success && <div className="success-text">{success}</div>}
          </form>
        </div>
      </div>
    </div>
  )
}
