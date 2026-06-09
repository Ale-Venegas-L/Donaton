export function formatDate(dateStr) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('es-CL')
}

export function formatDateLong(dateStr) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('es-CL', {
    day: 'numeric',
    month: 'short',
    year: 'numeric'
  })
}

const currencyFormatter = new Intl.NumberFormat('es-CL', {
  style: 'currency',
  currency: 'CLP'
})

export function formatCurrency(amount) {
  return currencyFormatter.format(amount)
}

export function formatDonationAmount(donation) {
  if (donation.type === 'MONETARY') {
    if (donation.currency === 'CLP') {
      return formatCurrency(donation.amount)
    }
    return `${donation.currency || 'CLP'} ${donation.amount?.toLocaleString('es-CL') || '0'}`
  }
  const qty = donation.quantity ?? ''
  const name = donation.objectName || donation.description || 'Objeto'
  return `${qty} ${name}`.trim()
}
