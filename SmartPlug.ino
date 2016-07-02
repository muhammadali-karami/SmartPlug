#include <TimeAlarms.h>
#include <Time.h>
#include <TimeLib.h>
#include <ESP8266WiFi.h>
#include <EEPROM.h>

const String AP_PRE_NAME = "Smart Plug - ";
const char AP_Password[] = "amirkarimi";
const int PLUG_1 = 0;
const int PLUG_2 = 2;
long schTime = 0;
boolean schFlag = false;
WiFiClient client;

#define PLUG_SIZE 2
#define ALARM_SIZE 15
int plugNum[PLUG_SIZE];
String plugName[PLUG_SIZE];
String plugStatus[PLUG_SIZE];

boolean isAlarmSetArray[ALARM_SIZE]; // true - false
int plugNumArray[ALARM_SIZE];
String alarmNameArray[ALARM_SIZE];
String whenSetTimeArray[ALARM_SIZE];
String executeTimeArray[ALARM_SIZE];
String alarmStatusArray[ALARM_SIZE]; // on - off

String req;
int firstSpaceIndex = 0;
int secondSpaceIndex = 0;
String strOrder;
String response;

/* ERROR CODES
 *  1001 : Max alarm added
*/

WiFiServer server(80);

void setup()
{
  initHardware();
  setupWiFi();
  server.begin();
}

