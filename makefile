JCC = javac

default: all

all: RockPaperSocket.class RockPaperSocketGUI.class

RockPaperSocket.class: RockPaperSocket.java
	$(JCC) RockPaperSocket.java

RockPaperSocketGUI.class: RockPaperSocketGUI.java
	$(JCC) RPSpanel.java RockPaperSocketGUI.java

.PHONY: clean
clean: 
	rm -rf *.class