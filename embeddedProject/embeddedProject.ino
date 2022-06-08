//
#include <DHT.h>                    // 온습도 센서 모듈 라이브러리
#include <Wire.h>
#include <LiquidCrystal_I2C.h>      // LCD 모듈 라이브러리
#include <Emotion_Farm.h>           // 특수 문자 및 이모티콘 라이브러리
//
#define DHTPIN 14                    // 온습도센서 모듈 핀
#define DHTTYPE DHT11               // 온습도 센서타입 설정
#define soilmoisturePin A0         // 토양수분센서 핀
//
LiquidCrystal_I2C lcd(0x27, 16, 2); //LCD 초기화 (LCD 주소값, x축, y축)
DHT dht(DHTPIN, DHTTYPE);           //온습도 센서 모듈
//

 
// 문자열을 출력하기 위한 변수
char str_M[10];
char str_T[10];
char str_H[10];

#include <string.h>
#include "FirebaseESP8266.h"
#include <ESP8266WiFi.h>


#define FIREBASE_HOST "plantproject-51423-default-rtdb.firebaseio.com" //앞에 http 빼야함
#define FIREBASE_AUTH "3ffcF0l2YTxxni3w8VAqsQ3XTnJGAgyDq0Z5Yqa8" // 데이터베이스 비밀번호

#define WIFI_SSID "KSM1226" // 연결 가능한 wifi의 ssid
#define WIFI_PASSWORD "ksm122626" // wifi 비밀번호
//#define WIFI_SSID "sw718_5G" // 연결 가능한 wifi의 ssid
//#define WIFI_PASSWORD "knusw=9216" // wifi 비밀번호


FirebaseData firebaseData;
FirebaseJson json;


int Relaypin = D2;

//char* pump_state = "watering";

void setup() {
  pinMode(Relaypin, OUTPUT);
  
  Serial.begin(115200);

  pinMode(soilmoisturePin, INPUT);
//
      //LCD에 인트로 출력
  lcd.begin();
  lcd.clear();
  lcd.noBacklight();
  delay(500);
  lcd.backlight();
  delay(500);
  lcd.setCursor(1,0);
  lcd.print("Now");
  delay(1000);
  lcd.setCursor(6,0);
  lcd.print("Our Plant");
  delay(1000);
  lcd.setCursor(5,1);
  lcd.print("is...");
  delay(1000);
  lcd.clear();
//
//
  // 라이브러리로 추가한 특수 문자 및 이모티콘 추가
  lcd.createChar(0, temp);
  lcd.createChar(1, C);
  lcd.createChar(2, humi);  
  lcd.createChar(3, Qmark);
  lcd.createChar(4, water);
  lcd.createChar(5, good);
  lcd.createChar(6, wind);



  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.println();
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();
 
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);
 
  //Set the size of WiFi rx/tx buffers in the case where we want to work with large data.
  firebaseData.setBSSLBufferSize(1024, 1024);
  //Set the size of HTTP response buffers in the case where we want to work with large data.
  firebaseData.setResponseSize(1024);
  //Set database read timeout to 1 minute (max 15 minutes)
  Firebase.setReadTimeout(firebaseData, 1000 * 60);
  //tiny, small, medium, large and unlimited.
  //Size and its write timeout e.g. tiny (1s), small (10s), medium (30s) and large (60s).
  Firebase.setwriteSizeLimit(firebaseData, "tiny");



}


void loop() {


  int soilmoistureValue = analogRead(soilmoisturePin);               // 토양수분 값 측정: 0(습함) ~ 1023(건조)
//  Serial.println("토양수분 값");
  Serial.println(soilmoistureValue);
  int soilmoisture_per = map(soilmoistureValue, 400, 1023, 100, 0);  // 센서 값을 퍼센트로 변경
  if(soilmoisture_per >= 90)
    soilmoisture_per = 85;

  unsigned char h_Value = dht.readHumidity();                        // 공기 중 습도 값 측정
  unsigned char t_Value = dht.readTemperature();                     // 공기 중 온도 값 측정

  float humi = dht.readHumidity();                        // 공기 중 습도 값 측정
  float temp = dht.readTemperature();                     // 공기 중 온도 값 측정

  if (isnan(humi) || isnan(temp)) // nan 값이 들어간 경우
  {
    delay(2000);
    return;
  }
  
  else // 값이 올바르게 들어간 경우 
  {
   Serial.println(humi);
   Serial.println(temp);  
  }

  //LCD에 토양 습도값 출력
  lcd.setCursor(1,0);
  lcd.print("MOIST:");
  sprintf(str_M, "%03d", soilmoisture_per);
  lcd.print(str_M);
  lcd.setCursor(10,0);
  lcd.print("%");

  //LCD에 온도값 출력
  lcd.setCursor(1,1);
  lcd.write(0);
  sprintf(str_T, "%02d", t_Value);
  lcd.setCursor(3,1);
  lcd.print(str_T);
  lcd.write(1);

  //LCD에 습도값 출력
  lcd.setCursor(7,1);
  lcd.write(2);
  sprintf(str_H, "%02d", h_Value);
  lcd.setCursor(9,1);
  lcd.print(str_H);
  lcd.print("%");


//  토양습도 값에 따른 LCD에 이모티콘 띄우기
  if(soilmoisture_per >= 0 && soilmoisture_per < 30){
    lcd.setCursor(13,0);
    lcd.write(3);
    lcd.setCursor(14,0);
    lcd.write(4);
  }
  else if(soilmoisture_per >= 30 && soilmoisture_per < 70){
    lcd.setCursor(13,0);
    lcd.print(" ");
    lcd.setCursor(14,0);
    lcd.write(5);
  }
  else if(soilmoisture_per >= 70){ 
    lcd.setCursor(13,0);
    lcd.write(3);
    lcd.setCursor(14,0);
    lcd.write(6);
  }

  

  Firebase.setFloat(firebaseData, "plant/temp", temp);
  Firebase.setFloat(firebaseData, "plant/humi", humi);
  Firebase.setFloat(firebaseData, "plant/soil_humi", soilmoisture_per);


  if( Firebase.getString(firebaseData, "plant/setting")) {      //수동인지 자동인지 파악
    String setting_state = firebaseData.stringData();
    Serial.println(setting_state);

    if(setting_state=="auto")           //자동모드인 경우
    {
      if(soilmoisture_per <=25.0)       //토양수분 값이 25퍼 이하일 때 물 주기
      {
       StartA();
       delay(3000);
       StopA();
       Firebase.setString(firebaseData, "plant/pump", "stop");
  
       lcd.setCursor(1, 0);
       lcd.print("MOIST:     ");
      }
    }

    else if(setting_state=="manual") {   //수동모드인 경우
      
       if( Firebase.getString(firebaseData,"/plant/pump")){

       String pump_state = firebaseData.stringData();
       Serial.println(pump_state);
  
       if(pump_state =="watering")      //버튼이 눌렸을 때(watering)일 때만 물 주기
       {
         StartA();
         delay(3000);
         StopA();
         Firebase.setString(firebaseData, "plant/pump", "stop");
    
         lcd.setCursor(1, 0);
         lcd.print("MOIST:     ");
       }
     }
  }

  
  

    
     
  }
  
  delay(1000);

}

//모터A,B 정회전
void StartA()
{
    digitalWrite(Relaypin, HIGH);
}
 
//모터A,B Stop
void StopA()
{
  digitalWrite(Relaypin, LOW);

}
