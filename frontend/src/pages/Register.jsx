import { useReducer, useRef, useEffect } from 'react'
import { useNavigate, Link } from 'react-router'
import { auth } from '../services/api'

const initialState = {
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  error: '',
  success: '',
  loading: false
}

function reducer(state, action) {
  switch (action.type) {
    case 'SET_FIELD':
      return { ...state, [action.field]: action.value }
    case 'SET_ERROR':
      return { ...state, error: action.value, success: '' }
    case 'SET_SUCCESS':
      return { ...state, success: action.value, error: '' }
    case 'SET_LOADING':
      return { ...state, loading: action.value }
    case 'RESET_MESSAGES':
      return { ...state, error: '', success: '' }
    default:
      return state
  }
}

export default function Register() {
  const [state, dispatch] = useReducer(reducer, initialState)
  const { username, email, password, confirmPassword, error, success, loading } = state
  const navigate = useNavigate()
  const redirectTimer = useRef(null)

  useEffect(() => {
    document.title = 'Registro - Donaton'
  }, [])

  useEffect(() => {
    const timerRef = redirectTimer.current
    return () => {
      if (timerRef) clearTimeout(timerRef)
    }
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    dispatch({ type: 'RESET_MESSAGES' })

    if (password !== confirmPassword) {
      dispatch({ type: 'SET_ERROR', value: 'Las contraseñas no coinciden' })
      return
    }

    dispatch({ type: 'SET_LOADING', value: true })
    try {
      await auth.register({ username, password, email })
      dispatch({ type: 'SET_SUCCESS', value: 'Usuario registrado exitosamente. Redirigiendo al login...' })
      redirectTimer.current = setTimeout(() => navigate('/login'), 2000)
    } catch (err) {
      dispatch({ type: 'SET_ERROR', value: err.response?.data?.error || 'Error al registrar usuario' })
    } finally {
      dispatch({ type: 'SET_LOADING', value: false })
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
            <button type="button" className="btn-close" onClick={() => dispatch({ type: 'SET_ERROR', value: '' })} aria-label="Cerrar alerta de error" />
          </div>
        )}
        {success && (
          <div className="alert alert-success alert-dismissible py-2 fade show">
            {success}
            <button type="button" className="btn-close" onClick={() => dispatch({ type: 'SET_SUCCESS', value: '' })} aria-label="Cerrar alerta de éxito" />
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
              onChange={(e) => dispatch({ type: 'SET_FIELD', field: 'username', value: e.target.value })}
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
              onChange={(e) => dispatch({ type: 'SET_FIELD', field: 'email', value: e.target.value })}
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
              onChange={(e) => dispatch({ type: 'SET_FIELD', field: 'password', value: e.target.value })}
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
              onChange={(e) => dispatch({ type: 'SET_FIELD', field: 'confirmPassword', value: e.target.value })}
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