void loop()
{
  Alarm.delay(0);

  // Check if a client has connected
  client = server.available();
  if (!client) {
    return;
  }
  // Wait until the client sends some data
  while(!client.available()){
    delay(1);
  }

  // Read the first line of the request
  req = client.readStringUntil('\r');
  client.flush();

  firstSpaceIndex = req.indexOf(' ');
  secondSpaceIndex = req.indexOf(' ', firstSpaceIndex + 1);
  // (GET ) ( HTTP/1.1) removed from request - complete sentense is (GET X HTTP/1.1)
  strOrder = req.substring(firstSpaceIndex + 1, secondSpaceIndex);
  Serial.println(strOrder);

      // * * * * * * * * * *
      // SYNC
      if (strOrder.indexOf("/sync") != -1) {
         sync();
      }
      // * * * * * * * * * *
      // Cancel Alarm
      else if(strOrder.indexOf("/cancel/") != -1) {
        String strReceivedd = String(strOrder.substring(strOrder.lastIndexOf("/") + 1, strOrder.length()));
        long alarmId = atol(strReceivedd.c_str());

        cancelAlarm(alarmId);
      }
      // * * * * * * * * * *
      // Power on/off
      else if(strOrder.indexOf("/simple/") != -1) {
          if(strOrder.indexOf("/1/") != -1) {
              if (strOrder.indexOf("/on") != -1) {
                turnOn(1);
              }
              else if(strOrder.indexOf("/off") != -1) {
                turnOff(1);
              }
          }
          else if(strOrder.indexOf("/2/") != -1) {
              if (strOrder.indexOf("/on") != -1) {
                turnOn(2);
              }
              else if(strOrder.indexOf("/off") != -1) {
                turnOff(2);
              }
          }

          response = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n";
          response += "{\"ok\":true,\"messages\":[""]}";
      }
      // * * * * * * * * * *
      // Add Alarm
      else if(strOrder.indexOf("/schedule/") != -1) {
        int numberOfAlarmThatSet = 0;
        for(int i = 0; i < ALARM_SIZE; i++) {
            if(isAlarmSetArray[i])
              numberOfAlarmThatSet++;
        }
        // max of alarm is 15
        if(numberOfAlarmThatSet < 15) {

            if(strOrder.indexOf("/1/") != -1) {
                if (strOrder.indexOf("/on/") != -1) {
                  String strReceived = String(strOrder.substring(strOrder.lastIndexOf("/") + 1, strOrder.length()));
                  // split receive string by sign '&'
                  int firstSign = strReceived.indexOf('&');
                  int secondSign = strReceived.indexOf('&', firstSign + 1);
                  int thirdSign = strReceived.indexOf('&', secondSign + 1);

                  // alarm info from receive string
                  String strAlarmName = String(strReceived.substring(0, firstSign));
                  String strWhenSetTime = String(strReceived.substring(firstSign + 1, secondSign));
                  String strExecuteTime = String(strReceived.substring(secondSign + 1, thirdSign));
                  String strIntervalTime = String(strReceived.substring(thirdSign + 1, strOrder.length()));

                  // convert received interval time to long
                  long intervalTime = atol(strIntervalTime.c_str());

                  int alarmId = Alarm.timerOnce(intervalTime, turnOnPlug1Schedule);
                  isAlarmSetArray[alarmId] = true;
                  plugNumArray[alarmId] = 1;
                  alarmNameArray[alarmId] = strAlarmName;
                  whenSetTimeArray[alarmId] = strWhenSetTime;
                  executeTimeArray[alarmId] = strExecuteTime;
                  alarmStatusArray[alarmId] = "on";
                }
                else if(strOrder.indexOf("/off/") != -1) {
                  String strReceived = String(strOrder.substring(strOrder.lastIndexOf("/") + 1, strOrder.length()));
                  // split receive string by sign '&'
                  int firstSign = strReceived.indexOf('&');
                  int secondSign = strReceived.indexOf('&', firstSign + 1);
                  int thirdSign = strReceived.indexOf('&', secondSign + 1);

                  // alarm info from receive string
                  String strAlarmName = String(strReceived.substring(0, firstSign));
                  String strWhenSetTime = String(strReceived.substring(firstSign + 1, secondSign));
                  String strExecuteTime = String(strReceived.substring(secondSign + 1, thirdSign));
                  String strIntervalTime = String(strReceived.substring(thirdSign + 1, strOrder.length()));

                  // convert received interval time to long
                  long intervalTime = atol(strIntervalTime.c_str());

                  int alarmId = Alarm.timerOnce(intervalTime, turnOffPlug1Schedule);
                  isAlarmSetArray[alarmId] = true;
                  plugNumArray[alarmId] = 1;
                  alarmNameArray[alarmId] = strAlarmName;
                  whenSetTimeArray[alarmId] = strWhenSetTime;
                  executeTimeArray[alarmId] = strExecuteTime;
                  alarmStatusArray[alarmId] = "off";
                }
            }
            else if(strOrder.indexOf("/2/") != -1) {
                if (strOrder.indexOf("/on/") != -1) {
                  String strReceived = String(strOrder.substring(strOrder.lastIndexOf("/") + 1, strOrder.length()));
                  // split receive string by sign '&'
                  int firstSign = strReceived.indexOf('&');
                  int secondSign = strReceived.indexOf('&', firstSign + 1);
                  int thirdSign = strReceived.indexOf('&', secondSign + 1);

                  // alarm info from receive string
                  String strAlarmName = String(strReceived.substring(0, firstSign));
                  String strWhenSetTime = String(strReceived.substring(firstSign + 1, secondSign));
                  String strExecuteTime = String(strReceived.substring(secondSign + 1, thirdSign));
                  String strIntervalTime = String(strReceived.substring(thirdSign + 1, strOrder.length()));

                  // convert received interval time to long
                  long intervalTime = atol(strIntervalTime.c_str());

                  int alarmId = Alarm.timerOnce(intervalTime, turnOnPlug2Schedule);
                  isAlarmSetArray[alarmId] = true;
                  plugNumArray[alarmId] = 2;
                  alarmNameArray[alarmId] = strAlarmName;
                  whenSetTimeArray[alarmId] = strWhenSetTime;
                  executeTimeArray[alarmId] = strExecuteTime;
                  alarmStatusArray[alarmId] = "on";
                }
                else if(strOrder.indexOf("/off/") != -1) {
                  String strReceived = String(strOrder.substring(strOrder.lastIndexOf("/") + 1, strOrder.length()));
                  // split receive string by sign '&'
                  int firstSign = strReceived.indexOf('&');
                  int secondSign = strReceived.indexOf('&', firstSign + 1);
                  int thirdSign = strReceived.indexOf('&', secondSign + 1);

                  // alarm info from receive string
                  String strAlarmName = String(strReceived.substring(0, firstSign));
                  String strWhenSetTime = String(strReceived.substring(firstSign + 1, secondSign));
                  String strExecuteTime = String(strReceived.substring(secondSign + 1, thirdSign));
                  String strIntervalTime = String(strReceived.substring(thirdSign + 1, strOrder.length()));

                  // convert received interval time to int
                  long intervalTime = atol(strIntervalTime.c_str());

                  int alarmId = Alarm.timerOnce(intervalTime, turnOffPlug2Schedule);
                  isAlarmSetArray[alarmId] = true;
                  plugNumArray[alarmId] = 2;
                  alarmNameArray[alarmId] = strAlarmName;
                  whenSetTimeArray[alarmId] = strWhenSetTime;
                  executeTimeArray[alarmId] = strExecuteTime;
                  alarmStatusArray[alarmId] = "off";
                }
            }

            response = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n";
            response += "{\"ok\":true,\"messages\":[""]}";
        }
        else {
            response = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n";
            response += "{\"ok\":false,\"messages\":[\"1001\"]}";
        }
      }

  client.print(response);
  client.flush();
}

