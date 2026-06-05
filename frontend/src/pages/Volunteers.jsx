import { useState, useEffect } from 'react'
import { volunteers, campaigns } from '../services/api'
import {  } from '../utils/format'

function Volunteers() {
  const [volunteerList, setVolunteerList] = useState([])
  const [campaignList, setCampaignList] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [deletingId, setDeletingId] = useState(null)
  const [submitting, setSubmitting] = useState(false)
  const [submittingAssign, setSubmittingAssign] = useState(false)
  const [successMsg, setSuccessMsg] = useState('')
  const [errorMsg, setErrorMsg] = useState('')
  const [assignMode, setAssignMode] = useState(null)
  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    email: '',
    telefono: '',
    direccion: ''
  })
  const [assignData, setAssignData] = useState({ campaignId: '' })

  const loadVolunteers = async () => {
    try {
      const response = await volunteers.list()
      setVolunteerList(response.data)
    } catch (error) {
      console.error('Error loading volunteers:', error)
    } finally {
      setLoading(false)
    }
  }

  const loadCampaigns = async () => {
    try {
      const response = await campaigns.list()
      setCampaignList(response.data)
    } catch (error) {
      console.error('Error loading campaigns:', error)
    }
  }

  useEffect(() => {
    const init = async () => {
      await loadVolunteers()
      await loadCampaigns()
    }
    init()
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSubmitting(true)
    try {
      if (editingId) {
        await volunteers.update(editingId, formData)
        setSuccessMsg('Voluntario actualizado exitosamente')
      } else {
        await volunteers.create(formData)
        setSuccessMsg('Voluntario registrado exitosamente')
      }
      setErrorMsg('')
      setShowForm(false)
      setEditingId(null)
      resetForm()
      loadVolunteers()
    } catch (error) {
      console.error('Error saving volunteer:', error)
      setErrorMsg(error.response?.data?.error || 'Error al guardar el voluntario')
    } finally {
      setSubmitting(false)
    }
  }

  const handleAssignCampaign = async (volunteerId) => {
    if (!assignData.campaignId) return
    setSubmittingAssign(true)
    try {
      await volunteers.assignToCampaign(volunteerId, {
        campaignId: assignData.campaignId ? parseInt(assignData.campaignId) : null
      })
      setAssignMode(null)
      setAssignData({ campaignId: '' })
      setSuccessMsg('Voluntario asignado a campaña exitosamente')
      setErrorMsg('')
      loadVolunteers()
    } catch (error) {
      console.error('Error assigning campaign:', error)
      setErrorMsg(error.response?.data?.error || 'Error al asignar a campaña')
    } finally {
      setSubmittingAssign(false)
    }
  }

  const handleEdit = (volunteer) => {
    setEditingId(volunteer.id)
    setFormData({
      nombre: volunteer.nombre || '',
      apellido: volunteer.apellido || '',
      email: volunteer.email || '',
      telefono: volunteer.telefono || '',
      direccion: volunteer.direccion || ''
    })
    setShowForm(true)
  }

  const handleDelete = async (id) => {
    if (!window.confirm('¿Está seguro de eliminar este voluntario?')) return
    setDeletingId(id)
    try {
      await volunteers.delete(id)
      setSuccessMsg('Voluntario eliminado exitosamente')
      setErrorMsg('')
      loadVolunteers()
    } catch (error) {
      console.error('Error deleting volunteer:', error)
      setErrorMsg(error.response?.data?.error || 'Error al eliminar el voluntario')
    } finally {
      setDeletingId(null)
    }
  }

  const resetForm = () => {
    setFormData({
      nombre: '',
      apellido: '',
      email: '',
      telefono: '',
      direccion: ''
    })
  }

  const cancelEdit = () => {
    setShowForm(false)
    setEditingId(null)
    resetForm()
  }

  useEffect(() => {
    document.title = 'Voluntarios - Donaton'
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
        <h1 className="fw-bold mb-0" style={{ color: 'var(--rojo)' }}>Voluntarios</h1>
        <button className="btn btn-primary" onClick={() => {
          setShowForm(!showForm)
          setEditingId(null)
          resetForm()
        }}>
          {showForm ? 'Cancelar' : 'Nuevo Voluntario'}
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
            <h5 className="card-title">{editingId ? 'Editar Voluntario' : 'Registrar Nuevo Voluntario'}</h5>
            <form onSubmit={handleSubmit}>
              <div className="row g-3">
                <div className="col-md-6">
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
                <div className="col-md-6">
                  <label className="form-label">Apellido:</label>
                <input
                  type="text"
                  className="form-control"
                  value={formData.apellido}
                  onChange={(e) => setFormData({...formData, apellido: e.target.value})}
                  required
                  maxLength={100}
                />
                </div>
              </div>
              <div className="mb-3">
                <label className="form-label">Email:</label>
                <input
                  type="email"
                  className="form-control"
                  value={formData.email}
                  onChange={(e) => setFormData({...formData, email: e.target.value})}
                  required
                />
              </div>
              <div className="row g-3">
                <div className="col-md-6">
                  <label className="form-label">Teléfono:</label>
                  <input
                    type="tel"
                    className="form-control"
                    value={formData.telefono}
                    onChange={(e) => setFormData({...formData, telefono: e.target.value})}
                  />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Dirección:</label>
                  <input
                    type="text"
                    className="form-control"
                    value={formData.direccion}
                    onChange={(e) => setFormData({...formData, direccion: e.target.value})}
                  />
                </div>
              </div>
              <div className="d-flex gap-2 mt-3">
                <button type="submit" className="btn btn-primary" disabled={submitting}>
                  {submitting ? 'Guardando...' : editingId ? 'Guardar Cambios' : 'Registrar'}
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
      ) : volunteerList.length === 0 ? (
        <div className="text-muted text-center py-4">No hay voluntarios registrados</div>
      ) : (
        <div className="table-responsive bg-white rounded shadow-sm">
          <table className="table table-hover align-middle mb-0">
            <thead className="table-light">
              <tr>
                <th>Nombre</th>
                <th>Email</th>
                <th>Teléfono</th>
                <th>Campañas</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {volunteerList.map((volunteer) => (
                <tr key={volunteer.id}>
                  <td>{volunteer.nombre} {volunteer.apellido}</td>
                  <td>{volunteer.email}</td>
                  <td>{volunteer.telefono || '-'}</td>
                  <td>
                    {volunteer.campaigns?.length > 0 ? (
                      <div className="d-flex flex-wrap gap-1">
                        {volunteer.campaigns.map((c) => (
                          <span key={c.id} className="tag">{c.nombre}</span>
                        ))}
                      </div>
                    ) : (
                      <span className="no-campaigns">Sin asignar</span>
                    )}
                  </td>
                   <td>
                     <div className="d-flex gap-1 align-items-center">
                       {assignMode === volunteer.id ? (
                         <div className="d-flex align-items-center gap-1">
                           <select
                             className="form-select form-select-sm"
                             value={assignData.campaignId}
                             onChange={(e) => setAssignData({...assignData, campaignId: e.target.value})}
                             style={{ width: 'auto' }}
                           >
                             <option value="">Seleccionar</option>
                             {campaignList.map((c) => (
                               <option key={c.id} value={c.id}>{c.nombre}</option>
                             ))}
                           </select>
                           <button
                             className="btn btn-sm btn-success"
                             onClick={() => handleAssignCampaign(volunteer.id)}
                             disabled={submittingAssign}
                           >
                             {submittingAssign ? '...' : 'Asignar'}
                           </button>
                         </div>
                       ) : (
                         <div className="d-flex gap-1">
                           <button className="btn btn-sm btn-outline-primary" onClick={() => setAssignMode(volunteer.id)} title="Asignar Campaña"><i className="bi bi-megaphone"></i></button>
                           <button className="btn btn-sm btn-outline-secondary" onClick={() => handleEdit(volunteer)} title="Editar"><i className="bi bi-pencil"></i></button>
                           <button className="btn btn-sm btn-outline-danger" onClick={() => handleDelete(volunteer.id)} disabled={deletingId === volunteer.id} title="Eliminar">
                             {deletingId === volunteer.id ? '...' : <i className="bi bi-trash"></i>}
                           </button>
                         </div>
                       )}
                     </div>
                   </td>


                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}

export default Volunteers
