import { Download } from './Icons'

const platform = [
  { href: '#desafio', label: 'O desafio' },
  { href: '#solucao', label: 'Solução' },
  { href: '#demonstracao', label: 'Demonstração' },
  { href: '#premio', label: 'Prêmio InovaGAB' },
  { href: '#jeito', label: 'Nosso jeito' },
]

export default function Footer() {
  return (
    <footer className="footer">
      <div className="container footer-main">
        <div className="footer-brand">
          <img
            src="/brand/grupo-aguia-branca-branco.svg"
            alt="Grupo Águia Branca"
            width="150"
            height="39"
          />
          <p className="footer-product">
            Inova<span>GAB</span>
          </p>
          <p className="footer-purpose">Mover o mundo com excelência e respeito às pessoas.</p>
        </div>

        <div className="footer-col">
          <h3>Plataforma</h3>
          <ul>
            {platform.map((item) => (
              <li key={item.href}>
                <a href={item.href}>{item.label}</a>
              </li>
            ))}
          </ul>
        </div>

        <div className="footer-col">
          <h3>Projeto</h3>
          <ul>
            <li>Challenge FIAP 2026</li>
            <li>Grupo Águia Branca</li>
            <li>Turma 2TDSA</li>
            <li>
              <a href="#equipe">Equipe</a>
            </li>
          </ul>
        </div>

        <div className="footer-col">
          <h3>Baixe o app</h3>
          <p className="footer-meta">Android 8+ · Sprint 2 · build de demonstração</p>
          <a className="footer-download" href="/InovaGAB.apk" download>
            Baixar APK <Download size={16} />
          </a>
        </div>
      </div>

      <div className="container footer-bottom">
        <span>© 2026 InovaGAB · Projeto acadêmico Challenge FIAP 2026.</span>
        <span>Marca Grupo Águia Branca utilizada com autorização para fins educacionais.</span>
        <span>Imagens: Grupo Águia Branca.</span>
      </div>
    </footer>
  )
}
