import HeroVisual from './HeroVisual'
import { Check, Download, Play, Shield, Sparkles } from './Icons'

export default function Hero() {
  return (
    <section className="hero" id="topo">
      <div className="hero-photo" aria-hidden="true">
        <picture>
          <source
            srcSet="/brand/hero-operacao-1200.webp 1200w, /brand/hero-operacao.webp 1920w"
            sizes="100vw"
            type="image/webp"
          />
          <img src="/brand/hero-operacao.webp" alt="" />
        </picture>
        <span className="hero-overlay" />
      </div>

      <div className="container hero-grid">
        <div className="hero-copy">
          <p className="eyebrow eyebrow--light hero-eyebrow">
            <span className="eyebrow-dot" />
            Challenge FIAP 2026 · Grupo Águia Branca
          </p>

          <h1>
            Da voz no pátio ao <span className="hero-accent">resultado</span> no dashboard.
          </h1>

          <p className="hero-lead">
            O InovaGAB digitaliza o Prêmio InovaGAB. O operador captura a dor, o gestor decide com
            apoio de IA e a liderança enxerga o retorno — em um único app, do campo à diretoria.
          </p>

          <div className="hero-actions">
            <a className="btn btn--light" href="/InovaGAB.apk" download>
              <Download size={19} />
              Baixar o APK
            </a>
            <a className="btn btn--ghost" href="#demonstracao">
              <span className="btn-play">
                <Play size={14} />
              </span>
              Ver a demonstração
            </a>
          </div>

          <p className="hero-note">Android 8+ · build de demonstração</p>

          <ul className="hero-proof">
            <li>
              <Check size={16} />
              3 perfis com permissões reais
            </li>
            <li>
              <Sparkles size={16} />
              2 IAs com decisão humana
            </li>
            <li>
              <Shield size={16} />
              509 testes automatizados
            </li>
          </ul>
        </div>

        <div className="hero-visual">
          <HeroVisual />
        </div>
      </div>
    </section>
  )
}
