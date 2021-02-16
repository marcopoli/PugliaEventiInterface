
import cgi, cgitb 
cgitb.enable()  # for troubleshooting

#the cgi library gets vars from html
data = cgi.FieldStorage()
#this is the actual output
print ("Content-Type: text/html\n")
print ("Event1 is: ",data["event1"].value)
print ("<br />")
print ("Event2 is: ",data["event2"].value)
print ("<br />")
#print (data)