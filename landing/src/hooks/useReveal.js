import { useEffect, useState } from 'react'

export default function useReveal() {
  useEffect(() => {
    const elements = Array.from(document.querySelectorAll('[data-reveal]'))
    const reduced = window.matchMedia('(prefers-reduced-motion: reduce)').matches

    if (reduced || !('IntersectionObserver' in window)) {
      elements.forEach((el) => el.classList.add('is-visible'))
      return undefined
    }

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add('is-visible')
            observer.unobserve(entry.target)
          }
        })
      },
      { threshold: 0.12, rootMargin: '0px 0px -48px 0px' },
    )

    elements.forEach((el) => observer.observe(el))
    return () => observer.disconnect()
  }, [])
}

export function useScrolledPast(threshold = 0.4) {
  const [past, setPast] = useState(false)

  useEffect(() => {
    const onScroll = () => {
      setPast(window.scrollY > window.innerHeight * threshold)
    }
    onScroll()
    window.addEventListener('scroll', onScroll, { passive: true })
    return () => window.removeEventListener('scroll', onScroll)
  }, [threshold])

  return past
}
