import { useState, useEffect } from 'react'
import { donations, campaigns } from '../services/api'
import { formatDate, formatDonationAmount } from '../utils/format'

function Donations() {
  const [donationList, setDonationList] = useState([])
  const [campaignList, setCampaignList] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [submitting, setSubmitting] = useState(false)
  const [deletingId, setDeletingId] = useState(null)
  const [successMsg, setSuccessMsg] = useState('')
  const [errorMsg, setErrorMsg] = useState('')
  const [formData, setFormData] = useState({
    type: 'MONETARY',
    donorName: '',
    description: '',
    campaignId: '',
    amount: '',
    currency: 'CLP',
    objectName: '',
    category: '',
    quantity: 1,
    estimatedValue: ''
  })

  const loadDonations = async () => {
    try {
      const response = await donations.list()
      setDonationList(response.data)
    } catch (error) {
      console.error('Error loading donations:', error)
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
      await loadDonations()
      await loadCampaigns()
    }
    init()
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

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSubmitting(true)
    try {
      const payload = {
        type: formData.type,
        donorName: formData.donorName,
        description: formData.description,
        campaignId: formData.campaignId ? parseInt(formData.campaignId) : null
      }

      if (formData.type === 'MONETARY') {
        payload.amount = parseFloat(formData.amount || 0)
        payload.currency = formData.currency
      } else {
        payload.objectName = formData.objectName
        payload.category = formData.category
        payload.quantity = parseInt(formData.quantity) || 1
        payload.estimatedValue = parseFloat(formData.estimatedValue || 0)
      }

      if (editingId) {
        await donations.update(editingId, payload)
        setSuccessMsg('Donación actualizada exitosamente')
      } else {
        await donations.create(payload)
        setSuccessMsg('Donación registrada exitosamente')
      }
      setErrorMsg('')
      setShowForm(false)
      setEditingId(null)
      resetForm()
      loadDonations()
    } catch (error) {
      console.error('Error saving donation:', error)
      setErrorMsg(error.response?.data?.error || 'Error al guardar la donación')
    } finally {
      setSubmitting(false)
    }
  }

  const handleEdit = (donation) => {
    setEditingId(donation.id)
    setFormData({
      type: donation.type || 'MONETARY',
      donorName: donation.donorName || '',
      description: donation.description || '',
      campaignId: donation.campaignId?.toString() || '',
      amount: donation.amount?.toString() || '',
      currency: donation.currency || 'CLP',
      objectName: donation.objectName || '',
      category: donation.category || '',
      quantity: donation.quantity?.toString() || '1',
      estimatedValue: donation.estimatedValue?.toString() || ''
    })
    setShowForm(true)
  }

  const handleDelete = async (id) => {
    if (!window.confirm('¿Está seguro de eliminar esta donación?')) return
    setDeletingId(id)
    try {
      await donations.delete(id)
      setSuccessMsg('Donación eliminada exitosamente')
      setErrorMsg('')
      loadDonations()
    } catch (error) {
      console.error('Error deleting donation:', error)
      setErrorMsg(error.response?.data?.error || 'Error al eliminar la donación')
    } finally {
      setDeletingId(null)
    }
  }

  const resetForm = () => {
    setFormData({
      type: 'MONETARY',
      donorName: '',
      description: '',
      campaignId: '',
      amount: '',
      currency: 'CLP',
      objectName: '',
      category: '',
      quantity: 1,
      estimatedValue: ''
    })
  }

  const cancelEdit = () => {
    setShowForm(false)
    setEditingId(null)
    resetForm()
  }

  useEffect(() => {
    document.title = 'Donaciones - Donaton'
  }, [])

  return (
    <div className="container py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1 className="fw-bold mb-0" style={{ color: 'var(--rojo)' }}>Donaciones</h1>
        <button className="btn btn-primary" onClick={() => {
          if (showForm) { setShowForm(false); setEditingId(null); resetForm() }
          else { setShowForm(true); setEditingId(null); resetForm() }
        }}>
          {showForm ? 'Cancelar' : 'Registrar Donación'}
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
            <h5 className="card-title">{editingId ? 'Editar Donación' : 'Nueva Donación'}</h5>
            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label className="form-label">Tipo de Donación:</label>
                <select
                  className="form-select"
                  value={formData.type}
                  onChange={(e) => setFormData({...formData, type: e.target.value})}
                >
                  <option value="MONETARY">Monetaria</option>
                  <option value="OBJECT">Objeto</option>
                </select>
              </div>
              <div className="mb-3">
                <label className="form-label">Nombre del Donador:</label>
                <input
                  type="text"
                  className="form-control"
                  value={formData.donorName}
                  onChange={(e) => setFormData({...formData, donorName: e.target.value})}
                  required
                  maxLength={100}
                />
              </div>
              <div className="mb-3">
                <label className="form-label">Campaña:</label>
                <select
                  className="form-select"
                  value={formData.campaignId}
                  onChange={(e) => setFormData({...formData, campaignId: e.target.value})}
                  required
                >
                  <option value="">Seleccionar campaña</option>
                  {campaignList.map((c) => (
                    <option key={c.id} value={c.id}>{c.nombre}</option>
                  ))}
                </select>
              </div>
              <div className="mb-3">
                <label className="form-label">Descripción:</label>
                <textarea
                  className="form-control"
                  value={formData.description}
                  onChange={(e) => setFormData({...formData, description: e.target.value})}
                  maxLength={500}
                />
              </div>

              {formData.type === 'MONETARY' ? (
                <div className="row g-3">
                  <div className="col-md-6">
                    <label className="form-label">Monto:</label>
                    <input
                      type="number"
                      className="form-control"
                      value={formData.amount}
                      onChange={(e) => setFormData({...formData, amount: e.target.value})}
                      required
                      min="0"
                      step="0.01"
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label">Moneda:</label>
                    <select
                      className="form-select"
                      value={formData.currency}
                      onChange={(e) => setFormData({...formData, currency: e.target.value})}
                    >
                      <option value="CLP">CLP</option>
                      <option value="USD">USD</option>
                      <option value="EUR">EUR</option>
                    </select>
                  </div>
                </div>
              ) : (
                <>
                  <div className="row g-3">
                    <div className="col-md-6">
                      <label className="form-label">Nombre del Objeto:</label>
                      <input
                        type="text"
                        className="form-control"
                        value={formData.objectName}
                        onChange={(e) => setFormData({...formData, objectName: e.target.value})}
                        required
                      />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Categoría:</label>
                      <input
                        type="text"
                        className="form-control"
                        value={formData.category}
                        onChange={(e) => setFormData({...formData, category: e.target.value})}
                      />
                    </div>
                  </div>
                  <div className="row g-3">
                    <div className="col-md-6">
                      <label className="form-label">Cantidad:</label>
                      <input
                        type="number"
                        min="1"
                        className="form-control"
                        value={formData.quantity}
                        onChange={(e) => setFormData({...formData, quantity: e.target.value})}
                        required
                      />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Valor Estimado:</label>
                      <input
                        type="number"
                        className="form-control"
                        value={formData.estimatedValue}
                        onChange={(e) => setFormData({...formData, estimatedValue: e.target.value})}
                      />
                    </div>
                  </div>
                </>
              )}

              <div className="d-flex gap-2 mt-3">
                <button type="submit" className="btn btn-primary" disabled={submitting}>
                  {submitting ? 'Guardando...' : editingId ? 'Guardar Cambios' : 'Registrar Donación'}
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
      ) : donationList.length === 0 ? (
        <div className="text-muted text-center py-4">No hay donaciones registradas</div>
      ) : (
        <div className="table-responsive bg-white rounded shadow-sm">
          <table className="table table-hover align-middle mb-0">
            <thead className="table-light">
              <tr>
                <th>ID</th>
                <th>Donador</th>
                <th>Tipo</th>
                <th>Monto/Objeto</th>
                <th>Campaña</th>
                <th>Fecha</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {donationList.map((donation) => (
                <tr key={donation.id}>
                  <td>{donation.id}</td>
                  <td>{donation.donorName}</td>
                  <td>
                    <span className={`status ${donation.type === 'MONETARY' ? 'status-active' : 'status-pending'}`}>
                      {donation.type === 'MONETARY' ? 'Monetaria' : 'Objeto'}
                    </span>
                  </td>
                    <td>{formatDonationAmount(donation)}</td>
                  <td>{donation.campaign?.nombre || campaignList.find(c => c.id === donation.campaignId)?.nombre || '-'}</td>
                  <td>{formatDate(donation.registrationDate)}</td>
                  <td>
                    <button className="btn btn-sm btn-success me-1" onClick={() => handleEdit(donation)} title="Editar" aria-label="Editar donación"><i className="bi bi-pencil"></i></button>
                    <button className="btn btn-sm btn-outline-danger" onClick={() => handleDelete(donation.id)} disabled={deletingId === donation.id} title="Eliminar" aria-label="Eliminar donación">
                      {deletingId === donation.id ? '...' : <i className="bi bi-trash"></i>}
                    </button>
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

export default Donations
