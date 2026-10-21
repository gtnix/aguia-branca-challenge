import { fieldFeatures } from '../data'
import { icons } from './Icons'

export default function Field() {
  return (
    <section className="section field">
      <div className="container">
        <div className="section-head section-head--split" data-reveal>
          <div>
            <p className="eyebrow">
              <span className="eyebrow-line" />
              Feito para o campo
            </p>
            <h2>
              Para quem está no pátio,
              <br />
              não no escritório.
            </h2>
          </div>
          <p className="section-lead">
            Turnos, pouco tempo, celular na mão. O app foi desenhado para o operador que conhece a
            dor — não para um comitê que lê planilha.
          </p>
        </div>

        <div className="field-grid">
          {fieldFeatures.map((feature, index) => {
            const Icon = icons[feature.icon]
            return (
              <article
                className="field-card"
                key={feature.title}
                data-reveal
                style={{ '--delay': `${index * 70}ms` }}
              >
                <span className="field-icon">
                  <Icon size={21} />
                </span>
                <h3>{feature.title}</h3>
                <p>{feature.text}</p>
              </article>
            )
          })}
        </div>

        <div className="field-quote" data-reveal>
          <span className="field-quote-mark">“</span>
          <p>
            Quem conhece a dor é quem tem menos tempo e canal para falar. O InovaGAB existe para
            inverter essa ordem.
          </p>
        </div>
      </div>
    </section>
  )
}
