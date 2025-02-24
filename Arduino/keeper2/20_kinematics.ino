void setupServo() {
  upper_servo.attach(DO0);

  upper_servo.write(90);

  delay(1000);
}

void move() {
  inverseKinematics();

  theta1 = 0.03*theta1 + 0.97*prev_theta1;

  prev_theta1 = theta1;

  Serial.print(theta1);
  Serial.print(" ");

  upper_servo.write(theta1);
  delay(10);
}

void inverseKinematics() {
  float distance = sqrt(x * x + y * y);

  if (distance > (upper_arm + lower_arm)) {
    Serial.println("Target terlalu jauh");
    return; // Target terlalu jauh untuk dijangkau
  }

  float theta1_rad = atan2(y, x);

  // theta1: angle for upper arm servo (adjusted for CCW movement and 0 on the left)
  theta1 = theta1_rad * 180 / PI;

  // Pastikan theta1 dalam rentang 0-180 derajat
  if (theta1 < 0) {
    theta1 += 180;
  }

  // Sesuaikan theta1 dan theta2 untuk orientasi servo
  theta1 = constrain(theta1, 0, 180); // Untuk gerakan CCW dengan 0 di kiri

  // Output to serial for debugging
  Serial.print("Theta1: ");
  Serial.println(theta1);
}
