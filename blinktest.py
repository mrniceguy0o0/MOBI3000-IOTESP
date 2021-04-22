from time import sleep
import RPi.GPIO as GPIO
import sys
import urllib2

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)
GPIO.setup(8, GPIO.OUT, initial=GPIO.LOW)
GPIO.setup(10, GPIO.OUT, initial=GPIO.LOW)
# def mathMap(value, inMin, inMax, outMin, outMax):
#     return int((value-inMin)*(outMax-outMin)/(inMax-inMin)+(outMin))
    
# CHANNEL_ID = <1366211>    
    
def redON():
    GPIO.output(8,GPIO.HIGH)
    
def redOFF():
    GPIO.output(8,GPIO.LOW)
    
def greenON():
    GPIO.output(10,GPIO.HIGH)
    
def greenOFF():
    GPIO.output(10,GPIO.LOW)

def main():
    while True:
        redOFF()
        greenON()
        sleep(1)
        redON()
        greenOFF()
        sleep(1)
#     if len(sys.argv) < 2:
#         print('Usage: python tstest.py PRIVATE_KEY')
#         exit(0)
#     print 'starting...'
#     
#     baseURL = 'https://api.thingspeak.com/update?api_key=%s' % sys.argv[1]
#        
#     while True:
#         try:
#             ultrasound = getUltrasoundData()
#             infrared = getInfraredData()
#             
#             f = urllib2.urlopen(baseURL + "&field1=%s" % int(ultrasound/5))
#             print f.read()
#             f.close()
#              
#             g = urllib2.urlopen(baseURL + "&field2=%s" % int(infrared))
#             print g.read()
#             g.close()
#             
#             sleep(15)
#         except:
#             print 'exiting.'
#             break
#                    
# call main
if __name__ == '__main__':
    main()            
