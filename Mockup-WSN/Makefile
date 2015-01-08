#SENSORBOARD=SENSORBOARD_STANDARD
SENSORBOARD=SENSORBOARD_SE1000
#SENSORBOARD=SENSORBOARD_EM1000
COMPONENT=MockupC
PFLAGS=-I../CommunicationLayer/nesc -I../../2-Ctrl/ControlModule -I../../3-Learn/LearningModule -I../CommunicationLayer/nesc/MTS_SE1000 -I../CommunicationLayer/nesc/MTS_EM1000 -I../CommunicationLayer/nesc/NetReboot

#--------------------------------------------#
# Start of changes by Filippo Barontini      #
#--------------------------------------------#

PFLAGS+=-I../RssiRubicon/RssiTransducer/

#--------------------------------------------#
# End of changes by Filippo Barontini        #
#--------------------------------------------#

#PFLAGS+=-DRF230_DEF_RFPOWER=15
CFLAGS += -DTOSH_DATA_LENGTH=114

ifeq ($(SS),YES)
	PFLAGS+=-I../StreamSystem
	CFLAGS += -DSS
endif
	
ifdef AUTO
	CFLAGS += -DAUTO
endif

PFLAGS += -I../Accellerometer/	
ifeq ($(SENSORBOARD),SENSORBOARD_EM1000)
     CFLAGS += -DSENSORBOARD_EM1000
else
ifeq ($(SENSORBOARD),SENSORBOARD_SE1000)
     
     CFLAGS += -DSENSORBOARD_SE1000
else
     CFLAGS += -DSENSORBOARD_STANDARD
endif
endif

include $(MAKERULES)
#CFLAGS+=-D_FORTIFY_SOURCE=0
#CFLAGS += -DCC2420_NO_ACKNOWLEDGEMENTS
#CFLAGS += -DCC2420_NO_ADDRESS_RECOGNITION
#CFLAGS += -DTASKLET_IS_TASK
