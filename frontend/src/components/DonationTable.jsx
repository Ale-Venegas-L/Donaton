import { formatDate, formatDonationAmount } from '../utils/format'

function DonationTable({ donations, campaigns, onEdit, onDelete, deletingId }) {
  return (
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
          {donations.map((donation) => (
            <tr key={donation.id}>
              <td>{donation.id}</td>
              <td>{donation.donorName}</td>
              <td>
                <span className={`status ${donation.type === 'MONETARY' ? 'status-active' : 'status-pending'}`}>
                  {donation.type === 'MONETARY' ? 'Monetaria' : 'Objeto'}
                </span>
              </td>
              <td>{formatDonationAmount(donation)}</td>
              <td>{donation.campaign?.nombre || campaigns.find(c => c.id === donation.campaignId)?.nombre || '-'}</td>
              <td>{formatDate(donation.registrationDate)}</td>
              <td>
                <button 
                  type="button" 
                  className="btn btn-sm btn-success me-1" 
                  onClick={() => onEdit(donation)} 
                  title="Editar" 
                  aria-label="Editar donación"
                >
                  <i className="bi bi-pencil"></i>
                </button>
                <button 
                  type="button" 
                  className="btn btn-sm btn-outline-danger" 
                  onClick={() => onDelete(donation.id)} 
                  disabled={deletingId === donation.id} 
                  title="Eliminar" 
                  aria-label="Eliminar donación"
                >
                  {deletingId === donation.id ? '...' : <i className="bi bi-trash"></i>}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default DonationTable
