import { useState, useEffect, useReducer } from 'react'
import { donations, campaigns } from '../services/api'
import { formatDate, formatDonationAmount } from '../utils/format'
import DonationForm from '../components/DonationForm'
import DonationTable from '../components/DonationTable'

const initialState = {
  donationList: [],
  campaignList: [],
  loading: true,
  showForm: false,
  editingId: null,
  submitting: false,
  deletingId: null,
  successMsg: '',
  errorMsg: '',
  formData: {
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
  }
}

function donationReducer(state, action) {
  switch (action.type) {
    case 'SET_DONATIONS': return { ...state, donationList: action.payload, loading: false }
    case 'SET_CAMPAIGNS': return { ...state, campaignList: action.payload }
    case 'SET_LOADING': return { ...state, loading: action.payload }
    case 'TOGGLE_FORM': return { ...state, showForm: !state.showForm, editingId: null, formData: action.payload || initialState.formData }
    case 'SET_EDITING': return { ...state, editingId: action.id, formData: action.formData, showForm: true }
    case 'SUBMITTING': return { ...state, submitting: action.payload }
    case 'SET_SUCCESS': return { ...state, successMsg: action.payload, errorMsg: '' }
    case 'SET_ERROR': return { ...state, errorMsg: action.payload, successMsg: '' }
    case 'SET_DELETING': return { ...state, deletingId: action.payload }
    case 'RESET_FORM': return { ...state, formData: initialState.formData, showForm: false, editingId: null }
    case 'SET_FORM_DATA': return { ...state, formData: action.payload }
    default: return state
  }
}

function Donations() {
  const [state, dispatch] = useReducer(donationReducer, initialState)
  const { donationList, campaignList, loading, showForm, editingId, submitting, deletingId, successMsg, errorMsg, formData } = state

  const loadDonations = async () => {
    try {
      const response = await donations.list()
      dispatch({ type: 'SET_DONATIONS', payload: response.data })
    } catch (error) {
      console.error('Error loading donations:', error)
    } finally {
      dispatch({ type: 'SET_LOADING', payload: false })
    }
  }

  const loadCampaigns = async () => {
    try {
      const response = await campaigns.list()
      dispatch({ type: 'SET_CAMPAIGNS', payload: response.data })
    } catch (error) {
      console.error('Error loading campaigns:', error)
    }
  }

  useEffect(() => {
    const init = async () => {
      await Promise.all([loadDonations(), loadCampaigns()])
    }
    init()
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

  const handleSubmit = async (e) => {
    e.preventDefault()
    dispatch({ type: 'SUBMITTING', payload: true })
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
        dispatch({ type: 'SET_SUCCESS', payload: 'Donación actualizada exitosamente' })
      } else {
        await donations.create(payload)
        dispatch({ type: 'SET_SUCCESS', payload: 'Donación registrada exitosamente' })
      }
      dispatch({ type: 'RESET_FORM' })
      loadDonations()
    } catch (error) {
      console.error('Error saving donation:', error)
      dispatch({ type: 'SET_ERROR', payload: error.response?.data?.error || 'Error al guardar la donación' })
    } finally {
      dispatch({ type: 'SUBMITTING', payload: false })
    }
  }

  const handleEdit = (donation) => {
    dispatch({ 
      type: 'SET_EDITING', 
      id: donation.id, 
      formData: {
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
      } 
    })
  }

  const handleDelete = async (id) => {
    if (!window.confirm('¿Está seguro de eliminar esta donación?')) return
    dispatch({ type: 'SET_DELETING', payload: id })
    try {
      await donations.delete(id)
      dispatch({ type: 'SET_SUCCESS', payload: 'Donación eliminada exitosamente' })
      loadDonations()
    } catch (error) {
      console.error('Error deleting donation:', error)
      dispatch({ type: 'SET_ERROR', payload: error.response?.data?.error || 'Error al eliminar la donación' })
    } finally {
      dispatch({ type: 'SET_DELETING', payload: null })
    }
  }

  useEffect(() => {
    document.title = 'Donaciones - Donaton'
  }, [])

  return (
    <div className="container py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1 className="fw-bold mb-0" style={{ color: 'var(--rojo)' }}>Donaciones</h1>
        <button type="button" className="btn btn-primary" onClick={() => dispatch({ type: 'TOGGLE_FORM' })}>
          {showForm ? 'Cancelar' : 'Registrar Donación'}
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
        <DonationForm 
          formData={formData} 
          setFormData={(newData) => dispatch({ type: 'SET_FORM_DATA', payload: newData })} // Note: I need to add SET_FORM_DATA to reducer
          handleSubmit={handleSubmit} 
          submitting={submitting} 
          editingId={editingId} 
          cancelEdit={() => dispatch({ type: 'RESET_FORM' })}
          campaignList={campaignList}
        />
      )}

      {loading ? (
        <div className="text-center py-4 text-muted">Cargando...</div>
      ) : donationList.length === 0 ? (
        <div className="text-muted text-center py-4">No hay donaciones registradas</div>
      ) : (
        <DonationTable 
          donations={donationList} 
          campaigns={campaignList} 
          onEdit={handleEdit} 
          onDelete={handleDelete} 
          deletingId={deletingId} 
        />
      )}
    </div>
  )
}

export default Donations
