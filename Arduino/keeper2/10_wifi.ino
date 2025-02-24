void setupWifi() {
  Serial.println("Setting AP (Access Point)...");
  WiFi.softAP(ssid, password);

  IPAddress IP = WiFi.softAPIP();
  Serial.print("AP IP address: ");
  Serial.println(IP);

  getMessageHttp();
  server.begin();
}

void getMessageHttp() {
  server.on("/", HTTP_GET, [](AsyncWebServerRequest *request){
    if (request->hasParam("x") && request->hasParam("y")) {
        xString = request->getParam("x")->value();
        yString = request->getParam("y")->value();

        request->send(200, "text/plain", "Data received: x=" + xString + ", y=" + yString);
        // kalo http request berhasil
        digitalWrite(LED_BUILTIN, HIGH);
    } else {
      request->send(400, "text/plain", "Bad Request: Missing parameters");
      // kalo http request gagal
      digitalWrite(LED_BUILTIN, LOW);
    }
  });
}