<<<<<<< HEAD
import { useState, useEffect } from 'react';
import { volunteers, campaigns } from '../services/api';
import { formatDate } from '../utils/format';

function Volunteers() {
  const [volunteerList, setVolunteerList] = useState([]);
  const [campaignList, setCampaignList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [deletingId, setDeletingId] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [submittingAssign, setSubmittingAssign] = useState(false);
  const [successMsg, setSuccessMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');
  const [assignMode, setAssignMode] = useState(null);
  const [formData, setFormData] = useState({
=======
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
>>>>>>> develop
    nombre: '',
    apellido: '',
    email: '',
    telefono: '',
    direccion: ''
<<<<<<< HEAD
  });
  const [assignData, setAssignData] = useState({ campaignId: '' });

  const loadVolunteers = async () => {
    try {
      const response = await volunteers.list();
      setVolunteerList(response.data);
    } catch (error) {
      console.error('Error loading volunteers:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadCampaigns = async () => {
    try {
      const response = await campaigns.list();
      setCampaignList(response.data);
    } catch (error) {
      console.error('Error loading campaigns:', error);
    }
  };

  useEffect(() => {
    loadVolunteers();
    loadCampaigns();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      if (editingId) {
        await volunteers.update(editingId, formData);
        setSuccessMsg('Voluntario actualizado exitosamente');
      } else {
        await volunteers.create(formData);
        setSuccessMsg('Voluntario registrado exitosamente');
      }
      setErrorMsg('');
      setShowForm(false);
      setEditingId(null);
      resetForm();
      loadVolunteers();
    } catch (error) {
      console.error('Error saving volunteer:', error);
      setErrorMsg(error.response?.data?.error || 'Error al guardar el voluntario');
    } finally {
      setSubmitting(false);
    }
  };

  const handleAssignCampaign = async (volunteerId) => {
    if (!assignData.campaignId) return;
    setSubmittingAssign(true);
    try {
      await volunteers.assignToCampaign(volunteerId, {
        campaignId: assignData.campaignId ? parseInt(assignData.campaignId) : null
      });
      setAssignMode(null);
      setAssignData({ campaignId: '' });
      setSuccessMsg('Voluntario asignado a campaña exitosamente');
      setErrorMsg('');
      loadVolunteers();
    } catch (error) {
      console.error('Error assigning campaign:', error);
      setErrorMsg(error.response?.data?.error || 'Error al asignar a campaña');
    } finally {
      setSubmittingAssign(false);
    }
  };

  const handleEdit = (volunteer) => {
    setEditingId(volunteer.id);
    setFormData({
      nombre: volunteer.nombre || '',
      apellido: volunteer.apellido || '',
      email: volunteer.email || '',
      telefono: volunteer.telefono || '',
      direccion: volunteer.direccion || ''
    });
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('¿Está seguro de eliminar este voluntario?')) return;
    setDeletingId(id);
    try {
      await volunteers.delete(id);
      setSuccessMsg('Voluntario eliminado exitosamente');
      setErrorMsg('');
      loadVolunteers();
    } catch (error) {
      console.error('Error deleting volunteer:', error);
      setErrorMsg(error.response?.data?.error || 'Error al eliminar el voluntario');
    } finally {
      setDeletingId(null);
    }
  };

  const resetForm = () => {
    setFormData({
      nombre: '',
      apellido: '',
      email: '',
      telefono: '',
      direccion: ''
    });
  };

  const cancelEdit = () => {
    setShowForm(false);
    setEditingId(null);
    resetForm();
  };

  useEffect(() => {
    document.title = 'Voluntarios - Donaton';
  }, []);

  useEffect(() => {
    if (!successMsg) return;
    const t = setTimeout(() => setSuccessMsg(''), 5000);
    return () => clearTimeout(t);
  }, [successMsg]);

  useEffect(() => {
    if (!errorMsg) return;
    const t = setTimeout(() => setErrorMsg(''), 5000);
    return () => clearTimeout(t);
  }, [errorMsg]);
=======
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
>>>>>>> develop

  return (
    <div className="container py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1 className="fw-bold mb-0" style={{ color: 'var(--rojo)' }}>Voluntarios</h1>
<<<<<<< HEAD
        <button className="btn btn-primary" onClick={() => {
          setShowForm(!showForm);
          setEditingId(null);
          resetForm();
        }}>
=======
        <button type="button" className="btn btn-primary" onClick={() => dispatch({ type: 'TOGGLE_FORM' })}>
>>>>>>> develop
          {showForm ? 'Cancelar' : 'Nuevo Voluntario'}
        </button>
      </div>

      {successMsg && (
        <div className="alert alert-success alert-dismissible py-2 fade show">
          {successMsg}
<<<<<<< HEAD
          <button type="button" className="btn-close" onClick={() => setSuccessMsg('')} />
=======
           <button type="button" className="btn-close" onClick={() => dispatch({ type: 'SET_SUCCESS', payload: '' })} aria-label="Cerrar mensaje de éxito" />

>>>>>>> develop
        </div>
      )}
      {errorMsg && (
        <div className="alert alert-danger alert-dismissible py-2 fade show">
          {errorMsg}
<<<<<<< HEAD
          <button type="button" className="btn-close" onClick={() => setErrorMsg('')} />
=======
           <button type="button" className="btn-close" onClick={() => dispatch({ type: 'SET_ERROR', payload: '' })} aria-label="Cerrar mensaje de error" />

>>>>>>> develop
        </div>
      )}

      {showForm && (
<<<<<<< HEAD
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
=======
        <VolunteerForm 
          formData={formData} 
          setFormData={(newData) => dispatch({ type: 'SET_FORM_DATA', payload: newData })}
          handleSubmit={handleSubmit} 
          submitting={submitting} 
          editingId={editingId} 
          cancelEdit={() => dispatch({ type: 'RESET_FORM' })}
        />
>>>>>>> develop
      )}

      {loading ? (
        <div className="text-center py-4 text-muted">Cargando...</div>
      ) : volunteerList.length === 0 ? (
        <div className="text-muted text-center py-4">No hay voluntarios registrados</div>
      ) : (
<<<<<<< HEAD
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
                            {submittingAssign ? '...' : '✓'}
                          </button>
                          <button
                            className="btn btn-sm btn-outline-secondary"
                            onClick={() => setAssignMode(null)}
                          >
                            ✕
                          </button>
                        </div>
                      ) : (
                        <>
                          <button
                            className="btn btn-sm btn-secondary"
                            onClick={() => setAssignMode(volunteer.id)}
                          >
                            Asignar a Campaña
                          </button>
                          <button className="btn btn-sm btn-success" onClick={() => handleEdit(volunteer)} title="Editar" aria-label="Editar voluntario">✏️</button>
                          <button className="btn btn-sm btn-outline-danger" onClick={() => handleDelete(volunteer.id)} disabled={deletingId === volunteer.id} title="Eliminar" aria-label="Eliminar voluntario">
                            {deletingId === volunteer.id ? '...' : '🗑️'}
                          </button>
                        </>
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
  );
}

export default Volunteers;
=======
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
>>>>>>> develop
