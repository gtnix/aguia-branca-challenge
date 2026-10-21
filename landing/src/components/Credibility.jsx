import { stack } from '../data'

const stats = [
  { value: '509', label: 'testes automatizados', detail: '241 API · 268 app' },
  { value: '3', label: 'perfis com RBAC', detail: 'Operador · Gestor · Líder' },
  { value: '2', label: 'IAs com fallback', detail: 'IA-A e IA-C' },
  { value: '0', label: 'aprovações automáticas', detail: 'decisão sempre humana' },
]

export default function Credibility() {
  return (
    <section className="section credibility">
      <div className="container">
        <div className="cred-grid">
          <div className="cred-copy" data-reveal>
            <p className="eyebrow">
              <span className="eyebrow-line" />
              Prova técnica
            </p>
            <h2>
              509 testes.
              <br />
              3 perfis.
              <br />
              <span className="ink-accent">1 base de dados.</span>
            </h2>
            <p>
              O InovaGAB não é protótipo de tela. App Android, API Spring Boot e MongoDB rodando
              ponta a ponta, com permissões verificadas no servidor e duas IAs com fallback.
            </p>
          </div>

          <div className="cred-stats" data-reveal style={{ '--delay': '90ms' }}>
            {stats.map((stat) => (
              <div className="cred-stat" key={stat.label}>
                <strong>{stat.value}</strong>
                <span>{stat.label}</span>
                <em>{stat.detail}</em>
              </div>
            ))}
          </div>
        </div>

        <div className="stack-row" data-reveal>
          <span className="stack-label">Stack</span>
          <ul>
            {stack.map((item) => (
              <li key={item}>{item}</li>
            ))}
          </ul>
          <span className="stack-note">Sprint 2 · setembro de 2026</span>
        </div>
      </div>
    </section>
  )
}
