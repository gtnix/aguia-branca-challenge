import { useRef, useState } from 'react'
import { profiles } from '../data'
import { ArrowRight } from './Icons'

const order = ['operador', 'gestor', 'lider']

const shots = {
  operador: '/app-screens/operador-home.webp',
  gestor: '/app-screens/gestor-home.webp',
  lider: '/app-screens/lider-resumo.webp',
}

function AppShell({ src }) {
  return (
    <div className="app-frame" aria-hidden="true">
      <div className="app-screen app-screen--shot">
        <img className="app-shot" src={src} alt="" width={720} height={1560} />
      </div>
    </div>
  )
}

export default function Demo() {
  const [active, setActive] = useState('operador')
  const tabs = useRef([])
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
            <AppShell src={shots[active]} />
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
