export type StripeLike = {
  elements: () => {
    create: (fieldType: string) => {
      mount: (selector: string) => void
    }
  }
  confirmCardPayment: (
    clientSecret: string,
    payload: { payment_method: { card: unknown } }
  ) => Promise<{ error?: { message?: string }; paymentIntent?: { status?: string } }>
}

declare global {
  interface Window {
    Stripe: (key: string) => StripeLike
  }
}

export const loadStripeJs = () => {
  if (document.getElementById('stripe-js')) return Promise.resolve()

  return new Promise<void>((resolve, reject) => {
    const script = document.createElement('script')
    script.id = 'stripe-js'
    script.src = 'https://js.stripe.com/v3/'
    script.onload = () => resolve()
    script.onerror = () => reject(new Error('Stripe JS load failed'))
    document.head.appendChild(script)
  })
}
