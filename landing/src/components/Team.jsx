import { team } from '../data'

export default function Team() {
  return (
    <section className="section team" id="equipe">
      <div className="container team-grid">
        <div className="team-head" data-reveal>
          <p className="eyebrow">
            <span className="eyebrow-line" />
            Quem construiu
          </p>
          <h2>
            Dois alunos.
            <br />
            Um produto inteiro.
          </h2>
          <p>
            Challenge FIAP 2026 · Grupo Águia Branca · Turma 2TDSA. Da pesquisa de marca ao APK, do
            MongoDB ao relatório executivo.
          </p>
          <div className="team-brand">
            <img src="/brand/grupo-aguia-branca.svg" alt="Grupo Águia Branca" width="132" height="34" />
            <span>Projeto acadêmico · uso autorizado da marca</span>
          </div>
        </div>

        <div className="team-cards">
          {team.map((member, index) => (
            <article
              className={`team-card team-card--${member.accent}`}
              key={member.name}
              data-reveal
              style={{ '--delay': `${index * 90}ms` }}
            >
              <span className="team-avatar">{member.initials}</span>
              <div>
                <strong>{member.name}</strong>
                <span>{member.rm}</span>
                <em>Challenge FIAP 2026 · 2TDSA</em>
              </div>
            </article>
          ))}
        </div>
      </div>
    </section>
  )
}
