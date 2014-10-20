#include "stdlib.h"

/* constructor
 *
 */
Simulator()
{

}

/* constructor
 *
 */
Simulator(DataFeed datafeed)
{
	this->market = datafeed;
}

/* destructor
 *
 */
~Simulator()
{

}