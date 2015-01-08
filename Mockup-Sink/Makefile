SENSORBOARD=mts300
COMPONENT=MockupC
PFLAGS=-I../CommunicationLayer/nesc -I../../3-Learn/LearningModule-Sink -I../CommunicationLayer/nesc/MTS_EM1000 -I../CommunicationLayer/nesc/MTS_SE1000
CFLAGS += -I$(TOSDIR)/lib/printf
CFLAGS += -DPRINTF_BUFFER_SIZE=1024
CFLAGS += -DTOSH_DATA_LENGTH=114

ifeq ($(SS),YES)
	PFLAGS+=-I../StreamSystem
	CFLAGS += -DSS
endif

#PFLAGS+=-DRF230_DEF_RFPOWER=15
#the following instruction are needed by the MIG tool to automatically generate 
#the java classes corresponding to the messages (struct) defined in the mockup.h file
JAVAOPTS= -source 1.4 -target 1.4
BUILD_EXTRA_DEPS += SerialJoinedMsg.class NetIdCmdMsg.class BroadcastCmdMsg.class SynCreateCmdMsg.class LearningModuleCmdMsg.class LearningModuleFloatCmdMsg.class LearningNotificationMsg.class LearningModuleDownMsg.class LearningOutputMsg.class UpdatesMsg.class ReadUpdatesMsg.class NetMsg.class ConfigMsg.class RAddr.class AppMsg.class
CLEAN_EXTRA = *.class SerialJoinedMsg.java NetIdCmdMsg.java BroadcastCmdMsg.java SynCreateCmdMsg.java LearningModuleCmdMsg.java LearningModuleFloatCmdMsg.java LearningNotificationMsg.java LearningModuleDownMsg.java LearningOutputMsg.java UpdatesMsg.java ReadUpdatesMsg.java NetMsg.java ConfigMsg.java RAddr.class AppMsg.java

#SerialReader.class: $(wildcard *.java) SerialReader.java
#	javac -target 1.4 -source 1.4 *.java
	
#SerialDataMsg.java: mockup.h
#	mig java -target=$(PLATFORM) -java-classname=SerialDataMsg mockup.h serial_data_msg -o $@
#SerialDataMsg.class: SerialDataMsg.java
#	javac $(JAVAOPTS) -d . SerialDataMsg.java

#CommandMsg.java: mockup.h
#	mig java -target=$(PLATFORM) -java-classname=CommandMsg mockup.h command_msg -o $@
#CommandMsg.class: CommandMsg.java
#	javac $(JAVAOPTS) -d . CommandMsg.java
	
SerialJoinedMsg.java: ../CommunicationLayer/nesc/network.h
	mig java -target=$(PLATFORM) -java-classname=SerialJoinedMsg ../CommunicationLayer/nesc/network.h serial_joined_msg -o $@ && cp $@ ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/ && echo 'package cnr.rubicon.migFiles;' | cat - ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@ > temp && mv temp ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@
SerialJoinedMsg.class: SerialJoinedMsg.java
	javac $(JAVAOPTS) -d . SerialJoinedMsg.java
	
NetIdCmdMsg.java: mockup.h
	mig java -target=$(PLATFORM) -java-classname=NetIdCmdMsg mockup.h netid_cmd_msg -o $@ && cp $@ ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/ && echo 'package cnr.rubicon.migFiles;' | cat - ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@ > temp && mv temp ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@
NetIdCmdMsg.class: NetIdCmdMsg.java
	javac $(JAVAOPTS) -d . NetIdCmdMsg.java
	
BroadcastCmdMsg.java: mockup.h
	mig java -target=$(PLATFORM) -java-classname=BroadcastCmdMsg mockup.h broadcast_cmd_msg -o $@ && cp $@ ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/ && echo 'package cnr.rubicon.migFiles;' | cat - ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@ > temp && mv temp ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@
BroadcastCmdMsg.class: BroadcastCmdMsg.java
	javac $(JAVAOPTS) -d . BroadcastCmdMsg.java

SynCreateCmdMsg.java: mockup.h
	mig java -target=$(PLATFORM) -java-classname=SynCreateCmdMsg mockup.h syn_create_cmd_msg -o $@ && cp $@ ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/ && echo 'package cnr.rubicon.migFiles;' | cat - ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@ > temp && mv temp ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@
SynCreateCmdMsg.class: SynCreateCmdMsg.java
	javac $(JAVAOPTS) -d . SynCreateCmdMsg.java
	
LearningModuleCmdMsg.java: mockup.h
	mig java -target=$(PLATFORM) -java-classname=LearningModuleCmdMsg mockup.h learning_module_cmd_msg -o $@ && cp $@ ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/ && echo 'package cnr.rubicon.migFiles;' | cat - ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@ > temp && mv temp ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@
LearningModuleCmdMsg.class: LearningModuleCmdMsg.java
	javac $(JAVAOPTS) -d . LearningModuleCmdMsg.java

LearningModuleFloatCmdMsg.java: mockup.h
	mig java -target=$(PLATFORM) -java-classname=LearningModuleFloatCmdMsg mockup.h learning_module_float_cmd_msg -o $@ && cp $@ ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/ && echo 'package cnr.rubicon.migFiles;' | cat - ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@ > temp && mv temp ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@
LearningModuleFloatCmdMsg.class: LearningModuleFloatCmdMsg.java
	javac $(JAVAOPTS) -d . LearningModuleFloatCmdMsg.java

