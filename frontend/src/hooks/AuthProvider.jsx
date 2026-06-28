import { useState, useCallback, useMemo } from 'react'
import { auth as authService } from '../services/api'
import { AuthContext } from './AuthContext'

function parseUserFromToken(token) {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    const now = Math.floor(Date.now() / 1000)
    if (payload.exp && payload.exp < now) return null
    const roles = payload.realm_access?.roles || []
    return {
      authenticated: true,
      roles,
      username: payload.preferred_username || payload.sub || '',
      email: payload.email || ''
    }
  } catch {
    return null
  }
}

export const AuthProvider = ({ children }) => {
  const [loading] = useState(false)
  const [user, setUser] = useState(() => {
    let token = null
    try {
      token = localStorage.getItem('access_token')
    } catch {
      token = null
    }
    return token ? parseUserFromToken(token) : null
  })
 
 
  const login = useCallback(async (username, password) => {
    const response = await authService.login({ username, password })
    const data = response.data
    const token = data.access_token
    try {
      localStorage.setItem('access_token', token)
      localStorage.setItem('refresh_token', data.refresh_token)
    } catch {
      console.warn('Unable to access localStorage for auth tokens')
    }
    setUser(parseUserFromToken(token))
    return data
  }, [])

  const logout = useCallback(async () => {
    let refreshToken = null
    try {
      refreshToken = localStorage.getItem('refresh_token')
    } catch {
      refreshToken = null
    }
    if (refreshToken) {
      try {
        await authService.logout(refreshToken)
      } catch (e) {
        console.error('Logout error', e)
      }
    }
    try {
      localStorage.removeItem('access_token')
      localStorage.removeItem('refresh_token')
    } catch {}
    setUser(null)
  }, [])

  const value = useMemo(() => ({ user, loading, login, logout }), [user, loading, login, logout])

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}

AuthProvider.displayName = 'AuthProvider'
