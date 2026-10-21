import { pillars } from '../data'
import { Check, icons } from './Icons'

export default function Pillars() {
  return (
    <section className="section pillars-section" id="solucao">
      <div className="container">
        <div className="section-head section-head--center" data-reveal>
          <p className="eyebrow eyebrow--center">
            <span className="eyebrow-line" />
            A solução
          </p>
          <h2>
            Três movimentos.
            <br />
            Um dono para cada um.
          </h2>
          <p className="section-lead">
            Capturar, estruturar e acompanhar — o mesmo arco “da ideia ao resultado” que o Prêmio
            InovaGAB já celebra. Agora dentro de um sistema único, com permissões reais e uma base de
            dados só.
          </p>
        </div>

        <div className="pillars">
          {pillars.map((pillar, index) => {
            const Icon = icons[pillar.icon]
            return (
              <article
                className={`pillar pillar--${index + 1}`}
                key={pillar.n}
                data-reveal
                style={{ '--delay': `${index * 90}ms` }}
              >
                <div className="pillar-head">
                  <span className="pillar-number">{pillar.n}</span>
                  <span className={`pillar-icon pillar-icon--${pillar.n}`}>
                    <Icon size={22} />
                  </span>
                </div>

                <span className="pillar-role">{pillar.role}</span>
                <h3>{pillar.title}</h3>
                <p className="pillar-text">{pillar.text}</p>

                <ul className="pillar-points">
                  {pillar.points.map((point) => (
                    <li key={point}>
                      <Check size={15} />
                      {point}
                    </li>
                  ))}
                </ul>

                <span className="pillar-tag">{pillar.tag}</span>
              </article>
            )
          })}
        </div>

        <div className="pillars-strip" data-reveal>
          <span className="pillars-strip-icon">
            <Check size={16} />
          </span>
          <p>
            A mesma ideia atravessa os três papéis. O app não é três produtos colados: é um fluxo só,
            com uma base de dados e RBAC no servidor.
          </p>
        </div>
      </div>
    </section>
  )
}
