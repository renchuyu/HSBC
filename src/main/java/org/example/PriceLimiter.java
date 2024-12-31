package org.example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * The type Price limiter.
 */
public class PriceLimiter {


    //default value is Advantage
    private String validationScenario = Constants.SCENARIOS_ADVANTAGE;

    //limit is hashtable with product_tye as key, VariationType and Value pair are stored as SimpleEntry, this SimpleEntry is stored as value in hashtable
    //hashmap should be used as there is no synchronized issue.
    private static final HashMap<String, AbstractMap.SimpleEntry> limits = new HashMap<String, AbstractMap.SimpleEntry>();

    //priceReferenceTable for different instruments with instrument as key,
    private static final HashMap<String, PriceReference> priceReferenceTable = new HashMap<String, PriceReference>();

    //tickTables for different instruments, use instruments as key to access TickTable
    private static final HashMap<String, TickTable> tickTables = new HashMap<String, TickTable>();


    /**
     * Add price refrence.
     *
     * @param priceReference the price reference
     */
    public void addPriceRefrence(PriceReference priceReference) {
        priceReferenceTable.put(priceReference.getInstrument(), priceReference);
    }

    /**
     * Add a tick table
     *
     * @param instrument the instrument
     * @param tickTable  the tick table
     */
    public void addTickTable(String instrument, TickTable tickTable) {
        tickTables.put(instrument, tickTable);
    }


    /**
     * Sets scenarios.
     *
     * @param scenario the working scenario of the price limiter: Advantage, Disadvantage and Both;
     */
    public void setScenarios(String scenario) {
        if (scenario != null) {
            switch (scenario.toUpperCase()) {
                case Constants.SCENARIOS_ADVANTAGE:
                    this.validationScenario = Constants.SCENARIOS_ADVANTAGE;
                    System.out.println("Validation scenarios:" + scenario);
                    break;

                case Constants.SCENARIOS_DISADVANTAGE:
                    this.validationScenario = Constants.SCENARIOS_DISADVANTAGE;
                    System.out.println("Validation scenarios:" + scenario);
                    break;
                case Constants.SCENARIOS_BOTH:
                    this.validationScenario = Constants.SCENARIOS_BOTH;
                    System.out.println("Validation scenarios:" + scenario);
                    break;
                default:
                    System.out.println(" Your input:" + scenario + "  is invalid, please type in: advantage,disadvantage or both)");
                    break;
            }
        }
    }