LearningNotificationMsg.java: mockup.h
	mig java -target=$(PLATFORM) -java-classname=LearningNotificationMsg mockup.h learning_notification_msg -o $@ && cp $@ ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/ && echo 'package cnr.rubicon.migFiles;' | cat - ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@ > temp && mv temp ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@
LearningNotificationMsg.class: LearningNotificationMsg.java
	javac $(JAVAOPTS) -d . LearningNotificationMsg.java
	
LearningModuleDownMsg.java: mockup.h
	mig java -target=$(PLATFORM) -java-classname=LearningModuleDownMsg mockup.h learning_module_down_msg -o $@ && cp $@ ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/ && echo 'package cnr.rubicon.migFiles;' | cat - ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@ > temp && mv temp ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@
LearningModuleDownMsg.class: LearningModuleDownMsg.java
	javac $(JAVAOPTS) -d . LearningModuleDownMsg.java
	
LearningOutputMsg.java: mockup.h
	mig java -target=$(PLATFORM) -java-classname=LearningOutputMsg mockup.h learning_output_msg -o $@ && cp $@ ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/ && echo 'package cnr.rubicon.migFiles;' | cat - ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@ > temp && mv temp ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@
LearningOutputMsg.class: LearningOutputMsg.java
	javac $(JAVAOPTS) -d . LearningOutputMsg.java

UpdatesMsg.java: mockup.h
	mig java -target=$(PLATFORM) -java-classname=UpdatesMsg mockup.h updates_msg -o $@ && cp $@ ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/ && echo 'package cnr.rubicon.migFiles;' | cat - ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@ > temp && mv temp ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@
UpdatesMsg.class: UpdatesMsg.java
	javac $(JAVAOPTS) -d . UpdatesMsg.java

ReadUpdatesMsg.java: mockup.h
	mig java -target=$(PLATFORM) -java-classname=ReadUpdatesMsg mockup.h read_updates_msg -o $@ && cp $@ ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/ && echo 'package cnr.rubicon.migFiles;' | cat - ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@ > temp && mv temp ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@
ReadUpdatesMsg.class: ReadUpdatesMsg.java
	javac $(JAVAOPTS) -d . ReadUpdatesMsg.java

#ConnlessMsg.java: mockup.h
#	mig java -target=$(PLATFORM) -java-classname=ConnlessMsg mockup.h connless_msg -o ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@
#ConnlessMsg.class: ConnlessMsg.java
#	javac $(JAVAOPTS) -d . ConnlessMsg.java

NetMsg.java: ../CommunicationLayer/nesc/network.h
	mig java -target=$(PLATFORM) -java-classname=NetMsg ../CommunicationLayer/nesc/network.h net_msg -o $@ && cp $@ ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/ && echo 'package cnr.rubicon.migFiles;' | cat - ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@ > temp && mv temp ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@
NetMsg.class: NetMsg.java
	javac $(JAVAOPTS) -d . NetMsg.java

ConfigMsg.java: ../CommunicationLayer/nesc/network.h
	mig java -target=$(PLATFORM) -java-classname=ConfigMsg ../CommunicationLayer/nesc/network.h config_msg -o $@ && cp $@ ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/ && echo 'package cnr.rubicon.migFiles;' | cat - ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@ > temp && mv temp ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@
ConfigMsg.class: ConfigMsg.java
	javac $(JAVAOPTS) -d . ConfigMsg.java

RAddr.java: ../CommunicationLayer/nesc/definitions.h
	mig java -target=$(PLATFORM) -java-classname=RAddr ../CommunicationLayer/nesc/definitions.h r_addr -o $@ && cp $@ ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/ && echo 'package cnr.rubicon.migFiles;' | cat - ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@ > temp && mv temp ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@
RAddr.class: RAddr.java
	javac $(JAVAOPTS) -d . RAddr.java

AppMsg.java: ../CommunicationLayer/nesc/definitions.h
	mig java -target=$(PLATFORM) -java-classname=AppMsg ../CommunicationLayer/nesc/definitions.h app_msg -o $@ && cp $@ ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/ && echo 'package cnr.rubicon.migFiles;' | cat - ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@ > temp && mv temp ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/$@
AppMsg.class: AppMsg.java
	javac $(JAVAOPTS) -d . AppMsg.java

#DemoMsg.java: ../CommunicationLayer/nesc/network.h
#	mig java -target=$(PLATFORM) -java-classname=DemoMsg ../CommunicationLayer/nesc/network.h demo_msg -o $@
#DemoMsg.class: DemoMsg.java
#	javac $(JAVAOPTS) -d . DemoMsg.java

#CompleteMsg.java: ../CommunicationLayer/nesc/network.h
#	mig java -target=$(PLATFORM) -java-classname=CompleteMsg ../CommunicationLayer/nesc/network.h complete_msg -o $@
#CompleteMsg.class: CompleteMsg.java
#	javac $(JAVAOPTS) -d . CompleteMsg.java

#DemoDataMsg.java: mockup.h
#	mig java -target=$(PLATFORM) -java-classname=DemoDataMsg mockup.h demo_data_msg -o $@
#DemoDataMsg.class: DemoDataMsg.java
#	javac $(JAVAOPTS) -d . DemoDataMsg.java

#jar cf ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/migPackage.jar ../cnr.rubicon.cml/src/cnr/rubicon/migFiles/\*.class
	
include $(MAKERULES)
#CFLAGS+=-D_FORTIFY_SOURCE=0
#CFLAGS += -DCC2420_NO_ACKNOWLEDGEMENTS
#CFLAGS += -DCC2420_NO_ADDRESS_RECOGNITION
#CFLAGS += -DTASKLET_IS_TASK
