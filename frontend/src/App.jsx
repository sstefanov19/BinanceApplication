import { useState, useEffect } from 'react'
import './App.css'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

function App() {
  const [prices, setPrices] = useState({})
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    const fetchPrices = async () => {
      try {
        const response = await fetch(`${API_URL}/api/v1/prices`)
        if (!response.ok) throw new Error('Failed to fetch prices')
        const data = await response.json()
        setPrices(data)
        setError(null)
      } catch (err) {
        setError(err.message)
      } finally {
        setLoading(false)
      }
    }

    fetchPrices()
    const interval = setInterval(fetchPrices, 1000)
    return () => clearInterval(interval)
  }, [])

  const formatPrice = (price) => {
    return parseFloat(price).toLocaleString('en-US', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    })
  }

  if (loading) {
    return <div className="container"><p>Loading...</p></div>
  }

  if (error) {
    return <div className="container"><p className="error">Error: {error}</p></div>
  }

  return (
    <div className="container">
      <h1>Binance Live Prices</h1>
      <div className="prices-grid">
        {Object.entries(prices).map(([symbol, price]) => (
          <div key={symbol} className="price-card">
            <h2>{symbol}</h2>
            <p className="price">${formatPrice(price)}</p>
          </div>
        ))}
      </div>
      {Object.keys(prices).length === 0 && (
        <p>No prices available yet...</p>
      )}
    </div>
  )
}

export default App