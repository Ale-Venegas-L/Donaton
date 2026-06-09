import React from 'react'

function VolunteerTable({ 
  volunteers, 
  campaigns, 
  onEdit, 
  onDelete, 
  onAssignCampaign, 
  deletingId, 
  assignMode, 
  setAssignMode, 
  assignData, 
  setAssignData, 
  submittingAssign 
}) {
  return (
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
          {volunteers.map((volunteer) => (
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
                        onChange={(e) => setAssignData({ ...assignData, campaignId: e.target.value })}
                        style={{ width: 'auto' }}
                      >
                        <option value="">Seleccionar</option>
                        {campaigns.map((c) => (
                          <option key={c.id} value={c.id}>{c.nombre}</option>
                        ))}
                      </select>
                      <button
                        type="button"
                        className="btn btn-sm btn-success"
                        onClick={() => onAssignCampaign(volunteer.id)}
                        disabled={submittingAssign}
                      >
                        {submittingAssign ? '...' : 'Asignar'}
                      </button>
                    </div>
                  ) : (
                    <div className="d-flex gap-1">
                      <button type="button" className="btn btn-sm btn-outline-primary" onClick={() => setAssignMode(volunteer.id)} title="Asignar Campaña" aria-label="Asignar Campaña"><i className="bi bi-megaphone"></i></button>
                      <button type="button" className="btn btn-sm btn-outline-secondary" onClick={() => onEdit(volunteer)} title="Editar" aria-label="Editar voluntario"><i className="bi bi-pencil"></i></button>
                      <button type="button" className="btn btn-sm btn-outline-danger" onClick={() => onDelete(volunteer.id)} disabled={deletingId === volunteer.id} title="Eliminar" aria-label="Eliminar voluntario">
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
  )
}

export default VolunteerTable
