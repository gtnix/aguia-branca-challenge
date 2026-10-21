import { useEffect, useState } from 'react'
import { Download } from './Icons'

const links = [
  { href: '#desafio', label: 'O desafio' },
  { href: '#solucao', label: 'Solução' },
  { href: '#demonstracao', label: 'Demonstração' },
  { href: '#premio', label: 'Prêmio' },
  { href: '#jeito', label: 'Nosso jeito' },
]

export default function Nav() {
  const [scrolled, setScrolled] = useState(false)

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 12)
    onScroll()
    window.addEventListener('scroll', onScroll, { passive: true })
    return () => window.removeEventListener('scroll', onScroll)
  }, [])

  return (
    <>
      <a className="skip-link" href="#conteudo">
        Ir para o conteúdo
      </a>
      <header className="nav-shell">
        <div className={`nav${scrolled ? ' is-scrolled' : ''}`}>
          <a
            className="brand"
            href="#topo"
            aria-label="InovaGAB, uma plataforma do Grupo Águia Branca"
          >
            <img
              className="brand-group"
              src="/brand/grupo-aguia-branca.svg"
              alt="Grupo Águia Branca"
              width="132"
              height="34"
            />
            <span className="brand-sep" aria-hidden="true" />
            <span className="brand-product">
              Inova<span>GAB</span>
            </span>
          </a>

          <nav className="nav-links" aria-label="Seções da apresentação">
            <ul>
              {links.map((link) => (
                <li key={link.href}>
                  <a href={link.href}>{link.label}</a>
                </li>
              ))}
            </ul>
          </nav>

          <div className="nav-actions">
            <a className="nav-cta" href="/InovaGAB.apk" download>
              <Download size={16} />
              <span>Baixar APK</span>
            </a>
          </div>
        </div>
      </header>
    </>
  )
}
