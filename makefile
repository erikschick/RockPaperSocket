JCC = javac

default: RockPaperSocket.class

RockPaperSocket.class: RockPaperSocket.java
	$(JCC) RockPaperSocket.java

.PHONY: clean
clean: 
	rm -rf *.class