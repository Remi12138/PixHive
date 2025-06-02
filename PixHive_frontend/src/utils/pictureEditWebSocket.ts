export default class PictureEditWebSocket {
  private pictureId: number
  private socket: WebSocket | null
  private eventHandlers: any

  constructor(pictureId: number) {
    this.pictureId = pictureId
    this.socket = null // WebSocket instance
    this.eventHandlers = {} // Custom event handler
  }

  /**
   * init WebSocket connect
   */
  connect() {
    const url = `ws://localhost:8123/api/ws/picture/edit?pictureId=${this.pictureId}`
    this.socket = new WebSocket(url)

    // Set to carry cookie
    this.socket.binaryType = 'blob'

    // connect success
    this.socket.onopen = () => {
      console.log('The WebSocket connection has been established')
      this.triggerEvent('open')
    }

    // message
    this.socket.onmessage = (event) => {
      const message = JSON.parse(event.data)
      console.log('Receive message:', message)

      // Trigger the corresponding event according to the message type
      const type = message.type
      this.triggerEvent(type, message)
    }

    // connect close
    this.socket.onclose = (event) => {
      console.log('The WebSocket connection has been closed:', event)
      this.triggerEvent('close', event)
    }

    // error
    this.socket.onerror = (error) => {
      console.error('WebSocket error:', error)
      this.triggerEvent('error', error)
    }
  }

  /**
   * close WebSocket connect
   */
  disconnect() {
    if (this.socket) {
      this.socket.close()
      console.log('The WebSocket connection has been manually closed')
    }
  }

  /**
   * send message to backend
   * @param {Object} message
   */
  sendMessage(message: object) {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      this.socket.send(JSON.stringify(message))
      console.log('Send Message:', message)
    } else {
      console.error('The WebSocket is not connected and messages cannot be sent:', message)
    }
  }

  /**
   * custom event listeners
   * @param {string} type
   * @param {Function} handler
   */
  on(type: string, handler: (data?: any) => void) {
    if (!this.eventHandlers[type]) {
      this.eventHandlers[type] = []
    }
    this.eventHandlers[type].push(handler)
  }

  /**
   * event trigger
   * @param {string} type
   * @param {Object} data
   */
  triggerEvent(type: string, data?: any) {
    const handlers = this.eventHandlers[type]
    if (handlers) {
      handlers.forEach((handler: any) => handler(data))
    }
  }
}
