JFLAGS = -g
JC = javac
JVM = java
NRO=5
BEARER=2
SERVER=servidor/SemaforoImp
PROCESS=cliente/Process
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	cliente/Semaforo.java \
	cliente/Token.java \
	cliente/Process.java \
	servidor/SemaforoImp.java \

MAIN = process

default: classes

classes: $(CLASSES:.java=.class)

target: proceso0 proceso1 proceso2 proceso3 proceso4


servidor:
	$(JVM) $(SERVER) $(NRO)

proceso0:
ifeq (0,$(BEARER))
	$(JVM) $(PROCESS) 0 $(NRO) 1000 true
else
	$(JVM) $(PROCESS) 0 $(NRO) 1000 false
endif
proceso1:
ifeq (1,$(BEARER))
	$(JVM) $(PROCESS) 1 $(NRO) 2000 true
else
	$(JVM) $(PROCESS) 1 $(NRO) 2000 false
endif
proceso2:
ifeq (2,$(BEARER))
	$(JVM) $(PROCESS) 2 $(NRO) 3000 true
else
	$(JVM) $(PROCESS) 2 $(NRO) 3000 false
endif
proceso3:
ifeq (3,$(BEARER))
	$(JVM) $(PROCESS) 3 $(NRO) 1000 true
else
	$(JVM) $(PROCESS) 3 $(NRO) 1000 false
endif
proceso4:
ifeq (4,$(BEARER))
	$(JVM) $(PROCESS) 4 $(NRO) 4000 true
else
	$(JVM) $(PROCESS) 4 $(NRO) 4000 false
endif
run: classes

clean:
	$(RM) *.class
	$(RM) cliente/*.class
	$(RM) servidor/*.class
	$(RM) log.txt
