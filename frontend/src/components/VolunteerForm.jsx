import React from 'react'

function VolunteerForm({ formData, setFormData, handleSubmit, submitting, editingId, cancelEdit }) {
  return (
    <div className="card shadow-sm mb-4">
      <div className="card-body">
        <h5 className="card-title">{editingId ? 'Editar Voluntario' : 'Registrar Nuevo Voluntario'}</h5>
        <form onSubmit={handleSubmit}>
          <div className="row g-3">
            <div className="col-md-6">
              <label htmlFor="volunteer-first-name" className="form-label">Nombre:</label>
              <input
                id="volunteer-first-name"
                type="text"
                className="form-control"
                value={formData.nombre}
                onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
                required
                maxLength={100}
              />
            </div>
            <div className="col-md-6">
              <label htmlFor="volunteer-last-name" className="form-label">Apellido:</label>
              <input
                id="volunteer-last-name"
                type="text"
                className="form-control"
                value={formData.apellido}
                onChange={(e) => setFormData({ ...formData, apellido: e.target.value })}
                required
                maxLength={100}
              />
            </div>
          </div>
          <div className="mb-3">
            <label htmlFor="volunteer-email" className="form-label">Email:</label>
            <input
              id="volunteer-email"
              type="email"
              className="form-control"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              required
            />
          </div>
          <div className="row g-3">
            <div className="col-md-6">
              <label htmlFor="volunteer-phone" className="form-label">Teléfono:</label>
              <input
                id="volunteer-phone"
                type="tel"
                className="form-control"
                value={formData.telefono}
                onChange={(e) => setFormData({ ...formData, telefono: e.target.value })}
              />
            </div>
            <div className="col-md-6">
              <label htmlFor="volunteer-address" className="form-label">Dirección:</label>
              <input
                id="volunteer-address"
                type="text"
                className="form-control"
                value={formData.direccion}
                onChange={(e) => setFormData({ ...formData, direccion: e.target.value })}
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
  )
}

export default VolunteerForm
