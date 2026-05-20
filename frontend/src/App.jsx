import { Routes, Route, Link, Navigate } from 'react-router'
import Donations from './pages/Donations'
import Home from './pages/Home'
import Campaigns from './pages/Campaigns'
import Volunteers from './pages/Volunteers'
import Login from './pages/Login'
import Register from './pages/Register'
import { useAuth } from './hooks/useAuth.jsx'

function NotFound() {
  return (
    <div className="text-center py-5">
      <h1 className="display-1 fw-bold" style={{ color: 'var(--rojo)' }}>404</h1>
      <p className="text-muted fs-5">Página no encontrada</p>
      <Link to="/" style={{ color: 'var(--azul)' }}>Volver al inicio</Link>
    </div>
  )
}

function ProtectedRoute({ children }) {
  const { user, loading } = useAuth()
  if (loading) return <div className="text-center py-4 text-muted">Cargando...</div>
  if (!user) return <Navigate to="/login" replace />
  return children
}

function ProtectedRouteByRole({ children, role }) {
  const { user, loading } = useAuth()
  if (loading) return <div className="text-center py-4 text-muted">Cargando...</div>
  if (!user) return <Navigate to="/login" replace />
  if (!user.roles?.includes(role)) return <Navigate to="/" replace />
  return children
}

function GuestRoute({ children }) {
  const { user, loading } = useAuth()
  if (loading) return <div className="text-center py-4 text-muted">Cargando...</div>
  if (user) return <Navigate to="/" replace />
  return children
}

function Navbar() {
  const { user, logout } = useAuth()
  const canViewVolunteers = user?.roles?.includes('VolunteerManager')
  return (
    <nav className="navbar navbar-expand navbar-dark">
      <div className="container">
        <Link className="navbar-brand fw-bold" to="/">Donaton</Link>
        <div className="navbar-nav">
          <Link className="nav-link" to="/">Inicio</Link>
          <Link className="nav-link" to="/campaigns">Campañas</Link>
          <Link className="nav-link" to="/donations">Donaciones</Link>
          {canViewVolunteers && <Link className="nav-link" to="/volunteers">Voluntarios</Link>}
          {!user && <Link className="nav-link" to="/login">Iniciar Sesión</Link>}
          {!user && <Link className="nav-link" to="/register">Registro</Link>}
        </div>
        <div className="navbar-nav ms-auto align-items-center">
          {user && (
            <>
              <span className="text-light opacity-75 small me-2">{user.username}</span>
              <button onClick={logout} className="btn btn-outline-light btn-sm">Cerrar Sesión</button>
            </>
          )}
        </div>
      </div>
    </nav>
  )
}

function App() {
  return (
    <>
      <Navbar />
      <main>
        <Routes>
          <Route path="/login" element={<GuestRoute><Login /></GuestRoute>} />
          <Route path="/register" element={<GuestRoute><Register /></GuestRoute>} />
          <Route path="/" element={
            <ProtectedRoute>
              <Home />
            </ProtectedRoute>
          } />
          <Route path="/campaigns" element={
            <ProtectedRoute>
              <Campaigns />
            </ProtectedRoute>
          } />
          <Route path="/donations" element={
            <ProtectedRoute>
              <Donations />
            </ProtectedRoute>
          } />
          <Route path="/volunteers" element={
            <ProtectedRouteByRole role="VolunteerManager">
              <Volunteers />
            </ProtectedRouteByRole>
          } />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </main>
    </>
  )
}

export default App
