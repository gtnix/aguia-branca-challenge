import { useRef, useState } from 'react'
import { flagship, profiles } from '../data'
import {
  ArrowRight,
  Bell,
  Bulb,
  Chart,
  Home,
  Lock,
  Mic,
  Shield,
  Sparkles,
  Trophy,
  User,
  UserCheck,
  Users,
  Wing,
} from './Icons'

const order = ['operador', 'gestor', 'lider']

const nav = [
  { icon: Home, label: 'Home' },
  { icon: Bulb, label: 'Ideias' },
  { icon: Chart, label: 'Projetos' },
  { icon: User, label: 'Perfil' },
]

function AppShell({ children, user }) {
  return (
    <div className="app-frame" aria-hidden="true">
      <div className="app-notch" />
      <div className="app-screen">
        <div className="app-status">
          <span>9:41</span>
          <span className="phone-status-icons">
            <i className="sig" />
            <i className="sig" />
            <i className="batt" />
          </span>
        </div>
        <div className="app-scroll">
          <div className="app-head">
            <span className={`app-avatar app-avatar--${user.tone}`}>{user.initials}</span>
            <div className="app-user">
              <span>{user.greeting}</span>
              <strong>{user.name}</strong>
              <em>{user.context}</em>
            </div>
            <span className="app-bell">
              <Bell size={15} />
              <i />
            </span>
          </div>
          {children}
        </div>
        <nav className="app-nav">
          {nav.map((item, index) => {
            const Icon = item.icon
            return (
              <span key={item.label} className={index === 0 ? 'is-active' : ''}>
                <Icon size={15} />
                {item.label}
              </span>
            )
          })}
        </nav>
      </div>
    </div>
  )
}

function OperadorScreen() {
  return (
    <AppShell
      user={{
        initials: 'PS',
        greeting: 'Bom dia,',
        name: 'Pedro Santos',
        context: 'Operador · Logística',
        tone: 'blue',
      }}
    >
      <div className="app-hero">
        <span className="app-hero-kicker">Sua experiência no dia a dia</span>
        <strong>Você viu algo que pode melhorar?</strong>
        <p>Sua experiência no dia a dia é valiosa. Compartilhe suas ideias e ajude a transformar a empresa.</p>
        <span className="app-primary app-primary--hero">
          <Mic size={14} />
          Compartilhar Ideia
          <ArrowRight size={14} />
        </span>
      </div>

      <span className="app-label">Orientações estratégicas</span>
      <div className="orientation">
        <div className="orientation-top">
          <span className="tag tag--safety">Eficiência</span>
          <span className="tag tag--bonus">Circuito 2026</span>
        </div>
        <strong>{flagship.orientation}</strong>
        <p>{flagship.orientationDetail}</p>
      </div>

      <span className="app-label">Minhas ideias</span>
      <div className="idea-detail">
        <div className="idea-detail-head">
          <strong>{flagship.title}</strong>
          <span className="status-pill">{flagship.status}</span>
        </div>
        <p className="idea-detail-desc">{flagship.shortDescription}</p>
        <div className="idea-meta">
          <span>
            Área <b>{flagship.area}</b>
          </span>
          <span>
            Impacto <b>{flagship.impact}/5</b>
          </span>
          <span>
            Esforço <b>{flagship.effort}/5</b>
          </span>
        </div>
        <div className="idea-supports-line">
          <Users size={13} />
          {flagship.supportsLine}
        </div>
      </div>

      <div className="level">
        <div className="level-top">
          <span>Em Ascensão</span>
          <b>69 / 150 XP</b>
        </div>
        <div className="pbar">
          <i style={{ width: '46%' }} />
        </div>
        <div className="level-meta">
          <span>
            <Trophy size={12} /> 3º na Logística
          </span>
          <span>
            <Wing size={12} /> 0 Asas
          </span>
        </div>
      </div>
    </AppShell>
  )
}

function Score({ value }) {
  return (
    <span className="score" style={{ '--score': `${value * 3.6}deg` }}>
      <span>{value}</span>
    </span>
  )
}

