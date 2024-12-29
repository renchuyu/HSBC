package org.example;

import org.junit.Test;

import static org.junit.Assert.*;

public class PriceLimiterTest {

    private int rowcount;
    @Test
    public void processCommandOption() throws Exception {

        //initlize the price limiter with test data
        PriceLimiter priceLimiter = new PriceLimiter();
        priceLimiter.setScenarios(Constants.SCENARIOS_ADVANTAGE);

        TickTable tickTable = new TickTable();
        tickTable.addTickRow( 0f,10, 0.01f);
        tickTable.addTickRow(10,0, 0.05f);
        priceLimiter.addTickTable("KS200400F5.KS", tickTable);

        PriceReference HSIZ4 = new PriceReference("HSIZ4" ,"Future", 19000	,19010,19020 );
        priceLimiter.addPriceRefrence(HSIZ4);

        PriceReference KS200400F5_KS = new PriceReference("KS200400F5.KS" ,"Option", 8.91f	, 8.88f, 8.84f );
        priceLimiter.addPriceRefrence(KS200400F5_KS);

        PriceReference VOD_L = new PriceReference("VOD.L" ,"Stock", 240	,245,231 );
        priceLimiter.addPriceRefrence(VOD_L);

        priceLimiter.setVariationLimit( Constants.PRODUCT_TYPE_OPTION, Constants.VARIATION_TYPE_TICK, 8);
        priceLimiter.setVariationLimit( Constants.PRODUCT_TYPE_STOCK, Constants.VARIATION_TYPE_VALUE, 10);

        System.out.println("*******Options********");
        try {
            priceLimiter.setLastTradingPrice("KS200400F5.KS", 8.81f);


            Message message = priceLimiter.processCommand("KS200400F5.KS", "Buy", 8.81f);


            printOutMessage("KS200400F5.KS", "Buy", 8.81f, message);
            priceLimiter.setLastTradingPrice("KS200400F5.KS", 8.81f);

            message = priceLimiter.processCommand("KS200400F5.KS", "Buy", 8.72f);
            printOutMessage("KS200400F5.KS", "Buy", 8.72f, message);

            message = priceLimiter.processCommand("KS200400F5.KS", "Buy", 8.90f);
            printOutMessage("KS200400F5.KS", "Buy", 8.90f, message);

            priceLimiter.setLastTradingPrice("KS200400F5.KS", 8.91f);
            message = priceLimiter.processCommand("KS200400F5.KS", "Sell", 8.92f);
            printOutMessage("KS200400F5.KS", "Sell", 8.92f, message);

            priceLimiter.setLastTradingPrice("KS200400F5.KS", 8.91f);
            message = priceLimiter.processCommand("KS200400F5.KS", "Sell", 8.82f);
            printOutMessage("KS200400F5.KS", "Sell", 8.82f, message);

            message = priceLimiter.processCommand("KS200400F5.KS", "Sell", 9.00f);
            printOutMessage("KS200400F5.KS", "Sell", 9.00f, message);

            priceLimiter.setLastTradingPrice("KS200400F5.KS", 9.93f);
            message = priceLimiter.processCommand("KS200400F5.KS", "Buy", 9.94f);
            printOutMessage("KS200400F5.KS", "Buy", 9.94f, message);



            message = priceLimiter.processCommand("KS200400F5.KS", "Buy", 9.84f);
            printOutMessage("KS200400F5.KS", "Buy", 9.84f, message);


            message = priceLimiter.processCommand("KS200400F5.KS", "Buy", 10.10f);
            printOutMessage("KS200400F5.KS", "Buy", 10.10f, message);

            priceLimiter.setLastTradingPrice("KS200400F5.KS", 9.95f);

            message = priceLimiter.processCommand("KS200400F5.KS", "Sell", 9.94f);
            printOutMessage("KS200400F5.KS", "Sell", 9.94f, message);

            message = priceLimiter.processCommand("KS200400F5.KS", "Sell", 9.87f);
            printOutMessage("KS200400F5.KS", "Sell", 9.87f, message);

            message = priceLimiter.processCommand("KS200400F5.KS", "Sell", 10.20f);
            printOutMessage("KS200400F5.KS", "Sell", 10.20f, message);

            priceLimiter.setLastTradingPrice("KS200400F5.KS", 10.15f);

            message = priceLimiter.processCommand("KS200400F5.KS", "Buy", 10.10f);
            printOutMessage("KS200400F5.KS", "Buy", 10.10f, message);

            message = priceLimiter.processCommand("KS200400F5.KS", "Buy", 9.94f);
            printOutMessage("KS200400F5.KS", "Buy", 9.94f, message);

            // this row may contain error, no price ofr 10.06 found in (10.15-10.60)/0.05 = -9 , maybe 10.60 is 10.06?
            //| 14 | KS200400F5.KS | Buy  | 10.06	 | No    | (10.15-10.60)/0.05 = -9                | buy higher, pass    |

            message = priceLimiter.processCommand("KS200400F5.KS", "Buy", 10.06f);
            printOutMessage("KS200400F5.KS", "Buy", 10.06f, message);

            priceLimiter.setLastTradingPrice("KS200400F5.KS", 10.25f);
            message = priceLimiter.processCommand("KS200400F5.KS", "Sell", 10.30f);
            printOutMessage("KS200400F5.KS", "Sell", 10.30f, message);

            message = priceLimiter.processCommand("KS200400F5.KS", "Sell", 9.96f);
            printOutMessage("KS200400F5.KS", "Sell", 9.96f, message);

            message = priceLimiter.processCommand("KS200400F5.KS", "Sell", 10.70f);
            printOutMessage("KS200400F5.KS", "Sell", 10.70f, message);


        }
        catch(Exception e)
        {

            System.out.printf(e.toString());

        }

    }

