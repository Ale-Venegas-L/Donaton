import { useState, useEffect } from 'react';
import { donations } from '../services/api';
import { formatDateLong, formatCurrency } from '../utils/format';

export function RecentDonations() {
  const [recentDonations, setRecentDonations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let cancelled = false;
    const fetchDonations = async () => {
      try {
        const res = await donations.list();
        if (cancelled) return;
        setRecentDonations(res.data.slice(0, 5));
      } catch (e) {
        if (cancelled) return;
        setError(e);
        console.error('Error loading recent donations:', e);
      } finally {
        if (!cancelled) setLoading(false);
      }
    };
    fetchDonations();
    return () => { cancelled = true; };
  }, []);

  if (loading) return <div className="text-center py-3 text-muted">Cargando donaciones...</div>;

  if (error) return <div className="text-center py-3 text-danger">Error al cargar donaciones</div>;

  return (
    <div className="card shadow-sm">
      <div className="card-body">
        <h5 className="card-title mb-3">Donaciones Recientes</h5>
        {recentDonations.length === 0 ? (
          <p className="text-muted text-center py-3 mb-0">No hay donaciones recientes</p>
        ) : (
          <div className="table-responsive">
            <table className="table align-middle mb-0">
              <thead className="table-light">
                <tr>
                  <th>Donante</th>
                  <th>Monto/Objeto</th>
                  <th>Fecha</th>
                  <th>Tipo</th>
                </tr>
              </thead>
              <tbody>
                {recentDonations.map(donation => (
                  <tr key={donation.id}>
                    <td>{donation.donorName || '-'}</td>
                    <td>
                      {donation.type === 'MONETARY'
                        ? formatCurrency(donation.amount)
                        : [donation.quantity, donation.objectName || donation.description || '-'].filter(Boolean).join(' ')}
                    </td>
                    <td>{formatDateLong(donation.registrationDate)}</td>
                    <td>
                      <span className={`status status-${donation.type === 'MONETARY' ? 'active' : 'pending'}`}>
                        {donation.type === 'MONETARY' ? 'Monetaria' : 'Objeto'}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

export default RecentDonations;
