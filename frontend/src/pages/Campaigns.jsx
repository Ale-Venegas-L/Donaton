import { useReducer, useEffect } from 'react'
import { campaigns } from '../services/api'
import { formatDate } from '../utils/format'

const getStatusClass = (status) => {
  switch (status) {
    case 'ACTIVE': return 'status-active'
    case 'PLANNED': return 'status-pending'
    case 'COMPLETED': return 'status-completed'
    default: return 'status-pending'
  }
}

const initialState = {
  campaignList: [],
  loading: true,
  showForm: false,
  editingId: null,
  formData: {
    nombre: '',
    descripcion: '',
    fechaInicio: '',
    fechaFin: '',
    estado: 'PLANNED'
  },
  submitting: false,
  successMsg: '',
  errorMsg: '',
  deletingId: null
}

function campaignReducer(state, action) {
  switch (action.type) {
    case 'SET_CAMPAIGNS': return { ...state, campaignList: action.payload, loading: false }
    case 'SET_LOADING': return { ...state, loading: action.payload }
    case 'TOGGLE_FORM': return { ...state, showForm: !state.showForm, editingId: null, formData: initialState.formData }
    case 'SET_EDITING': return { ...state, editingId: action.id, formData: action.formData, showForm: true }
    case 'SET_FORM_DATA': return { ...state, formData: { ...state.formData, ...action.payload } }
    case 'SUBMITTING': return { ...state, submitting: action.payload }
    case 'SET_SUCCESS': return { ...state, successMsg: action.payload, errorMsg: '' }
    case 'SET_ERROR': return { ...state, errorMsg: action.payload, successMsg: '' }
    case 'SET_DELETING': return { ...state, deletingId: action.payload }
    case 'RESET_FORM': return { ...state, formData: initialState.formData, showForm: false, editingId: null }
    default: return state
  }
}

