const cards = [
  {
    img: '/brand/premio-imovase.webp',
    alt: 'Colaboradores do Grupo Águia Branca em uma operação de campo',
    tag: 'Imova-se',
    title: 'Ideias que viram melhoria',
    text: 'Projetos internos, intraempreendedorismo e protagonismo de quem vive a operação.',
  },
  {
    img: '/brand/premio-demoday.webp',
    alt: 'Ônibus da Viação Águia Branca em operação',
    tag: 'Demoday',
    title: 'Soluções que já entregam',
    text: 'O que saiu do papel e virou resultado chega ao palco — como o Mais Carga, Mais Valor, que passou de 4 para 5 veículos por carreta.',
  },
  {
    img: '/brand/premio-oscar.webp',
    alt: 'Troféus do Prêmio Inovação do Grupo Águia Branca',
    tag: 'Oscar da Inovação',
    title: 'Reconhecimento que marca',
    text: 'Os principais projetos das divisões e da holding, com protagonismo de quem fez acontecer.',
  },
  {
    img: '/brand/premio-gab.webp',
    alt: 'Colaboradores do Grupo Águia Branca em uma imersão',
    tag: 'GAB Experience',
    title: 'Imersão para quem vence',
    text: 'Viagens a centros globais de inovação para os vencedores do Prêmio InovaGAB.',
  },
]

export default function Premio() {
  return (
    <section className="section premio" id="premio">
      <div className="container">
        <div className="section-head section-head--split" data-reveal>
          <div>
            <p className="eyebrow">
              <span className="eyebrow-line" />
              Prêmio InovaGAB
            </p>
            <h2>
              O programa já existe.
              <br />
              O app é a camada digital dele.
            </h2>
          </div>
          <p className="section-lead">
            Imova-se, Demoday, Oscar da Inovação e GAB Experience. O InovaGAB não cria um programa
            paralelo: conecta a ideia do campo a cada etapa do Prêmio.
          </p>
        </div>

        <div className="premio-grid">
          {cards.map((card, index) => (
            <article
              className="premio-card"
              key={card.tag}
              data-reveal
              style={{ '--delay': `${index * 70}ms` }}
            >
              <img src={card.img} alt={card.alt} loading="lazy" />
              <span className="premio-shade" aria-hidden="true" />
              <div className="premio-body">
                <span className="premio-tag">{card.tag}</span>
                <h3>{card.title}</h3>
                <p>{card.text}</p>
              </div>
            </article>
          ))}
        </div>

        <p className="premio-proof">
          No Circuito Inovação 2025, a VIXPar mobilizou <strong>734 ideias</strong> novas e
          implantadas. O InovaGAB é a camada que conecta essa energia ao Prêmio.
        </p>
        <p className="premio-credit">
          Fotografias: Grupo Águia Branca · uso autorizado para o Challenge FIAP 2026.
        </p>
      </div>
    </section>
  )
}
