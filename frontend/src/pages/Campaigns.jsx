import { useState, useEffect } from 'react'
import { campaigns } from '../services/api'
import { formatDate } from '../utils/format'

function Campaigns() {
  const [campaignList, setCampaignList] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [deletingId, setDeletingId] = useState(null)
  const [submitting, setSubmitting] = useState(false)
  const [successMsg, setSuccessMsg] = useState('')
  const [errorMsg, setErrorMsg] = useState('')
  const [formData, setFormData] = useState({
    nombre: '',
    descripcion: '',
    fechaInicio: '',
    fechaFin: '',
    estado: 'PLANNED'
  })

  const loadCampaigns = async () => {
    try {
      const response = await campaigns.list()
      setCampaignList(response.data)
    } catch (error) {
      console.error('Error loading campaigns:', error)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    const init = async () => {
      await loadCampaigns()
    }
    init()
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSubmitting(true)
    try {
      if (editingId) {
        await campaigns.update(editingId, formData)
        setSuccessMsg('Campaña actualizada exitosamente')
      } else {
        await campaigns.create(formData)
        setSuccessMsg('Campaña creada exitosamente')
      }
      setErrorMsg('')
      setShowForm(false)
      setEditingId(null)
      resetForm()
      loadCampaigns()
    } catch (error) {
      console.error('Error saving campaign:', error)
      setErrorMsg(error.response?.data?.error || 'Error al guardar la campaña')
    } finally {
      setSubmitting(false)
    }
  }

  const getStatusClass = (status) => {
    switch (status) {
      case 'ACTIVE': return 'status-active'
      case 'PLANNED': return 'status-pending'
      case 'COMPLETED': return 'status-completed'
      default: return 'status-pending'
    }
  }

  const handleEdit = (campaign) => {
    setEditingId(campaign.id)
    setFormData({
      nombre: campaign.nombre || '',
      descripcion: campaign.descripcion || '',
      fechaInicio: campaign.fechaInicio || '',
      fechaFin: campaign.fechaFin || '',
      estado: campaign.estado || 'PLANNED'
    })
    setShowForm(true)
  }

  const handleDelete = async (id) => {
    if (!window.confirm('¿Está seguro de eliminar esta campaña?')) return
    setDeletingId(id)
    try {
      await campaigns.delete(id)
      setSuccessMsg('Campaña eliminada exitosamente')
      setErrorMsg('')
      loadCampaigns()
    } catch (error) {
      console.error('Error deleting campaign:', error)
      setErrorMsg(error.response?.data?.error || 'Error al eliminar la campaña')
    } finally {
      setDeletingId(null)
    }
  }

  const resetForm = () => {
    setFormData({
      nombre: '',
      descripcion: '',
      fechaInicio: '',
      fechaFin: '',
      estado: 'PLANNED'
    })
  }

  const cancelEdit = () => {
    setShowForm(false)
    setEditingId(null)
    resetForm()
  }

  useEffect(() => {
    document.title = 'Campañas - Donaton'
  }, [])

  useEffect(() => {
    if (!successMsg) return
    const t = setTimeout(() => setSuccessMsg(''), 5000)
    return () => clearTimeout(t)
  }, [successMsg])

  useEffect(() => {
    if (!errorMsg) return
    const t = setTimeout(() => setErrorMsg(''), 5000)
    return () => clearTimeout(t)
  }, [errorMsg])

  return (
    <div className="container py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1 className="fw-bold mb-0" style={{ color: 'var(--rojo)' }}>Campañas de Ayuda Humanitaria</h1>
        <button className="btn btn-primary" onClick={() => {
          setShowForm(!showForm)
          setEditingId(null)
          resetForm()
        }}>
          {showForm ? 'Cancelar' : 'Nueva Campaña'}
        </button>
      </div>

      {successMsg && (
        <div className="alert alert-success alert-dismissible py-2 fade show">
          {successMsg}
          <button type="button" className="btn-close" onClick={() => setSuccessMsg('')} />
        </div>
      )}
      {errorMsg && (
        <div className="alert alert-danger alert-dismissible py-2 fade show">
          {errorMsg}
          <button type="button" className="btn-close" onClick={() => setErrorMsg('')} />
        </div>
      )}

      {showForm && (
        <div className="card shadow-sm mb-4">
          <div className="card-body">
            <h5 className="card-title">{editingId ? 'Editar Campaña' : 'Crear Nueva Campaña'}</h5>
            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label className="form-label">Nombre:</label>
                <input
                  type="text"
                  className="form-control"
                  value={formData.nombre}
                  onChange={(e) => setFormData({...formData, nombre: e.target.value})}
                  required
                  maxLength={100}
                />
              </div>
              <div className="mb-3">
                <label className="form-label">Descripción:</label>
                <textarea
                  className="form-control"
                  value={formData.descripcion}
                  onChange={(e) => setFormData({...formData, descripcion: e.target.value})}
                  required
                  maxLength={500}
                />
              </div>
              <div className="row g-3">
                <div className="col-md-6">
                  <label className="form-label">Fecha Inicio:</label>
                  <input
                    type="date"
                    className="form-control"
                    value={formData.fechaInicio}
                    onChange={(e) => setFormData({...formData, fechaInicio: e.target.value})}
                    required
                  />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Fecha Fin:</label>
                  <input
                    type="date"
                    className="form-control"
                    value={formData.fechaFin}
                    onChange={(e) => setFormData({...formData, fechaFin: e.target.value})}
                    required
                  />
                </div>
              </div>
              <div className="mb-3">
                <label className="form-label">Estado:</label>
                <select
                  className="form-select"
                  value={formData.estado}
                  onChange={(e) => setFormData({...formData, estado: e.target.value})}
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
                   <button className="btn btn-sm btn-success" onClick={() => handleEdit(campaign)} title="Editar" aria-label="Editar campaña"><i className="bi bi-pencil"></i></button>
                   <button className="btn btn-sm btn-outline-danger" onClick={() => handleDelete(campaign.id)} disabled={deletingId === campaign.id} title="Eliminar" aria-label="Eliminar campaña">
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
