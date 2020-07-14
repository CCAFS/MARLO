export const spyWpmp = {
  handlers: [],

  clearHandlers() {
    spyWpmp.handlers.length = 0;
  },

  addHandlerSpy(handler) {
    spyWpmp.handlers.push(handler);
  },

  addHandler: jasmine.createSpy("addHandler").and.callFake((x) => spyWpmp.addHandlerSpy(x)),

  postMessageSpy: jasmine.createSpy("postMessage"),
  postMessage<T>(message): Promise<T> {
    spyWpmp.postMessageSpy(message);
    return Promise.resolve(null);
  },

  start: jasmine.createSpy("start"),
  stop: jasmine.createSpy("stop"),

  onMessageReceived(event) {
    let message: any = event.data;

    const handled = spyWpmp.handlers.some(handler => {
      if (handler.test(message)) {
        Promise.resolve(handler.handle(message))

        return true;
      }
    });

    if (!handled) {
      throw Error(`nothing handled message`);
    }
  }
};