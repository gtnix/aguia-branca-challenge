export const flagship = {
  title: 'Docas inteligentes no CD Serra',
  shortDescription:
    'Carreta espera até 3h por uma doca. Com janela agendada e alerta no celular do motorista, o pátio gira mais.',
  description:
    'Carreta espera até 3h por uma doca porque o pátio não tem visão das janelas. Com agendamento por janela e alerta no celular do motorista, o CD Serra reduz a espera, libera o pátio e aumenta as viagens por dia.',
  orientation: 'Disponibilidade da frota e eficiência do pátio',
  shortOrientation: 'Eficiência do pátio',
  orientationDetail: 'Disponibilidade da frota e menos espera na doca.',
  area: 'Logística',
  status: 'Aguardando avaliação',
  author: 'Pedro Santos',
  authorContext: 'Operador de pátio · CD Serra',
  supports: 12,
  supportsLine: '12 colegas de pátio e estrada apoiam esta ideia. O problema é de toda a operação.',
  impact: 4,
  effort: 3,
  score: 83,
  band: 'ALTA',
  rationale:
    'A dor é mensurável e tem dono no pátio. O ganho vem de janela agendada e visibilidade em tempo real; começar por um piloto no CD Serra antes de escalar.',
}

export const problems = [
  {
    n: '01',
    icon: 'radio',
    title: 'A ideia morre no rádio',
    text: 'No CD Serra, a carreta espera até 3h por uma doca porque o pátio não tem visão das janelas. Quem está no pátio vê o problema, mas não tem canal para registrar.',
  },
  {
    n: '02',
    icon: 'split',
    title: 'O funil é fragmentado',
    text: 'Imova-se captura, Demoday premia o que já foi implementado, Oscar reconhece. Falta um sistema único que ligue estratégia, execução e retorno.',
  },
  {
    n: '03',
    icon: 'layers',
    title: 'Volume não é valor',
    text: 'Sem priorização e sem vínculo com a estratégia, o comitê recebe ruído — e a melhor ideia se perde na fila.',
  },
  {
    n: '04',
    icon: 'users',
    title: 'Ponto virtual não mobiliza',
    text: 'Engajamento sem contrapartida concreta não muda a operação. O grupo já provou, com a AMIGAB, que gincana entre equipes funciona.',
  },
]

export const pillars = [
  {
    n: '01',
    icon: 'mic',
    role: 'Operador',
    title: 'Capturar',
    tag: 'Imova-se no bolso',
    text: 'A dor do pátio vira registro em menos de um minuto.',
    points: [
      'Voz em português transcrita no app',
      'Ideia nasce ligada à eficiência do pátio',
      'Status, apoios e feedback no celular',
    ],
  },
  {
    n: '02',
    icon: 'sparkles',
    role: 'Gestor',
    title: 'Estruturar',
    tag: 'Decisão com critério',
    text: 'A fila vira decisão — e a decisão vira projeto.',
    points: [
      'IA sugere nota de 0 a 100 com justificativa',
      'Aprovação e reprovação são sempre humanas',
      'Projeto nasce com investimento, prazo e retorno',
    ],
  },
  {
    n: '03',
    icon: 'chart',
    role: 'Líder',
    title: 'Acompanhar',
    tag: 'Resultado visível',
    text: 'O portfólio vira resultado — e o resultado vira diretoria.',
    points: [
      'Orientações e campanhas patrocinadas',
      'ROI, lucro, prazos, funil e desempenho por área',
      'Relatório executivo gerado por IA',
    ],
  },
]

export const profiles = {
  operador: {
    role: 'Operador',
    name: 'Pedro Santos',
    context: 'Logística · CD Serra',
    lead: 'Captura a dor do pátio em menos de um minuto.',
    bullets: [
      'Registra a espera na doca em menos de um minuto',
      'A ideia nasce ligada à eficiência do pátio',
      'Acompanha status e apoios no celular',
    ],
  },
  gestor: {
    role: 'Gestor',
    name: 'Ana Oliveira',
    context: 'Logística · CD Serra',
    lead: 'Prioriza com IA, decide com critério.',
    bullets: [
      'Encontra as Docas inteligentes na fila',
      'IA sugere score 0–100 com justificativa',
      'Aprova e devolve feedback ao autor',
    ],
  },
  lider: {
    role: 'Líder',
    name: 'Marcos Silva',
    context: 'Holding · Gestão da inovação',
    lead: 'Enxerga o retorno, não a lista de tickets.',
    bullets: [
      'Funil 11 → 3 → 3 em tempo real',
      'ROI de 192,7% na carteira da demo',
      'Resumo executivo em linguagem de diretoria',
    ],
  },
}

export const ledger = [
  { label: 'Ideias enviadas', detail: 'Docas, Preditiva e WhatsApp', xp: '+6', wings: '—' },
  { label: 'Ideia aprovada', detail: 'Preditiva de cubo e suspensão', xp: '+20', wings: '+10' },
  { label: 'Ideia convertida', detail: 'Preditiva → projeto', xp: '+40', wings: '+30' },
  { label: 'Feedback recebido', detail: 'Grupo de WhatsApp do pátio', xp: '+3', wings: '—' },
  { label: 'Resgate', detail: 'Trilha na Universidade Corporativa GAB', xp: '—', wings: '-40' },
]

export const rewards = [
  {
    icon: 'ticket',
    name: 'Passagem Águia Branca',
    cost: 120,
    partner: 'Viação Águia Branca',
    accent: 'blue',
  },
  {
    icon: 'graduation',
    name: 'Trilha na Universidade Corporativa GAB',
    cost: 40,
    partner: 'Universidade Corporativa',
    accent: 'cyan',
  },
  {
    icon: 'clock',
    name: 'Day-off de reconhecimento',
    cost: 200,
    partner: 'RH Águia Branca',
    accent: 'green',
  },
  {
    icon: 'coffee',
    name: 'Almoço com a liderança do Grupo',
    cost: 250,
    partner: 'Holding Águia Branca',
    accent: 'orange',
  },
]

export const fieldFeatures = [
  {
    icon: 'mic',
    title: 'Voz em português',
    text: 'A ideia nasce falada, não digitada. O app transcreve e o operador revisa antes de enviar.',
  },
  {
    icon: 'phone',
    title: 'Poucas telas',
    text: 'Login, orientação, ideia, status. Enviar leva menos de um minuto — no turno, não no escritório.',
  },
  {
    icon: 'shield',
    title: '3 ideias por dia',
    text: 'Limite no servidor, similaridade de título e chave única contra spam e pontos repetidos.',
  },
  {
    icon: 'message',
    title: 'Feedback obrigatório',
    text: 'Reprovar exige explicação. O autor recebe o motivo claro — e ganha 3 XP por aprender com ele.',
  },
]

export const stack = [
  'Kotlin',
  'Jetpack Compose',
  'Material 3',
  'Clean Architecture',
  'Spring Boot 3',
  'MongoDB',
  'JWT',
  'Google Gemini',
]

export const team = [
  {
    initials: 'GA',
    name: 'Giuliana Abe Takara',
    rm: 'RM 562736',
    accent: 'blue',
  },
  {
    initials: 'AS',
    name: 'André Luiz Oliveira da Silva',
    rm: 'RM 565836',
    accent: 'orange',
  },
]
