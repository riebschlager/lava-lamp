Flock flock;
PImage[] dots = new PImage[3];

void setup() {
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

void draw() {
  background(0);
  blendMode(SCREEN);
  flock.run();
}

