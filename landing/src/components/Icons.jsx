const base = {
  viewBox: '0 0 24 24',
  fill: 'none',
  stroke: 'currentColor',
  strokeWidth: 1.8,
  strokeLinecap: 'round',
  strokeLinejoin: 'round',
  'aria-hidden': 'true',
  focusable: 'false',
}

function Svg({ children, size = 24, ...props }) {
  return (
    <svg {...base} width={size} height={size} {...props}>
      {children}
    </svg>
  )
}

export function Download(props) {
  return (
    <Svg {...props}>
      <path d="M12 3v12" />
      <path d="m7 10 5 5 5-5" />
      <path d="M5 21h14" />
    </Svg>
  )
}

export function ArrowRight(props) {
  return (
    <Svg {...props}>
      <path d="M5 12h14" />
      <path d="m13 6 6 6-6 6" />
    </Svg>
  )
}

export function ArrowDown(props) {
  return (
    <Svg {...props}>
      <path d="M12 5v14" />
      <path d="m6 13 6 6 6-6" />
    </Svg>
  )
}

export function ArrowUpRight(props) {
  return (
    <Svg {...props}>
      <path d="M7 17 17 7" />
      <path d="M8 7h9v9" />
    </Svg>
  )
}

export function ArrowUp(props) {
  return (
    <Svg {...props}>
      <path d="M12 19V5" />
      <path d="m6 11 6-6 6 6" />
    </Svg>
  )
}

export function Play(props) {
  return (
    <Svg {...props}>
      <path d="M8 5.5v13l11-6.5-11-6.5Z" fill="currentColor" stroke="none" />
    </Svg>
  )
}

export function Sparkles(props) {
  return (
    <Svg {...props}>
      <path d="m12 3 1.7 4.3L18 9l-4.3 1.7L12 15l-1.7-4.3L6 9l4.3-1.7L12 3Z" />
      <path d="m19 15 .9 2.1L22 18l-2.1.9L19 21l-.9-2.1L16 18l2.1-.9L19 15Z" />
    </Svg>
  )
}

export function Mic(props) {
  return (
    <Svg {...props}>
      <rect x="9" y="3" width="6" height="11" rx="3" />
      <path d="M5 11a7 7 0 0 0 14 0" />
      <path d="M12 18v3" />
      <path d="M8 21h8" />
    </Svg>
  )
}

export function Shield(props) {
  return (
    <Svg {...props}>
      <path d="m12 3 7 3v6c0 4.2-3 7.4-7 9-4-1.6-7-4.8-7-9V6l7-3Z" />
      <path d="m9 12 2 2 4-4" />
    </Svg>
  )
}

export function ShieldCheck(props) {
  return (
    <Svg {...props}>
      <path d="m12 3 7 3v6c0 4.2-3 7.4-7 9-4-1.6-7-4.8-7-9V6l7-3Z" />
      <path d="m9 12 2 2 4-4" />
    </Svg>
  )
}

export function Users(props) {
  return (
    <Svg {...props}>
      <circle cx="9" cy="8" r="3.2" />
      <path d="M3.5 20a5.5 5.5 0 0 1 11 0" />
      <path d="M16 5.2a3.2 3.2 0 0 1 0 5.6" />
      <path d="M17.5 20a5.5 5.5 0 0 0-3-4.9" />
    </Svg>
  )
}

export function User(props) {
  return (
    <Svg {...props}>
      <circle cx="12" cy="8" r="4" />
      <path d="M4 21a8 8 0 0 1 16 0" />
    </Svg>
  )
}

export function UserCheck(props) {
  return (
    <Svg {...props}>
      <circle cx="9" cy="8" r="4" />
      <path d="M2 21a7 7 0 0 1 12-4.9" />
      <path d="m16 18 2 2 4-4" />
    </Svg>
  )
}

export function Chart(props) {
  return (
    <Svg {...props}>
      <path d="M3 3v18h18" />
      <path d="M7 16v-4" />
      <path d="M12 16V8" />
      <path d="M17 16v-6" />
    </Svg>
  )
}

