
# Problem Definition
Trading with a price far away from market can be very risky. This is called Price Variation Variation and can happened to due a variety of causes, such as mispricing or human error. In algo trading, price variation result in a huge finical loss.
To minimize these risks, you can configure to restrict the max and min price allowed to trade an instrument with. If trader attempts to trade the instrument with an order price beyond the configured limit, an alert will be triggered to traders.

# Examples
Let's say that you've configured a price variation limit of 20%. This means that if you try to send an order witch price away from a reference price by 20%, the price variation limit will be violated.

As a simple example

If a trader tries to submit an order with following details as 'Buy 100 lots of Apple Inc at 300 $, which reference price is 230 $'.  then price variation limit will be activated, an alert message will be sent to user.

## Tick size
Tick size is the minimum price change up or down of a trading instrument in a market.

## Tick table
Tick table for Apple Inc, following tick table means,
* if price is between 0 and 20, then tick size is 1 $.
* if price is between 20(include 20) and 100, then tick size is 5 $. 
* if price is above 100(include 100), then tick size is 10 $

| Min | Max | Tick Size |
|-----|-----|-----------|
| 0   | 20  | 1         |
| 20  | 100 | 5         |
| 100 |     | 10        |

# Requirements
Implement a price variation limiter with following features, the input is an Order command. the output is an indicator of whether price variation limiter is activated or not and why. 

## Price Variation Limiter should apply to multiple types of equity products.  can set different limits for different type of products. 
* Products type: Stock, Option, Future
## Price Variation Limiter should support multiple ways to calculate the price variations. 
* By percentage: In above example, the price variation by percentage is (300 - 230 ) / 230 = 30.43%
* By absolute value: In above example, the price variation by absolute  value is 300 - 230 = 70
* By Tick size: In above example, the price variation by tick size is (300 - 230) / 10 = 7 (T) 
## Price Variation Limiter should support different validation scenarios
* Only at advantage:  this means that Price Variation Limiter only can be activated when HSBC is at an advantage situation. For example, buy something at a price much lower than refrence price,  or sell something at a price much higher than refrence price. This can be a requirement from regulatory compliance
* Only at disadvantage: this means that Price Variation Limiter only can be activated when HSBC is at a disadvantage situation. For example, buy something at a price much higher than refrence price,  or sell something at a price much lower than refrence price. This mainly to protect HSBC from losing money.
* Both: sometimes Price Variation Limiter need to be activated in both advantage and disadvantage case.
## Price Variation Limiter should support different sources of reference price with the following fallback logic 
* last traded price should be firstly used if available
* close price should be fallen back to if above is not available
* theo price should be fallen back to if above is not available
* No trades should be allowed if there are not any reference prices.

# Acceptance Criteria
* After program starts, 

# Tests Data & Scenarios 
## Tick table of KS200400F5.KS

| Min  | Max  | Tick Size |
|------|------|-----------|
| 0.0  | 10.0 | 0.01      |
| 10.0 |      | 0.05      |

## Reference price

| Instrument     | Product Type | Theo Price | Last Trade Price | Close Price |
|----------------|--------------|------------|------------------|-------------|
| HSIZ4          | Future       | 19000	     | 19010            | 19020       |
| KS200400F5.KS  | Option       | 8.91       | 8.88             | 8.84        |
| VOD.L          | Stock        | 240        | 245              | 231         |

## Orders and output
### Option

