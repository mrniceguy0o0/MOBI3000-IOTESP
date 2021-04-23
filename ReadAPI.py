import time
import RPi.GPIO as GPIO
import sys
import urllib2, json

READ_API_KEY='RUGONLXB7P486W10'
CHANNEL_ID='1344637'

def main():
    conn = urllib2.urlopen("http://api.thingspeak.com/channels/%s/status.json?api_key=%s" \
                            % (CHANNEL_ID,READ_API_KEY))
    response = conn.read()
    print "http status code=%s" % (conn.getcode())
    data=json.loads(response)
    print data['- channel'].name#,data['created_at']
    conn.close()
    
# call main
if __name__ == '__main__':
    main()