function GestorScreen() {
  return (
    <AppShell
      user={{
        initials: 'AO',
        greeting: 'Boa tarde,',
        name: 'Ana Oliveira',
        context: 'Gestora · CD Serra',
        tone: 'purple',
      }}
    >
      <div className="queue-top">
        <div>
          <span className="app-label">Fila do gestor</span>
          <strong>4 ideias aguardando avaliação</strong>
        </div>
        <span className="ai-pill">
          <Sparkles size={13} /> Pontuar com IA
        </span>
      </div>
      <div className="ai-caption">
        <i /> Recomendação da IA · atualizada agora
      </div>

      <div className="queue-card queue-card--lead">
        <div className="queue-card-head">
          <div className="queue-title">
            <strong>{flagship.title}</strong>
            <span>
              {flagship.author} · {flagship.area} · {flagship.supports} apoios
            </span>
          </div>
          <div className="score-wrap">
            <Score value={flagship.score} />
            <span className="score-band">
              {flagship.score}/100 · {flagship.band}
            </span>
          </div>
        </div>
        <p className="queue-why">
          <b>Recomendação da IA:</b> {flagship.rationale}
        </p>
        <div className="queue-meta">
          <span>
            Impacto <b>{flagship.impact}</b>
          </span>
          <span>
            Esforço <b>{flagship.effort}</b>
          </span>
          <span className="tag tag--safety">Eficiência</span>
        </div>
        <div className="queue-actions">
          <span className="mini-btn mini-btn--approve">Aprovar</span>
          <span className="mini-btn">Reprovar</span>
          <span className="human">
            <UserCheck size={13} /> decisão humana
          </span>
        </div>
      </div>

      <div className="queue-card">
        <div className="queue-card-head">
          <div className="queue-title">
            <strong>Ponto cego em manobras de carreta</strong>
            <span>Joana Ribeiro · CD Serra · há 7 dias</span>
          </div>
        </div>
      </div>

      <div className="queue-card">
        <div className="queue-card-head">
          <div className="queue-title">
            <strong>Estoque inteligente de peças críticas</strong>
            <span>Rafael Costa · VIX · Jardim Limoeiro · há 9 dias</span>
          </div>
        </div>
      </div>

      <div className="feedback-note">
        <Lock size={13} /> Feedback obrigatório para não priorizar
      </div>

      <div className="chart-card chart-card--pipeline">
        <div className="chart-card-head">
          <span>Pipeline</span>
          <b>11 ideias</b>
        </div>
        <div className="exec-stats">
          <div>
            <span>Ideias</span>
            <strong>11</strong>
          </div>
          <div>
            <span>Aprovadas</span>
            <strong>3</strong>
          </div>
          <div>
            <span>Em projeto</span>
            <strong>3</strong>
          </div>
        </div>
      </div>
    </AppShell>
  )
}

function LiderScreen() {
  return (
    <AppShell
      user={{
        initials: 'MS',
        greeting: 'Boa tarde,',
        name: 'Marcos Silva',
        context: 'Líder · Holding',
        tone: 'navy',
      }}
    >
      <div className="exec-head">
        <div>
          <span className="app-label">Temporada 2026-T4</span>
          <strong>Resumo Executivo</strong>
          <em>Visão consolidada do portfólio</em>
        </div>
        <span className="live-chip">
          <i /> ao vivo
        </span>
      </div>

      <div className="kpis">
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
          <strong>R$ 1,11 mi</strong>
        </div>
        <div>
          <span>Lucro a.a.</span>
          <strong>R$ 8,77 mi</strong>
        </div>
      </div>

      <div className="chart-card">
        <div className="chart-card-head">
          <span>Funil</span>
          <b>11 → 3 → 3</b>
        </div>
        <div className="funnel">
          <div className="funnel-row">
            <span>Ideias</span>
            <div className="funnel-track">
              <i style={{ width: '100%' }} />
              <b>11</b>
            </div>
          </div>
          <div className="funnel-row">
            <span>Aprovadas</span>
            <div className="funnel-track">
              <i style={{ width: '38%' }} className="funnel-track--mid" />
              <b>3</b>
            </div>
          </div>
          <div className="funnel-row">
            <span>Em projeto</span>
            <div className="funnel-track">
              <i style={{ width: '38%' }} className="funnel-track--end" />
              <b>3</b>
            </div>
          </div>
        </div>
      </div>

      <div className="chart-card">
        <div className="chart-card-head">
          <span>Execução da carteira</span>
          <b>4 projetos</b>
        </div>
        <div className="exec-stats">
          <div>
            <span>No prazo</span>
            <strong>3</strong>
          </div>
          <div>
            <span>Atrasados</span>
            <strong>1</strong>
          </div>
          <div>
            <span>Progresso</span>
            <strong>58,8%</strong>
          </div>
        </div>
      </div>

      <div className="ai-report">
        <div className="ai-report-head">
          <span className="score-badge">IA-C</span>
          <strong>Resumo Executivo</strong>
          <span className="report-live">gerado agora</span>
        </div>
        <p>
          Portfólio com 1 projeto atrasado. O portfólio conta com 11 ideias e ROI de 193% a.a. Há 1
          projeto fora do prazo exigindo atenção.
        </p>
        <span className="ai-report-foot">
          <Shield size={12} /> fallback auditável
        </span>
      </div>
    </AppShell>
  )
}