    /**
     * Sets variation limit.
     *
     * @param productType   the product type
     * @param variationType the variation type
     * @param value         the value
     */
    public void setVariationLimit(String productType, String variationType, float value)
    {
        setVariationLimit(productType, variationType, BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP));

    }
    /**
     * Sets variation limit.
     *
     * @param productType   the product type Stock, Option, Future
     * @param variationType the variation type  Percentage, Value, Tick
     * @param value         the actual value
     */
    private void setVariationLimit(String productType, String variationType, BigDecimal value)
    {
        Objects.requireNonNull(variationType, "variationType is null, need to be percentage,value or tick");
        Objects.requireNonNull(productType, "productType is null, need to be stock, option or future");

        //prodcut type need to be stock, option or future only
        if(!productType.equalsIgnoreCase(Constants.PRODUCT_TYPE_STOCK)&& !productType.equalsIgnoreCase(Constants.PRODUCT_TYPE_OPTION)&&!productType.equalsIgnoreCase(Constants.PRODUCT_TYPE_FUTURE))
        {
            System.out.println("invalid product type:"+  variationType +" (Stock, Option or Future is needed)");
            return;
        }
        //variation need to be percentage, value or tick
        if(!variationType.equalsIgnoreCase(Constants.VARIATION_TYPE_PERCENTAGE) && !variationType.equalsIgnoreCase(Constants.VARIATION_TYPE_VALUE)&&!variationType.equalsIgnoreCase(Constants.VARIATION_TYPE_TICK))
        {
            System.out.println("invalid variation type:"+  variationType +" (Percentage, Value or Tick is needed)");
            return;
        }

        //VariatoinType and Value paire are stored as SimpleEntry
        AbstractMap.SimpleEntry<String, BigDecimal> limitEntry = new AbstractMap.SimpleEntry<String, BigDecimal>(variationType.toUpperCase(),value);
        //always use upper case for productType
        limits.put(productType.toUpperCase(), limitEntry);
        System.out.println("Variations limit is set to new value:"+  value +" by " + variationType +  " for " + productType);

    }

    /**
     * Process command message.
     *
     * @param instrument the instrument
     * @param side       the side, Buy or Sell
     * @param price      the price
     * @return the message
     * @throws Exception the exception
     */
    public Message processCommand(String instrument, String side, float price) throws Exception {
        return processCommand(instrument,side,BigDecimal.valueOf(price).setScale(2,RoundingMode.HALF_UP));
    }
    /**
     * Process command message.
     *
     * @param instrument the instrument
     * @param side       the side, Buy or Sell
     * @param price      the price
     * @return the message
     * @throws Exception the exception
     */
    private Message processCommand(String instrument, String side, BigDecimal price) throws Exception {



        if ((side == null) || (!side.equalsIgnoreCase(Constants.ORDER_SIDE_BUY) && !side.equalsIgnoreCase(Constants.ORDER_SIDE_SELL))) {
            throw new IllegalArgumentException("side  parameter must be buy or sell");
        }

        PriceReference priceReference = priceReferenceTable.get(instrument);
        if (priceReference == null) {
            throw new Exception("can't find price reference for:" + instrument + ", please set price reference first.");
        }

        //check if priceReference is valid
        if (priceReference.getReferencePrice().floatValue() == 0) {

            throw new Exception("price reference for:" + instrument + " is not valid, please set price reference first.");


        }


        AbstractMap.SimpleEntry limit = limits.get(priceReference.getType().toUpperCase());

        if (limit == null) {
            throw new Exception("no limit set for: " + priceReference.getType() + ", please set limit type first.");
        }


        switch (limit.getKey().toString().toUpperCase()) {


            case Constants.VARIATION_TYPE_PERCENTAGE:
                //((order price - reference price)/ reference price) *100 is the percentage
                BigDecimal variationPercentage = (price.subtract(priceReference.getReferencePrice()).divide(priceReference.getReferencePrice(),4, RoundingMode.HALF_UP));

                //string concatenation for variation description
                String variationStrPecentage = "(" + price + "-"+priceReference.getReferencePrice() + ")/" + priceReference.getReferencePrice() + "="+formatToPercentage(variationPercentage.floatValue()) ;

                String pecentageStr = "";
                //compose the description str
                if (variationPercentage.floatValue()<0)
                {
                    pecentageStr= "abs("+ formatToPercentage(variationPercentage.floatValue()) + ")";
                    variationPercentage = variationPercentage.abs();
                }
                else
                    pecentageStr = formatToPercentage(variationPercentage.floatValue());

                if (variationPercentage.floatValue() >= ((BigDecimal)limit.getValue()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP).floatValue()) {
                    Message message = new Message(true, variationStrPecentage,pecentageStr + ">=" + formatToPercentage(((BigDecimal)limit.getValue()).divide(BigDecimal.valueOf(100),4,RoundingMode.HALF_UP).floatValue())+ ", block") ;
                    //handle the scenario before return
                    return scenarioHandling(side,price,priceReference,message);
                }
                else {
                    Message message = new Message(false,  variationStrPecentage,  pecentageStr + "<" + formatToPercentage(((BigDecimal)limit.getValue()).divide(BigDecimal.valueOf(100),4,RoundingMode.HALF_UP).floatValue())+ ", pass") ;
                    //handle the scenario before return
                    return scenarioHandling(side,price,priceReference,message);
                }

                //break;
            case Constants.VARIATION_TYPE_VALUE:
                BigDecimal variationValue;
                String variationStrValue="";

                // buy order and sell order has diffrent formula
                // sell order is the opposite of buy, so switch two prices
                if(side.equalsIgnoreCase(Constants.ORDER_SIDE_BUY))
                {
                    variationValue = price.subtract(priceReference.getReferencePrice());
                    variationStrValue= " " + price.floatValue() + "-"+priceReference.getReferencePrice().floatValue() + " = " + variationValue.floatValue() ;

                }
                else{
                    variationValue = priceReference.getReferencePrice().subtract(price);
                    variationStrValue= " " + priceReference.getReferencePrice().floatValue() + "-"+ price.floatValue()  + " = " + variationValue.floatValue() ;

                }




//                //compose the description str
//                if (variationValue.floatValue()<0)
//                {
//                    valueStr= "abs("+ formatToValue(variationValue.floatValue()) + ")";
//                    variationValue = variationValue.abs();
//                }
//                else
//                    valueStr = formatToValue(variationValue.floatValue());


                if (variationValue.floatValue() >= ((BigDecimal) limit.getValue()).floatValue()) {
                    Message message = new Message(true,  variationStrValue, variationValue.floatValue() + ">=" + formatToValue(((BigDecimal) limit.getValue()).floatValue())+", block") ;
                    return scenarioHandling(side,price,priceReference,message);
                }
                else {
                    Message message = new Message(false,  variationStrValue, variationValue.floatValue() + "<" + formatToValue(((BigDecimal) limit.getValue()).floatValue())+", pass") ;
                    return scenarioHandling(side,price,priceReference,message);
                }
                //break;

            case Constants.VARIATION_TYPE_TICK:
                TickTable tickTable = tickTables.get(instrument);
                if(tickTable==null)
                    throw new Exception("no tick table found for :" + instrument);

                //pass message to counTick so the Variation description can be generated as text
                Message message   =  new Message(true,"","");



                int  totalTick = tickTable.countTick(price, priceReference.getReferencePrice(), message);



                String tickCountStr = "";
                //compose the description str
                if (totalTick<0)
                {
                    tickCountStr= "abs("+ totalTick + ")";
                    totalTick = Math.abs(totalTick);
                }
                else
                    tickCountStr = String.valueOf(totalTick);


                if (totalTick >= ((BigDecimal)limit.getValue()).intValue()) {
                    message.setAlert(true);
                    message.setDescription(tickCountStr + ">=" + limit.getValue()+ ", block");
                    return scenarioHandling(side,price,priceReference,message);
                }
                else {
                    message.setAlert(false);
                    message.setDescription(tickCountStr + "<" + limit.getValue()+ ", pass");
                    return scenarioHandling(side,price,priceReference,message);
                }

        }
        return new Message(true,"error","Limiter is not activated");
    }

    /**
     * Format to percentage string, such as 0.1234f to 12.34%
     *
     * @param number the number
     * @return the string
     */

    public static String formatToPercentage(float number) {
        return String.format("%.2f%%", number * 100);
    }

    /**
     * Format to value string.
     *
     * @param number the number
     * @return the string
     */
    public static String formatToValue(float number) {
        return String.format("%.2f", number);
    }

    /**
     * Scenario handling message.
     *
     * @param side           the side
     * @param price          the price
     * @param priceReference the price reference
     * @param message        the message
     * @return the message
     * @throws Exception the exception
     */
    public Message scenarioHandling(String side, BigDecimal price, PriceReference priceReference, Message message) throws Exception {

        //scenario handling
        switch (validationScenario)
        {
            case Constants.SCENARIOS_ADVANTAGE:
                // advantage scenario, buy high and sell low is ok

                if(side.equalsIgnoreCase(Constants.ORDER_SIDE_BUY)&& (price.floatValue()> priceReference.getReferencePrice().floatValue()))
                {

                    if(message.isAlert())
                    {
                        message.setDescription("buy higher, pass") ;
                        message.setAlert(false);

                    }

                }
                if( side.equalsIgnoreCase(Constants.ORDER_SIDE_SELL)&&(price.floatValue()<priceReference.getReferencePrice().floatValue()))
                {
                    if(message.isAlert()) {
                        message.setAlert(false);
                        message.setDescription("sell lower, pass");
                    }
                }
                break;
            case Constants.SCENARIOS_DISADVANTAGE:
                // disadvantage scenario, buy low and sell high is ok
                if(side.equalsIgnoreCase(Constants.ORDER_SIDE_BUY)&& (price.floatValue()<priceReference.getReferencePrice().floatValue()))
                {
                    if(message.isAlert()) {
                        message.setAlert(false);
                        message.setDescription("buy lower, pass");
                    }
                }
                if( side.equalsIgnoreCase(Constants.ORDER_SIDE_SELL)&&(price.floatValue()>priceReference.getReferencePrice().floatValue()))
                {
                    if(message.isAlert()) {
                        message.setAlert(false);
                        message.setDescription("sell higher, pass");
                    }
                }
                break;
            default:


        }

        return message;
    }

    /**
     * Sets last trading price.
     *
     * @param instrument       the instrument
     * @param lastTradingPrice the last trading price
     */
    public void setLastTradingPrice(String instrument, float lastTradingPrice)
    {

        PriceReference priceReference =  priceReferenceTable.get(instrument);

        if(priceReference!=null)
        {
            priceReference.setLastTradePrice(BigDecimal.valueOf(lastTradingPrice).setScale(2,RoundingMode.HALF_UP));
        }


    }
}
