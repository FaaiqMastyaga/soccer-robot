void printReceivedMessage();

// Setup
void setup() {
  Serial.begin(BAUD);
  pinMode(LED_BUILTIN, OUTPUT);
  setupServo();
  setupWifi();
}

// Main
void loop() {
  if (WiFi.softAPgetStationNum()) {
    digitalWrite(LED_BUILTIN, HIGH);
    if (millis() - prev_time > 3) {
      printReceivedMessage();
      prev_time = millis();
    }
  } else {
    digitalWrite(LED_BUILTIN, LOW);
  }

  x = xString.toInt();
  y = yString.toInt();

  x = constrain(x, -400, 480);
  y = constrain(x, -200, 200);

  x = map(x, -400, 480, -15, 12);
  y = map(y, -200, 200, 0, 12);

  move();
}

void printReceivedMessage() {
  if (millis() - print_time > 3) {
    Serial.print("x: ");
    Serial.print(x);
    Serial.print(", y: ");
    Serial.println(y);
    print_time = millis();
  }
}