const screens = {
  operador: OperadorScreen,
  gestor: GestorScreen,
  lider: LiderScreen,
}

export default function Demo() {
  const [active, setActive] = useState('operador')
  const tabs = useRef([])
  const Screen = screens[active]
  const profile = profiles[active]

  const onKeyDown = (event, index) => {
    const keys = ['ArrowRight', 'ArrowDown', 'ArrowLeft', 'ArrowUp', 'Home', 'End']
    if (!keys.includes(event.key)) return
    event.preventDefault()

    let next = index
    if (event.key === 'ArrowRight' || event.key === 'ArrowDown') {
      next = (index + 1) % order.length
    } else if (event.key === 'ArrowLeft' || event.key === 'ArrowUp') {
      next = (index - 1 + order.length) % order.length
    } else if (event.key === 'Home') {
      next = 0
    } else if (event.key === 'End') {
      next = order.length - 1
    }

    setActive(order[next])
    tabs.current[next]?.focus()
  }

  return (
    <section className="section demo" id="demonstracao">
      <div className="demo-bg" aria-hidden="true">
        <span className="demo-word">Inovação</span>
      </div>

      <div className="container">
        <div className="section-head section-head--split section-head--dark" data-reveal>
          <div>
            <p className="eyebrow eyebrow--light">
              <span className="eyebrow-line" />
              Demonstração
            </p>
            <h2>
              Um fluxo,
              <br />
              três perspectivas.
            </h2>
          </div>
          <p className="section-lead">
            A mesma ideia percorre o app inteiro. Do pátio do CD Serra à mesa da Ana, até o resumo
            que o Marcos leva para a diretoria.
          </p>
        </div>

        <div className="demo-grid-layout">
          <div className="demo-tabs-wrap" data-reveal>
            <div
              className="demo-tabs"
              role="tablist"
              aria-label="Perfis do InovaGAB"
              aria-orientation="vertical"
            >
              {order.map((key, index) => {
                const item = profiles[key]
                const selected = active === key
                return (
                  <button
                    key={key}
                    ref={(el) => {
                      tabs.current[index] = el
                    }}
                    type="button"
                    role="tab"
                    id={`tab-${key}`}
                    aria-selected={selected}
                    aria-controls={`panel-${key}`}
                    tabIndex={selected ? 0 : -1}
                    className={`demo-tab${selected ? ' is-active' : ''}`}
                    onClick={() => setActive(key)}
                    onKeyDown={(event) => onKeyDown(event, index)}
                  >
                    <span className="demo-tab-index">0{index + 1}</span>
                    <span className="demo-tab-body">
                      <strong>{item.role}</strong>
                      <span>{item.name}</span>
                      <em>{item.lead}</em>
                    </span>
                    <ArrowRight size={17} className="demo-tab-arrow" />
                  </button>
                )
              })}
            </div>

            <div className="demo-bullets" key={active}>
              <span className="demo-bullets-title">O que este perfil vê</span>
              <ul>
                {profile.bullets.map((bullet) => (
                  <li key={bullet}>
                    <span className="demo-bullet-dot" />
                    {bullet}
                  </li>
                ))}
              </ul>
              <p className="demo-source">Dados da demonstração · seed real da Sprint 2</p>
            </div>
          </div>

          <div
            className="demo-panel"
            role="tabpanel"
            id={`panel-${active}`}
            aria-labelledby={`tab-${active}`}
            tabIndex={0}
            key={active}
          >
            <Screen />
            <div className="demo-panel-caption">
              <span className="demo-caption-dot" />
              {profile.role} · {profile.context}
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}
