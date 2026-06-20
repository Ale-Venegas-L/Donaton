<<<<<<< HEAD
import { Link } from 'react-router';
import { useAuth } from '../hooks/useAuth.jsx';
=======
import { Link } from 'react-router'
import { useAuth } from '../hooks/useAuth.jsx'
>>>>>>> develop

export function QuickActions() {
  const { user } = useAuth()
  const canViewVolunteers = user?.roles?.includes('VolunteerManager')
  const actions = [
<<<<<<< HEAD
    { to: '/donations', label: 'Nueva Donación', icon: '💝' },
    { to: '/campaigns', label: 'Crear Campaña', icon: '📢' },
    ...(canViewVolunteers ? [{ to: '/volunteers', label: 'Registrar Voluntario', icon: '🙋' }] : []),
  ];
=======
    { to: '/donations', label: 'Nueva Donación', icon: 'bi-heart-fill' },
    { to: '/campaigns', label: 'Crear Campaña', icon: 'bi-megaphone' },
    ...(canViewVolunteers ? [{ to: '/volunteers', label: 'Registrar Voluntario', icon: 'bi-person-raised-hand' }] : [])
  ]
>>>>>>> develop

  return (
    <div className="card shadow-sm">
      <div className="card-body">
        <h5 className="card-title mb-3">Acciones Rápidas</h5>
        <div className="row g-2">
          {actions.map(action => (
            <div className="col-6" key={action.to + action.label}>
              <Link to={action.to} className="btn btn-outline-secondary w-100 h-100 d-flex flex-column align-items-center justify-content-center py-3 text-decoration-none">
<<<<<<< HEAD
                <span className="fs-2 mb-1">{action.icon}</span>
=======
                 <span className="fs-2 mb-1"><i className={`bi ${action.icon}`}></i></span>

>>>>>>> develop
                <span className="small">{action.label}</span>
              </Link>
            </div>
          ))}
        </div>
      </div>
    </div>
<<<<<<< HEAD
  );
}

export default QuickActions;
=======
  )
}

export default QuickActions
>>>>>>> develop
