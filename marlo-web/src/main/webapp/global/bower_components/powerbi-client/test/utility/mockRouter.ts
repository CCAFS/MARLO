export const spyRouter = {
  get: jasmine.createSpy("get"),
  post: jasmine.createSpy("post"),
  patch: jasmine.createSpy("patch"),
  put: jasmine.createSpy("put"),
  delete: jasmine.createSpy("delete")
};