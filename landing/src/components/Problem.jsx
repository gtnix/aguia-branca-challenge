import { problems } from '../data'
import { icons } from './Icons'

export default function Problem() {
  return (
    <section className="section problem" id="desafio">
      <div className="container">
        <div className="section-head section-head--split" data-reveal>
          <div>
            <p className="eyebrow">
              <span className="eyebrow-line" />
              O desafio
            </p>
            <h2>
              Não falta ideia. Falta um caminho para ela <span className="ink-accent">virar resultado</span>.
            </h2>
          </div>
          <p className="section-lead">
            Mais de 20 mil colaboradores, 25 empresas e 690 bases. A maior parte está no campo — em
            turno, longe do computador. E é justamente quem vive a operação que conhece a dor real.
          </p>
        </div>

        <div className="problem-grid">
          {problems.map((problem, index) => {
            const Icon = icons[problem.icon]
            return (
              <article
                className="problem-card"
                key={problem.n}
                data-reveal
                style={{ '--delay': `${index * 70}ms` }}
              >
                <div className="problem-card-top">
                  <span className="problem-number">{problem.n}</span>
                  <span className="problem-icon">
                    <Icon size={21} />
                  </span>
                </div>
                <h3>{problem.title}</h3>
                <p>{problem.text}</p>
              </article>
            )
          })}
        </div>

        <blockquote className="pull-quote" data-reveal>
          <p>
            “O desafio não é ter mais um app de ideias. É fechar o ciclo da estratégia ao resultado —
            com reconhecimento para quem está na ponta.”
          </p>
          <footer>Premissa do InovaGAB · Challenge FIAP 2026</footer>
        </blockquote>
      </div>
    </section>
  )
}
