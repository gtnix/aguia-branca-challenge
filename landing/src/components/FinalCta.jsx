import { ArrowUpRight, Download, Play } from './Icons'

export default function FinalCta() {
  return (
    <section className="cta" id="app">
      <div className="cta-bg" aria-hidden="true">
        <img className="cta-wing" src="/brand/grupo-aguia-branca-branco.svg" alt="" />
        <span className="cta-glow" />
        <svg className="cta-lines" viewBox="0 0 1440 700" fill="none" preserveAspectRatio="none">
          <path d="M0 520C300 500 520 400 720 250S1180 80 1440 60" stroke="white" strokeOpacity=".08" />
          <path d="M0 610C330 580 560 470 780 320S1200 150 1440 130" stroke="white" strokeOpacity=".05" />
        </svg>
      </div>

      <div className="container cta-inner">
        <p className="eyebrow eyebrow--light" data-reveal>
          <span className="eyebrow-dot" />
          O próximo passo
        </p>

        <h2 data-reveal>
          O InovaGAB não cria um programa paralelo.
          <br />
          <span>Ele digitaliza o Prêmio InovaGAB.</span>
        </h2>

        <p className="cta-lead" data-reveal>
          Da voz no pátio ao pódio do Oscar. O piloto cabe em uma base — e o caminho já está
          construído.
        </p>

        <div className="cta-actions" data-reveal>
          <a className="btn btn--light" href="/InovaGAB.apk" download>
            <Download size={19} />
            Baixar o APK
          </a>
          <a className="btn btn--ghost" href="#demonstracao">
            <span className="btn-play">
              <Play size={14} />
            </span>
            Rever a demonstração
            <ArrowUpRight size={17} />
          </a>
        </div>

        <div className="cta-bottom" data-reveal>
          <div className="cta-qr-card">
            <img
              src="/qr-apk.png"
              alt="QR code para baixar o APK do InovaGAB"
              width="128"
              height="128"
              loading="lazy"
            />
            <div>
              <strong>Aponte a câmera</strong>
              <span>e instale o APK no Android</span>
              <em>Android 8+ · build de demonstração</em>
            </div>
          </div>

          <p className="cta-meta">
            Apresentação · 20 de outubro de 2026
            <br />
            Challenge FIAP 2026 · Grupo Águia Branca
          </p>
        </div>
      </div>
    </section>
  )
}