function Campaigns() {
  const [state, dispatch] = useReducer(campaignReducer, initialState)
  const { campaignList, loading, showForm, editingId, formData, submitting, successMsg, errorMsg, deletingId } = state

  const loadCampaigns = async () => {
    dispatch({ type: 'SET_LOADING', payload: true })
    try {
      const response = await campaigns.list()
      dispatch({ type: 'SET_CAMPAIGNS', payload: response.data })
    } catch (error) {
      console.error('Error loading campaigns:', error)
      dispatch({ type: 'SET_ERROR', payload: 'Error al cargar campañas' })
    }
  }

  useEffect(() => {
    const init = async () => {
      await loadCampaigns()
    }
    init()
  }, [])

  const handleEdit = (campaign) => {
    dispatch({
      type: 'SET_EDITING',
      id: campaign.id,
      formData: {
        nombre: campaign.nombre || '',
        descripcion: campaign.descripcion || '',
        fechaInicio: campaign.fechaInicio || '',
        fechaFin: campaign.fechaFin || '',
        estado: campaign.estado || 'PLANNED'
      }
    })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    dispatch({ type: 'SUBMITTING', payload: true })
    try {
      if (editingId) {
        await campaigns.update(editingId, formData)
        dispatch({ type: 'SET_SUCCESS', payload: 'Campaña actualizada exitosamente' })
      } else {
        await campaigns.create(formData)
        dispatch({ type: 'SET_SUCCESS', payload: 'Campaña creada exitosamente' })
      }
      dispatch({ type: 'RESET_FORM' })
      await loadCampaigns()
    } catch (error) {
      console.error('Error saving campaign:', error)
      dispatch({ type: 'SET_ERROR', payload: error.response?.data?.error || 'Error al guardar la campaña' })
    } finally {
      dispatch({ type: 'SUBMITTING', payload: false })
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('¿Está seguro de eliminar esta campaña?')) return
    dispatch({ type: 'SET_DELETING', payload: id })
    try {
      await campaigns.delete(id)
      dispatch({ type: 'SET_SUCCESS', payload: 'Campaña eliminada exitosamente' })
      dispatch({ type: 'SET_ERROR', payload: '' })
      await loadCampaigns()
    } catch (error) {
      console.error('Error deleting campaign:', error)
      dispatch({ type: 'SET_ERROR', payload: error.response?.data?.error || 'Error al eliminar la campaña' })
    } finally {
      dispatch({ type: 'SET_DELETING', payload: null })
    }
  }

  const cancelEdit = () => {
    dispatch({ type: 'RESET_FORM' })
  }

  useEffect(() => {
    document.title = 'Campañas - Donaton'
  }, [])

  useEffect(() => {
    if (!successMsg) return
    const t = setTimeout(() => dispatch({ type: 'SET_SUCCESS', payload: '' }), 5000)
    return () => clearTimeout(t)
  }, [successMsg])

  useEffect(() => {
    if (!errorMsg) return
    const t = setTimeout(() => dispatch({ type: 'SET_ERROR', payload: '' }), 5000)
    return () => clearTimeout(t)
  }, [errorMsg])

  return (
    <div className="container py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1 className="fw-bold mb-0" style={{ color: 'var(--rojo)' }}>Campañas de Ayuda Humanitaria</h1>
          <button type="button" className="btn btn-primary" onClick={() => {
            if (showForm) {
              dispatch({ type: 'RESET_FORM' })
            } else {
              dispatch({ type: 'TOGGLE_FORM' })
            }
          }}>
            {showForm ? 'Cancelar' : 'Nueva Campaña'}
          </button>
      </div>

      {successMsg && (
        <div className="alert alert-success alert-dismissible py-2 fade show">
          {successMsg}
            <button type="button" className="btn-close" onClick={() => dispatch({ type: 'SET_SUCCESS', payload: '' })} aria-label="Cerrar mensaje de éxito" />
        </div>
      )}
      {errorMsg && (
        <div className="alert alert-danger alert-dismissible py-2 fade show">
          {errorMsg}
            <button type="button" className="btn-close" onClick={() => dispatch({ type: 'SET_ERROR', payload: '' })} aria-label="Cerrar mensaje de error" />
        </div>
      )}

      {showForm && (
        <div className="card shadow-sm mb-4">
          <div className="card-body">
            <h5 className="card-title">{editingId ? 'Editar Campaña' : 'Crear Nueva Campaña'}</h5>
            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label className="form-label" htmlFor="campaign-nombre">Nombre:</label>
                <input
                  id="campaign-nombre"
                  type="text"
                  className="form-control"
                  value={formData.nombre}
                  onChange={(e) => dispatch({ type: 'SET_FORM_DATA', payload: { nombre: e.target.value } })}
                  required
                  maxLength={100}
                />
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="campaign-descripcion">Descripción:</label>
                <textarea
                  id="campaign-descripcion"
                  className="form-control"
                  value={formData.descripcion}
                  onChange={(e) => dispatch({ type: 'SET_FORM_DATA', payload: { descripcion: e.target.value } })}
                  required
                  maxLength={500}
                />
              </div>
              <div className="row g-3">
                <div className="col-md-6">
                  <label className="form-label" htmlFor="campaign-fecha-inicio">Fecha Inicio:</label>
                  <input
                    id="campaign-fecha-inicio"
                    type="date"
                    className="form-control"
                    value={formData.fechaInicio}
                    onChange={(e) => dispatch({ type: 'SET_FORM_DATA', payload: { fechaInicio: e.target.value } })}
                    required
                  />
                </div>
                <div className="col-md-6">
                  <label className="form-label" htmlFor="campaign-fecha-fin">Fecha Fin:</label>
                  <input
                    id="campaign-fecha-fin"
                    type="date"
                    className="form-control"
                    value={formData.fechaFin}
                    onChange={(e) => dispatch({ type: 'SET_FORM_DATA', payload: { fechaFin: e.target.value } })}
                    required
                  />
                </div>
              </div>
              <div className="mb-3">
                <label className="form-label" htmlFor="campaign-estado">Estado:</label>
                <select
                  id="campaign-estado"
                  className="form-select"
                  value={formData.estado}
                  onChange={(e) => dispatch({ type: 'SET_FORM_DATA', payload: { estado: e.target.value } })}
                >
                  <option value="PLANNED">Planificada</option>
                  <option value="ACTIVE">Activa</option>
                  <option value="COMPLETED">Completada</option>
                </select>
              </div>
              <div className="d-flex gap-2">
                <button type="submit" className="btn btn-primary" disabled={submitting}>
                  {submitting ? 'Guardando...' : editingId ? 'Guardar Cambios' : 'Crear Campaña'}
                </button>
                {editingId && (
                  <button type="button" className="btn btn-secondary" onClick={cancelEdit}>
                  Cancelar
                </button>
                )}
              </div>
            </form>
          </div>
        </div>
      )}

      {loading ? (
        <div className="text-center py-4 text-muted">Cargando...</div>
      ) : campaignList.length === 0 ? (
        <div className="text-muted text-center py-4">No hay campañas registradas</div>
      ) : (
        <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
          {campaignList.map((campaign) => (
            <div key={campaign.id} className="col">
              <div className="card h-100 shadow-sm">
                <div className="card-body pb-2">
                  <h5 className="card-title">{campaign.nombre}</h5>
                  <p className="card-text text-muted">{campaign.descripcion}</p>
                  <div className="d-flex justify-content-between align-items-center mt-3">
                    <span className={`status ${getStatusClass(campaign.estado)}`}>
                      {campaign.estado === 'ACTIVE' ? 'Activa' : 
                       campaign.estado === 'PLANNED' ? 'Planificada' : 'Completada'}
                    </span>
                    <small className="text-muted">
                      {formatDate(campaign.fechaInicio)} - {formatDate(campaign.fechaFin)}
                    </small>
                  </div>
                </div>
                <div className="card-footer bg-white border-top-0 d-flex gap-2 justify-content-end pt-0">
                   <button type="button" className="btn btn-sm btn-success" onClick={() => handleEdit(campaign)} title="Editar" aria-label="Editar campaña"><i className="bi bi-pencil"></i></button>
                   <button type="button" className="btn btn-sm btn-outline-danger" onClick={() => handleDelete(campaign.id)} disabled={deletingId === campaign.id} title="Eliminar" aria-label="Eliminar campaña">
                     {deletingId === campaign.id ? '...' : <i className="bi bi-trash"></i>}
                   </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default Campaigns