// * * * * * * * * * * * * * * * * * * * *
// Turn on
// * * * * * * * * * * * * * * * * * * * *
void turnOn(int plugNum){
  if (plugNum == 1) {
    digitalWrite(PLUG_1, HIGH);
    plugStatus[0] = "on";
  }
  else if (plugNum == 2) {
    digitalWrite(PLUG_2, HIGH);
    plugStatus[1] = "on";
  }
  response = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n";
  response += "{\"ok\":true,\"messages\":[""]}";
  client.print(response);
  delay(1);
}

// * * * * * * * * * * * * * * * * * * * *
// Turn off
// * * * * * * * * * * * * * * * * * * * *
void turnOff(int plugNum){
  if (plugNum == 1) {
    digitalWrite(PLUG_1, LOW);
    plugStatus[0] = "off";
  }
  else if (plugNum == 2) {
    digitalWrite(PLUG_2, LOW);
    plugStatus[1] = "off";
  }
  response = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n";
  response += "{\"ok\":true,\"messages\":[""]}";
  client.print(response);
  delay(1);
}

// * * * * * * * * * * * * * * * * * * * *
// Turn on Plug 1 (schedule)
// * * * * * * * * * * * * * * * * * * * *
void turnOnPlug1Schedule(){
  digitalWrite(PLUG_1, HIGH);
  plugStatus[0] = "on";
  // unset this alarm from alarms info
  int alarmId = Alarm.getTriggeredAlarmId();
  isAlarmSetArray[alarmId] = false;
  plugNumArray[alarmId] = 1;
  alarmNameArray[alarmId] = "";
  whenSetTimeArray[alarmId] = "";
  executeTimeArray[alarmId] = "";
  alarmStatusArray[alarmId] = "";

  response = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n";
  response += "{\"ok\":true,\"messages\":[""]}";
  client.print(response);
  delay(1);
}

// * * * * * * * * * * * * * * * * * * * *
// Turn off Plug 1 (schedule)
// * * * * * * * * * * * * * * * * * * * *
void turnOffPlug1Schedule(){
  digitalWrite(PLUG_1, LOW);
  plugStatus[0] = "off";
  // unset this alarm from alarms info
  int alarmId = Alarm.getTriggeredAlarmId();
  isAlarmSetArray[alarmId] = false;
  plugNumArray[alarmId] = 1;
  alarmNameArray[alarmId] = "";
  whenSetTimeArray[alarmId] = "";
  executeTimeArray[alarmId] = "";
  alarmStatusArray[alarmId] = "";

  response = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n";
  response += "{\"ok\":true,\"messages\":[""]}";
  client.print(response);
  delay(1);
}

// * * * * * * * * * * * * * * * * * * * *
// Turn on Plug 2 (schedule)
// * * * * * * * * * * * * * * * * * * * *
void turnOnPlug2Schedule(){
  digitalWrite(PLUG_2, HIGH);
  plugStatus[1] = "on";
  // unset this alarm from alarms info
  int alarmId = Alarm.getTriggeredAlarmId();
  isAlarmSetArray[alarmId] = false;
  plugNumArray[alarmId] = 2;
  alarmNameArray[alarmId] = "";
  whenSetTimeArray[alarmId] = "";
  executeTimeArray[alarmId] = "";
  alarmStatusArray[alarmId] = "";

  response = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n";
  response += "{\"ok\":true,\"messages\":[""]}";
  client.print(response);
  delay(1);
}

// * * * * * * * * * * * * * * * * * * * *
// Turn off Plug 2 (schedule)
// * * * * * * * * * * * * * * * * * * * *
void turnOffPlug2Schedule(){
  digitalWrite(PLUG_2, LOW);
  plugStatus[1] = "off";
  // unset this alarm from alarms info
  int alarmId = Alarm.getTriggeredAlarmId();
  isAlarmSetArray[alarmId] = false;
  plugNumArray[alarmId] = 2;
  alarmNameArray[alarmId] = "";
  whenSetTimeArray[alarmId] = "";
  executeTimeArray[alarmId] = "";
  alarmStatusArray[alarmId] = "";

  response = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n";
  response += "{\"ok\":true,\"messages\":[""]}";
  client.print(response);
  delay(1);
}

// * * * * * * * * * * * * * * * * * * * *
// Cancel alarm
// * * * * * * * * * * * * * * * * * * * *
void cancelAlarm(int alarmId){
    Alarm.free(alarmId);
    isAlarmSetArray[alarmId] = false;
    plugNumArray[alarmId] = 1;
    alarmNameArray[alarmId] = "";
    whenSetTimeArray[alarmId] = "";
    executeTimeArray[alarmId] = "";
    alarmStatusArray[alarmId] = "";

    response = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n";
    response += "{\"ok\":true,\"messages\":[""]}";
    client.print(response);
    delay(1);
}

