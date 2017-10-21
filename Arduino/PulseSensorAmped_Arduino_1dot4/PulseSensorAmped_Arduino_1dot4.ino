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
   Serial.print('S');

   if(QS == true)
   {;
//      Serial.print('G');
//      Serial.print(BPM);
//      Serial.print('B');
      QS = false;
   }

   delay(100);
}


