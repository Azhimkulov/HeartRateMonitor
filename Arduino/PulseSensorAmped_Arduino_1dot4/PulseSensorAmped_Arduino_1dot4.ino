//int PulseSensorPurplePin = 0;
int LED = 13;

//int Threshold = 550; 

int pulsePin = A0; 
int blinkPin = 13;

volatile int BPM;
volatile int Signal;
volatile int IBI = 600;
volatile boolean Pulse = false; 
volatile boolean QS = false;

void setup() {
  pinMode(LED,OUTPUT);
  Serial.begin(9600);
  interruptSetup(); 
   
}
void loop() {

  

   Signal = analogRead(pulsePin);
   
   Serial.print('G');
   Serial.print(Signal);
   Serial.print("ES");

   if(QS == true)
   {
      delay(100);
      
      Serial.print('G');
      Serial.print(BPM);
      Serial.print("BS");
      QS = false;
   }

   delay(100);
}
