<<<<<<< HEAD
import { createContext, useContext, useState, useEffect, useCallback } from 'react'
import { auth as authService } from '../services/api'

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

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  const checkAuth = useCallback(() => {
    const token = localStorage.getItem('access_token')
    if (token) {
      setUser(parseUserFromToken(token))
    } else {
      setUser(null)
    }
    setLoading(false)
  }, [])

  useEffect(() => {
    checkAuth()
  }, [checkAuth])

  const login = async (username, password) => {
    const response = await authService.login({ username, password })
    const data = response.data
    localStorage.setItem('access_token', data.access_token)
    localStorage.setItem('refresh_token', data.refresh_token)
    setUser(parseUserFromToken(data.access_token))
    return data
  }

  const logout = async () => {
    const refreshToken = localStorage.getItem('refresh_token')
    if (refreshToken) {
      try {
        await authService.logout(refreshToken)
      } catch (e) {
        console.error('Logout error', e)
      }
    }
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
=======
import { use } from 'react'
import { AuthContext } from './AuthContext'

export const useAuth = () => {
  const ctx = use(AuthContext)
>>>>>>> develop
  if (!ctx) {
    throw new Error('useAuth debe usarse dentro de un AuthProvider')
  }
  return ctx
}
