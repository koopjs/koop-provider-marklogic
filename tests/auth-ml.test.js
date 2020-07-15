const users = {
  deptA: {
    username: "koop-marklogic-provider-test-dept-a-user",
    password: "test"
  },
  deptB: {
    username: "koop-marklogic-provider-test-dept-b-user",
    password: "test"
  },
  deptAdmin: {
    username: "koop-marklogic-provider-test-dept-admin",
    password: "test"
  }
};

describe('test-auth-ml tests', () => {
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
      .query({ "username": users.deptA.username, "password": users.deptA.password })
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
      .query({ "username": users.deptA.username, "password": users.deptA.password })
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

  it('should return one feature object visible by department A', done => {
    request
      .get('/marklogic/tokens')
      .query({ "username": users.deptA.username, "password": users.deptA.password })
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
            if (body.features.length !== 1)
              throw new Error("Response expected to have 1 feature");
            if (body.features[0].attributes.OBJECTID !== 6000)
              throw new Error("Expected feature to have an OBJECTID of 6000");
          })
          .end((err, res) => {
            if (err) return done(err);
            done();
          });
      });
  });

  it('should return one feature object visible by department B', done => {
    request
      .get('/marklogic/tokens')
      .query({ "username": users.deptB.username, "password": users.deptB.password })
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
            if (body.features.length !== 1)
              throw new Error("Response expected to have 1 feature");
            if (body.features[0].attributes.OBJECTID !== 6001)
              throw new Error("Expected feature to have an OBJECTID of 6001");
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
      .query({ "username": users.deptAdmin.username, "password": users.deptAdmin.password })
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
