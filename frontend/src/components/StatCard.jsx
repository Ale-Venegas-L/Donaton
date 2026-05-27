import { useContador } from '../hooks/useContador'

export function StatCard({ title, apiPath, icon, color }) {
  const { count, loading, error } = useContador(apiPath)

  return (
    <div className="card h-100 shadow-sm" style={{ borderLeft: `4px solid ${color}` }}>
      <div className="card-body d-flex align-items-center gap-3">
        <span className="fs-1">{icon}</span>
        <div>
          <h6 className="card-subtitle mb-1 text-muted">{title}</h6>
          {loading ? (
            <span className="text-muted">Cargando...</span>
          ) : error ? (
            <span className="text-muted">-</span>
          ) : (
            <span className="fs-3 fw-bold">{count}</span>
          )}
        </div>
      </div>
    </div>
  )
}