export function Trophy(props) {
  return (
    <Svg {...props}>
      <path d="M8 4h8v5a4 4 0 0 1-8 0V4Z" />
      <path d="M8 6H5v2a3 3 0 0 0 3 3" />
      <path d="M16 6h3v2a3 3 0 0 1-3 3" />
      <path d="M12 13v5" />
      <path d="M8 21h8" />
      <path d="M10 18h4" />
    </Svg>
  )
}

export function Wing(props) {
  return (
    <Svg {...props}>
      <path d="M21 3c-6 1.4-10.2 4.3-12.8 8.6C6.5 14.8 5.2 18.4 5 21c2.7-.4 5.9-1.8 8.4-4.2C17 13.6 19.7 8.7 21 3Z" />
      <path d="M5 21c1-4.2 3.2-7.8 6.4-10.4" />
      <path d="M11.4 17.6c2.4-1.2 4.4-2.9 6-5.1" />
    </Svg>
  )
}

export function Check(props) {
  return (
    <Svg {...props}>
      <path d="m4 12.5 5 5L20 6.5" />
    </Svg>
  )
}

export function Bell(props) {
  return (
    <Svg {...props}>
      <path d="M18 9a6 6 0 1 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9Z" />
      <path d="M10 21h4" />
    </Svg>
  )
}

export function Home(props) {
  return (
    <Svg {...props}>
      <path d="M4 11l8-7 8 7v9a1 1 0 0 1-1 1h-5v-6H10v6H5a1 1 0 0 1-1-1v-9Z" />
    </Svg>
  )
}

export function Bulb(props) {
  return (
    <Svg {...props}>
      <path d="M9 18h6" />
      <path d="M10 21h4" />
      <path d="M8.5 15.5a6 6 0 1 1 7 0c-.7.7-1 1.5-1 2.5h-5c0-1-.3-1.8-1-2.5Z" />
    </Svg>
  )
}

export function Lock(props) {
  return (
    <Svg {...props}>
      <rect x="5" y="11" width="14" height="9" rx="2" />
      <path d="M8 11V8a4 4 0 0 1 8 0v3" />
    </Svg>
  )
}

export function Clock(props) {
  return (
    <Svg {...props}>
      <circle cx="12" cy="12" r="9" />
      <path d="M12 7v5l3 2" />
    </Svg>
  )
}

export function Target(props) {
  return (
    <Svg {...props}>
      <circle cx="12" cy="12" r="9" />
      <circle cx="12" cy="12" r="5" />
      <circle cx="12" cy="12" r="1.4" fill="currentColor" stroke="none" />
    </Svg>
  )
}

export function Split(props) {
  return (
    <Svg {...props}>
      <path d="M6 3v6a4 4 0 0 0 4 4h8" />
      <path d="M6 21v-6a4 4 0 0 1 4-4h8" />
      <path d="m15 10 3 3-3 3" />
      <path d="m15 4 3 3-3 3" />
    </Svg>
  )
}

export function Layers(props) {
  return (
    <Svg {...props}>
      <path d="m12 3 9 5-9 5-9-5 9-5Z" />
      <path d="m3 12 9 5 9-5" />
      <path d="m3 16 9 5 9-5" />
    </Svg>
  )
}

export function Radio(props) {
  return (
    <Svg {...props}>
      <path d="M4 15a8 8 0 0 1 16 0" />
      <path d="M8 17a4 4 0 0 1 8 0" />
      <circle cx="12" cy="19" r="1.2" fill="currentColor" stroke="none" />
      <path d="M12 21v0" />
    </Svg>
  )
}

export function Gift(props) {
  return (
    <Svg {...props}>
      <rect x="3" y="8" width="18" height="4" rx="1" />
      <path d="M5 12v8h14v-8" />
      <path d="M12 8v12" />
      <path d="M12 8C10 8 8 7 8 5a2 2 0 0 1 4-.5A2 2 0 0 1 16 5c0 2-2 3-4 3Z" />
    </Svg>
  )
}

