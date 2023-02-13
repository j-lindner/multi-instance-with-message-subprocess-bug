When modelling process like
![mi-properties](multi-instance-properties.png)
![message-subprocess-properties](event-subprocess-properties.png)
Incident occurs in Operate "failed to evaluate expression 'myObject.myId': no variable found for name 'myObject'"

Workaround: put another subprocess in multi-instance to make sure input collection gets processed before event-subprocess (probably :-))
![message-subprocess-properties](workaround.png)

failing test and working test (using workaround) provided