| No | Instrument    | Side | Price  | Alert | Variation                              | Description         |
|----|---------------|------|--------|-------|----------------------------------------|---------------------|
| 0  | KS200400F5.KS | Buy  | 8.81   | No    | (8.81-8.81)/0.01 = 0                   | 0 < 8, pass         |
| 1  | KS200400F5.KS | Buy  | 	8.72  | Yes   | (8.81-8.72)/0.01 = 9                   | 9 >= 8, block       |
| 2  | KS200400F5.KS | Buy  | 8.90   | No    | (8.81-8.90)/0.01 = -9                  | buy higher, pass    |
| 3  | KS200400F5.KS | Sell | 8.92   | No    | (8.91-8.92)/0.01 = -1                  | abs(-1) < 8, pass   |
| 4  | KS200400F5.KS | Sell | 8.82   | No    | (8.91-8.82)/0.01 = 9                   | sell lower, pass    |
| 5  | KS200400F5.KS | Sell | 9.00   | Yes   | (8.91-9.00)/0.01 = -9	                 | abs(-9) >= 8, block |
| 6  | KS200400F5.KS | Buy  | 9.94   | No    | (9.93-9.94)/0.01 = -1                  | abs(-1) < 8, pass   |
| 7  | KS200400F5.KS | Buy  | 9.84   | Yes   | (9.93-9.84)/0.01 = 9                   | 9 >= 8, block       |
| 8  | KS200400F5.KS | Buy  | 10.10	 | No    | (9.93-10)/0.01 + (10-10.10)/0.05 = -9	 | buy higher, pass    |
| 9  | KS200400F5.KS | Sell | 9.94   | No    | (9.95-9.94)/0.01 = 1                   | 1 < 8, pass         |
| 10 | KS200400F5.KS | Sell | 9.87	  | No    | 9.95-9.87)/0.01 = 8                    | sell lower, pass    |
| 11 | KS200400F5.KS | Sell | 10.20  | Yes   | (9.95-10)/0.01 + (10-10.20)/0.05 = -9  | abs(-9) >= 8, block |
| 12 | KS200400F5.KS | Buy  | 10.10  | No    | (10.15-10.10)/0.05 = 1                 | 1 < 8, pass         |
| 13 | KS200400F5.KS | Buy  | 9.94   | Yes   | 10.15-10)/0.05 + (10-9.94)/0.01 = 9    | 9 >= 8, block       |
| 14 | KS200400F5.KS | Buy  | 10.06	 | No    | (10.15-10.60)/0.05 = -9                | buy higher, pass    |
| 15 | KS200400F5.KS | Sell | 	10.30 | No    | (10.25-10.30)/0.05 = -1                | abs(-1) < 8, pass   |
| 16 | KS200400F5.KS | Sell | 9.96   | No    | (10.25-10)/0.05 + (10-9.96)/0.01 = 9   | sell lower, pass    |
| 17 | KS200400F5.KS | Sell | 10.70  | Yes   | (10.25-10.70)/0.05 = -9                | abs(-9) >= 8, block |
### Stock

| No | Instrument | Side  | Price | Alert | Variation        | Description      |
|----|------------|-------|-------|-------|------------------|------------------|
| 1	 | VOD.L	     | Buy   | 	245  | 	No	  | 245 - 245 = 0	   | 0 < 10, pass     |
| 2	 | VOD.L	     | Buy   | 	255  | 	Yes	 | 255 - 245 = 10	  | 10 >= 10, block  |
| 3	 | VOD.L	     | Buy   | 	265  | 	No	  | (265 - 245) = 20 | 	20 >= 10, block |
| 4	 | VOD.L	     | Sell  | 	245  | 	No	  | 245 - 245 = 0	   | 0 < 10, pass     |
| 5	 | VOD.L	     | Sell	 | 235   | 	Yes	 | 245 - 235 = 10	  | 10 >= 10, block  |
| 6	 | VOD.L	     | Sell	 | 225   | 	Yes	 | 245 - 225= 20	   | 20 >= 10, block  |

==================================================================================

# Implementation:

PriceLimiter.java is the utility class to do the calculation, user can call processCommand to do the caculation, a Message object will be returned with wether price variation limiter is activated (using Message.isAlert()) or not, along with the reason description and variation (calcualtion formula). 

Before use PriceLimiter to do the caulculation, user need to set up the ticktable, scenarios, limit conditions..etc, PriceLimiterTest.java show all the steps for this.

Also, PriceLimter is not thread safe, if multithread is required please let me know.

