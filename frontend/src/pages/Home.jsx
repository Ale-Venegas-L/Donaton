import { useEffect } from 'react'
import { StatCard } from '../components/StatCard'
import RecentDonations from '../components/RecentDonations'
import QuickActions from '../components/QuickActions'

function Home() {
  useEffect(() => {
    document.title = 'Dashboard - Donaton'
  }, [])
  return (
    <div className="container py-4">
      <header className="text-center mb-4">
        <h1 className="fw-bold" style={{ color: 'var(--rojo)' }}>Donaton - Dashboard</h1>
        <p className="text-muted">Plataforma de Gestión de Donaciones</p>
      </header>

      <section className="row g-3 mb-4">
        <div className="col-md-3 col-sm-6">
           <StatCard title="Campañas Activas" apiPath="/campaigns/active/count" icon="bi-megaphone" color="#4b7bec" />

           <StatCard title="Total Donaciones" apiPath="/donation/count" icon="bi-heart-fill" color="#ed4b69" />

           <StatCard title="Donadores" apiPath="/donation/donors/count" icon="bi-people-fill" color="#20bf6b" />

           <StatCard title="Voluntarios" apiPath="/volunteers/count" icon="bi-person-raised-hand" color="#f7b731" />

        </div>
      </section>

      <div className="row">
        <div className="col-md-8 mb-3">
          <RecentDonations />
        </div>
        <div className="col-md-4 mb-3">
          <QuickActions />
        </div>
      </div>
    </div>
  )
}

export default Home
