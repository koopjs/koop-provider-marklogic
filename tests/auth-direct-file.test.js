const users = require('./user-store.json');
const user = users[0];

describe('test-auth-direct-file tests', () => {
  let request = null;
  let app = null;

  beforeAll(done => {
    app = require('../server');
    request = require('supertest')(app);
    done();
  });

  afterAll(done => { 
    if (app)
      app.close();
    done();
  });

  it('should reject the request', done => {
    request
      .get('/marklogic/GDeltExample/FeatureServer')
      .expect('Content-Type', /json/)
      .expect(200)
      .expect(res => {
        const body = res.body;
        if (!(body.error && body.error.code && body.error.code === 499))
          throw new Error("Expected error code of 499");
      })
      .end((err, res) => {
        if (err) return done(err);
        done();
      });
  });
  

  it('should reject the request', done => {
    request
      .get('/marklogic/GDeltExample/FeatureServer/0/query')
      .query({ "where": "1=1" })
      .expect('Content-Type', /json/)
      .expect(200)
      .expect(res => {
        const body = res.body;
        if (!(body.error && body.error.code && body.error.code === 499))
          throw new Error("Expected error code of 499");
      })
      .end((err, res) => {
        if (err) return done(err);
        done();
      });
  });

  it('should provide a token', done => {
    request
      .get('/marklogic/tokens')
      .query({ "username": user.username, "password": user.password })
      .expect('Content-Type', /json/)
      .expect(200)
      .expect(res => {
        const body = res.body;
        if (!body.token)
          throw new Error("Expected token property");
      })
      .end((err, res) => {
        if (err) return done(err);
        done();
      });
  });

  it('should not provide a token', done => {
    request
      .get('/marklogic/tokens')
      .query({ "username": "__an_invalid_user__", "password": "an_invalid_password" })
      .expect('Content-Type', /json/)
      .expect(200)
      .expect(res => {
        const body = res.body;
        if (!(body.error && body.error.code && body.error.code === 400))
          throw new Error("Expected error code of 400");
      })
      .end((err, res) => {
        if (err) return done(err);
        done();
      });
  });

  it('should return the feature server information', done => {
    request
      .get('/marklogic/tokens')
      .query({ "username": user.username, "password": user.password })
      .expect('Content-Type', /json/)
      .expect(200)
      .end((err, res) => {
        if (err) return done(err);

        const token = res.body.token;
        request
          .get('/marklogic/GDeltExample/FeatureServer')
          .set('Authorization', token)
          .expect('Content-Type', /json/)
          .expect(res => {
            const body = res.body;
            if (!(body.serviceDescription && body.serviceDescription === 'GDeltExample'))
              throw new Error("Expected serviceDescription property with value of GDeltExample");
          })
          .end((err, res) => {
            if (err) return done(err);
            done();
          });
      });
  });

  it('should return all feature objects', done => {
    request
      .get('/marklogic/tokens')
      .query({ "username": user.username, "password": user.password })
      .expect('Content-Type', /json/)
      .expect(200)
      .end((err, res) => {
        if (err) return done(err);

        const token = res.body.token;
        request
          .get('/marklogic/GDeltExample/FeatureServer/0/query')
          .query({ "where": "1=1" })
          .set('Authorization', token)
          .expect('Content-Type', /json/)
          .expect(200)
          .expect(res => {
            const body = res.body;
            if (!(body.features && Array.isArray(body.features)))
              throw new Error("Response expected to have a features array");
            if (body.features.length !== 2)
              throw new Error("Response expected to have 2 features");
          })
          .end((err, res) => {
            if (err) return done(err);
            done();
          });
      });
  });
});