    @Test
    public void processCommandStock() throws Exception {


         //initlize the price limiter with test data
        PriceLimiter priceLimiter = new PriceLimiter();
        priceLimiter.setScenarios(Constants.SCENARIOS_BOTH);

        PriceReference HSIZ4 = new PriceReference("HSIZ4" ,"Future", 19000	,19010,19020 );
        priceLimiter.addPriceRefrence(HSIZ4);

        PriceReference KS200400F5_KS = new PriceReference("KS200400F5.KS" ,"Option", 8.91f	, 8.88f, 8.84f );
        priceLimiter.addPriceRefrence(KS200400F5_KS);

        PriceReference VOD_L = new PriceReference("VOD.L" ,"Stock", 240	,245,231 );
        priceLimiter.addPriceRefrence(VOD_L);

        priceLimiter.setVariationLimit( Constants.PRODUCT_TYPE_OPTION, Constants.VARIATION_TYPE_TICK, 8);
        priceLimiter.setVariationLimit( Constants.PRODUCT_TYPE_STOCK, Constants.VARIATION_TYPE_VALUE, 10);

        System.out.println("****** Stocks ********");

        //priceLimiter.setLastTradingPrice("VOD.L", 245);
        Message message = priceLimiter.processCommand("VOD.L", "Buy", 245);
        printOutMessage("VOD.L", "Buy", 245, message);

        message = priceLimiter.processCommand("VOD.L", "Buy", 255);
        printOutMessage("VOD.L", "Buy", 255, message);

        message = priceLimiter.processCommand("VOD.L", "Buy", 265);
        printOutMessage("VOD.L", "Buy", 265, message);

        message = priceLimiter.processCommand("VOD.L", "Sell", 245);
        printOutMessage("VOD.L", "Sell", 245, message);
        message = priceLimiter.processCommand("VOD.L", "Sell", 235);
        printOutMessage("VOD.L", "Sell", 235, message);
        message = priceLimiter.processCommand("VOD.L", "Sell", 225);
        printOutMessage("VOD.L", "Sell", 225, message);


    }

        public void printOutMessage(String instrument, String side, float price, Message message)
    {
        System.out.println( "\t|" + rowcount + "\t|" + instrument + "\t|" + side + "\t|" + price + "\t|" + convertBoolean(message.alert) + "\t|" + message.variation + "\t|" + message.getDescription());

        rowcount++;
    }
    public String convertBoolean(boolean aboolean)
    {
        if(aboolean)
            return "YES";
        else
            return "NO";
    }
}