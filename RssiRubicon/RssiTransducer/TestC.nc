#include "Timer.h"
#include "printf.h"
#include "../Rssi.h"

module TestC @safe()
{
  uses interface Timer<TMilli> as Timer0;
  uses interface Timer<TMilli> as Timer1;
  uses interface Leds;
  uses interface Boot;
  uses interface RssiTransducer;
}
implementation
{
	uint16_t sum = 0;
	uint16_t iteration = 0;
	uint16_t totalIteration = 5000;
	uint8_t expected = TIMER_TRANSDUCER;	
	uint16_t higherThanExpected = 0;
	uint32_t lastTime = 0;
	uint8_t min = 200;	
	uint8_t max = 0;	
	uint8_t stat[120];

        uint16_t interval()
        {
                uint32_t nowTime = call Timer0.getNow();
                uint16_t i = nowTime - lastTime;
                lastTime = nowTime;
                return i;
        }
	
	event void Boot.booted()
	{
		call Timer0.startPeriodic( 100 );
	}
	
	event void Timer0.fired()
	{
		// printf("* [%d]\t Execute read\n",interval());
		// printf("  Iteration: %d\r",iteration);
		interval();
		call RssiTransducer.read();
	}
	
	event void Timer1.fired()
	{
	}
	
	event void RssiTransducer.readDone(uint16_t length, uint16_t data[])
	{
		uint8_t rt;
		uint8_t i = 0;
	
		rt = interval();

		stat[rt] = stat[rt]++;

		// printf("%d iterations, responce time: %d\n",iteration,rt);
		// printf("responce time: %d\n",rt);

		iteration++;
		sum=sum+rt;

		if (rt >= expected) higherThanExpected++;
		if (rt > max) max = rt;
		if (rt < min) min = rt;

		if (iteration >= totalIteration)
		{
			call Timer0.stop();
			printf("\n------------------------------\n");
			printf("Number of anchors: %d\n",NUM_ANCHORS);
			printf("Iterations done: %d\n",iteration);
			// printf("Sum of all response time: %d\n",sum);
			printf("Average response time: %d\n",sum/iteration);
			printf("Higher than %d: %d\n",expected,higherThanExpected);
			printf("Minimum response time: %d\n",min);
			printf("Maximum response time: %d\n",max);
			printf("------------------------------\n");
			for (i = 0; i < 120; i++)
			{
				printf("%d\t%d\n",i,stat[i]);	
			}		
			printfflush();			
		}
	}
}

