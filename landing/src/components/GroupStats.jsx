const stats = [
  { value: '+20 mil', label: 'colaboradores' },
  { value: '+25', label: 'empresas' },
  { value: '+690', label: 'bases operacionais' },
  { value: '3', label: 'divisões de negócios' },
]

export default function GroupStats() {
  return (
    <section className="group-stats" aria-label="O Grupo Águia Branca em números">
      <div className="container group-stats-inner">
        <div className="group-stats-brand">
          <span>Grupo Águia Branca</span>
          <strong>Desde 1946 movendo o Brasil</strong>
        </div>
        {stats.map((stat) => (
          <div className="group-stat" key={stat.label}>
            <strong>{stat.value}</strong>
            <span>{stat.label}</span>
          </div>
        ))}
        <p className="group-stats-note">Passageiros · Logística · Comércio</p>
      </div>
    </section>
  )
}
