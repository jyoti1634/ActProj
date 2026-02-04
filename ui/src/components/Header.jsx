import React from 'react'
import { Link, useNavigate } from 'react-router-dom'
import useAuth from '../store/authStore'

export default function Header(){
  const user = useAuth(state => state.user)
  const logout = useAuth(state => state.logout)
  const navigate = useNavigate()

  function doLogout(){
    logout()
    navigate('/login')
  }

  return (
    <header className="header">
      <div className="container header-inner">
        <div className="brand"><Link to="/">CashBook</Link></div>
        <nav>
          <Link to="/">Home</Link>
          {user ? (
            <>
              <span className="user-greeting">Hi, {user.username}</span>
              <button className="btn" onClick={doLogout} style={{marginLeft:8}}>Logout</button>
            </>
          ) : (
            <>
              <Link to="/login">Login</Link>
              <Link to="/register">Register</Link>
            </>
          )}
        </nav>
      </div>
    </header>
  )
}
