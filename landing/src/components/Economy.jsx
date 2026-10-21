import { ledger, rewards } from '../data'
import { Lock, Shield, Wing, icons } from './Icons'

export default function Economy() {
  return (
    <section className="section economy" id="economia">
      <div className="container">
        <div className="section-head section-head--split" data-reveal>
          <div>
            <p className="eyebrow">
              <span className="eyebrow-line" />
              Economia de Inovação
            </p>
            <h2>
              Reconhecimento que
              <br />
              chega ao campo.
            </h2>
          </div>
          <p className="section-lead">
            XP é reputação. Asas são moeda de verdade, trocada por ativos do próprio grupo. Quem gera
            resultado ganha reconhecimento — e os melhores de cada divisão chegam ao Imova-se.
          </p>
        </div>

        <div className="economy-grid">
          <div className="ledger-card" data-reveal>
            <header className="ledger-head">
              <span className="ledger-avatar">PS</span>
              <div>
                <strong>Extrato do Pedro</strong>
                <span>Temporada 2026-T4 · auditável</span>
              </div>
              <span className="ledger-level">Em Ascensão</span>
            </header>

            <div className="ledger-balance">
              <div>
                <span>XP na temporada</span>
                <strong>69</strong>
              </div>
              <div>
                <span>Saldo em Asas</span>
                <strong>
                  0 <Wing size={16} />
                </strong>
              </div>
            </div>

            <div className="ledger-table">
              <div className="ledger-row ledger-row--head">
                <span>Evento</span>
                <span>XP</span>
                <span>Asas</span>
              </div>
              {ledger.map((row) => (
                <div className="ledger-row" key={row.label}>
                  <span>
                    <strong>{row.label}</strong>
                    <em>{row.detail}</em>
                  </span>
                  <b>{row.xp}</b>
                  <b className="ledger-wings">{row.wings}</b>
                </div>
              ))}
            </div>

            <div className="ledger-future">
              <div>
                <span>Próximo marco</span>
                <strong>Projeto concluído</strong>
              </div>
              <b>+80 XP · +100 Asas</b>
            </div>

            <footer className="ledger-foot">
              <Lock size={14} />
              Cada ponto é um evento com motivo e data. Nada é calculado no celular.
            </footer>
          </div>

          <div className="rewards" data-reveal style={{ '--delay': '90ms' }}>
            <div className="rewards-head">
              <span>Loja de recompensas</span>
              <span>ativos do próprio grupo</span>
            </div>

            <div className="rewards-grid">
              {rewards.map((reward) => {
                const Icon = icons[reward.icon]
                return (
                  <div className={`reward reward--${reward.accent}`} key={reward.name}>
                    <span className="reward-icon">
                      <Icon size={20} />
                    </span>
                    <strong>{reward.name}</strong>
                    <span className="reward-partner">{reward.partner}</span>
                    <span className="reward-cost">
                      <Wing size={14} /> {reward.cost} Asas
                    </span>
                  </div>
                )
              })}
            </div>

            <div className="rewards-note">
              <Shield size={14} />
              Resgate validado no servidor: saldo, estoque e nível.
            </div>
          </div>
        </div>

        <div className="economy-facts" data-reveal>
          <div>
            <span className="economy-fact-n">01</span>
            <p>Os 3 primeiros de cada divisão são indicados ao Imova-se ao fim da temporada.</p>
          </div>
          <div>
            <span className="economy-fact-n">02</span>
            <p>
              Segurança e campanha patrocinada valem <b>x1,5</b> cada — e acumulam <b>x2,25</b>.
            </p>
          </div>
          <div>
            <span className="economy-fact-n">03</span>
            <p>Gincana por equipe e base, no espírito da AMIGAB desde 1998.</p>
          </div>
        </div>
      </div>
    </section>
  )
}
