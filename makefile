JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	server.java \
	cliente/Semaforo.java \
	cliente/Token.java \
	cliente/process.java \
	servidor/SemaforoImp.java \

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
	$(RM) cliente/*.class
	$(RM) servidor/*.class