PriceLimiterTest.java is the test class and it can replicate tesitng data, it wil generate the following printout when tested through Junit.


new validation scenarios:BOTH

variations limit is set to new value:10.00 by VALUE for STOCK


****** Stocks ********

	|0	|VOD.L	|Buy	|245.0	|NO	| 245.0-245.0 = 0.0	|0.0<10.00, pass
 
	|1	|VOD.L	|Buy	|255.0	|YES	| 255.0-245.0 = 10.0	|10.0>=10.00, block
 
	|2	|VOD.L	|Buy	|265.0	|YES	| 265.0-245.0 = 20.0	|20.0>=10.00, block
 
	|3	|VOD.L	|Sell	|245.0	|NO	| 245.0-245.0 = 0.0	|0.0<10.00, pass
 
	|4	|VOD.L	|Sell	|235.0	|YES	| 245.0-235.0 = 10.0	|10.0>=10.00, block
 
	|5	|VOD.L	|Sell	|225.0	|YES	| 245.0-225.0 = 20.0	|20.0>=10.00, block
 
validation scenarios:ADVANTAGE

variations limit is set to new value:8.00 by TICK for OPTION

*******Options********

	|0	|KS200400F5.KS	|Buy	|8.81	|NO	|(8.81-8.81)/0.01=0	|0<8.00, pass
 
	|1	|KS200400F5.KS	|Buy	|8.72	|YES	|(8.81-8.72)/0.01=9	|9>=8.00, block
 
	|2	|KS200400F5.KS	|Buy	|8.9	|NO	|(8.81-8.9)/0.01=-9	|buy higher, pass
 
	|3	|KS200400F5.KS	|Sell	|8.92	|NO	|(8.91-8.92)/0.01=-1	|abs(-1)<8.00, pass
 
	|4	|KS200400F5.KS	|Sell	|8.82	|NO	|(8.91-8.82)/0.01=9	|sell lower, pass
 
	|5	|KS200400F5.KS	|Sell	|9.0	|YES	|(8.91-9.0)/0.01=-9	|abs(-9)>=8.00, block
 
	|6	|KS200400F5.KS	|Buy	|9.94	|NO	|(9.93-9.94)/0.01=-1	|abs(-1)<8.00, pass
 
	|7	|KS200400F5.KS	|Buy	|9.84	|YES	|(9.93-9.84)/0.01=9	|9>=8.00, block
 
	|8	|KS200400F5.KS	|Buy	|10.1	|NO	|((10.0-9.93)/0.01+(10.1-10.0)/0.05)*-1=-9	|buy higher, pass
 
	|9	|KS200400F5.KS	|Sell	|9.94	|NO	|(9.95-9.94)/0.01=1	|1<8.00, pass
 
	|10	|KS200400F5.KS	|Sell	|9.87	|NO	|(9.95-9.87)/0.01=8	|sell lower, pass
 
	|11	|KS200400F5.KS	|Sell	|10.2	|YES	|((10.0-9.95)/0.01+(10.2-10.0)/0.05)*-1=-9	|abs(-9)>=8.00, block
 
	|12	|KS200400F5.KS	|Buy	|10.1	|NO	|(10.15-10.1)/0.05=1	|1<8.00, pass
 
	|13	|KS200400F5.KS	|Buy	|9.94	|YES	|(10.0-9.94)/0.01+(10.15-10.0)/0.05=9	|9>=8.00, block
 
	|14	|KS200400F5.KS	|Buy	|10.06	|NO	|(10.15-10.06)/0.05=1	|1<8.00, pass
 
	|15	|KS200400F5.KS	|Sell	|10.3	|NO	|(10.25-10.3)/0.05=-1	|abs(-1)<8.00, pass
 
	|16	|KS200400F5.KS	|Sell	|9.96	|NO	|(10.0-9.96)/0.01+(10.25-10.0)/0.05=9	|sell lower, pass
 
	|17	|KS200400F5.KS	|Sell	|10.7	|YES	|(10.25-10.7)/0.05=-9	|abs(-9)>=8.00, block

