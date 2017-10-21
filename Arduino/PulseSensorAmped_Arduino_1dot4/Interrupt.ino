


volatile int rate[10];                    // array to hold last ten IBI values
volatile unsigned long sampleCounter = 0;          // used to determine pulse timing
volatile unsigned long lastBeatTime = 0;           // used to find IBI
volatile int P =512;                      // used to find peak in pulse wave, seeded
volatile int T = 512;                     // used to find trough in pulse wave, seeded
volatile int thresh = 525;                // used to find instant moment of heart beat, seeded
volatile int amp = 100;                   // used to hold amplitude of pulse waveform, seeded
volatile boolean firstBeat = true;        // used to seed rate array so we startup with reasonable BPM
volatile boolean secondBeat = false;      // used to seed rate array so we startup with reasonable BPM


void interruptSetup(){     
  // Initializes Timer2 to throw an interrupt every 2mS.
  TCCR2A = 0x02;     // DISABLE PWM ON DIGITAL PINS 3 AND 11, AND GO INTO CTC MODE
  TCCR2B = 0x06;     // DON'T FORCE COMPARE, 256 PRESCALER 
  OCR2A = 0X7C;      // SET THE TOP OF THE COUNT TO 124 FOR 500Hz SAMPLE RATE
  TIMSK2 = 0x02;     // ENABLE INTERRUPT ON MATCH BETWEEN TIMER2 AND OCR2A
  sei();             // MAKE SURE GLOBAL INTERRUPTS ARE ENABLED      
} 


// THIS IS THE TIMER 2 INTERRUPT SERVICE ROUTINE. 
// Timer 2 makes sure that we take a reading every 2 miliseconds
ISR(TIMER2_COMPA_vect){                         // triggered when Timer2 counts to 124
  cli();                                      // disable interrupts while we do this
  Signal = analogRead(pulsePin);              // read the Pulse Sensor 
  sampleCounter += 2;                         // keep track of the time in mS with this variable
  int N = sampleCounter - lastBeatTime;       // monitor the time since the last beat to avoid noise

    //  find the peak and trough of the pulse wave
  if(Signal < thresh && N > (IBI/5)*3){      
    if (Signal < T){                    
      T = Signal;                       
    }
  }

  if(Signal > thresh && Signal > P){     
    P = Signal;                         
  }                             

  //  NOW IT'S TIME TO LOOK FOR THE HEART BEAT
  // signal surges up in value every time there is a pulse
  if (N > 250){                                  
    if ( (Signal > thresh) && (Pulse == false) && (N > (IBI/5)*3) ){        
      Pulse = true;                             
      digitalWrite(blinkPin,HIGH);           
      IBI = sampleCounter - lastBeatTime;      
      lastBeatTime = sampleCounter;             

      if(secondBeat){                        
        secondBeat = false;                  
        for(int i=0; i<=9; i++){             
          rate[i] = IBI;                      
        }
      }

      if(firstBeat){                         
        firstBeat = false;                  
        secondBeat = true;                   
        sei();                              
        return;                             
      }   


      // keep a running total of the last 10 IBI values
      word runningTotal = 0;                    

      for(int i=0; i<=8; i++){                
        rate[i] = rate[i+1];                  
        runningTotal += rate[i];              
      }

      rate[9] = IBI;                         
      runningTotal += rate[9];               
      runningTotal /= 10;                   
      BPM = 60000/runningTotal;              
      QS = true;                              
      // QS FLAG IS NOT CLEARED INSIDE THIS ISR
    }                       
  }

  if (Signal < thresh && Pulse == true){   
    digitalWrite(blinkPin,LOW);         
    Pulse = false;                        
    amp = P - T;                          
    thresh = amp/2 + T;             
    P = thresh;                         
    T = thresh;
  }

  if (N > 2500){                       
    thresh = 512;                        
    P = 512;                            
    T = 512;                             
    lastBeatTime = sampleCounter;         
    firstBeat = true;        
    secondBeat = false;      
  }

  sei(); 
}





