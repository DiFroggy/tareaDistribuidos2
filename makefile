JFLAGS = -g
JC = javac
JVM = java
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	cliente/Semaforo.java \
	cliente/Token.java \
	cliente/process.java \
	servidor/SemaforoImp.java \

MAIN = process

default: classes

classes: $(CLASSES:.java=.class)

run:	$(MAIN)
	$(JVM) $(MAIN)
	
	

clean:
	$(RM) *.class
	$(RM) cliente/*.class
	$(RM) servidor/*.class
	$(RM) log.txt
