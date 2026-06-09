import { useEffect, useReducer } from 'react'
import { volunteers, campaigns } from '../services/api'
import {  } from '../utils/format'
import VolunteerForm from '../components/VolunteerForm'
import VolunteerTable from '../components/VolunteerTable'

const initialState = {
  volunteerList: [],
  campaignList: [],
  loading: true,
  showForm: false,
  editingId: null,
  deletingId: null,
  submitting: false,
  submittingAssign: false,
  successMsg: '',
  errorMsg: '',
  assignMode: null,
  formData: {
    nombre: '',
    apellido: '',
    email: '',
    telefono: '',
    direccion: ''
  },
  assignData: { campaignId: '' }
}

function volunteerReducer(state, action) {
  switch (action.type) {
    case 'SET_VOLUNTEERS': return { ...state, volunteerList: action.payload, loading: false }
    case 'SET_CAMPAIGNS': return { ...state, campaignList: action.payload }
    case 'SET_LOADING': return { ...state, loading: action.payload }
    case 'TOGGLE_FORM': return { ...state, showForm: !state.showForm, editingId: null, formData: action.payload || initialState.formData }
    case 'SET_EDITING': return { ...state, editingId: action.id, formData: action.formData, showForm: true }
    case 'SUBMITTING': return { ...state, submitting: action.payload }
    case 'SUBMITTING_ASSIGN': return { ...state, submittingAssign: action.payload }
    case 'SET_SUCCESS': return { ...state, successMsg: action.payload, errorMsg: '' }
    case 'SET_ERROR': return { ...state, errorMsg: action.payload, successMsg: '' }
    case 'SET_DELETING': return { ...state, deletingId: action.payload }
    case 'SET_ASSIGN_MODE': return { ...state, assignMode: action.payload }
    case 'SET_ASSIGN_DATA': return { ...state, assignData: action.payload }
    case 'RESET_FORM': return { ...state, formData: initialState.formData, showForm: false, editingId: null }
    case 'SET_FORM_DATA': return { ...state, formData: action.payload }
    default: return state
  }
}

function Volunteers() {
  const [state, dispatch] = useReducer(volunteerReducer, initialState)
  const { volunteerList, campaignList, loading, showForm, editingId, deletingId, submitting, submittingAssign, successMsg, errorMsg, assignMode, formData, assignData } = state

  const loadVolunteers = async () => {
    try {
      const response = await volunteers.list()
      dispatch({ type: 'SET_VOLUNTEERS', payload: response.data })
    } catch (error) {
      console.error('Error loading volunteers:', error)
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
      await Promise.all([loadVolunteers(), loadCampaigns()])
    }
    init()
  }, [])

  useEffect(() => {
    document.title = 'Voluntarios - Donaton'
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
      if (editingId) {
        await volunteers.update(editingId, formData)
        dispatch({ type: 'SET_SUCCESS', payload: 'Voluntario actualizado exitosamente' })
      } else {
        await volunteers.create(formData)
        dispatch({ type: 'SET_SUCCESS', payload: 'Voluntario registrado exitosamente' })
      }
      dispatch({ type: 'RESET_FORM' })
      loadVolunteers()
    } catch (error) {
      console.error('Error saving volunteer:', error)
      dispatch({ type: 'SET_ERROR', payload: error.response?.data?.error || 'Error al guardar el voluntario' })
    } finally {
      dispatch({ type: 'SUBMITTING', payload: false })
    }
  }

  const handleAssignCampaign = async (volunteerId) => {
    if (!assignData.campaignId) return
    dispatch({ type: 'SUBMITTING_ASSIGN', payload: true })
    try {
      await volunteers.assignToCampaign(volunteerId, {
        campaignId: assignData.campaignId ? parseInt(assignData.campaignId) : null
      })
      dispatch({ type: 'SET_ASSIGN_MODE', payload: null })
      dispatch({ type: 'SET_ASSIGN_DATA', payload: { campaignId: '' } })
      dispatch({ type: 'SET_SUCCESS', payload: 'Voluntario asignado a campaña exitosamente' })
      loadVolunteers()
    } catch (error) {
      console.error('Error assigning campaign:', error)
      dispatch({ type: 'SET_ERROR', payload: error.response?.data?.error || 'Error al asignar a campaña' })
    } finally {
      dispatch({ type: 'SUBMITTING_ASSIGN', payload: false })
    }
  }

  const handleEdit = (volunteer) => {
    dispatch({ 
      type: 'SET_EDITING', 
      id: volunteer.id, 
      formData: {
        nombre: volunteer.nombre || '',
        apellido: volunteer.apellido || '',
        email: volunteer.email || '',
        telefono: volunteer.telefono || '',
        direccion: volunteer.direccion || ''
      } 
    })
  }

  const handleDelete = async (id) => {
    if (!window.confirm('¿Está seguro de eliminar este voluntario?')) return
    dispatch({ type: 'SET_DELETING', payload: id })
    try {
      await volunteers.delete(id)
      dispatch({ type: 'SET_SUCCESS', payload: 'Voluntario eliminado exitosamente' })
      loadVolunteers()
    } catch (error) {
      console.error('Error deleting volunteer:', error)
      dispatch({ type: 'SET_ERROR', payload: error.response?.data?.error || 'Error al eliminar el voluntario' })
    } finally {
      dispatch({ type: 'SET_DELETING', payload: null })
    }
  }

  return (
    <div className="container py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1 className="fw-bold mb-0" style={{ color: 'var(--rojo)' }}>Voluntarios</h1>
        <button type="button" className="btn btn-primary" onClick={() => dispatch({ type: 'TOGGLE_FORM' })}>
          {showForm ? 'Cancelar' : 'Nuevo Voluntario'}
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
        <VolunteerForm 
          formData={formData} 
          setFormData={(newData) => dispatch({ type: 'SET_FORM_DATA', payload: newData })}
          handleSubmit={handleSubmit} 
          submitting={submitting} 
          editingId={editingId} 
          cancelEdit={() => dispatch({ type: 'RESET_FORM' })}
        />
      )}

      {loading ? (
        <div className="text-center py-4 text-muted">Cargando...</div>
      ) : volunteerList.length === 0 ? (
        <div className="text-muted text-center py-4">No hay voluntarios registrados</div>
      ) : (
        <VolunteerTable 
          volunteers={volunteerList} 
          campaigns={campaignList} 
          onEdit={handleEdit} 
          onDelete={handleDelete} 
          onAssignCampaign={handleAssignCampaign}
          deletingId={deletingId}
          assignMode={assignMode}
          setAssignMode={(id) => dispatch({ type: 'SET_ASSIGN_MODE', payload: id })}
          assignData={assignData}
          setAssignData={(data) => dispatch({ type: 'SET_ASSIGN_DATA', payload: data })}
          submittingAssign={submittingAssign}
        />
      )}
    </div>
  )
}

export default Volunteers
