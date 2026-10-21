import Nav from './components/Nav'
import Hero from './components/Hero'
import GroupStats from './components/GroupStats'
import Problem from './components/Problem'
import Pillars from './components/Pillars'
import Demo from './components/Demo'
import Premio from './components/Premio'
import Ai from './components/Ai'
import Economy from './components/Economy'
import Values from './components/Values'
import Credibility from './components/Credibility'
import Team from './components/Team'
import FinalCta from './components/FinalCta'
import Footer from './components/Footer'
import useReveal from './hooks/useReveal'

export default function App() {
  useReveal()

  return (
    <>
      <Nav />
      <main id="conteudo">
        <Hero />
        <GroupStats />
        <Problem />
        <Pillars />
        <Demo />
        <Premio />
        <Ai />
        <Economy />
        <Values />
        <Credibility />
        <Team />
        <FinalCta />
      </main>
      <Footer />
    </>
  )
}