export function Ticket(props) {
  return (
    <Svg {...props}>
      <path d="M3 8a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2v2a2 2 0 0 0 0 4v2a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-2a2 2 0 0 0 0-4V8Z" />
      <path d="M14 6v12" strokeDasharray="2 3" />
    </Svg>
  )
}

export function Graduation(props) {
  return (
    <Svg {...props}>
      <path d="m2 9 10-5 10 5-10 5L2 9Z" />
      <path d="M6 11v4c0 1.5 3 3 6 3s6-1.5 6-3v-4" />
      <path d="M22 9v6" />
    </Svg>
  )
}

export function Car(props) {
  return (
    <Svg {...props}>
      <path d="M4 17h16" />
      <path d="M4 17v-4l2-5h12l2 5v4" />
      <circle cx="7.5" cy="17.5" r="1.5" />
      <circle cx="16.5" cy="17.5" r="1.5" />
      <path d="M6 12h12" />
    </Svg>
  )
}

export function Coffee(props) {
  return (
    <Svg {...props}>
      <path d="M4 8h12v7a4 4 0 0 1-4 4H8a4 4 0 0 1-4-4V8Z" />
      <path d="M16 9h2a3 3 0 0 1 0 6h-2" />
      <path d="M7 3v2" />
      <path d="M11 3v2" />
    </Svg>
  )
}

export function Store(props) {
  return (
    <Svg {...props}>
      <path d="M4 9h16l-1-5H5L4 9Z" />
      <path d="M5 9v11h14V9" />
      <path d="M9 20v-5h6v5" />
    </Svg>
  )
}

export function Phone(props) {
  return (
    <Svg {...props}>
      <rect x="7" y="2" width="10" height="20" rx="2" />
      <path d="M11 19h2" />
    </Svg>
  )
}

export function Message(props) {
  return (
    <Svg {...props}>
      <path d="M21 11.5a8 8 0 0 1-8 8H4l2-3a8 8 0 1 1 15-5Z" />
      <path d="M8.5 11.5h.01" />
      <path d="M12 11.5h.01" />
      <path d="M15.5 11.5h.01" />
    </Svg>
  )
}

export function FileText(props) {
  return (
    <Svg {...props}>
      <path d="M14 3H6a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V9l-6-6Z" />
      <path d="M14 3v6h6" />
      <path d="M8 13h8" />
      <path d="M8 17h5" />
    </Svg>
  )
}

export function Route(props) {
  return (
    <Svg {...props}>
      <circle cx="6" cy="19" r="2" />
      <circle cx="18" cy="5" r="2" />
      <path d="M8 19h5a4 4 0 0 0 0-8H9a4 4 0 0 1 0-8h7" />
      <path d="m14 1 2 2-2 2" />
    </Svg>
  )
}

export function Star(props) {
  return (
    <Svg {...props}>
      <path d="m12 3 2.7 5.5 6.1.9-4.4 4.3 1 6-5.4-2.9-5.4 2.9 1-6L3.2 9.4l6.1-.9L12 3Z" />
    </Svg>
  )
}

export function Quote(props) {
  return (
    <Svg {...props}>
      <path d="M9 7H5v6h4c0 2-1.5 3.4-4 4" />
      <path d="M19 7h-4v6h4c0 2-1.5 3.4-4 4" />
    </Svg>
  )
}

export const icons = {
  radio: Radio,
  split: Split,
  layers: Layers,
  users: Users,
  mic: Mic,
  sparkles: Sparkles,
  chart: Chart,
  ticket: Ticket,
  graduation: Graduation,
  clock: Clock,
  coffee: Coffee,
  phone: Phone,
  shield: Shield,
  message: Message,
  bulb: Bulb,
  trophy: Trophy,
  wing: Wing,
  gift: Gift,
  car: Car,
  store: Store,
  route: Route,
  target: Target,
  fileText: FileText,
  user: User,
}
