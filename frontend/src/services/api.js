import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const campaigns = {
  list: () => api.get('/campaigns'),
  get: (id) => api.get(`/campaigns/${id}`),
  create: (data) => api.post('/campaigns', data),
  update: (id, data) => api.put(`/campaigns/${id}`, data),
  delete: (id) => api.delete(`/campaigns/${id}`),
}

export const donations = {
  list: () => api.get('/donation'),
  get: (id) => api.get(`/donation/${id}`),
  create: (data) => api.post('/donation', data),
  update: (id, data) => api.put(`/donation/${id}`, data),
  delete: (id) => api.delete(`/donation/${id}`),
}

export const volunteers = {
  list: () => api.get('/volunteers'),
  get: (id) => api.get(`/volunteers/${id}`),
  create: (data) => api.post('/volunteers', data),
  update: (id, data) => api.put(`/volunteers/${id}`, data),
  delete: (id) => api.delete(`/volunteers/${id}`),
  assignToCampaign: (volunteerId, data) =>
    api.post(`/volunteers/${volunteerId}/campaigns`, data),
}

export const auth = {
  login: (credentials) => api.post('/auth/login', credentials),
  logout: (refreshToken) => api.post('/auth/logout', null, { params: { refresh_token: refreshToken } }),
  refresh: (refreshToken) => api.post('/auth/refresh', null, { params: { refresh_token: refreshToken } }),
  register: (data) => api.post('/auth/register', data),
}

const safeStorage = {
  getItem: (key) => {
    try {
      return localStorage.getItem(key)
    } catch {
      return null
    }
  },
  setItem: (key, value) => {
    try {
      localStorage.setItem(key, value)
    } catch {}
  },
  removeItem: (key) => {
    try {
      localStorage.removeItem(key)
    } catch {}
  }
}

api.interceptors.request.use((config) => {
  const token = safeStorage.getItem('access_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

let isRefreshing = false
let failedQueue = []

function processQueue(error, token = null) {
  failedQueue.forEach(({ resolve, reject }) => {
    if (error) {
      reject(error)
    } else {
      resolve(token)
    }
  })
  failedQueue = []
}

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    if (error.response?.status !== 401 || originalRequest._retry) {
      return Promise.reject(error)
    }

    if (originalRequest.url?.includes('/auth/refresh')) {
      safeStorage.removeItem('access_token')
      safeStorage.removeItem('refresh_token')
      window.location.href = '/login'
      return Promise.reject(error)
    }

    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        failedQueue.push({ resolve, reject })
      }).then((token) => {
        originalRequest.headers.Authorization = `Bearer ${token}`
        return api(originalRequest)
      })
    }

    originalRequest._retry = true
    isRefreshing = true

    const refreshToken = safeStorage.getItem('refresh_token')
    if (!refreshToken) {
      isRefreshing = false
      processQueue(new Error('No refresh token'))
      safeStorage.removeItem('access_token')
      window.location.href = '/login'
      return Promise.reject(error)
    }

    try {
      const response = await auth.refresh(refreshToken)
      const data = response.data
      safeStorage.setItem('access_token', data.access_token)
      safeStorage.setItem('refresh_token', data.refresh_token)
      isRefreshing = false
      processQueue(null, data.access_token)
      originalRequest.headers.Authorization = `Bearer ${data.access_token}`
      return api(originalRequest)
    } catch (refreshError) {
      isRefreshing = false
      processQueue(refreshError)
      safeStorage.removeItem('access_token')
      safeStorage.removeItem('refresh_token')
      window.location.href = '/login'
      return Promise.reject(refreshError)
    }
  }
)

export default api
