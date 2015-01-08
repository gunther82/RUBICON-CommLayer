configuration TestAppC
{
}
implementation
{
  components MainC, TestC, LedsC;
  components new TimerMilliC() as Timer;
  TestC -> MainC.Boot;
  TestC.Timer0 -> Timer;
  TestC.Timer1 -> Timer;
  components RssiTransducerC;
  TestC.RssiTransducer -> RssiTransducerC.RssiTransducer;

}

