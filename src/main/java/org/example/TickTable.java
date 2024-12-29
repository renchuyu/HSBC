package org.example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.TreeMap;

/**
 * The type Tick table.
 */
public class TickTable {

    // using ordered map and Tickrange as a key to fetch tick size
    private TreeMap<TickRange, BigDecimal> tickMap = new TreeMap<TickRange, BigDecimal>();


//
//    private void addTickRow(BigDecimal min, BigDecimal max, BigDecimal tick){
//
//        TickRange tickrange = new TickRange(min, max);
//        tickMap.put(tickrange, tick);
//    }

    /**
     * Add tick row.
     *
     * @param min  the min
     * @param max  the max
     * @param tick the tick
     */
    public void addTickRow(float min, float max, float tick){
        TickRange tickrange = new TickRange(BigDecimal.valueOf(min).setScale(2,RoundingMode.HALF_UP), BigDecimal.valueOf(max).setScale(2,RoundingMode.HALF_UP));
        tickMap.put(tickrange, BigDecimal.valueOf(tick).setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Gets tick.
     *
     * @param price the price
     * @return the tick
     */
    public BigDecimal getTick(BigDecimal price)
    {
            for( TickRange tickRange: tickMap.keySet())
            {

                // maxPrice =0 for the last row
                if(tickRange.getMaxPrice().floatValue()==0)
                {
                   if(price.floatValue()> tickRange.getMinPrice().floatValue())
                       return tickMap.get(tickRange);
                }
                else
                if(price.floatValue() > tickRange.getMinPrice().floatValue() && price.floatValue()< tickRange.getMaxPrice().floatValue() )
                {
                    return tickMap.get(tickRange);
                }
            }
            return null;
    }

    /**
     * Gets tick range.
     *
     * @param price the price
     * @return the tick range
     * @throws Exception the exception
     */
    public TickRange getTickRange(BigDecimal price) throws Exception {
        for( TickRange tickRange: tickMap.keySet())
        {

            // maxPrice =0 for the last row
            if(tickRange.getMaxPrice().floatValue()==0)
            {
                if(price.floatValue()> tickRange.getMinPrice().floatValue())
                    return tickRange;
            }
            else
            if(price.floatValue() > tickRange.getMinPrice().floatValue() && price.floatValue()< tickRange.getMaxPrice().floatValue() )
            {
                return tickRange;
            }
        }
        throw new Exception("no tick record found for price:" + price);
    }
//    // get the tick index
//    public float getTickIndex(float price)
//    {
//        int i=0;
//        for( Float minPrice: tickMap.keySet())
//        {
//            if(price > minPrice)
//            {
//                return tickMap.get(minPrice);
//            }
//            i++;
//        }
//        return 0;
//    }


    /**
     * Count tick int.
     *
     * @param price          the price
     * @param referencePrice the reference price
     * @param message        the message
     * @return the int
     * @throws Exception the exception
     */
    public int countTick(BigDecimal price, BigDecimal referencePrice, Message message) throws Exception {

        //if both price has the same tick size
        if(getTick(price).compareTo(getTick(referencePrice))==0)
        {

           int tickSize =  referencePrice.subtract(price).divide(getTick(price),2,RoundingMode.HALF_UP).intValue();



           message.setVariation("(" + referencePrice.floatValue()+"-"+ price.floatValue()+")/"+ getTick(price).floatValue() + "=" + tickSize );
           return tickSize;

        }
        //if two price has the different tick size, calculation will has three parts
        // first calculate ticks for the tick range of higher price, second for the tick range of lower price
        // third for all tick ranges between those two ranges, add them together is the total tick.

        int totalTick=0;
        BigDecimal higherPrice = price;
        BigDecimal lowerPrice = referencePrice;

        // convert price and reference pirce to higher price and lower price and calculate the tick distance between them
        // will always get a positive tick number and add sign later on
        if(higherPrice.floatValue() < lowerPrice.floatValue())
        {
            higherPrice = referencePrice;
            lowerPrice = price;
        }


        //get the tick range of the higher price
        TickRange higherPriceRange = getTickRange(higherPrice);


        totalTick = totalTick +  higherPrice.subtract(higherPriceRange.getMinPrice()).divide(tickMap.get(higherPriceRange), 2,RoundingMode.HALF_UP).intValue();


        //try to make some string like (10-10.20)/0.05
        String higherPriceVariationStr = "(" + higherPrice.floatValue()+"-"+ higherPriceRange.getMinPrice().floatValue()+")/"+ tickMap.get(higherPriceRange).floatValue();



        TickRange lowerPriceRange = getTickRange(lowerPrice);
        // tick count for this part is (lowerPriceRange.maxprice - lowerPrice)/ ticksize
       // totalTick = totalTick + (int)((lowerPriceRange.getMaxPrice() - lowerPrice )/ tickMap.get(lowerPriceRange));

        totalTick = totalTick + lowerPriceRange.getMaxPrice().subtract(lowerPrice).divide(tickMap.get(lowerPriceRange),2,RoundingMode.HALF_UP).intValue();


        //try to make some string like （9.93-10)/0.01
        String lowerPriceVariationStr = "(" + lowerPriceRange.getMaxPrice().floatValue() +"-"+ lowerPrice.floatValue()+")/"+tickMap.get(lowerPriceRange).floatValue();

        //make variation string for the middle part
        StringBuilder middlePriceVariationStr = new StringBuilder();

        //calculate the tick between higher price and lower price.
        for( TickRange tickRange: tickMap.keySet())
        {

            if(tickRange.compareTo(lowerPriceRange)>0 && tickRange.compareTo(higherPriceRange)<0)
            {

                totalTick = totalTick + tickRange.getMaxPrice().subtract(tickRange.getMinPrice()).divide(tickMap.get(tickRange),2,RoundingMode.HALF_UP).intValue();

                //generate variation string, add "+" for the variation string between different tick
                if(!middlePriceVariationStr.isEmpty())
                {
                    middlePriceVariationStr.append(" + ").append("(").append(tickRange.getMaxPrice().floatValue()).append("-").append(tickRange.getMinPrice().floatValue()).append(")/").append(tickMap.get(tickRange).floatValue());
                }
                else
                {
                    middlePriceVariationStr = new StringBuilder("(" + tickRange.getMaxPrice().toString() + "-" + tickRange.getMinPrice().toString() + ")/" + tickMap.get(tickRange).toString());

                }

            }


        }
        if(middlePriceVariationStr.isEmpty())
        {
            message.setVariation(lowerPriceVariationStr + "+" +higherPriceVariationStr);
        }
        else
            message.setVariation(lowerPriceVariationStr + " + "+ middlePriceVariationStr + " + "+ higherPriceVariationStr);

        //so far the stick is always positive, if price > reference price, it will be a negative number
        if(price.floatValue() >referencePrice.floatValue()) {
            totalTick = -totalTick;
            //update variation to (variation)*-1, variation will be slightly different with the test file but with the same result
            message.setVariation("(" + message.getVariation() +")*-1" + "="+ totalTick);
            return totalTick;
        }
            else {
            message.setVariation( message.getVariation() + "=" + totalTick);
            return totalTick;
        }



    }


}
