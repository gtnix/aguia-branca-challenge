import { ArrowRight, Wing } from './Icons'

export default function HeroVisual() {
  return (
    <div className="hero-stage" aria-hidden="true">
      <div className="hero-dash">
        <img
          className="hero-dash-shot"
          src="/app-screens/lider-resumo.webp"
          alt=""
          width={720}
          height={1560}
        />
      </div>

      <div className="phone">
        <div className="phone-screen phone-screen--shot">
          <img
            className="phone-shot"
            src="/app-screens/operador-home.webp"
            alt=""
            width={720}
            height={1560}
          />
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
