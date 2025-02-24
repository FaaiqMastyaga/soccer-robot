// WiFi
const char* ssid = "K01-2-Keeper";
const char* password = "Plmokn098";

AsyncWebServer server(80);
String serverName = "http://192.168.4.1/";

String xString;
String yString;

// Arm
int x;
int y;
const float upper_arm = 8.143;
const float lower_arm = 9.537;

float theta1 = 90;
float theta2 = 90;

float prev_theta1 = theta1;
float prev_theta2 = theta2;

// Servo
Servo upper_servo;
Servo lower_servo;

int prev_time;
int print_time;