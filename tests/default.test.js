describe('test-default tests', () => {
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

  it('should return the feature server information', done => {
    request
      .get('/marklogic/GDeltExample/FeatureServer')
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
  

  it('should return all features', done => {
    request
      .get('/marklogic/GDeltExample/FeatureServer/0/query?where=1=1')
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