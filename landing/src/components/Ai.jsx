import { flagship } from '../data'
import { Shield, Sparkles, UserCheck } from './Icons'

const criteria = [
  { label: 'Alinhamento', value: 5 },
  { label: 'Impacto', value: 4 },
  { label: 'Viabilidade', value: 3 },
  { label: 'Clareza', value: 5 },
  { label: 'Originalidade', value: 4 },
]

export default function Ai() {
  return (
    <section className="section ai" id="ia">
      <div className="container">
        <div className="section-head section-head--split" data-reveal>
          <div>
            <p className="eyebrow">
              <span className="eyebrow-line" />
              Inteligência com governança
            </p>
            <h2>
              A IA recomenda.
              <br />
              <span className="ink-accent">A decisão é humana.</span>
            </h2>
          </div>
          <p className="section-lead">
            Duas IAs apoiam duas decisões. Nenhuma aprova, reprova ou promete resultado sozinha. E se
            o modelo externo cair, o fluxo continua por regra local.
          </p>
        </div>

        <div className="ai-grid">
          <article className="ai-card ai-card--a" data-reveal>
            <header className="ai-card-head">
              <span className="ai-badge">IA-A</span>
              <span className="ai-kicker">Priorização de ideias</span>
            </header>
            <h3>Score de priorização, com o porquê.</h3>
            <p>
              A IA lê a ideia junto com a orientação estratégica e devolve nota, critérios e
              recomendação. O gestor continua dono da fila.
            </p>

            <div className="ai-reading">
              <div className="ai-reading-head">
                <div>
                  <span>Caso da demo</span>
                  <strong>{flagship.title}</strong>
                </div>
                <div className="ai-score-large">
                  <strong>{flagship.score}</strong>
                  <span>/100 · {flagship.band}</span>
                </div>
              </div>

              <ul className="ai-criteria">
                {criteria.map((criterion) => (
                  <li key={criterion.label}>
                    <span>{criterion.label}</span>
                    <div className="ai-criteria-bar">
                      <i style={{ width: `${criterion.value * 20}%` }} />
                    </div>
                    <b>{criterion.value}/5</b>
                  </li>
                ))}
              </ul>

              <p className="ai-reading-note">
                <b>Recomendação:</b> {flagship.rationale}
              </p>
            </div>

            <footer className="ai-card-foot">
              <UserCheck size={15} />
              Aprovação exclusivamente humana
            </footer>
          </article>

          <article className="ai-card ai-card--c" data-reveal style={{ '--delay': '90ms' }}>
            <header className="ai-card-head">
              <span className="ai-badge ai-badge--cyan">IA-C</span>
              <span className="ai-kicker">Resumo executivo</span>
            </header>
            <h3>Da carteira para a diretoria.</h3>
            <p>
              Transforma o portfólio em linguagem de diretoria: o que aconteceu, a evidência em
              números e a ação recomendada.
            </p>

            <div className="ai-report-demo">
              <div className="ai-report-demo-head">
                <span className="report-live">gerado agora</span>
                <span>2026-T4</span>
              </div>
              <p>
                “Portfólio com 1 projeto atrasado. O portfólio conta com 11 ideias e ROI de 193% a.a.
                Há 1 projeto fora do prazo exigindo atenção.”
              </p>
              <div className="ai-report-demo-meta">
                <span>Ação recomendada</span>
                <strong>Revisar o projeto atrasado e definir plano de recuperação</strong>
              </div>
            </div>

            <div className="ai-evidence">
              <span>ROI de 193% a.a.</span>
              <span>1 projeto atrasado</span>
              <span>3 no prazo</span>
            </div>

            <footer className="ai-card-foot">
              <Shield size={15} />
              Fallback local quando o modelo externo falha
            </footer>
          </article>
        </div>

        <div className="ai-strip" data-reveal>
          <span className="ai-strip-icon">
            <Sparkles size={18} />
          </span>
          <p>
            <strong>“A IA ranqueia; a Ana decide.”</strong> A nota é um insumo. A aprovação, a
            reprovação e o feedback continuam no gestor — e ficam registrados.
          </p>
        </div>
      </div>
    </section>
  )
}
