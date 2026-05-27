import { useState, useRef, useEffect } from 'react'
import { useNavigate, Link } from 'react-router'
import { auth } from '../services/api'

export default function Register() {
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const redirectTimer = useRef(null)

  useEffect(() => {
    document.title = 'Registro - Donaton'
  }, [])

  useEffect(() => {
    return () => {
      if (redirectTimer.current) clearTimeout(redirectTimer.current)
    }
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSuccess('')

    if (password !== confirmPassword) {
      setError('Las contraseñas no coinciden')
      return
    }

    setLoading(true)
    try {
      await auth.register({ username, password, email })
      setSuccess('Usuario registrado exitosamente. Redirigiendo al login...')
      redirectTimer.current = setTimeout(() => navigate('/login'), 2000)
    } catch (err) {
      setError(err.response?.data?.error || 'Error al registrar usuario')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-vh-100 d-flex align-items-center justify-content-center p-4">
      <div className="card shadow-lg p-4" style={{ maxWidth: 400, width: '100%' }}>
        <h1 className="text-center mb-1" style={{ color: 'var(--rojo)' }}>Registro</h1>
        <p className="text-muted text-center mb-4">Crear una cuenta de voluntario</p>
        {error && (
          <div className="alert alert-danger alert-dismissible py-2 fade show">
            {error}
            <button type="button" className="btn-close" onClick={() => setError('')} />
          </div>
        )}
        {success && (
          <div className="alert alert-success alert-dismissible py-2 fade show">
            {success}
            <button type="button" className="btn-close" onClick={() => setSuccess('')} />
          </div>
        )}
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label htmlFor="username" className="form-label">Usuario</label>
            <input
              type="text"
              id="username"
              autoComplete="username"
              className="form-control"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              maxLength={50}
            />
          </div>
          <div className="mb-3">
            <label htmlFor="email" className="form-label">Email</label>
            <input
              type="email"
              id="email"
              autoComplete="email"
              className="form-control"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              maxLength={254}
            />
          </div>
          <div className="mb-3">
            <label htmlFor="password" className="form-label">Contraseña</label>
            <input
              type="password"
              id="password"
              autoComplete="new-password"
              className="form-control"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              minLength={8}
            />
          </div>
          <div className="mb-3">
            <label htmlFor="confirmPassword" className="form-label">Confirmar Contraseña</label>
            <input
              type="password"
              id="confirmPassword"
              autoComplete="new-password"
              className="form-control"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
              minLength={8}
            />
          </div>
          <button type="submit" disabled={loading} className="btn btn-primary w-100 py-2 mt-3">
            {loading ? 'Registrando...' : 'Registrarse'}
          </button>
        </form>
        <p className="mt-3 text-center">
          ¿Ya tienes cuenta? <Link to="/login">Inicia sesión</Link>
        </p>
      </div>
    </div>
  )
}
