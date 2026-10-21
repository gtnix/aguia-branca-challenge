import {
  ArrowRight,
  Bell,
  Bulb,
  Chart,
  Home,
  Mic,
  Trophy,
  User,
  Users,
  Wing,
} from './Icons'

export default function HeroVisual() {
  return (
    <div className="hero-stage" aria-hidden="true">
      <div className="hero-dash">
        <div className="hero-dash-head">
          <div>
            <span>Resumo Executivo</span>
            <strong>Visão consolidada do portfólio</strong>
          </div>
          <span className="mini-chip">2026-T4</span>
        </div>

        <div className="hero-kpis">
          <div>
            <span>ROI a.a.</span>
            <strong>192,7%</strong>
          </div>
          <div>
            <span>Investido</span>
            <strong>R$ 4,55 mi</strong>
          </div>
          <div>
            <span>Retorno mensal</span>
            <strong>
              R$ 1,11 mi<em>/mês</em>
            </strong>
          </div>
        </div>

        <div className="hero-funnel">
          <div className="hero-funnel-label">
            <span>Funil</span>
            <b>11 → 3 → 3</b>
          </div>
          <div className="hero-funnel-bars">
            <i style={{ width: '100%' }} />
            <i style={{ width: '38%' }} />
            <i style={{ width: '38%' }} />
          </div>
          <div className="hero-funnel-legend">
            <span>Ideias</span>
            <span>Aprovadas</span>
            <span>Em projeto</span>
          </div>
        </div>

        <div className="hero-spark">
          <svg viewBox="0 0 220 54" preserveAspectRatio="none">
            <defs>
              <linearGradient id="heroArea" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stopColor="#008FFF" stopOpacity=".28" />
                <stop offset="100%" stopColor="#008FFF" stopOpacity="0" />
              </linearGradient>
            </defs>
            <path
              d="M0 44c18 0 22-10 38-12s22 10 39 5 20-15 37-16 25 8 42 2 27-14 44-17 18 2 20 3v37H0Z"
              fill="url(#heroArea)"
            />
            <path
              d="M0 44c18 0 22-10 38-12s22 10 39 5 20-15 37-16 25 8 42 2 27-14 44-17 18 2 20 3"
              fill="none"
              stroke="#005FC5"
              strokeWidth="2"
              strokeLinecap="round"
            />
          </svg>
        </div>
      </div>

      <div className="phone">
        <div className="phone-notch" />
        <div className="phone-screen">
          <div className="phone-status">
            <span>9:41</span>
            <span className="phone-status-icons">
              <i className="sig" />
              <i className="sig" />
              <i className="batt" />
            </span>
          </div>

          <div className="phone-app">
            <header className="phead">
              <span className="avatar">PS</span>
              <div className="phead-text">
                <span>Bom dia,</span>
                <strong>Pedro Santos</strong>
              </div>
              <Bell size={15} />
            </header>

            <div className="phero">
              <span className="phero-kicker">Sua experiência no dia a dia</span>
              <strong>Você viu algo que pode melhorar?</strong>
              <span className="phero-cta">
                <span className="pmic">
                  <Mic size={12} />
                </span>
                Compartilhar Ideia
                <ArrowRight size={12} />
              </span>
            </div>

            <span className="plabel">Orientações estratégicas</span>
            <div className="porient">
              <div className="porient-chips">
                <span className="pchip pchip--safety">Eficiência</span>
                <span className="pchip pchip--bonus">Circuito 2026</span>
              </div>
              <strong>Eficiência do pátio</strong>
              <p>Disponibilidade da frota e menos espera na doca.</p>
            </div>

            <span className="plabel">Minhas ideias</span>
            <div className="pidea">
              <div className="pidea-head">
                <span className="pstatus">Aguardando avaliação</span>
                <span className="psupports">
                  <Users size={11} /> 12
                </span>
              </div>
              <p className="pidea-title">Docas inteligentes no CD Serra</p>
              <p className="pidea-desc">
                Carreta espera até 3h por uma doca. Com janela agendada, o pátio gira mais.
              </p>
            </div>

            <div className="plevel">
              <div className="plevel-top">
                <span>Em Ascensão</span>
                <b>69 / 150 XP</b>
              </div>
              <div className="pbar">
                <i style={{ width: '46%' }} />
              </div>
              <div className="plevel-meta">
                <span>
                  <Trophy size={12} /> 3º na Logística
                </span>
                <span>
                  <Wing size={12} /> 0 Asas
                </span>
              </div>
            </div>
          </div>

          <nav className="phone-nav">
            <span className="is-active">
              <Home size={14} /> Home
            </span>
            <span>
              <Bulb size={14} /> Ideias
            </span>
            <span>
              <Chart size={14} /> Projetos
            </span>
            <span>
              <User size={14} /> Perfil
            </span>
          </nav>
        </div>
      </div>

      <div className="float-card float-card--score">
        <span className="score-badge">IA</span>
        <div>
          <span>Score de Priorização</span>
          <strong>83/100 · ALTA</strong>
        </div>
      </div>

      <div className="float-card float-card--xp">
        <span className="xp-wing">
          <Wing size={18} />
        </span>
        <div>
          <strong>+80 XP</strong>
          <span>+100 Asas ao concluir</span>
        </div>
        <ArrowRight size={14} className="xp-arrow" />
      </div>
    </div>
  )
}
