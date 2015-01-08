interface RssiTransducer
{

	command void read();

	event void readDone(error_t result, uint16_t length, uint16_t data[]);

}