all these records are the same with test data except record 14, the oringinal record of test data looks like this:

| 14 | KS200400F5.KS | Buy  | 10.06	 | No    | (10.15-10.60)/0.05 = -9                | buy higher, pass    |

it seems buy price of 10.06 is not used in the caculation: (10.15-10.60)/0.05 = -9, could be an error and my program generate the following message for the same order:

|14	|KS200400F5.KS	|Buy	|10.06	|NO	|(10.15-10.06)/0.05=1	|1<8.00, pass


except this, priceLimiter generated same outcome as test data through proper configuration, futhere testing is needed once more data are avaliable.


the testing data of option show that instead of (buy_price - reference_price)/tick,  (reference_price - buy)/tick is used for the calculation, which is not as described in the apple example, so priceLimiter used the same formula for option. program will also assume that last_trade_price is keeping changing in order to match the test data.

##Further testing
New test for future with limit type by percentage is added in the processCommandFuture method in the PriceLimiterTest.java. the test put 5% limit with scenarios of DISADVANTAGE on all trade and it will generated following outcome:
****** Future ********
	|0	|HSIZ4	|Buy	|18900.0	|NO	|(18900.00-19010.00)/19010.00=-0.58%	|abs(-0.58%)<5.00%, pass
 
	|1	|HSIZ4	|Buy	|20000.0	|NO	|(19050.00-19010.00)/19010.00=0.21%	|0.21%<5.00%, pass
 
	|2	|HSIZ4	|Buy	|18000.0	|NO	|(18000.00-19010.00)/19010.00=-5.31%	|buy lower, pass
 
	|3	|HSIZ4	|Buy	|18000.0	|YES	|(20000.00-19010.00)/19010.00=5.21%	|5.21%>=5.00%, block
 
	|4	|HSIZ4	|Sell	|18900.0	|NO	|(18900.00-19010.00)/19010.00=-0.58%	|abs(-0.58%)<5.00%, pass
 
	|5	|HSIZ4	|Sell	|19050.0	|NO	|(19050.00-19010.00)/19010.00=0.21%	|0.21%<5.00%, pass
 
	|6	|HSIZ4	|Sell	|18000.0	|YES	|(18000.00-19010.00)/19010.00=-5.31%	|abs(-5.31%)>=5.00%, block
 
	|7	|HSIZ4	|Sell	|20000.0	|NO	|(20000.00-19010.00)/19010.00=5.21%	|sell higher, pass
 
	|8	|HSIZ4	|Buy	|18000.0	|NO	|(18000.00-18800.00)/18800.00=-4.26%	|abs(-4.26%)<5.00%, pass
 
	|9	|HSIZ4	|Buy	|19060.0	|NO	|(19060.00-18800.00)/18800.00=1.38%	|1.38%<5.00%, pass
 
	|10	|HSIZ4	|Buy	|17000.0	|NO	|(17000.00-18800.00)/18800.00=-9.57%	|buy lower, pass
 
	|11	|HSIZ4	|Buy	|20200.0	|YES	|(20200.00-18800.00)/18800.00=7.45%	|7.45%>=5.00%, block
 
	|12	|HSIZ4	|Sell	|18000.0	|NO	|(18000.00-18800.00)/18800.00=-4.26%	|abs(-4.26%)<5.00%, pass
 
	|13	|HSIZ4	|Sell	|19060.0	|NO	|(19060.00-18800.00)/18800.00=1.38%	|1.38%<5.00%, pass
 
	|14	|HSIZ4	|Sell	|17000.0	|YES	|(17000.00-18800.00)/18800.00=-9.57%	|abs(-9.57%)>=5.00%, block
 
	|15	|HSIZ4	|Sell	|20200.0	|NO	|(20200.00-18800.00)/18800.00=7.45%	|sell higher, pass


If there is any question, please feel free to contact me at renchuyubj@163.com directly and thank you.

best regards





