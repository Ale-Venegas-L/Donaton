import React from 'react'

function DonationForm({ formData, setFormData, handleSubmit, submitting, editingId, cancelEdit, campaignList }) {
  return (
    <div className="card shadow-sm mb-4">
      <div className="card-body">
        <h5 className="card-title">{editingId ? 'Editar Donación' : 'Nueva Donación'}</h5>
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label htmlFor="donation-type" className="form-label">Tipo de Donación:</label>
            <select
              id="donation-type"
              className="form-select"
              value={formData.type}
              onChange={(e) => setFormData({ ...formData, type: e.target.value })}
            >
              <option value="MONETARY">Monetaria</option>
              <option value="OBJECT">Objeto</option>
            </select>
          </div>
          <div className="mb-3">
            <label htmlFor="donor-name" className="form-label">Nombre del Donador:</label>
            <input
              id="donor-name"
              type="text"
              className="form-control"
              value={formData.donorName}
              onChange={(e) => setFormData({ ...formData, donorName: e.target.value })}
              required
              maxLength={100}
            />
          </div>
          <div className="mb-3">
            <label htmlFor="campaign-id" className="form-label">Campaña:</label>
            <select
              id="campaign-id"
              className="form-select"
              value={formData.campaignId}
              onChange={(e) => setFormData({ ...formData, campaignId: e.target.value })}
              required
            >
              <option value="">Seleccionar campaña</option>
              {campaignList.map((c) => (
                <option key={c.id} value={c.id}>{c.nombre}</option>
              ))}
            </select>
          </div>
          <div className="mb-3">
            <label htmlFor="description" className="form-label">Descripción:</label>
            <textarea
              id="description"
              className="form-control"
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              maxLength={500}
            />
          </div>

          {formData.type === 'MONETARY' ? (
            <div className="row g-3">
              <div className="col-md-6">
                <label htmlFor="amount" className="form-label">Monto:</label>
                <input
                  id="amount"
                  type="number"
                  className="form-control"
                  value={formData.amount}
                  onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                  required
                  min="0"
                  step="0.01"
                />
              </div>
              <div className="col-md-6">
                <label htmlFor="currency" className="form-label">Moneda:</label>
                <select
                  id="currency"
                  className="form-select"
                  value={formData.currency}
                  onChange={(e) => setFormData({ ...formData, currency: e.target.value })}
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
                  <label htmlFor="object-name" className="form-label">Nombre del Objeto:</label>
                  <input
                    id="object-name"
                    type="text"
                    className="form-control"
                    value={formData.objectName}
                    onChange={(e) => setFormData({ ...formData, objectName: e.target.value })}
                    required
                  />
                </div>
                <div className="col-md-6">
                  <label htmlFor="category" className="form-label">Categoría:</label>
                  <input
                    id="category"
                    type="text"
                    className="form-control"
                    value={formData.category}
                    onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                  />
                </div>
              </div>
              <div className="row g-3">
                <div className="col-md-6">
                  <label htmlFor="quantity" className="form-label">Cantidad:</label>
                  <input
                    id="quantity"
                    type="number"
                    min="1"
                    className="form-control"
                    value={formData.quantity}
                    onChange={(e) => setFormData({ ...formData, quantity: e.target.value })}
                    required
                  />
                </div>
                <div className="col-md-6">
                  <label htmlFor="estimated-value" className="form-label">Valor Estimado:</label>
                  <input
                    id="estimated-value"
                    type="number"
                    className="form-control"
                    value={formData.estimatedValue}
                    onChange={(e) => setFormData({ ...formData, estimatedValue: e.target.value })}
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
  )
}

export default DonationForm
