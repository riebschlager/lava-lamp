class Boid {

  PVector location, velocity, acceleration;
  float r, maxforce, maxspeed;
  PImage tex;

  Boid(float x, float y, PImage _tex) {
    acceleration = new PVector(0, 0);
    velocity = new PVector(random(-1, 1), random(-1, 1));
    location = new PVector(x, y);
    r = 2.0;
    maxspeed = 2;
    maxforce = 0.03;
    tex = _tex;
  }

  void run(ArrayList < Boid > boids) {
    flock(boids);
    update();
    borders();
    render();
  }

  void applyForce(PVector force) {
    acceleration.add(force);
  }

  void flock(ArrayList < Boid > boids) {
    PVector sep = separate(boids);
    PVector ali = align(boids);
    PVector coh = cohesion(boids);
    sep.mult(10.0);
    ali.mult(1.0);
    coh.mult(1.0);
    applyForce(sep);
    applyForce(ali);
    applyForce(coh);
  }

  void update() {
    velocity.add(acceleration);
    velocity.limit(maxspeed);
    location.add(velocity);
    acceleration.mult(0);
  }

  PVector seek(PVector target) {
    PVector desired = PVector.sub(target, location);
    desired.normalize();
    desired.mult(maxspeed);
    PVector steer = PVector.sub(desired, velocity);
    steer.limit(maxforce);
    return steer;
  }

  void render() {
    float theta = velocity.heading() + radians(90);
    pushMatrix();
    translate(location.x, location.y);
    imageMode(CENTER);
    image(tex, 0, 0);
    popMatrix();
  }

  void borders() {
    if (location.x < -r) velocity.x = -velocity.x;
    if (location.y < -r) velocity.y = -velocity.y;
    if (location.x > width + r) velocity.x = -velocity.x;
    if (location.y > height + r) velocity.y = -velocity.y;
  }


  PVector separate(ArrayList < Boid > boids) {
    float desiredseparation = 25.0f;
    PVector steer = new PVector(0, 0, 0);
    int count = 0;
    for (Boid other: boids) {
      float d = PVector.dist(location, other.location);
      if ((d > 0) && (d < desiredseparation)) {
        PVector diff = PVector.sub(location, other.location);
        diff.normalize();
        diff.div(d);
        steer.add(diff);
        count++;
      }
    }
    if (count > 0) {
      steer.div((float) count);
    }
    if (steer.mag() > 0) {
      steer.normalize();
      steer.mult(maxspeed);
      steer.sub(velocity);
      steer.limit(maxforce);
    }
    return steer;
  }

  PVector align(ArrayList < Boid > boids) {
    float neighbordist = 50;
    PVector sum = new PVector(0, 0);
    int count = 0;
    for (Boid other: boids) {
      float d = PVector.dist(location, other.location);
      if ((d > 0) && (d < neighbordist)) {
        sum.add(other.velocity);
        count++;
      }
    }
    if (count > 0) {
      sum.div((float) count);
      sum.normalize();
      sum.mult(maxspeed);
      PVector steer = PVector.sub(sum, velocity);
      steer.limit(maxforce);
      return steer;
    } 
    else {
      return new PVector(0, 0);
    }
  }

  PVector cohesion(ArrayList < Boid > boids) {
    float neighbordist = 50;
    PVector sum = new PVector(0, 0);
    int count = 0;
    for (Boid other: boids) {
      float d = PVector.dist(location, other.location);
      if ((d > 0) && (d < neighbordist)) {
        sum.add(other.location);
        count++;
      }
    }
    if (count > 0) {
      sum.div(count);
      return seek(sum);
    } 
    else {
      return new PVector(0, 0);
    }
  }
}