// * * * * * * * * * * * * * * * * * * * *
// SYNC
// * * * * * * * * * * * * * * * * * * * *
void sync(){
  response = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n";
  response += "{\"ok\":true,\"messages\":[""],\"data\":{\"plugs\":[";
    for(int i = 0; i < PLUG_SIZE; i++) {
      String str = "";
      str += "{\"plugNum\":";
      str += plugNum[i];
      str += ",\"plugName\":\"";
      str += plugName[i];
      str += "\",\"plugStatus\":\"";
      str += plugStatus[i];
      str += "\"}";
      if (i != 1) {
        response += str;
        response += ",";
      }
      else {
        response += str;
      }
    }
  response += "],\"alarms\":[";
  int lastAlarmIdThatSet = -1;
  for(int i = 0; i < ALARM_SIZE; i++) {
    if(isAlarmSetArray[i])
      lastAlarmIdThatSet = i;
  }
  for(int i = 0; i < ALARM_SIZE; i++) {
      if(isAlarmSetArray[i]) {
        String str = "";
        str += "{\"alarmId\":";
        str += i;
        str += ",\"plugNum\":";
        str += plugNumArray[i];
        str += ",\"alarmName\":\"";
        str += alarmNameArray[i];
        str += "\",\"whenSetTime\":\"";
        str += whenSetTimeArray[i];
        str += "\",\"executeTime\":\"";
        str += executeTimeArray[i];
        str += "\",\"alarmStatus\":\"";
        str += alarmStatusArray[i];
        str += "\"}";
        if (lastAlarmIdThatSet != -1 && i != lastAlarmIdThatSet) {
          response += str;
          response += ",";
        }
        else {
          response += str;
        }
      }
  }
  response += "]}}";
  client.print(response);
  delay(1);
}

// * * * * * * * * * * * * * * * * * * * *
// Init Hardware
// * * * * * * * * * * * * * * * * * * * *
void initHardware()
{
  Serial.begin(9600);
  pinMode(PLUG_1, OUTPUT);
  pinMode(PLUG_2, OUTPUT);
  digitalWrite(PLUG_1, HIGH);
  digitalWrite(PLUG_2, HIGH);

  // init plugs
  plugNum[0] = 1;
  plugName[0] = "Plug 1";
  plugStatus[0] = "on";
  plugNum[1] = 2;
  plugName[1] = "Plug 2";
  plugStatus[1] = "on";
}

// * * * * * * * * * * * * * * * * * * * *
// Custom Setup WiFi
// * * * * * * * * * * * * * * * * * * * *
void customSetupWiFi(String ssid, String password) {
  WiFi.mode(WIFI_AP);
  char NEW_Name[ssid.length()];
  char NEW_Password[password.length()];
  for (int i=0; i<ssid.length(); i++)
    NEW_Name[i] = ssid.charAt(i);
  for (int i=0; i<password.length(); i++)
    NEW_Password[i] = password.charAt(i);

  WiFi.softAP(NEW_Name, NEW_Password);
  IPAddress local_ip = IPAddress(192, 168, 0, 1);
  IPAddress gateway_ip = IPAddress(192, 168, 0, 1);
  IPAddress subnet_ip = IPAddress(255, 255, 255, 0);
  WiFi.softAPConfig(local_ip, gateway_ip, subnet_ip);
}

// * * * * * * * * * * * * * * * * * * * *
// Setup WiFi
// * * * * * * * * * * * * * * * * * * * *
void setupWiFi() {
  WiFi.mode(WIFI_AP);
  // * * * * * * * * * *
  // create AP name
      uint8_t mac[WL_MAC_ADDR_LENGTH];
      WiFi.softAPmacAddress(mac);
      String macID = String(mac[WL_MAC_ADDR_LENGTH - 2], HEX) +
                     String(mac[WL_MAC_ADDR_LENGTH - 1], HEX);
      macID.toUpperCase();
      String AP_Name = AP_PRE_NAME + macID;

      char AP_NameChar[AP_Name.length() + 1];
      memset(AP_NameChar, 0, AP_Name.length() + 1);

      for (int i=0; i<AP_Name.length(); i++)
          AP_NameChar[i] = AP_Name.charAt(i);
  // * * * * * * * * * *
  WiFi.softAP(AP_NameChar, AP_Password);

  IPAddress local_ip = IPAddress(192, 168, 0, 1);
  IPAddress gateway_ip = IPAddress(192, 168, 0, 1);
  IPAddress subnet_ip = IPAddress(255, 255, 255, 0);
  WiFi.softAPConfig(local_ip, gateway_ip, subnet_ip);
//  while (WiFi.status() != WL_CONNECTED) {
    delay(10000);
//    Serial.print(".");
//  }
}
