import { Lock, Message, Shield, Sparkles } from './Icons'

const values = [
  {
    icon: Shield,
    title: 'Segurança',
    text: 'Ideias de segurança valem x1,5. É o primeiro valor do Grupo — e o primeiro critério do app.',
  },
  {
    icon: Message,
    title: 'Relacionamento',
    text: 'Reprovar exige feedback. O autor recebe o motivo e ganha 3 XP por aprender com ele.',
  },
  {
    icon: Lock,
    title: 'Integridade & Confiança',
    text: 'Cada ponto é um evento auditável. Regras públicas, sem cálculo no celular.',
  },
  {
    icon: Sparkles,
    title: 'Evolução',
    text: 'Temporadas trimestrais, ranking por divisão e ponte com o Prêmio InovaGAB.',
  },
]

export default function Values() {
  return (
    <section className="section values" id="jeito">
      <div className="container values-grid">
        <figure className="values-media" data-reveal>
          <img
            src="/brand/pessoas-operacao.webp"
            alt="Colaboradores do Grupo Águia Branca em uma base operacional"
            loading="lazy"
          />
          <figcaption>Gente que move o Grupo.</figcaption>
        </figure>

        <div className="values-copy">
          <p className="eyebrow">
            <span className="eyebrow-line" />
            Nosso jeito
          </p>
          <h2>
            Os valores do Grupo,
            <br />
            dentro do fluxo.
          </h2>
          <p className="section-lead">
            Segurança, relacionamento, integridade e evolução não são poster. São regras do produto.
          </p>

          <div className="values-list">
            {values.map((value) => {
              const Icon = value.icon
              return (
                <article className="value-item" key={value.title}>
                  <span className="value-icon">
                    <Icon size={20} />
                  </span>
                  <div>
                    <h3>{value.title}</h3>
                    <p>{value.text}</p>
                  </div>
                </article>
              )
            })}
          </div>
        </div>
      </div>
    </section>
  )
}
