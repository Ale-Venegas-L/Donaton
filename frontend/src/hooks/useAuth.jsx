import { use } from 'react'
import { AuthContext } from './AuthContext'

export const useAuth = () => {
  const ctx = use(AuthContext)
  if (!ctx) {
    throw new Error('useAuth debe usarse dentro de un AuthProvider')
  }
  return ctx
}
