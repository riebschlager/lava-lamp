import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Flocking extends PApplet {

Flock flock;
PImage[] dots = new PImage[3];

public void setup() {
    size(1280, 720, P2D);
    background(0);
    for (int i = 0; i < dots.length; i++) {
        dots[i] = loadImage("dot" + i + ".png");
    }
    flock = new Flock();
    for (int i = 0; i < 50; i++) {
        flock.addBoid(new Boid(width / 2, height / 2, dots[(int) random(0, dots.length)]));
    }
}

public void draw() {
    background(0);
    blendMode(SCREEN);
    flock.run();
}
class Boid {

    PVector location, velocity, acceleration;
    float r, maxforce, maxspeed;
    PImage tex;

    Boid(float x, float y, PImage _tex) {
        acceleration = new PVector(0, 0);
        velocity = new PVector(random(-1, 1), random(-1, 1));
        location = new PVector(x, y);
        r = 2.0f;
        maxspeed = 2;
        maxforce = 0.03f;
        tex = _tex;
    }

    public void run(ArrayList < Boid > boids) {
        flock(boids);
        update();
        borders();
        render();
    }

    public void applyForce(PVector force) {
        acceleration.add(force);
    }

    public void flock(ArrayList < Boid > boids) {
        PVector sep = separate(boids);
        PVector ali = align(boids);
        PVector coh = cohesion(boids);
        sep.mult(10.0f);
        ali.mult(1.0f);
        coh.mult(1.0f);
        applyForce(sep);
        applyForce(ali);
        applyForce(coh);
    }

    public void update() {
        velocity.add(acceleration);
        velocity.limit(maxspeed);
        location.add(velocity);
        acceleration.mult(0);
    }

    public PVector seek(PVector target) {
        PVector desired = PVector.sub(target, location);
        desired.normalize();
        desired.mult(maxspeed);
        PVector steer = PVector.sub(desired, velocity);
        steer.limit(maxforce);
        return steer;
    }

    public void render() {
        float theta = velocity.heading() + radians(90);
        pushMatrix();
        translate(location.x, location.y);
        imageMode(CENTER);
        image(tex, 0, 0);
        popMatrix();
    }

    public void borders() {
        if (location.x < -r) velocity.x = -velocity.x;
        if (location.y < -r) velocity.y = -velocity.y;
        if (location.x > width + r) velocity.x = -velocity.x;
        if (location.y > height + r) velocity.y = -velocity.y;
    }


    public PVector separate(ArrayList < Boid > boids) {
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

    public PVector align(ArrayList < Boid > boids) {
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
        } else {
            return new PVector(0, 0);
        }
    }

    public PVector cohesion(ArrayList < Boid > boids) {
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
        } else {
            return new PVector(0, 0);
        }
    }
}
class Flock {

    ArrayList<Boid> boids;
    
    Flock() {
        boids = new ArrayList<Boid>();
    }

    public void run() {
        for (Boid b: boids) {
            b.run(boids);
        }
    }

    public void addBoid(Boid b) {
        boids.add(b);
    }

}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "Flocking" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
